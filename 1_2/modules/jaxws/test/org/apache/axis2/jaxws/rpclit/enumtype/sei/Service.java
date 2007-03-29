package org.apache.axis2.jaxws.rpclit.enumtype.sei;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0_01-b15-fcs
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "RPCLitEnumService", targetNamespace = "http://rpclit.test.org", wsdlLocation = "soapenc.wsdl")
public class Service
    extends javax.xml.ws.Service
{

    private final static URL SERVICE_WSDL_LOCATION;

    private static String wsdlLocation="/test/org/apache/axis2/jaxws/rpclit/enumtype/META-INF/rpclitenum.wsdl";
    static {
        URL url = null;
        try {
                try{
                        String baseDir = new File(System.getProperty("basedir",".")).getCanonicalPath();
                        wsdlLocation = new File(baseDir + wsdlLocation).getAbsolutePath();
                }catch(Exception e){
                        e.printStackTrace();
                }
                File file = new File(wsdlLocation);
                url = file.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SERVICE_WSDL_LOCATION = url;
    }

    public Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public Service() {
        super(SERVICE_WSDL_LOCATION, new QName("http://rpclit.test.org", "RPCLitEnumService"));
    }

    /**
     * 
     * @return
     *     returns PortType
     */
    @WebEndpoint(name = "Port")
    public PortType getPort() {
        return (PortType)super.getPort(new QName("http://rpclit.test.org", "Port"), PortType.class);
    }

}
