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

package test.interop.sun.round4.simple.util;

import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMFactory;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.soap.SOAP11Constants;

public class EchoMultipleFaults2ClientUtil implements SunGroupHClientUtil{

    public OMElement getEchoOMElement() {

        OMFactory fac = OMAbstractFactory.getOMFactory();

        OMNamespace omNs = fac.createOMNamespace("http://soapinterop.org/wsdl", "m");

        OMElement method = fac.createOMElement("echoMultipleFaults2", omNs);
        OMNamespace soapEnvNS = method.declareNamespace(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                SOAP11Constants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        method.addAttribute("encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/", soapEnvNS);

        method.declareNamespace("http://www.w3.org/2001/XMLSchema-instance","xsi");
        method.declareNamespace("http://soapinterop.org/types","ns2");
        method.declareNamespace("http://schemas.xmlsoap.org/wsdl/","wsdl");

        OMElement value = fac.createOMElement("whichFault", null);
        method.addChild(value);
        OMElement value1 = fac.createOMElement("param1", null);
        OMElement value2 = fac.createOMElement("param2", null);
        OMElement value3 = fac.createOMElement("param3", null);
        OMElement value4 = fac.createOMElement("Item", null);
        OMElement value5 = fac.createOMElement("Item", null);
        OMElement value6 = fac.createOMElement("Item", null);

        value3.addAttribute("soapenc:arrayType","nsa:string[3]",null);
        value3.addAttribute("soapenc:offset","[0]",null);
        value3.addAttribute("xmlns:soapenc","http://schemas.xmlsoap.org/soap/encoding/",null);
        value3.addAttribute("xmlns:nsa","http://www.w3.org/2001/XMLSchema",null);


        value.addChild(fac.createText(value, "2"));
        value1.addChild(fac.createText(value1, "hi"));
        value2.addChild(fac.createText(value2, "0.23"));
        value4.addChild(fac.createText(value4, "String 1"));
        value5.addChild(fac.createText(value5, "String 2"));
        value6.addChild(fac.createText(value6, "String 3"));

        value3.addChild(value4);
        value3.addChild(value5);
        value3.addChild(value6);


        method.addChild(value1);
        method.addChild(value2);
        method.addChild(value3);


        return method;
    }

}
