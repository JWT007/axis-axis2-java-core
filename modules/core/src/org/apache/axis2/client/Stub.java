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


package org.apache.axis2.client;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.impl.llom.builder.StAXOMBuilder;
import org.apache.axis2.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axis2.soap.SOAP11Constants;
import org.apache.axis2.soap.SOAP12Constants;
import org.apache.axis2.soap.SOAPBody;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.axis2.soap.SOAPProcessingException;
import org.apache.wsdl.WSDLService;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;

public abstract class Stub {
    protected static AxisService _service;
    protected ArrayList modules = new ArrayList();

    /**
     * If _maintainSession is set to true, all the calls can use the same
     * ServiceContext. The user can share information through this
     * ServiceContext across operations.
     */
    protected Options _options = new Options();
    protected ServiceClient _serviceClient;

    public Options _getOptions() {
        return _options;
    }

    public void _setOptions(Options _clientOptions) {
        this._options = _clientOptions;
    }

    protected SOAPEnvelope createEnvelope() throws SOAPProcessingException {
        return getFactory(this._options.getSoapVersionURI()).getDefaultEnvelope();
    }

    public void engageModule(QName moduleName) throws AxisFault {
        _serviceClient.engageModule(moduleName);
    }



    /**
     * A util method that extracts the correct element.
     *
     * @param env
     * @param type
     * @return the relevant element to be databound
     */
    protected OMElement getElement(SOAPEnvelope env, String type) {
        SOAPBody body = env.getBody();
        OMElement element = body.getFirstElement();

        if (WSDLService.STYLE_RPC.equals(type)) {
            return element.getFirstElement();    // todo this needs to be fixed
        } else if (WSDLService.STYLE_DOC.equals(type)) {
            return element;
        } else {
            throw new UnsupportedOperationException("Unsupported type");
        }
    }

    protected OMElement getElementFromReader(XMLStreamReader reader) {
        StAXOMBuilder builder =
                OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), reader);

        return builder.getDocumentElement();
    }

    protected SOAPFactory getFactory(String soapNamespaceURI) {
        String soapVersionURI = _options.getSoapVersionURI();

        if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(soapVersionURI)) {
            return OMAbstractFactory.getSOAP11Factory();
        } else if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(soapVersionURI)) {
            return OMAbstractFactory.getSOAP12Factory();
        } else {
            throw new RuntimeException("Unknown SOAP version");
        }
    }



}
