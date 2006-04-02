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

import org.apache.axis2.description.AxisService;
import org.apache.axis2.i18n.Messages;
import org.apache.ws.commons.om.OMAbstractFactory;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.impl.builder.StAXOMBuilder;
import org.apache.ws.commons.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.ws.commons.soap.SOAP11Constants;
import org.apache.ws.commons.soap.SOAP12Constants;
import org.apache.ws.commons.soap.SOAPBody;
import org.apache.ws.commons.soap.SOAPEnvelope;
import org.apache.ws.commons.soap.SOAPFactory;
import org.apache.ws.commons.soap.SOAPProcessingException;
import org.apache.wsdl.WSDLService;

import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;

public abstract class Stub {
    protected static AxisService _service;
    protected ArrayList modules = new ArrayList();


    protected ServiceClient _serviceClient;

    public ServiceClient _getServiceClient() {
        return _serviceClient;
    }

    public void _setServiceClient(ServiceClient _serviceClient) {
        this._serviceClient = _serviceClient;
    }

    protected SOAPEnvelope createEnvelope(Options options) throws SOAPProcessingException {
        return getFactory(options.getSoapVersionURI()).getDefaultEnvelope();
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
            throw new UnsupportedOperationException(Messages
                    .getMessage("unsupportedType"));
        }
    }

    protected OMElement getElementFromReader(XMLStreamReader reader) {
        StAXOMBuilder builder =
                OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), reader);

        return builder.getDocumentElement();
    }

    protected SOAPFactory getFactory(String soapVersionURI) {

        if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(soapVersionURI)) {
            return OMAbstractFactory.getSOAP11Factory();
        } else if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(soapVersionURI)) {
            return OMAbstractFactory.getSOAP12Factory();
        } else {
            throw new RuntimeException(Messages
                    .getMessage("unknownsoapversion"));
        }
    }


}
