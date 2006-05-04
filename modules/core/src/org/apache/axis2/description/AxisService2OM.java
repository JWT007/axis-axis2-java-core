package org.apache.axis2.description;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axis2.wsdl.SOAPHeaderMessage;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaRedefine;
import org.apache.ws.java2wsdl.Java2WSDLConstants;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PolicyConstants;
import org.apache.ws.policy.PolicyReference;
import org.apache.ws.policy.util.PolicyFactory;
import org.apache.ws.policy.util.PolicyRegistry;
import org.apache.ws.policy.util.StAXPolicyWriter;

import java.util.List;
import java.util.Arrays;
import java.util.Hashtable;
/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 *  
 */

public class AxisService2OM implements Java2WSDLConstants {

    private AxisService axisService;

    private String[] url;

    private String targetNamespace;
    
    private OMElement definition;

    private OMNamespace soap;

    private OMNamespace soap12;

    private OMNamespace http;

    private OMNamespace mime;

    private OMNamespace tns;

    private OMNamespace wsdl;

    private String style;

    private String use;

    private boolean generateHttp = false;

    public AxisService2OM(AxisService service, String[] serviceURL,
            String style, String use) {
        this.axisService = service;
        url = serviceURL;
        if (style == null) {
            this.style = DOCUMNT;
        } else {
            this.style = style;
        }
        if (use == null) {
            this.use = LITERAL;
        } else {
            this.use = use;
        }
        this.targetNamespace = service.getTargetNamespace();
    }

    public OMElement generateOM() throws Exception {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        wsdl = fac.createOMNamespace(WSDL_NAMESPACE,
                DEFAULT_WSDL_NAMESPACE_PREFIX);
        OMElement ele = fac.createOMElement("definitions", wsdl);
        setDefinitionElement(ele);
        
        Map nameSpaceMap = axisService.getNameSpacesMap();
        Iterator keys = nameSpaceMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if ("".equals(key)) {
                ele.declareDefaultNamespace((String) nameSpaceMap.get(key));
            } else {
                ele.declareNamespace((String) nameSpaceMap.get(key), key);
            }
        }
        soap = ele.declareNamespace(URI_WSDL11_SOAP, SOAP11_PREFIX);
        soap12 = ele.declareNamespace(URI_WSDL12_SOAP, SOAP12_PREFIX);
        http = ele.declareNamespace(HTTP_NAMESPACE, HTTP_PREFIX);
        mime = ele.declareNamespace(MIME_NAMESPACE, MIME_PREFIX);
        String prefix = getPrefix(axisService.getTargetNamespace());
        if (prefix == null || "".equals(prefix)) {
            prefix = DEFAULT_TARGET_NAMESPACE_PREFIX;
        }
        axisService.getNameSpacesMap().put(prefix,
                    axisService.getTargetNamespace());
        tns = ele.declareNamespace(axisService.getTargetNamespace(), prefix);

        ele.addAttribute("targetNamespace", axisService.getTargetNamespace(),
                null);
        OMElement wsdlTypes = fac.createOMElement("types", wsdl);
        ele.addChild(wsdlTypes);

        // populate the schema mappings
        axisService.populateSchemaMappings();

        ArrayList schemas = axisService.getSchema();
        for (int i = 0; i < schemas.size(); i++) {
            StringWriter writer = new StringWriter();
//            XmlSchema schema = (XmlSchema) schemas.get(i);
            XmlSchema schema = axisService.getSchema(i);

            schema.write(writer);
            if (!"".equals(writer.toString())) {
                XMLStreamReader xmlReader = StAXUtils
                        .createXMLStreamReader(new ByteArrayInputStream(writer
                                .toString().getBytes()));

                StAXOMBuilder staxOMBuilder = new StAXOMBuilder(fac, xmlReader);
                wsdlTypes.addChild(staxOMBuilder.getDocumentElement());
            }
        }
        

        generateMessages(fac, ele);
        generatePortType(fac, ele);
        generateSOAP11Binding(fac, ele);
        generateSOAP12Binding(fac, ele);
        //generateHttp
        if (axisService.getParent() != null) {
            AxisDescription axisdesc = axisService.getParent().getParent();
            Parameter parameter = axisdesc.getParameter("enableHTTP");
            if (parameter != null) {
                Object value = parameter.getValue();
                if ("true".equals(value.toString())) {
                    generateHttp = true;
                    generatePostBinding(fac, ele);
                }
            }
        }
        generateService(fac, ele);

        return ele;
    }


    private void generateMessages(OMFactory fac,
                                  OMElement defintions) {
        Iterator operations = axisService.getOperations();
        while (operations.hasNext()) {
            AxisOperation axisOperation = (AxisOperation) operations.next();

            String MEP = axisOperation.getMessageExchangePattern();
            if (WSDLConstants.MEP_URI_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage inaxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                if (inaxisMessage != null) {
                    writeMessage(inaxisMessage, fac, defintions);
                    generateHeaderMessages(inaxisMessage, fac, defintions);
                }
            }

            if (WSDLConstants.MEP_URI_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage outAxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
                if (outAxisMessage != null) {
                    writeMessage(outAxisMessage, fac, defintions);
                    generateHeaderMessages(outAxisMessage, fac, defintions);
                }
            }

            // generate fault Messages
            ArrayList faultyMessages = axisOperation.getFaultMessages();
            if (faultyMessages != null) {
                for (int i = 0; i < faultyMessages.size(); i++) {
                    AxisMessage axisMessage = (AxisMessage) faultyMessages
                            .get(i);
                    writeMessage(axisMessage, fac, defintions);
                    generateHeaderMessages(axisMessage, fac, defintions);
                }
            }
        }
    }

    private void generateHeaderMessages(AxisMessage axismessage, OMFactory fac,
            OMElement defintions) {
        ArrayList extList = axismessage.getSoapHeaders();
        for (int i = 0; i < extList.size(); i++) {
            SOAPHeaderMessage header = (SOAPHeaderMessage) extList.get(i);
            OMElement messageElement = fac.createOMElement(MESSAGE_LOCAL_NAME,
                    wsdl);
            messageElement.addAttribute(ATTRIBUTE_NAME, header.getMessage()
                    .getLocalPart(), null);
            defintions.addChild(messageElement);
            OMElement messagePart = fac.createOMElement(PART_ATTRIBUTE_NAME,
                    wsdl);
            messageElement.addChild(messagePart);
            messagePart.addAttribute(ATTRIBUTE_NAME, "part1", null);
            messagePart.addAttribute(ELEMENT_ATTRIBUTE_NAME, getPrefix(header
                    .getElement().getNamespaceURI())
                    + ":" + header.getElement().getLocalPart(), null);
        }
    }

    private void writeMessage(AxisMessage axismessage, OMFactory fac,
            OMElement defintions) {
        QName scheamElementName = axismessage.getElementQName();
        OMElement messageElement = fac
                .createOMElement(MESSAGE_LOCAL_NAME, wsdl);
        messageElement.addAttribute(ATTRIBUTE_NAME, axismessage.getName()
                , null);
        defintions.addChild(messageElement);
        if (scheamElementName != null) {
            OMElement messagePart = fac.createOMElement(PART_ATTRIBUTE_NAME,
                    wsdl);
            messageElement.addChild(messagePart);
            messagePart.addAttribute(ATTRIBUTE_NAME, "part1", null);
            messagePart.addAttribute(ELEMENT_ATTRIBUTE_NAME,
                    getPrefix(scheamElementName.getNamespaceURI()) + ":"
                            + scheamElementName.getLocalPart(), null);
        }

    }

    /**
     * Generate the porttypes
     */
    private void generatePortType(OMFactory fac, OMElement defintions)
            throws Exception {
        OMElement portType = fac.createOMElement(PORT_TYPE_LOCAL_NAME, wsdl);
        defintions.addChild(portType);
        portType.addAttribute(ATTRIBUTE_NAME, axisService.getName()
                + PORT_TYPE_SUFFIX, null);

        Iterator operations = axisService.getOperations();
        while (operations.hasNext()) {
            AxisOperation axisOperation = (AxisOperation) operations.next();
            if (axisOperation.isControlOperation()) {
                continue;
            }
            String operationName = axisOperation.getName().getLocalPart();
            OMElement operation = fac.createOMElement(OPERATION_LOCAL_NAME,
                    wsdl);
            portType.addChild(operation);
            operation.addAttribute(ATTRIBUTE_NAME, operationName, null);
            addPolicy(PolicyInclude.OPERATION_POLICY, axisOperation
                    .getPolicyInclude(), operation, fac);

            String MEP = axisOperation.getMessageExchangePattern();
            if (WSDLConstants.MEP_URI_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage inaxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                if (inaxisMessage != null) {
                    OMElement input = fac.createOMElement(IN_PUT_LOCAL_NAME,
                            wsdl);
                    input.addAttribute(MESSAGE_LOCAL_NAME, tns.getPrefix()
                            + ":" + inaxisMessage.getName() ,
                            null);
                    addPolicy(PolicyInclude.INPUT_POLICY, inaxisMessage
                            .getPolicyInclude(), input, fac);
                    operation.addChild(input);
                }
            }

            if (WSDLConstants.MEP_URI_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage outAxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
                if (outAxisMessage != null) {
                    OMElement output = fac.createOMElement(OUT_PUT_LOCAL_NAME,
                            wsdl);
                    output.addAttribute(MESSAGE_LOCAL_NAME, tns.getPrefix()
                            + ":" + outAxisMessage.getName() ,
                            null);
                    addPolicy(PolicyInclude.OUTPUT_POLICY, outAxisMessage
                            .getPolicyInclude(), output, fac);
                    operation.addChild(output);
                }
            }

            // generate fault Messages
            ArrayList faultyMessages = axisOperation.getFaultMessages();
            if (faultyMessages != null) {
                for (int i = 0; i < faultyMessages.size(); i++) {
                    AxisMessage faultyMessge = (AxisMessage) faultyMessages
                            .get(i);
                    OMElement fault = fac.createOMElement(FAULT_LOCAL_NAME,
                            wsdl);
                    fault.addAttribute(MESSAGE_LOCAL_NAME, tns.getPrefix()
                            + ":" + faultyMessge.getName() ,
                            null);
                    fault.addAttribute(ATTRIBUTE_NAME, faultyMessge.getName(),
                            null);
                    // TODO add policies for fault messages
                    operation.addChild(fault);
                }
            }

        }
    }

    /**
     * Generate the service
     */
    public void generateService(OMFactory fac, OMElement defintions)
            throws Exception {
        OMElement service = fac.createOMElement(SERVICE_LOCAL_NAME, wsdl);
        defintions.addChild(service);
        service.addAttribute(ATTRIBUTE_NAME, axisService.getName(), null);
        generateSOAP11Port(fac, service);
        generateSOAP12Port(fac, service);

        addPolicy(PolicyInclude.SERVICE_POLICY, axisService.getPolicyInclude(),
                service, fac);

        if (generateHttp) {
            generateHTTPPort(fac, service);
        }

    }

    private void generateSOAP11Port(OMFactory fac, OMElement service)
            throws Exception {
        for (int i = 0; i < url.length; i++) {
            String urlString = url[i];
            OMElement port = fac.createOMElement(PORT, wsdl);
            service.addChild(port);
            port.addAttribute(ATTRIBUTE_NAME, axisService.getName()
                    + SOAP11PORT + i, null);
            port.addAttribute(BINDING_LOCAL_NAME, tns.getPrefix() + ":"
                    + axisService.getName() + BINDING_NAME_SUFFIX, null);
            addExtensionElemnet(fac, port, SOAP_ADDRESS, LOCATION, urlString,
                    soap);

            addPolicy(PolicyInclude.PORT_POLICY,
                    axisService.getPolicyInclude(), service, fac);
        }

    }

    private void generateHTTPPort(OMFactory fac, OMElement service)
            throws Exception {
        for (int i = 0; i < url.length; i++) {
            String urlString = url[i];
            if (urlString.startsWith("http")) {
                OMElement port = fac.createOMElement(PORT, wsdl);
                service.addChild(port);
                port.addAttribute(ATTRIBUTE_NAME, axisService.getName()
                        + HTTP_PORT + i, null);
                port.addAttribute(BINDING_LOCAL_NAME, tns.getPrefix() + ":"
                        + axisService.getName() + HTTP_BINDING, null);
                OMElement extElement = fac.createOMElement("address", http);
                port.addChild(extElement);
                urlString = urlString.replaceAll("services", "rest");
                extElement.addAttribute("location", urlString, null);
            }
        }
    }

    private void generateSOAP12Port(OMFactory fac, OMElement service)
            throws Exception {
        for (int i = 0; i < url.length; i++) {
            String urlString = url[i];
            OMElement port = fac.createOMElement(PORT, wsdl);
            service.addChild(port);
            port.addAttribute(ATTRIBUTE_NAME, axisService.getName()
                    + SOAP12PORT + i, null);
            port.addAttribute(BINDING_LOCAL_NAME, tns.getPrefix() + ":"
                    + axisService.getName() + SOAP12BINDING_NAME_SUFFIX, null);
            addExtensionElemnet(fac, port, SOAP_ADDRESS, LOCATION, urlString,
                    soap12);

            addPolicy(PolicyInclude.PORT_POLICY,
                    axisService.getPolicyInclude(), service, fac);
        }
    }

    /**
     * Generate the bindings
     */
    private void generateSOAP11Binding(OMFactory fac, OMElement defintions)
            throws Exception {
        OMElement binding = fac.createOMElement(BINDING_LOCAL_NAME, wsdl);
        defintions.addChild(binding);
        binding.addAttribute(ATTRIBUTE_NAME, axisService.getName()
                + BINDING_NAME_SUFFIX, null);
        binding.addAttribute("type", tns.getPrefix() + ":"
                + axisService.getName() + PORT_TYPE_SUFFIX, null);
        addPolicy(PolicyInclude.BINDING_POLICY, axisService.getPolicyInclude(),
                binding, fac);

//Adding ext elements
        addExtensionElemnet(fac, binding, BINDING_LOCAL_NAME, TRANSPORT,
                TRANSPORT_URI, STYLE, style, soap);

        Iterator operations = axisService.getOperations();
        while (operations.hasNext()) {
            AxisOperation axisOperation = (AxisOperation) operations.next();
            if (axisOperation.isControlOperation()) {
                continue;
            }
            String opeartionName = axisOperation.getName().getLocalPart();
            OMElement operation = fac.createOMElement(OPERATION_LOCAL_NAME,
                    wsdl);
            binding.addChild(operation);
            String soapAction = axisOperation.getSoapAction();
            if (soapAction == null) {
                soapAction = "";
            }
            addExtensionElemnet(fac, operation, OPERATION_LOCAL_NAME,
                    SOAP_ACTION, soapAction, STYLE, style, soap);
            addPolicy(PolicyInclude.BINDING_OPERATION_POLICY, axisOperation
                    .getPolicyInclude(), operation, fac);

            String MEP = axisOperation.getMessageExchangePattern();

            if (WSDLConstants.MEP_URI_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage inaxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                if (inaxisMessage != null) {
                    operation.addAttribute(ATTRIBUTE_NAME, opeartionName, null);
                    OMElement input = fac.createOMElement(IN_PUT_LOCAL_NAME,
                            wsdl);
                    addExtensionElemnet(fac, input, SOAP_BODY, SOAP_USE, use,
                            "namespace", targetNamespace, soap);
                    addPolicy(PolicyInclude.BINDING_INPUT_POLICY, inaxisMessage
                            .getPolicyInclude(), input, fac);
                    operation.addChild(input);
                    writeSoapHeaders(inaxisMessage, fac, input, soap);
                }
            }

            if (WSDLConstants.MEP_URI_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage outAxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
                if (outAxisMessage != null) {
                    OMElement output = fac.createOMElement(OUT_PUT_LOCAL_NAME,
                            wsdl);
                    addExtensionElemnet(fac, output, SOAP_BODY, SOAP_USE, use,
                            "namespace", targetNamespace, soap);
                    addPolicy(PolicyInclude.BINDING_OUTPUT_POLICY,
                            outAxisMessage.getPolicyInclude(), output, fac);
                    operation.addChild(output);
                    writeSoapHeaders(outAxisMessage, fac, output, soap);
                }
            }

            // generate fault Messages
            ArrayList faultyMessages = axisOperation.getFaultMessages();
            if (faultyMessages != null) {
                for (int i = 0; i < faultyMessages.size(); i++) {
                    AxisMessage faultyMessge = (AxisMessage) faultyMessages
                            .get(i);
                    OMElement fault = fac.createOMElement(FAULT_LOCAL_NAME,
                            wsdl);
                    addExtensionElemnet(fac, fault, SOAP_BODY, SOAP_USE, use,
                            "namespace", targetNamespace, soap);
                    fault.addAttribute(ATTRIBUTE_NAME, faultyMessge.getName(),
                            null);
// TODO adding policies for fault messages
                    operation.addChild(fault);
                    writeSoapHeaders(faultyMessge, fac, fault, soap);
                }
            }
        }

    }

    /**
     * Generate the bindings
     */
    private void generateSOAP12Binding(OMFactory fac, OMElement defintions)
            throws Exception {
        OMElement binding = fac.createOMElement(BINDING_LOCAL_NAME, wsdl);
        defintions.addChild(binding);
        binding.addAttribute(ATTRIBUTE_NAME, axisService.getName()
                + SOAP12BINDING_NAME_SUFFIX, null);
        binding.addAttribute("type", tns.getPrefix() + ":"
                + axisService.getName() + PORT_TYPE_SUFFIX, null);
        addPolicy(PolicyInclude.BINDING_POLICY, axisService.getPolicyInclude(),
                binding, fac);

//Adding ext elements
        addExtensionElemnet(fac, binding, BINDING_LOCAL_NAME, TRANSPORT,
                TRANSPORT_URI, STYLE, style, soap12);

        Iterator operations = axisService.getOperations();
        while (operations.hasNext()) {
            AxisOperation axisOperation = (AxisOperation) operations.next();
            if (axisOperation.isControlOperation()) {
                continue;
            }
            String opeartionName = axisOperation.getName().getLocalPart();
            OMElement operation = fac.createOMElement(OPERATION_LOCAL_NAME,
                    wsdl);
            binding.addChild(operation);
            String soapAction = axisOperation.getSoapAction();
            if (soapAction == null) {
                soapAction = "";
            }
            addExtensionElemnet(fac, operation, OPERATION_LOCAL_NAME,
                    SOAP_ACTION, soapAction, STYLE, style, soap12);
            addPolicy(PolicyInclude.BINDING_OPERATION_POLICY, axisOperation
                    .getPolicyInclude(), operation, fac);

            String MEP = axisOperation.getMessageExchangePattern();

            if (WSDLConstants.MEP_URI_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage inaxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                if (inaxisMessage != null) {
                    operation.addAttribute(ATTRIBUTE_NAME, opeartionName, null);
                    OMElement input = fac.createOMElement(IN_PUT_LOCAL_NAME,
                            wsdl);
                    addExtensionElemnet(fac, input, SOAP_BODY, SOAP_USE, use,
                            "namespace", targetNamespace, soap12);
                    addPolicy(PolicyInclude.BINDING_INPUT_POLICY, inaxisMessage
                            .getPolicyInclude(), input, fac);
                    operation.addChild(input);
                    writeSoapHeaders(inaxisMessage, fac, input, soap12);
                }
            }

            if (WSDLConstants.MEP_URI_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage outAxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
                if (outAxisMessage != null) {
                    OMElement output = fac.createOMElement(OUT_PUT_LOCAL_NAME,
                            wsdl);
                    addExtensionElemnet(fac, output, SOAP_BODY, SOAP_USE, use,
                            "namespace", targetNamespace, soap12);
                    addPolicy(PolicyInclude.BINDING_OUTPUT_POLICY,
                            outAxisMessage.getPolicyInclude(), output, fac);
                    operation.addChild(output);
                    writeSoapHeaders(outAxisMessage, fac, output, soap12);
                }
            }

            // generate fault Messages
            ArrayList faultyMessages = axisOperation.getFaultMessages();
            if (faultyMessages != null) {
                for (int i = 0; i < faultyMessages.size(); i++) {
                    AxisMessage faultyMessge = (AxisMessage) faultyMessages
                            .get(i);
                    OMElement fault = fac.createOMElement(FAULT_LOCAL_NAME,
                            wsdl);
                    addExtensionElemnet(fac, fault, SOAP_BODY, SOAP_USE, use,
                            "namespace", targetNamespace, soap12);
                    fault.addAttribute(ATTRIBUTE_NAME, faultyMessge.getName(),
                            null);
// add policies for fault messages
                    operation.addChild(fault);
                    writeSoapHeaders(faultyMessge, fac, fault, soap12);
                }
            }
        }
    }

    private void generatePostBinding(OMFactory fac, OMElement defintions)
            throws Exception {
        OMElement binding = fac.createOMElement(BINDING_LOCAL_NAME, wsdl);
        defintions.addChild(binding);
        binding.addAttribute(ATTRIBUTE_NAME, axisService.getName()
                + HTTP_BINDING, null);
        binding.addAttribute("type", tns.getPrefix() + ":"
                + axisService.getName() + PORT_TYPE_SUFFIX, null);

//Adding ext elements
        OMElement httpBinding = fac.createOMElement("binding", http);
        binding.addChild(httpBinding);
        httpBinding.addAttribute("verb", "POST", null);

        Iterator operations = axisService.getOperations();
        while (operations.hasNext()) {
            AxisOperation axisOperation = (AxisOperation) operations.next();
            if (axisOperation.isControlOperation()) {
                continue;
            }
            String opeartionName = axisOperation.getName().getLocalPart();
            OMElement operation = fac.createOMElement(OPERATION_LOCAL_NAME,
                    wsdl);
            binding.addChild(operation);

            OMElement httpOperation = fac.createOMElement("operation", http);
            operation.addChild(httpOperation);
            httpOperation.addAttribute("location", axisOperation.getName()
                    .getLocalPart(), null);

            String MEP = axisOperation.getMessageExchangePattern();

            if (WSDLConstants.MEP_URI_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage inaxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                if (inaxisMessage != null) {
                    operation.addAttribute(ATTRIBUTE_NAME, opeartionName, null);
                    OMElement input = fac.createOMElement(IN_PUT_LOCAL_NAME,
                            wsdl);
                    OMElement inputelement = fac.createOMElement("content",
                            mime);
                    input.addChild(inputelement);
                    inputelement.addAttribute("type", "text/xml", null);
                    operation.addChild(input);
                }
            }

            if (WSDLConstants.MEP_URI_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
                    || WSDLConstants.MEP_URI_IN_OUT.equals(MEP)) {
                AxisMessage outAxisMessage = axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
                if (outAxisMessage != null) {
                    OMElement output = fac.createOMElement(OUT_PUT_LOCAL_NAME,
                            wsdl);
                    OMElement outElement = fac.createOMElement("content", mime);
                    outElement.addChild(outElement);
                    outElement.addAttribute("type", "text/xml", null);
                    output.addChild(outElement);
                    operation.addChild(output);
                }
            }
        }
    }

    private void writeSoapHeaders(AxisMessage inaxisMessage, OMFactory fac,
            OMElement input, OMNamespace soapNameSpace) throws Exception {
        ArrayList extElementList;
        extElementList = inaxisMessage.getSoapHeaders();
        if (extElementList != null) {
            Iterator elements = extElementList.iterator();
            while (elements.hasNext()) {
                SOAPHeaderMessage soapheader = (SOAPHeaderMessage) elements
                        .next();
                addSOAPHeader(fac, input, soapheader, soapNameSpace);
            }
        }
    }

    private void addExtensionElemnet(OMFactory fac, OMElement element,
            String name, String att1Name, String att1Value, String att2Name,
            String att2Value, OMNamespace soapNameSpace) {
        OMElement soapbinding = fac.createOMElement(name, soapNameSpace);
        element.addChild(soapbinding);
        soapbinding.addAttribute(att1Name, att1Value, null);
        soapbinding.addAttribute(att2Name, att2Value, null);
    }

    private void addExtensionElemnet(OMFactory fac, OMElement element,
            String name, String att1Name, String att1Value,
            OMNamespace soapNameSpace) {
        OMElement extElement = fac.createOMElement(name, soapNameSpace);
        element.addChild(extElement);
        extElement.addAttribute(att1Name, att1Value, null);
    }
    
    private void setDefinitionElement(OMElement defintion) {
        this.definition = defintion;
    }
    
    private OMElement getDefinitionElement() {
        return definition;
    }

    private void addSOAPHeader(OMFactory fac, OMElement element,
            SOAPHeaderMessage header, OMNamespace soapNameSpace) {
        OMElement extElement = fac.createOMElement("header", soapNameSpace);
        element.addChild(extElement);
        String use = header.getUse();
        if (use != null) {
            extElement.addAttribute("use", use, null);
        }
        if (header.part() != null) {
            extElement.addAttribute("part", header.part(), null);
        }
        if (header.getMessage() != null) {
            extElement.addAttribute("message", getPrefix(targetNamespace) + ":"
                    + header.getMessage().getLocalPart(), null);
        }
    }

    private String getPrefix(String targetNameSpace) {
        Map map = axisService.getNameSpacesMap();
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (map.get(key).equals(targetNameSpace)) {
                return key;
            }
        }
        return null;
    }

    private void addPolicy(int type, PolicyInclude policyInclude,
            OMElement element, OMFactory factory) throws Exception {
        ArrayList elementList = policyInclude.getPolicyElements(type);
        StAXPolicyWriter pwrt = (StAXPolicyWriter) PolicyFactory
                .getPolicyWriter(PolicyFactory.StAX_POLICY_WRITER);

        for (Iterator iterator = elementList.iterator(); iterator.hasNext();) {
            Object policyElement = iterator.next();

            if (policyElement instanceof Policy) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                pwrt.writePolicy((Policy) policyElement, baos);

                ByteArrayInputStream bais = new ByteArrayInputStream(baos
                        .toByteArray());
                element.addChild(OMXMLBuilderFactory.createStAXOMBuilder(
                        factory,
                        StAXUtils.createXMLStreamReader(
                                bais)).getDocumentElement());

            } else if (policyElement instanceof PolicyReference) {
                OMNamespace ns = factory.createOMNamespace(PolicyConstants.WS_POLICY_NAMESPACE_URI, PolicyConstants.WS_POLICY_PREFIX);
                OMElement refElement = factory.createOMElement(PolicyConstants.WS_POLICY_REFERENCE, ns);
                String policyURIString = ((PolicyReference) policyElement).getPolicyURIString();
                OMAttribute attribute = factory.createOMAttribute("URI", null, policyURIString);
                refElement.addAttribute(attribute);
                element.addChild(refElement);
                
                PolicyRegistry reg = policyInclude.getPolicyRegistry();
                Policy p = reg.lookup(policyURIString);
                
                if(p == null) {
                    throw new Exception("Policy not found for uri : " + policyURIString);
                }
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                pwrt.writePolicy(p, baos);

                ByteArrayInputStream bais = new ByteArrayInputStream(baos
                        .toByteArray());
                getDefinitionElement().addChild(OMXMLBuilderFactory.createStAXOMBuilder(
                        factory,
                        StAXUtils.createXMLStreamReader(
                                bais)).getDocumentElement());
                
                // TODO refactor this ..
                
            }

        }
    }
}