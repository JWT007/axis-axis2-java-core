package org.apache.axis2.async;

import junit.framework.TestCase;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AsyncResult;
import org.apache.axis2.client.async.Callback;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.Echo;
import org.apache.axis2.engine.util.TestConstants;
import org.apache.axis2.integration.TestingUtils;
import org.apache.axis2.integration.UtilServer;
import org.apache.axis2.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.om.OMAbstractFactory;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMFactory;
import org.apache.ws.commons.om.OMNamespace;

import javax.xml.namespace.QName;
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
*
*
*/

public class AsyncServiceTest extends TestCase implements TestConstants {

    protected Log log = LogFactory.getLog(getClass());
    protected QName transportName = new QName("http://localhost/my",
            "NullTransport");
    EndpointReference targetEPR = new EndpointReference(
            "http://127.0.0.1:" + (UtilServer.TESTING_PORT)
//            "http://127.0.0.1:" + 5556
                    + "/axis2/services/EchoXMLService/echoOMElement");

    protected AxisConfiguration engineRegistry;
    protected MessageContext mc;
    protected ServiceContext serviceContext;
    protected AxisService service;
    private boolean finish = false;

    protected void setUp() throws Exception {
        UtilServer.start();
        service = Utils.createSimpleService(serviceName,
                new AsyncMessageReceiver(),
                Echo.class.getName(),
                operationName);
        UtilServer.deployService(service);
    }

    protected void tearDown() throws Exception {
        UtilServer.unDeployService(serviceName);
        UtilServer.stop();
        UtilServer.unDeployClientService();
    }

    public void testEchoXMLCompleteASync() throws Exception {
        AxisService service =
                Utils.createSimpleServiceforClient(serviceName,
                        Echo.class.getName(),
                        operationName);

        ConfigurationContext configcontext = UtilServer.createClientConfigurationContext();

        OMFactory fac = OMAbstractFactory.getOMFactory();

        OMNamespace omNs = fac.createOMNamespace("http://localhost/my", "my");
        OMElement method = fac.createOMElement("echoOMElement", omNs);
        OMElement value = fac.createOMElement("myValue", omNs);
        value.setText("Isaac Asimov, The Foundation Trilogy");
        method.addChild(value);
        ServiceClient sender = null;

        try {
            Options options = new Options();
            options.setTo(targetEPR);
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
            options.setUseSeparateListener(true);
            options.setAction(operationName.getLocalPart());

            Callback callback = new Callback() {
                public void onComplete(AsyncResult result) {
                    TestingUtils.campareWithCreatedOMElement(
                            result.getResponseEnvelope().getBody()
                                    .getFirstElement());
                    System.out.println("result = " + result.getResponseEnvelope().getBody()
                                    .getFirstElement());
                    finish = true;
                }

                public void onError(Exception e) {
                    log.info(e.getMessage());
                    finish = true;
                }
            };

            sender = new ServiceClient(configcontext, service);
            sender.setOptions(options);

            sender.sendReceiveNonBlocking(operationName, method, callback);
            System.out.println("send the reqest");
            log.info("send the reqest");
            int index = 0;
            while (!finish) {
                Thread.sleep(1000);
                index++;
                if (index > 42) {
                    throw new AxisFault(
                            "Server was shutdown as the async response take too long to complete");
                }
            }
        } finally {
            if (sender != null)
                sender.finalizeInvoke();
        }

    }

}
