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

package org.apache.wsdl.impl;

import org.apache.wsdl.MessageReference;
import org.apache.wsdl.WSDLBinding;
import org.apache.wsdl.WSDLBindingFault;
import org.apache.wsdl.WSDLBindingMessageReference;
import org.apache.wsdl.WSDLBindingOperation;
import org.apache.wsdl.WSDLDescription;
import org.apache.wsdl.WSDLEndpoint;
import org.apache.wsdl.WSDLExtensibilityAttribute;
import org.apache.wsdl.WSDLFaultReference;
import org.apache.wsdl.WSDLFeature;
import org.apache.wsdl.WSDLImport;
import org.apache.wsdl.WSDLInclude;
import org.apache.wsdl.WSDLInterface;
import org.apache.wsdl.WSDLOperation;
import org.apache.wsdl.WSDLProperty;
import org.apache.wsdl.WSDLService;
import org.apache.wsdl.WSDLTypes;
import org.apache.wsdl.extensions.ExtensionFactory;
import org.apache.wsdl.extensions.impl.ExtensionFactoryImpl;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WSDLDescriptionImpl extends ComponentImpl
        implements WSDLDescription {
    /**
     * The name token of WSDL 1.1 Definition.
     */
    private QName wsdl1DefinitionName;

    // TODO local name and the naspace name to be made static or through a Constant class.
    // The attribute information items
    // TODO required; thus check it up

    /**
     * Field targetNameSpace
     */
    private String targetNameSpace;

    // private NamespaceMappings[] namespaceDefinitions;
    // TODO The object structure of some external xml data binding is going to be pluged here eventually.

    /**
     * Field types
     */
    private WSDLTypes types;

    /**
     * This List will be a list of <code>WSDLInterface</code> objects.
     */
    private Map wsdlInterfaces = new HashMap();

    /**
     * This <code>HashMap </code> is a Map of <code>WSDLBinding </code> objects.
     */
    private Map bindings = new HashMap();

    /**
     * This <code>HashMap </code> is a list of <code>WSDLService </code> objects.
     * Support of multiple is backed by the requirements in the specification.
     */
    private Map services = new HashMap();

    /**
     * WSDL imports
     */
    private ArrayList imports = new ArrayList();

    /**
     * WSDL Includes.
     */
    private ArrayList includes = new ArrayList();

    /**
     * Will keep a map of all the Namespaces associated with the
     * Definition Component and will be keyed by the Namespace prefix.
     */
    private Map namespaces = new HashMap();

    /**
     * Returns a Map of <code>WSDLBindings</code> Objects keyed by the <code>QName</code>
     * of the Binding.
     *
     * @return
     */
    public Map getBindings() {
        return bindings;
    }

    /**
     * Sets the whole new set of Bindings to the WSDLDefinition.
     *
     * @param bindings
     */
    public void setBindings(Map bindings) {

        // if(this.bindings.size() > 0) throw new WSDLProcessingException("WSDLBimding Map already contains " +
        // "one or more bindings. Trying to assign a new map will loose those Bindings.");
        this.bindings = bindings;
    }

    /**
     * The WSDLBinding Will be added to the map keyed  with its own QName.
     * If the WSDLBinding is null it will not be added.
     * If the WSDLBinding is not null and  Binding name is null then
     * WSDLProcessingException will be thrown
     *
     * @param binding <code>WSDLBinding</code> Object
     */
    public void addBinding(WSDLBinding binding) {
        if (null == binding) {
            return;
        }
        if (null == binding.getName()) {
            throw new WSDLProcessingException(
                    "The WSDLBinding name cannot be null(Required)");
        }
        this.bindings.put(binding.getName(), binding);
    }

    /**
     * Retrives the <code>WSDLBinding</code> by its QName. Wil return null
     * if <code>WSDLBinding</code> is not found.
     *
     * @param qName The QName of the Binding.
     * @return
     */
    public WSDLBinding getBinding(QName qName) {
        return (WSDLBinding) this.bindings.get(qName);
    }

    /**
     * The Interface component will be added to the map keyed with its own name.
     * If the Interface is null it will not be added.
     * If the interface name is null an WSDLProcessingException will be thrown
     *
     * @param interfaceComponent
     */
    public void addInterface(WSDLInterface interfaceComponent) {
        if (null == interfaceComponent) {
            return;
        }
        if (null == interfaceComponent.getName()) {
            throw new WSDLProcessingException(
                    "PortType/Interface name cannot be null(Required) ");
        }
        this.wsdlInterfaces.put(interfaceComponent.getName(),
                interfaceComponent);
    }

    /**
     * The Interface Component will be returned if it exsists,
     * otherwise null will be returned.
     *
     * @param qName qName of the Interface.
     * @return The Interface Component with the relavent QName
     */
    public WSDLInterface getInterface(QName qName) {
        return (WSDLInterface) this.wsdlInterfaces.get(qName);
    }

    /**
     * Method getServices
     *
     * @return
     */
    public Map getServices() {
        return services;
    }

    /**
     * Method setServices
     *
     * @param services
     */
    public void setServices(Map services) {
        this.services = services;
    }

    /**
     * Will return the <code>WSDLService </code> if found otherwise return null.
     *
     * @param qName <code>QName</code> of the Service
     * @return The Service with the relavent QName
     */
    public WSDLService getService(QName qName) {
        return (WSDLService) this.services.get(qName);
    }

    /**
     * Will add the <code>WSDLService</code> to the Map.
     * If object is null it will not be added.
     * If the <code>WSDLService</code> name is null a <code>WSDLProcessingException</code>
     * will be thrown.(its required)
     *
     * @param service
     */
    public void addService(WSDLService service) {
        if (null == service) {
            return;
        }
        if (null == service.getName()) {
            throw new WSDLProcessingException(
                    "The WSDLService name cannot be null (Required)");
        }
        this.services.put(service.getName(), service);
    }

    /**
     * Method getTargetNameSpace
     *
     * @return
     */
    public String getTargetNameSpace() {
        return targetNameSpace;
    }

    /**
     * Method setTargetNameSpace
     *
     * @param targetNameSpace
     */
    public void setTargetNameSpace(String targetNameSpace) {
        this.targetNameSpace = targetNameSpace;
    }

    /**
     * Method getWsdlInterfaces
     *
     * @return
     */
    public Map getWsdlInterfaces() {
        return wsdlInterfaces;
    }

    /**
     * Method setWsdlInterfaces
     *
     * @param wsdlInterfaces
     */
    public void setWsdlInterfaces(Map wsdlInterfaces) {
        this.wsdlInterfaces = wsdlInterfaces;
    }

    /**
     * Method getTypes
     *
     * @return
     */
    public WSDLTypes getTypes() {
        return types;
    }

    /**
     * Method setTypes
     *
     * @param types
     */
    public void setTypes(WSDLTypes types) {
        this.types = types;
    }

    /**
     * Gets the name attribute of the WSDL 1.1 Definitions Element
     *
     * @return
     */
    public QName getWSDL1DefinitionName() {
        return wsdl1DefinitionName;
    }

    /**
     * Sets the name attribute of the WSDL 1.1 Definitions Element
     *
     * @param wsdl1DefinitionName
     */
    public void setWSDL1DefinitionName(QName wsdl1DefinitionName) {
        this.wsdl1DefinitionName = wsdl1DefinitionName;
    }

    /**
     * Will return all the Namespaces associated with the Definition
     * Component and will be keyed by the Napespace Prefix.
     *
     * @return
     */
    public Map getNamespaces() {
        return namespaces;
    }

    /**
     * Sets the Namespaces associated with the Difinition Component
     * and they should be keyed by its Namespace Prefix.
     *
     * @param namespaces
     */
    public void setNamespaces(Map namespaces) {
        this.namespaces = namespaces;
    }

    /**
     * Will return the Namespace URI as a String if there exists an
     * Namespace URI associated with the given prefix, in the Definition
     * Component, Will return null if not found.
     *
     * @param prefix Prefix defined in the Definitions elemet in the WSDL file
     * @return The Namespace URI for the prefix.
     */
    public String getNamespace(String prefix) {
        if (null == prefix) {
            return null;
        }
        return (String) this.namespaces.get(prefix);
    }

    /**
     * Returns the WSDL Imports in an <code>ArrayList</code>
     *
     * @return
     */
    public ArrayList getImports() {
        return imports;
    }

    /**
     * Sets the imports as an <code>ArrayList</code>
     *
     * @param imports
     */
    public void setImports(ArrayList imports) {
        this.imports = imports;
    }

    /**
     * Adds an import to the list.
     *
     * @param wsdlImport
     */
    public void addImports(WSDLImport wsdlImport) {
        this.imports.add(wsdlImport);
    }

    /**
     * Returns the Includes as an <code>ArrayList</code>
     *
     * @return
     */
    public ArrayList getIncludes() {
        return includes;
    }

    /**
     * Sets the includes as an <code>Arraylist</code>
     *
     * @param includes
     */
    public void setIncludes(ArrayList includes) {
        this.includes = includes;
    }

    /**
     * Adds the WSDL Include to the list.
     *
     * @param wsdlInclude
     */
    public void addInclude(WSDLInclude wsdlInclude) {
        this.includes.add(wsdlInclude);
    }

    /**
     * @return A new instance of type <code>WSDLDescription</code>
     */
    public WSDLDescription createDescription() {
        return new WSDLDescriptionImpl();
    }

    /**
     * @return A new instance of type <code>WSDLService</code>
     */
    public WSDLService createService() {
        return new WSDLServiceImpl();
    }

    /**
     * @return A new instance of type <code>WSDLInterface</code>
     */
    public WSDLInterface createInterface() {
        return new WSDLInterfaceImpl();
    }

    /**
     * @return A new instance of type <code>WSDLTypes</code>
     */
    public WSDLTypes createTypes() {
        return new WSDLTypesImpl();
    }

    /**
     * @return A new instance of type <code>WSDLBinding</code>
     */
    public WSDLBinding createBinding() {
        return new WSDLBindingImpl();
    }

    /**
     * @return A new instance of type <code>WSDLOperation</code>
     */
    public WSDLOperation createOperation() {
        return new WSDLOperationImpl();
    }

    /**
     * @return A new instance of type <code>WSDLEndpoint</code>
     */
    public WSDLEndpoint createEndpoint() {
        return new WSDLEndpointImpl();
    }


    /**
     * @return A new instance of type <code>WSDLFeature</code>
     */
    public WSDLFeature createFeature() {
        return new WSDLFeatureImpl();
    }

    /**
     * @return A new instance of type <code>WSDLImport</code>
     */
    public WSDLImport createImport() {
        return new WSDLImportImpl();
    }

    /**
     * @return A new instance of type <code>WSDLInclude</code>
     */
    public WSDLInclude createInclude() {
        return new WSDLIncludeImpl();
    }

    /**
     * Method createProperty
     *
     * @return
     */
    public WSDLProperty createProperty() {
        return new WSDLPropertyImpl();
    }

    public MessageReference createMessageReference() {
        return new MessageReferenceImpl();
    }

    public WSDLBindingMessageReference createWSDLBindingMessageReference() {
        return new WSDLBindingMessageReferenceImpl();
    }

    public WSDLBindingOperation createWSDLBindingOperation() {
        return new WSDLBindingOperationImpl();
    }

    /**
     * @return A new Instance of <code>ExtensionFactory</code> that
     *         is capable of creating the correct <code>ExtensibilityElement</code>
     *         given a <code>QName</code>.
     */
    public ExtensionFactory createExtensionFactory() {
        return new ExtensionFactoryImpl();
    }


    public WSDLBinding getFirstBinding() {
        Iterator bindingIterator = this.bindings.values().iterator();
        if (bindingIterator.hasNext()) {
            return (WSDLBinding) bindingIterator.next();
        }
        return null;
       // throw new WSDLProcessingException(
       //       "No Binding Components are available");
    }

    public WSDLExtensibilityAttribute createWSDLExtensibilityAttribute() {
        return new WSDLExtensibilityAttributeImpl();
    }

    public WSDLFaultReference createFaultReference() {
        return new WSDLFaultReferenceImpl();
    }

    public WSDLBindingFault createBindingFault() {
        return new WSDLBindingFaultImpl();
    }
}
