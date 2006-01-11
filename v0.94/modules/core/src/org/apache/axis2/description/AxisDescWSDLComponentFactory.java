package org.apache.axis2.description;

import org.apache.axis2.wsdl.builder.WSDLComponentFactory;
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
import org.apache.wsdl.impl.MessageReferenceImpl;
import org.apache.wsdl.impl.WSDLBindingFaultImpl;
import org.apache.wsdl.impl.WSDLBindingImpl;
import org.apache.wsdl.impl.WSDLBindingMessageReferenceImpl;
import org.apache.wsdl.impl.WSDLBindingOperationImpl;
import org.apache.wsdl.impl.WSDLDescriptionImpl;
import org.apache.wsdl.impl.WSDLEndpointImpl;
import org.apache.wsdl.impl.WSDLExtensibilityAttributeImpl;
import org.apache.wsdl.impl.WSDLFaultReferenceImpl;
import org.apache.wsdl.impl.WSDLFeatureImpl;
import org.apache.wsdl.impl.WSDLImportImpl;
import org.apache.wsdl.impl.WSDLIncludeImpl;
import org.apache.wsdl.impl.WSDLInterfaceImpl;
import org.apache.wsdl.impl.WSDLOperationImpl;
import org.apache.wsdl.impl.WSDLPropertyImpl;
import org.apache.wsdl.impl.WSDLServiceImpl;
import org.apache.wsdl.impl.WSDLTypesImpl;

public class AxisDescWSDLComponentFactory implements WSDLComponentFactory {
    public WSDLBinding createBinding() {
        return new WSDLBindingImpl();
    }

    public WSDLBindingFault createBindingFault() {
        return new WSDLBindingFaultImpl();
    }

    public WSDLDescription createDescription() {
        return new WSDLDescriptionImpl();
    }

    public WSDLEndpoint createEndpoint() {
        return new WSDLEndpointImpl();
    }

    /**
     * @return A new Instance of <code>ExtensionFactory</code> that
     *         is capable of creating the correct <code>ExtensibilityElement</code>
     *         given a <code>QName</code>.
     */
    public ExtensionFactory createExtensionFactory() {
        return new ExtensionFactoryImpl();
    }

    public WSDLFaultReference createFaultReference() {
        return new WSDLFaultReferenceImpl();
    }

    public WSDLFeature createFeature() {
        return new WSDLFeatureImpl();
    }

    public WSDLImport createImport() {
        return new WSDLImportImpl();
    }

    public WSDLInclude createInclude() {
        return new WSDLIncludeImpl();
    }

    public WSDLInterface createInterface() {
        return new WSDLInterfaceImpl();
    }

    public MessageReference createMessageReference() {
        return new MessageReferenceImpl();
    }

    public WSDLOperation createOperation() {
        return new WSDLOperationImpl();
    }

    public WSDLProperty createProperty() {
        return new WSDLPropertyImpl();
    }

    public WSDLService createService() {
        return new WSDLServiceImpl();
    }

    public WSDLTypes createTypes() {
        return new WSDLTypesImpl();
    }

    public WSDLBindingMessageReference createWSDLBindingMessageReference() {
        return new WSDLBindingMessageReferenceImpl();
    }

    public WSDLBindingOperation createWSDLBindingOperation() {
        return new WSDLBindingOperationImpl();
    }

    public WSDLExtensibilityAttribute createWSDLExtensibilityAttribute() {
        return new WSDLExtensibilityAttributeImpl();
    }
}
