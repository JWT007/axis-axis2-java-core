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

package org.apache.axis2.engine;

//todo

import junit.framework.TestCase;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.ModuleDescription;
import org.apache.axis2.description.OperationDescription;
import org.apache.axis2.description.ServiceDescription;
import org.apache.axis2.integration.UtilServer;
import org.apache.axis2.transport.http.SimpleHTTPServer;
import org.apache.axis2.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;

public class MessageWithServerTest extends TestCase {
    private Log log = LogFactory.getLog(getClass());
    private QName serviceName = new QName("", "EchoService");
    private QName operationName =
            new QName("http://ws.apache.org/axis2", "echoVoid");
    private QName transportName = new QName("", "NullTransport");

    private AxisConfiguration engineRegistry;
    private MessageContext mc;
    private Thread thisThread;
    private SimpleHTTPServer sas;
    private ClassLoader cl;

    public MessageWithServerTest(String testName) {
        super(testName);
        cl = Thread.currentThread().getContextClassLoader();
    }

    protected void setUp() throws Exception {
        UtilServer.start();
        ServiceDescription service = Utils.createSimpleService(serviceName,
                Echo.class.getName(),
                operationName);


        service.setInFlow(new MockFlow("service inflow", 4));
        service.setOutFlow(new MockFlow("service outflow", 5));
        //service.setFaultInFlow(new MockFlow("service faultflow", 1));

        ModuleDescription m1 = new ModuleDescription(
                new QName("", "A Mdoule 1"));
        m1.setInFlow(new MockFlow("service module inflow", 4));
        //m1.setFaultInFlow(new MockFlow("service module faultflow", 1));
        engineRegistry = new AxisConfigurationImpl();
        service.engageModule(m1,engineRegistry);

        OperationDescription operation = new OperationDescription(
                operationName);
        service.addOperation(operation);

        UtilServer.deployService(service);
        UtilServer.start();
    }

    protected void tearDown() throws Exception {
        UtilServer.unDeployService(serviceName);
        UtilServer.stop();
    }

    public void testEchoStringServer() throws Exception {
        InputStream in = cl.getResourceAsStream("soap/soapmessage.txt");

        Socket socket = new Socket("127.0.0.1", UtilServer.TESTING_PORT);
        OutputStream out = socket.getOutputStream();
        byte[] buf = new byte[1024];
        int index = -1;
        while ((index = in.read(buf)) > 0) {
            out.write(buf, 0, index);
        }

        InputStream respose = socket.getInputStream();
        Reader rReader = new InputStreamReader(respose);
        char[] charBuf = new char[1024];
        while ((index = rReader.read(charBuf)) > 0) {
            log.info(new String(charBuf));
        }

        in.close();
        out.close();

        rReader.close();
        socket.close();
    }
}
