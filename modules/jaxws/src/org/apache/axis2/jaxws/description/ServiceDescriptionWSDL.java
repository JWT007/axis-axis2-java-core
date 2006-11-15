package org.apache.axis2.jaxws.description;

import java.net.URL;

import javax.wsdl.Definition;

import org.apache.axis2.jaxws.util.WSDLWrapper;

public interface ServiceDescriptionWSDL {
    // TODO: Remove these and replace with appropraite get* methods for WSDL information
    public abstract WSDLWrapper getWSDLWrapper();
    public abstract WSDLWrapper getGeneratedWsdlWrapper();

    public abstract URL getWSDLLocation();
    
    public Definition getWSDLDefinition();
    public boolean isWSDLSpecified();

}