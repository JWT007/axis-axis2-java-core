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
package org.apache.axis2.transport.tcp;

import junit.framework.TestCase;
import org.apache.axis2.AxisFault;
import org.apache.axis2.om.impl.llom.builder.StAXBuilder;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.impl.llom.builder.StAXSOAPModelBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

public class TCPTransportTest extends TestCase {
    public TCPTransportTest(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public void testTransportSender() throws AxisFault {
//        Thread thead = new Thread(new Runnable() {
//            public void run() {
//               try {
//                     ServerSocket serverSocket = new ServerSocket(45678);
//                        Socket s = serverSocket.accept();
//                        assertNotNull(createSOAPEnvelope(s.getInputStream()));
//                } catch (AxisFault e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        });
//        thead.start();
//
//        TCPTransportSender ts = new TCPTransportSender();
//        MessageContext msgctx = new MessageContext(null, null, null, null);
//        msgctx.setTo(new EndpointReference(AddressingConstants.WSA_TO,"http://127.0.0.1:45679"));
//        
//        msgctx.setEnvelope(createSOAPEnvelope(Thread.currentThread().getContextClassLoader().getResourceAsStream("org/apache/axis2/transport/sample.xml")));
//        ts.invoke(msgctx);


    }
    
//    public void testTransportReceiver(){
//        ConfigurationContext configContext = new ConfigurationContext(new AxisConfigurationImpl());
//        ServiceDescription serviceDesc = new ServiceDescription(new QName("TempService"));
//        OperationDescription opDesc = new OperationDescription();
//        TCPServer tcpServer = new TCPServer();
//    }
    

    public SOAPEnvelope createSOAPEnvelope(InputStream in) throws AxisFault {
        try {
            XMLStreamReader xmlreader =
                    XMLInputFactory.newInstance().createXMLStreamReader(in);
            StAXBuilder builder = new StAXSOAPModelBuilder(xmlreader, null);
            return (SOAPEnvelope) builder.getDocumentElement();
        } catch (Exception e) {
            throw new AxisFault(e.getMessage(), e);
        }
    }

}
