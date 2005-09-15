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

package org.apache.axis2.mtom;

import junit.framework.TestCase;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.attachments.utils.ImageDataSource;
import org.apache.axis2.attachments.utils.ImageIO;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.ServiceDescription;
import org.apache.axis2.engine.Echo;
import org.apache.axis2.integration.UtilServer;
import org.apache.axis2.om.*;
import org.apache.axis2.om.impl.llom.OMTextImpl;
import org.apache.axis2.soap.SOAP12Constants;
import org.apache.axis2.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.awt.*;
import java.io.InputStream;

public class EchoRawMTOMCommonsChunkingTest extends TestCase {
    private EndpointReference targetEPR = new EndpointReference("http://127.0.0.1:"
            + (UtilServer.TESTING_PORT)
            + "/axis/services/EchoXMLService/echoOMElement");

    private Log log = LogFactory.getLog(getClass());

    private QName serviceName = new QName("EchoXMLService");

    private QName operationName = new QName("echoOMElement");

    private ServiceContext serviceContext;

    private ServiceDescription service;

    private OMElement data;

    private boolean finish = false;

    public EchoRawMTOMCommonsChunkingTest() {
        super(EchoRawMTOMCommonsChunkingTest.class.getName());
    }

    public EchoRawMTOMCommonsChunkingTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        UtilServer.start(Constants.TESTING_PATH + "MTOM-enabledRepository");
        service = Utils.createSimpleService(serviceName, Echo.class.getName(),
                operationName);
        UtilServer.deployService(service);
    }

    protected void tearDown() throws Exception {
        UtilServer.unDeployService(serviceName);
        UtilServer.stop();
    }

    private OMElement createEnvelope() throws Exception {

        DataHandler expectedDH;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://localhost/my", "my");
        OMElement rpcWrapEle = fac.createOMElement("echoOMElement", omNs);
        data = fac.createOMElement("data", omNs);
        Image expectedImage;
        expectedImage = new ImageIO()
                .loadImage(getResourceAsStream("org/apache/axis2/mtom/test.jpg"));

        ImageDataSource dataSource = new ImageDataSource("test.jpg",
                expectedImage);
        expectedDH = new DataHandler(dataSource);
        OMElement subData = fac.createOMElement("subData", omNs);
        OMText textData = new OMTextImpl(expectedDH);
        subData.addChild(textData);
        data.addChild(subData);
        rpcWrapEle.addChild(data);
        return rpcWrapEle;

    }

    public void testEchoXMLSync() throws Exception {

        OMElement payload = createEnvelope();

        org.apache.axis2.clientapi.Call call = new org.apache.axis2.clientapi.Call(
                Constants.TESTING_PATH + "commons-http-enabledRepository");
        call.setTo(targetEPR);
        call.set(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        call.setTransportInfo(Constants.TRANSPORT_HTTP,
                Constants.TRANSPORT_HTTP, false);
        call.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        OMElement result = call.invokeBlocking(operationName
                .getLocalPart(), payload);

        OMElement ele = (OMElement) result.getFirstChild();
        this.campareWithCreatedOMElement(data);

    }

    private InputStream getResourceAsStream(String path) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl.getResourceAsStream(path);
    }

    private void campareWithCreatedOMElement(OMElement element) {
        OMElement firstChild = element.getFirstElement();
        TestCase.assertNotNull(firstChild);
        String originalTextValue = data.getFirstElement().getText();
        String returnedTextValue = firstChild.getText();
        TestCase.assertEquals(returnedTextValue, originalTextValue);
    }

}