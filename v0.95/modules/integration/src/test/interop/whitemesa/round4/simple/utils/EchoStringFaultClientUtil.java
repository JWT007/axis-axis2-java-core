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

package test.interop.whitemesa.round4.simple.utils;

import org.apache.ws.commons.om.OMAbstractFactory;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMNamespace;
import org.apache.ws.commons.soap.SOAPEnvelope;
import org.apache.ws.commons.soap.SOAPFactory;
import test.interop.whitemesa.SunClientUtil;


public class EchoStringFaultClientUtil implements SunClientUtil {
    public SOAPEnvelope getEchoSoapEnvelope() {

        SOAPFactory omfactory = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope reqEnv = omfactory.getDefaultEnvelope();

        OMNamespace omNs = omfactory.createOMNamespace("http://soapinterop.org/wsdl", "m");
        OMNamespace envNs = reqEnv.declareNamespace("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        OMNamespace typeNs = reqEnv.declareNamespace("http://www.w3.org/2001/XMLSchema", "xsi");

        OMElement operation = omfactory.createOMElement("echoStringFault", omNs);
        operation.declareNamespace(envNs);
        reqEnv.getBody().addChild(operation);
        operation.addAttribute("encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/", envNs);

        OMElement part = omfactory.createOMElement("param", null);
        part.declareNamespace(typeNs);
        part.addAttribute("type", "xsd:string", typeNs);
        part.addChild(omfactory.createText("String"));

        operation.addChild(part);

        return reqEnv;
    }
}