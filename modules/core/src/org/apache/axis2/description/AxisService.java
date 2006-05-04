/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package org.apache.axis2.description;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.deployment.util.PhasesInfo;
import org.apache.axis2.deployment.util.Utils;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.modules.Module;
import org.apache.axis2.phaseresolver.PhaseResolver;
import org.apache.axis2.transport.TransportListener;
import org.apache.axis2.util.PolicyUtil;
import org.apache.axis2.util.XMLUtils;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaExternal;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.java2wsdl.Java2WSDLConstants;
import org.apache.ws.java2wsdl.SchemaGenerator;
import org.apache.ws.java2wsdl.utils.TypeTable;
import org.codehaus.jam.JMethod;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

/**
 * Class AxisService
 */
public class AxisService extends AxisDescription {

    private int nsCount = 0;
	private static final Log log = LogFactory.getLog(AxisService.class);
    private URL fileName;

    private HashMap operationsAliasesMap = null;
//    private HashMap operations = new HashMap();

    // to store module ref at deploy time parsing
    private ArrayList moduleRefs = null;

    // to store engaged mdodules
    private ArrayList engagedModules = null;
    private String serviceDescription;

    // to store the wsdl definition , which is build at the deployment time
    // to keep the time that last update time of the service
    private long lastupdate;
    private HashMap moduleConfigmap;
    private String name;
    private ClassLoader serviceClassLoader;

    //to keep the XMLScheam getting either from WSDL or java2wsdl
    private ArrayList schemaList;
    //private XmlSchema schema;

    //wsdl is there for this service or not (in side META-INF)
    private boolean wsdlfound = false;

    //to store the scope of the service
    private String scope;

    //to store default message receivers
    private HashMap messageReceivers;

// to set the handler chain available in phase info
    private boolean useDefaultChains = true;

    //to keep the status of the service , since service can stop at the run time
    private boolean active = true;

    //to keep the service target name space
    private String targetNamespace =
            Java2WSDLConstants.DEFAULT_TARGET_NAMESPACE;
    private String targetNamespacePrefix =
            Java2WSDLConstants.TARGETNAMESPACE_PREFIX;

    // to store the target namespace for the schema
    private String schematargetNamespace;// = Java2WSDLConstants.AXIS2_XSD;
    private String schematargetNamespacePrefix =
            Java2WSDLConstants.SCHEMA_NAMESPACE_PRFIX;

    private boolean enableAllTransport = true;
    private String [] exposeTransports;

    /**
     * Keeps track whether the schema locations are adjusted
     */
    private boolean schemaLocationsAdjusted = false;

    /**
     * A table that keeps a mapping of unique xsd names (Strings)
     * against the schema objects. This is populated in the first
     * instance the schemas are asked for and then used to serve
     * the subsequent requests
     */
    private Hashtable schemaMappingTable = null;

    /**
     * counter variable for naming the schemas
     */
    private int count = 0;
    /**
     * A custom schema Name prefix. if set this will be used to
     * modify the schema names
     */
    private String customSchemaNamePrefix = null;

    /**
     * A custom schema name suffix. will be attached to the
     * schema file name when the files are uniquely named.
     * A good place to add a file extension if needed
     */
    private String customSchemaNameSuffix = null;
    /////////////////////////////////////////
    // WSDL related stuff ////////////////////
    ////////////////////////////////////////
    private Map nameSpacesMap;

    private String soapNsUri;
    private String endpoint;


    public boolean isSchemaLocationsAdjusted() {
        return schemaLocationsAdjusted;
    }

    public void setSchemaLocationsAdjusted(boolean schemaLocationsAdjusted) {
        this.schemaLocationsAdjusted = schemaLocationsAdjusted;
    }

    public Hashtable getSchemaMappingTable() {
        return schemaMappingTable;
    }

    public void setSchemaMappingTable(Hashtable schemaMappingTable) {
        this.schemaMappingTable = schemaMappingTable;
    }

    public String getCustomSchemaNamePrefix() {
        return customSchemaNamePrefix;
    }

    public void setCustomSchemaNamePrefix(String customSchemaNamePrefix) {
        this.customSchemaNamePrefix = customSchemaNamePrefix;
    }

    public String getCustomSchemaNameSuffix() {
        return customSchemaNameSuffix;
    }

    public void setCustomSchemaNameSuffix(String customSchemaNameSuffix) {
        this.customSchemaNameSuffix = customSchemaNameSuffix;
    }

    /**
     * Constructor AxisService.
     */
    public AxisService() {
        super();
        this.operationsAliasesMap = new HashMap();
        moduleConfigmap = new HashMap();
        //by dafault service scope is for the request
        scope = Constants.SCOPE_REQUEST;
        messageReceivers = new HashMap();
        moduleRefs = new ArrayList();
        engagedModules = new ArrayList();
        schemaList = new ArrayList();
        serviceClassLoader = Thread.currentThread().getContextClassLoader();
    }

    /**
     * get the SOAPVersion
     */
    public String getSoapNsUri() {
        return soapNsUri;
    }

    public void setSoapNsUri(String soapNsUri) {
        this.soapNsUri = soapNsUri;
    }

    /**
     * get the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Constructor AxisService.
     */
    public AxisService(String name) {
        this();
        this.name = name;
    }

    public void addMessageReceiver(String mepURL, MessageReceiver messageReceiver) {
        messageReceivers.put(mepURL, messageReceiver);
    }

    public MessageReceiver getMessageReceiver(String mepURL) {
        return (MessageReceiver) messageReceivers.get(mepURL);
    }

    /**
     * Adds module configuration , if there is moduleConfig tag in service.
     *
     * @param moduleConfiguration
     */
    public void addModuleConfig(ModuleConfiguration moduleConfiguration) {
        moduleConfigmap.put(moduleConfiguration.getModuleName(), moduleConfiguration);
    }

    /**
     * Adds an operation to a service if a module is required to do so.
     *
     * @param module
     */
    public void addModuleOperations(AxisModule module, AxisConfiguration axisConfig)
            throws AxisFault {
        HashMap map = module.getOperations();
        Collection col = map.values();
        for (Iterator iterator = col.iterator(); iterator.hasNext();) {
            AxisOperation axisOperation = copyOperation((AxisOperation) iterator.next());
            if (this.getOperation(axisOperation.getName()) == null) {
                ArrayList wsamappings = axisOperation.getWsamappingList();
                if (wsamappings != null) {
                    for (int j = 0; j < wsamappings.size(); j++) {
                        String mapping = (String) wsamappings.get(j);
                        this.mapActionToOperation(mapping, axisOperation);
                    }
                }
                // this opration is a control operation.
                axisOperation.setControlOperation(true);
                this.addOperation(axisOperation);
            }
        }
    }

    public void addModuleref(QName moduleref) {
        moduleRefs.add(moduleref);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.axis2.description.AxisService#addOperation(org.apache.axis2.description.AxisOperation)
     */

    /**
     * Method addOperation.
     *
     * @param axisOperation
     */
    public void addOperation(AxisOperation axisOperation) {
        axisOperation.setParent(this);

        Iterator modules = getEngagedModules().iterator();

        while (modules.hasNext()) {
            AxisModule module = (AxisModule) modules.next();
            AxisServiceGroup parent = (AxisServiceGroup) getParent();
            AxisConfiguration axisConfig = null;

            if (parent != null) {
                axisConfig = (AxisConfiguration) parent.getParent();
            }

            try {
                Module moduleImpl = module.getModule();
                if (moduleImpl != null) {
                    // notyfying module for service engagement
                    moduleImpl.engageNotify(axisOperation);
                }
                axisOperation.engageModule(module, axisConfig);
            } catch (AxisFault axisFault) {
                log.info(Messages.getMessage(
                        "modulealredyengagetoservice", module.getName().getLocalPart()));
            }
        }
        if (axisOperation.getMessageReceiver() == null) {
            axisOperation.setMessageReceiver(
                    loadDefaultMessageReceiver(axisOperation.getMessageExchangePattern(), this));
        }
        if (axisOperation.getSoapAction() == null) {
            axisOperation.setSoapAction("urn:" + axisOperation.getName().getLocalPart());
        }
        addChild(axisOperation);
        operationsAliasesMap.put(axisOperation.getName().getLocalPart(), axisOperation);
        operationsAliasesMap.put(axisOperation.getSoapAction(), axisOperation);
    }


    private MessageReceiver loadDefaultMessageReceiver(String mepURL, AxisService service) {
        MessageReceiver messageReceiver;
        if (mepURL == null) {
            mepURL = WSDLConstants.MEP_URI_IN_OUT;
        }
        if (service != null) {
            messageReceiver = service.getMessageReceiver(mepURL);
            if (messageReceiver != null)
                return messageReceiver;
        }
        if (getParent() != null && getParent().getParent() != null) {
            return ((AxisConfiguration) getParent().getParent()).getMessageReceiver(mepURL);
        }
        return null;
    }


    /**
     * Gets a copy from module operation.
     *
     * @param axisOperation
     * @return Returns AxisOperation.
     * @throws AxisFault
     */
    private AxisOperation copyOperation(AxisOperation axisOperation) throws AxisFault {
        AxisOperation operation =
                AxisOperationFactory.getOperationDescription(axisOperation.getMessageExchangePattern());

        operation.setMessageReceiver(axisOperation.getMessageReceiver());
        operation.setName(axisOperation.getName());

        Iterator parameters = axisOperation.getParameters().iterator();

        while (parameters.hasNext()) {
            Parameter parameter = (Parameter) parameters.next();

            operation.addParameter(parameter);
        }

        operation.setWsamappingList(axisOperation.getWsamappingList());
        operation.setRemainingPhasesInFlow(axisOperation.getRemainingPhasesInFlow());
        operation.setPhasesInFaultFlow(axisOperation.getPhasesInFaultFlow());
        operation.setPhasesOutFaultFlow(axisOperation.getPhasesOutFaultFlow());
        operation.setPhasesOutFlow(axisOperation.getPhasesOutFlow());

        return operation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.axis2.description.AxisService#addToengagedModules(javax.xml.namespace.QName)
     */

    /**
     * Engages a module. It is required to use this method.
     *
     * @param axisModule
     */
    public void engageModule(AxisModule axisModule, AxisConfiguration axisConfig)
            throws AxisFault {
        if (axisModule == null) {
            throw new AxisFault(Messages.getMessage("modulenf"));
        }
        Iterator itr_engageModules = engagedModules.iterator();

        while (itr_engageModules.hasNext()) {
            AxisModule module = (AxisModule) itr_engageModules.next();

            if (module.getName().equals(axisModule.getName())) {
                log.debug(Messages.getMessage("modulealredyengagetoservice",
                        axisModule.getName().getLocalPart()));
                throw new AxisFault(Messages.getMessage("modulealredyengagetoservice",
                        axisModule.getName().getLocalPart()));
            }
        }

        Module moduleImpl = axisModule.getModule();
        if (moduleImpl != null) {
            // notyfying module for service engagement
            moduleImpl.engageNotify(this);
        }
        // adding module operations
        addModuleOperations(axisModule, axisConfig);

        Iterator operations = getOperations();

        while (operations.hasNext()) {
            AxisOperation axisOperation = (AxisOperation) operations.next();
            if (moduleImpl != null) {
                // notyfying module for service engagement
                moduleImpl.engageNotify(axisOperation);
            }
            axisOperation.engageModule(axisModule, axisConfig);
        }
        engagedModules.add(axisModule);
    }

    /**
     * Maps an action (aka WSA action) to the given operation. This is used by
     * addressing based dispatching to figure out which operation it is that a
     * given message is for.
     *
     * @param action        the action key
     * @param axisOperation the operation to map to
     */
    public void mapActionToOperation(String action, AxisOperation axisOperation) {
        operationsAliasesMap.put(action, axisOperation);
    }


    public void printSchema(OutputStream out) throws AxisFault {
        for (int i = 0; i < schemaList.size(); i++) {
            XmlSchema schema = addNameSpaces(i);
            schema.write(out);
        }
    }

    public XmlSchema getSchema(int index) {
        return addNameSpaces(index);
    }

    private XmlSchema addNameSpaces(int i) {
        XmlSchema schema = (XmlSchema) schemaList.get(i);
        Iterator keys = nameSpacesMap.keySet().iterator();
        Hashtable prefixTable = schema.getPrefixToNamespaceMap();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (prefixTable.get(key) == null) {
                prefixTable.put(key, nameSpacesMap.get(key));
            }
        }
        return schema;
    }

    public void printPolicy(OutputStream out) throws AxisFault {
        PolicyUtil.writePolicy(getPolicyInclude(), out);
    }

    public void printPolicy(OutputStream out, String operationName) throws AxisFault {
        AxisOperation axisOperation = getOperation(new QName(operationName));
        if (axisOperation == null) {
            throw new AxisFault(Messages.getMessage("invalidoperation",
                    operationName));
        }
        PolicyUtil.writePolicy(axisOperation.getPolicyInclude(), out);
    }

    public AxisConfiguration getAxisConfiguration() {
        if (getParent() != null) return (AxisConfiguration) getParent().getParent();
        return null;
    }

    public void printWSDL(OutputStream out, String requestIP) throws AxisFault {
        ArrayList eprList = new ArrayList();
        AxisConfiguration axisConfig = getAxisConfiguration();
        if (enableAllTransport) {
            Iterator transports = axisConfig.getTransportsIn().values().iterator();
            while (transports.hasNext()) {
                TransportInDescription transportIn = (TransportInDescription) transports.next();
                TransportListener listener = transportIn.getReceiver();
                if (listener != null) {
                    try {
                        if (listener.getEPRForService(getName(), requestIP) != null) {
                            String address = listener.getEPRForService(getName(), requestIP).getAddress();
                            if (address != null) {
                                eprList.add(address);
                            }
                        }
                    } catch (AxisFault axisFault) {
                        log.info(axisFault.getMessage());
                    }
                }
            }
        } else {
            String trs [] = getExposeTransports();
            for (int i = 0; i < trs.length; i++) {
                String trsName = trs[i];
                TransportInDescription transportIn = axisConfig.getTransportIn(
                        new QName(trsName));
                if (transportIn != null) {
                    TransportListener listener = transportIn.getReceiver();
                    if (listener != null) {
                        try {
                            if (listener.getEPRForService(getName(), requestIP)
                                    != null) {
                                String address = listener.getEPRForService(
                                        getName(), requestIP).getAddress();
                                if (address != null) {
                                    eprList.add(address);
                                }
                            }
                        } catch (AxisFault axisFault) {
                            log.info(axisFault.getMessage());
                        }
                    }
                }
            }
        }
        String eprArray [] = (String[]) eprList.toArray(new String[eprList.size()]);
        getWSDL(out, eprArray);
    }

    /**
     * Print the WSDL with a default URL
     *
     * @param out
     * @throws AxisFault
     */
    public void printWSDL(OutputStream out) throws AxisFault {
        setWsdlfound(true);
        //pick the endpoint and take it as the epr for the WSDL
        getWSDL(out, new String[]{getEndpoint()});
    }

    private void getWSDL(OutputStream out, String [] serviceURL) throws AxisFault {
        if (isWsdlfound()) {
            AxisService2OM axisService2WOM = new AxisService2OM(this,
                    serviceURL, "document", "literal");
            try {
                OMElement wsdlElement = axisService2WOM.generateOM();
                wsdlElement.serialize(out);
                out.flush();
                out.close();
            } catch (Exception e) {
                throw new AxisFault(e);
            }
        } else {
            try {
                String wsdlntfound = "<error>" +
                        "<description>Unable to generate WSDL for this service</description>" +
                        "<reason>Either user has not dropped the wsdl into META-INF or" +
                        " operations use message receivers other than RPC.</reason>" +
                        "</error>";
                out.write(wsdlntfound.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new AxisFault(e);
            }
        }

    }

    /**
     * Gets the description about the service which is specified in services.xml.
     *
     * @return Returns String.
     */
    public String getServiceDescription() {
        return serviceDescription;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.axis2.description.AxisService#getClassLoader()
     */

    /**
     * Method getClassLoader.
     *
     * @return Returns ClassLoader.
     */
    public ClassLoader getClassLoader() {
        return this.serviceClassLoader;
    }

    /**
     * Gets the control operation which are added by module like RM.
     */
    public ArrayList getControlOperations() {
        Iterator op_itr = getOperations();
        ArrayList operationList = new ArrayList();

        while (op_itr.hasNext()) {
            AxisOperation operation = (AxisOperation) op_itr.next();

            if (operation.isControlOperation()) {
                operationList.add(operation);
            }
        }

        return operationList;
    }

    /**
     * Method getEngagedModules.
     *
     * @return Returns Collection.
     */
    public Collection getEngagedModules() {
        return engagedModules;
    }

    public URL getFileName() {
        return fileName;
    }

    public long getLastupdate() {
        return lastupdate;
    }

    public ModuleConfiguration getModuleConfig(QName moduleName) {
        return (ModuleConfiguration) moduleConfigmap.get(moduleName);
    }

    public ArrayList getModules() {
        return moduleRefs;
    }

    public String getName() {
        return name;
    }

    /**
     * Method getOperation.
     *
     * @param operationName
     * @return Returns AxisOperation.
     */
    public AxisOperation getOperation(QName operationName) {
//        AxisOperation axisOperation = (AxisOperation) operations.get(operationName);
        AxisOperation axisOperation = (AxisOperation) getChild(operationName);

        if (axisOperation == null) {
            axisOperation = (AxisOperation) operationsAliasesMap.get(
                    operationName.getLocalPart());
        }

        return axisOperation;
    }

    /**
     * Returns the AxisOperation which has been mapped to the given action.
     *
     * @param action the action key
     * @return Returns the corresponding AxisOperation or null if it isn't found.
     */
    public AxisOperation getOperationByAction(String action) {
        return (AxisOperation) operationsAliasesMap.get(action);
    }

    /**
     * Returns the operation given a SOAP Action. This
     * method should be called if only one Endpoint is defined for
     * this Service. If more than one Endpoint exists, one of them will be
     * picked. If more than one Operation is found with the given SOAP Action;
     * null will be returned. If no particular Operation is found with the given
     * SOAP Action; null will be returned.
     *
     * @param soapAction SOAP Action defined for the particular Operation
     * @return Returns an AxisOperation if a unique Operation can be found with the given
     *         SOAP Action otherwise will return null.
     */
    public AxisOperation getOperationBySOAPAction(String soapAction) {
        if ((soapAction == null) || soapAction.equals("")) {
            return null;
        }

//        AxisOperation operation = (AxisOperation) operations.get(new QName(soapAction));
        AxisOperation operation = (AxisOperation) getChild(new QName(soapAction));

        if (operation != null) {
            return operation;
        }

        operation = (AxisOperation) operationsAliasesMap.get(soapAction);

        return operation;
    }

    /**
     * Method getOperations.
     *
     * @return Returns HashMap
     */
    public Iterator getOperations() {
        return getChildren();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.axis2.description.ParameterInclude#getParameter(java.lang.String)
     */

    /**
     * Gets only the published operations.
     */
    public ArrayList getPublishedOperations() {
        Iterator op_itr = getOperations();
        ArrayList operationList = new ArrayList();

        while (op_itr.hasNext()) {
            AxisOperation operation = (AxisOperation) op_itr.next();

            if (!operation.isControlOperation()) {
                operationList.add(operation);
            }
        }

        return operationList;
    }

    /**
     * Sets the description about the service whish is specified in services.xml
     *
     * @param serviceDescription
     */
    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.axis2.description.AxisService#setClassLoader(java.lang.ClassLoader)
     */

    /**
     * Method setClassLoader.
     *
     * @param classLoader
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.serviceClassLoader = classLoader;
    }

    public void setFileName(URL fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets the current time as last update time of the service.
     */
    public void setLastupdate() {
        lastupdate = new Date().getTime();
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getSchema() {
        return schemaList;
    }

    public void addSchema(XmlSchema schema) {
        schemaList.add(schema);
        addSchemaNameSpace(schema.getTargetNamespace());
    }

    public void addSchema(Collection schemas) {
        Iterator iterator = schemas.iterator();
        while (iterator.hasNext()) {
            XmlSchema schema = (XmlSchema) iterator.next();
            schemaList.add(schema);
            addSchemaNameSpace(schema.getTargetNamespace());
        }
    }

    public boolean isWsdlfound() {
        return wsdlfound;
    }

    public void setWsdlfound(boolean wsdlfound) {
        this.wsdlfound = wsdlfound;
    }

    public String getScope() {
        return scope;
    }

    /**
     * @param scope - Available scopes :
     *              Constants.SCOPE_APPLICATION
     *              Constants.SCOPE_TRANSPORT_SESSION
     *              Constants.SCOPE_SOAP_SESSION
     *              Constants.SCOPE_REQUEST.equals
     */
    public void setScope(String scope) {
        if (Constants.SCOPE_APPLICATION.equals(scope) ||
                Constants.SCOPE_TRANSPORT_SESSION.equals(scope) ||
                Constants.SCOPE_SOAP_SESSION.equals(scope) ||
                Constants.SCOPE_REQUEST.equals(scope)) {
            this.scope = scope;
        }
    }

    public boolean isUseDefaultChains() {
        return useDefaultChains;
    }

    public void setUseDefaultChains(boolean useDefaultChains) {
        this.useDefaultChains = useDefaultChains;
    }

    public Object getKey() {
        return getName();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSchematargetNamespace() {
        return schematargetNamespace;
    }

    public void setSchematargetNamespace(String schematargetNamespace) {
        this.schematargetNamespace = schematargetNamespace;
    }

    public String getSchematargetNamespacePrefix() {
        return schematargetNamespacePrefix;
    }

    public void setSchematargetNamespacePrefix(String schematargetNamespacePrefix) {
        this.schematargetNamespacePrefix = schematargetNamespacePrefix;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public String getTargetNamespacePrefix() {
        return targetNamespacePrefix;
    }

    public void setTargetNamespacePrefix(String targetNamespacePrefix) {
        this.targetNamespacePrefix = targetNamespacePrefix;
    }

    public XmlSchemaElement getSchemaElement(QName elementQName) {
        XmlSchemaElement element;
        for (int i = 0; i < schemaList.size(); i++) {
            XmlSchema schema = (XmlSchema) schemaList.get(i);
            if (schema != null) {
                element = schema.getElementByName(elementQName);
                if (element != null) {
                    return element;
                }
            }
        }
        return null;
    }

    public boolean isEnableAllTransport() {
        return enableAllTransport;
    }

    public String[] getExposeTransports() {
        return exposeTransports;
    }

    public void setExposeTransports(String[] exposeTransports) {
        if (exposeTransports.length > 0) {
            enableAllTransport = false;
            this.exposeTransports = exposeTransports;
        }
    }

    public void disEngageModule(AxisModule module) {
        AxisConfiguration axisConfig = getAxisConfiguration();
        if (axisConfig != null) {
            PhaseResolver phaseResolver = new PhaseResolver(axisConfig);
            if (axisConfig.isEngaged(module.getName())) {
                removeModuleOperations(module);
                Iterator operations = getChildren();
                while (operations.hasNext()) {
                    AxisOperation axisOperation = (AxisOperation) operations.next();
                    phaseResolver.disEngageModulefromOperationChian(module, axisOperation);
                    axisOperation.removeFromEngageModueList(module);
                }
            } else {
                if (isEngaged(module.getName())) {
                    phaseResolver.disEngageModulefromGlobalChains(module);
                    removeModuleOperations(module);
                    Iterator operations = getChildren();
                    while (operations.hasNext()) {
                        AxisOperation axisOperation = (AxisOperation) operations.next();
                        phaseResolver.disEngageModulefromOperationChian(module, axisOperation);
                        axisOperation.removeFromEngageModueList(module);
                    }
                }
            }
        }
        engagedModules.remove(module);
    }

    /**
     * To remove module operations added at the time of engagement
     */
    private void removeModuleOperations(AxisModule module) {
        HashMap moduleOerations = module.getOperations();
        if (moduleOerations != null) {
            Iterator moduleOperations_itr = moduleOerations.values().iterator();
            while (moduleOperations_itr.hasNext()) {
                AxisOperation operation = (AxisOperation) moduleOperations_itr.next();
                removeOperation(operation.getName());
            }
        }
    }

    public boolean isEngaged(QName moduleName) {
        AxisModule module = getAxisConfiguration().getModule(moduleName);
        if (module == null) {
            return false;
        }
        Iterator engagedModuleItr = engagedModules.iterator();
        while (engagedModuleItr.hasNext()) {
            AxisModule axisModule = (AxisModule) engagedModuleItr.next();
            if (axisModule.getName().getLocalPart().equals(module.getName().getLocalPart())) {
                return true;
            }
        }
        return false;
    }

    //#######################################################################################
    //                    APIs to create AxisService

    //

    /**
     * To create a AxisService for a given WSDL and the created client is most suitable for clinet side
     * invocation not for server side invocation. Since all the soap acction and wsa action is added to
     * operations
     *
     * @param wsdlURL         location of the WSDL
     * @param wsdlServiceName name of the service to be invoke , if it is null then the first one will
     *                        be selected if there are more than one
     * @param portName        name of the port , if there are more than one , if it is null then the
     *                        first one in the  iterator will be selected
     * @param options         Service client options, to set the target EPR
     * @return AxisService , the created servie will be return
     */
    public static AxisService createClientSideAxisService(URL wsdlURL,
                                                          QName wsdlServiceName,
                                                          String portName,
                                                          Options options) throws AxisFault {
        try {
            InputStream in = wsdlURL.openConnection().getInputStream();
            Document doc = XMLUtils.newDocument(in);
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            reader.setFeature("javax.wsdl.importDocuments", true);
            Definition wsdlDefinition = reader.readWSDL(null, doc);
            return createClientSideAxisService(wsdlDefinition, wsdlServiceName, portName, options);
        } catch (IOException e) {
            throw new AxisFault("IOException" + e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new AxisFault("ParserConfigurationException" + e.getMessage());
        } catch (SAXException e) {
            throw new AxisFault("SAXException" + e.getMessage());
        } catch (WSDLException e) {
            throw new AxisFault("WSDLException" + e.getMessage());
        }
    }

    public static AxisService createClientSideAxisService(Definition wsdlDefinition,
                                                          QName wsdlServiceName,
                                                          String portName,
                                                          Options options) throws AxisFault {
        WSDL2AxisServiceBuilder serviceBuilder =
                new WSDL2AxisServiceBuilder(wsdlDefinition, wsdlServiceName, portName);
        serviceBuilder.setServerSide(false);
        AxisService axisService = serviceBuilder.populateService();
        options.setTo(new EndpointReference(axisService.getEndpoint()));
        options.setSoapVersionURI(axisService.getSoapNsUri());
        return axisService;
    }

    /**
     * To create an AxisService using given service impl class name
     * fisrt generate schema corresponding to the given java class , next for each methods AxisOperation
     * will be created.
     * <p/>
     * Note : Inorder to work this properly RPCMessageReceiver should be availble in the class path
     * otherewise operation can not continue
     *
     * @param implClass
     * @param axisConfig
     * @return return created AxisSrevice
     */
    public static AxisService createService(String implClass,
                                            AxisConfiguration axisConfig,
                                            Class messageReceiverClass) throws AxisFault {
        Parameter parameter = new Parameter(Constants.SERVICE_CLASS, implClass);
        OMElement paraElement = Utils.getParameter(Constants.SERVICE_CLASS, implClass, false);
        parameter.setParameterElement(paraElement);
        AxisService axisService = new AxisService();
        axisService.setUseDefaultChains(false);
        axisService.addParameter(parameter);

        int index = implClass.lastIndexOf(".");
        String serviceName;
        if (index > 0) {
            serviceName = implClass.substring(index + 1, implClass.length());
        } else {
            serviceName = implClass;
        }

        axisService.setName(serviceName);
        axisService.setClassLoader(axisConfig.getServiceClassLoader());

        ClassLoader serviceClassLoader = axisService.getClassLoader();
        SchemaGenerator schemaGenerator;
        try {
            schemaGenerator = new SchemaGenerator(serviceClassLoader,
                    implClass, axisService.getSchematargetNamespace(),
                    axisService.getSchematargetNamespacePrefix());
            ArrayList excludeOpeartion = new ArrayList();
            excludeOpeartion.add("init");
            excludeOpeartion.add("setOperationContext");
            excludeOpeartion.add("destroy");
            schemaGenerator.setExcludeMethods(excludeOpeartion);
            axisService.addSchema(schemaGenerator.generateSchema());
        } catch (Exception e) {
            throw new AxisFault(e);
        }

        JMethod [] method = schemaGenerator.getMethods();
        TypeTable table = schemaGenerator.getTypeTable();

        PhasesInfo pinfo = axisConfig.getPhasesInfo();

        for (int i = 0; i < method.length; i++) {
            JMethod jmethod = method[i];
            if (!jmethod.isPublic()) {
                // no need to expose , private and protected methods
                continue;
            } else if ("init".equals(jmethod.getSimpleName())) {
                continue;
            }
            AxisOperation operation = Utils.getAxisOperationforJmethod(jmethod, table);

            // loading message receivers
            try {
                MessageReceiver messageReceiver = (MessageReceiver) messageReceiverClass.newInstance();
                operation.setMessageReceiver(messageReceiver);
            } catch (IllegalAccessException e) {
                throw new AxisFault("IllegalAccessException occured during message receiver loading"
                        + e.getMessage());
            } catch (InstantiationException e) {
                throw new AxisFault("InstantiationException occured during message receiver loading"
                        + e.getMessage());
            }
            pinfo.setOperationPhases(operation);
            axisService.addOperation(operation);
        }
        return axisService;

    }

    /**
     * To create a service for a given java class with user defined schema and target
     * namespaces
     *
     * @param implClass            : full name of the class
     * @param axisConfig           : currcent AxisConfgiuration
     * @param messageReceiverClass : Messge reciver that you want to use
     * @param targetNameSpace      : Service namespace
     * @param scheamNameSpace      : Scheam Name space
     * @return
     * @throws AxisFault
     */

    public static AxisService createService(String implClass,
                                            AxisConfiguration axisConfig,
                                            Class messageReceiverClass,
                                            String targetNameSpace,
                                            String scheamNameSpace) throws AxisFault {
        Parameter parameter = new Parameter(Constants.SERVICE_CLASS, implClass);
        OMElement paraElement = Utils.getParameter(Constants.SERVICE_CLASS, implClass, false);
        parameter.setParameterElement(paraElement);
        AxisService axisService = new AxisService();
        axisService.setUseDefaultChains(false);
        axisService.addParameter(parameter);

        int index = implClass.lastIndexOf(".");
        String serviceName;
        if (index > 0) {
            serviceName = implClass.substring(index + 1, implClass.length());
        } else {
            serviceName = implClass;
        }

        axisService.setName(serviceName);
        axisService.setClassLoader(axisConfig.getServiceClassLoader());

        ClassLoader serviceClassLoader = axisService.getClassLoader();
        SchemaGenerator schemaGenerator;
        try {
            schemaGenerator = new SchemaGenerator(serviceClassLoader,
                    implClass, scheamNameSpace,
                    axisService.getSchematargetNamespacePrefix());
            ArrayList excludeOpeartion = new ArrayList();
            excludeOpeartion.add("init");
            excludeOpeartion.add("setOperationContext");
            excludeOpeartion.add("destroy");
            schemaGenerator.setExcludeMethods(excludeOpeartion);
            axisService.addSchema(schemaGenerator.generateSchema());
            if (targetNameSpace != null && !"".equals(targetNameSpace)) {
                axisService.setTargetNamespace(targetNameSpace);
            }
        } catch (Exception e) {
            throw new AxisFault(e);
        }

        JMethod [] method = schemaGenerator.getMethods();
        TypeTable table = schemaGenerator.getTypeTable();

        PhasesInfo pinfo = axisConfig.getPhasesInfo();

        for (int i = 0; i < method.length; i++) {
            JMethod jmethod = method[i];
            if (!jmethod.isPublic()) {
                // no need to expose , private and protected methods
                continue;
            } else if ("init".equals(jmethod.getSimpleName())) {
                continue;
            }
            AxisOperation operation = Utils.getAxisOperationforJmethod(jmethod, table);

            // loading message receivers
            try {
                MessageReceiver messageReceiver = (MessageReceiver) messageReceiverClass.newInstance();
                operation.setMessageReceiver(messageReceiver);
            } catch (IllegalAccessException e) {
                throw new AxisFault("IllegalAccessException occured during message receiver loading"
                        + e.getMessage());
            } catch (InstantiationException e) {
                throw new AxisFault("InstantiationException occured during message receiver loading"
                        + e.getMessage());
            }
            pinfo.setOperationPhases(operation);
            axisService.addOperation(operation);
        }
        return axisService;

    }

    public static AxisService createService(String implClass,
                                            AxisConfiguration axisConfig) throws AxisFault {
        Class clazz;
        try {
            clazz = Class.forName("org.apache.axis2.rpc.receivers.RPCMessageReceiver");
        } catch (ClassNotFoundException e) {
            throw new AxisFault("ClassNotFoundException occured during message receiver loading"
                    + e.getMessage());
        }

        return createService(implClass, axisConfig, clazz);

    }

    public void removeOperation(QName opName) {
        AxisOperation operation = getOperation(opName);
        if (operation != null) {
            removeChild(opName);
            ArrayList mappingList = operation.getWsamappingList();
            if (mappingList != null) {
                for (int i = 0; i < mappingList.size(); i++) {
                    String actionMapping = (String) mappingList.get(i);
                    operationsAliasesMap.remove(actionMapping);
                }
            }
            operationsAliasesMap.remove(operation.getName().getLocalPart());
        }
    }

    public Map getNameSpacesMap() {
        return nameSpacesMap;
    }

    public void setNameSpacesMap(Map nameSpacesMap) {
        this.nameSpacesMap = nameSpacesMap;
    }

    private void addSchemaNameSpace(String targetNameSpace) {
        boolean found = false;
        if (nameSpacesMap != null && nameSpacesMap.size() > 0) {
            Iterator itr = nameSpacesMap.values().iterator();
            while (itr.hasNext()) {
                String value = (String) itr.next();
                if (value.equals(targetNameSpace)) {
                    found = true;
                }
            }
        }
        if (nameSpacesMap == null) {
            nameSpacesMap = new HashMap();
        }
        if (!found) {
            nameSpacesMap.put("ns" + nsCount, targetNameSpace);
            nsCount ++;
        }
    }

    /**
     * runs the schema mappings if it has not been run previously
     * it is best that this logic be in the axis service since one can
     * call the axis service to populate the schema mappings
     */
    public void populateSchemaMappings() {

        //populate the axis service with the necessary schema references
        ArrayList schema = getSchema();
        if (!isSchemaLocationsAdjusted()) {
            Hashtable nameTable = new Hashtable();
            //calculate unique names for the schemas
            calcualteSchemaNames(schema, nameTable);
            //adjust the schema locations as per the calculated names
            adjustSchemaNames(schema, nameTable);
            //reverse the nametable so that there is a mapping from the
            //name to the schemaObject
            setSchemaMappingTable(swapMappingTable(nameTable));
            setSchemaLocationsAdjusted(true);
        }
    }

    /**
     * run 1 -calcualte unique names
     *
     * @param schemas
     */
    private void calcualteSchemaNames(List schemas, Hashtable nameTable) {
        //first traversal - fill the hashtable
        for (int i = 0; i < schemas.size(); i++) {
            XmlSchema schema = (XmlSchema) schemas.get(i);

            XmlSchemaObjectCollection includes = schema.getIncludes();
            for (int j = 0; j < includes.getCount(); j++) {
                Object item = includes.getItem(j);
                XmlSchema s;
                if (item instanceof XmlSchemaExternal) {
                    //recursively call the calculating
                    XmlSchemaExternal externalSchema = (XmlSchemaExternal) item;
                    s = externalSchema.getSchema();
                    if (s != null) {
                        calcualteSchemaNames(Arrays.asList(
                                new XmlSchema[]{s}),
                                nameTable);
                        nameTable.put(s,
                                ("xsd" + count++)
                                        + (customSchemaNameSuffix != null ?
                                        customSchemaNameSuffix :
                                        ""));
                    }

                }
            }
        }
    }

    /**
     * Run 2  - adjust the names
     *
     * @param schemas
     */
    private void adjustSchemaNames(List schemas, Hashtable nameTable) {
        //first traversal - fill the hashtable
        for (int i = 0; i < schemas.size(); i++) {
            XmlSchema schema = (XmlSchema) schemas.get(i);

            XmlSchemaObjectCollection includes = schema.getIncludes();
            for (int j = 0; j < includes.getCount(); j++) {
                Object item = includes.getItem(j);
                if (item instanceof XmlSchemaExternal) {
                    //recursively call the name adjusting
                    XmlSchemaExternal xmlSchemaExternal = (XmlSchemaExternal) item;
                    XmlSchema s = xmlSchemaExternal.getSchema();
                    if (s != null) {
                        adjustSchemaNames(Arrays.asList(
                                new XmlSchema[]{s}), nameTable);
                        xmlSchemaExternal.setSchemaLocation(
                                customSchemaNamePrefix == null ?
                                        //use the default mode
                                        (getName() +
                                                "?xsd=" +
                                                nameTable.get(s)) :
                                        //custom prefix is present - add the custom prefix
                                        (customSchemaNamePrefix +
                                                nameTable.get(s)));
                    }
                }
            }

        }
    }

    /**
     * Swap the key,value pairs
     *
     * @param originalTable
     */
    private Hashtable swapMappingTable(Hashtable originalTable) {
        Hashtable swappedTable = new Hashtable(originalTable.size());
        Iterator keys = originalTable.keySet().iterator();
        Object key;
        while (keys.hasNext()) {
            key = keys.next();
            swappedTable.put(originalTable.get(key), key);
        }

        return swappedTable;
    }

}
