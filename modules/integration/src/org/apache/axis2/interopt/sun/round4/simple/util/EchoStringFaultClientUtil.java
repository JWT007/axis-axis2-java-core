package org.apache.axis2.interopt.sun.round4.simple.util;

import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMFactory;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.soap.SOAP11Constants;

/**
 * Created by IntelliJ IDEA.
 * User: Nadana
 * Date: Aug 6, 2005
 * Time: 3:10:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class EchoStringFaultClientUtil implements SunGroupHClientUtil{

    public OMElement getEchoOMElement() {
        OMFactory fac = OMAbstractFactory.getOMFactory();

        OMNamespace omNs = fac.createOMNamespace("http://soapinterop.org/wsdl", "m");

        OMElement method = fac.createOMElement("echoStringFault", omNs);
        OMNamespace soapEnvNS = method.declareNamespace(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                SOAP11Constants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        method.addAttribute("encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/", soapEnvNS);




        OMElement value = fac.createOMElement("param", null);

        value.addChild(fac.createText(value, "Axis2 Echo String "));

        method.addChild(value);

        return method;
    }


}
