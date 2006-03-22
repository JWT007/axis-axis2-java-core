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
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.Echo;
import org.apache.axis2.engine.util.TestConstants;
import org.apache.axis2.integration.UtilServer;
import org.apache.axis2.util.Utils;
import org.apache.ws.commons.attachments.utils.ImageDataSource;
import org.apache.ws.commons.attachments.utils.ImageIO;
import org.apache.ws.commons.om.OMAbstractFactory;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMFactory;
import org.apache.ws.commons.om.OMNamespace;
import org.apache.ws.commons.om.OMText;
import org.apache.ws.commons.om.impl.llom.OMTextImpl;
import org.apache.ws.commons.soap.SOAP12Constants;

import javax.activation.DataHandler;
import java.awt.*;
import java.io.InputStream;

public class EchoRawMTOMCommonsChunkingTest extends TestCase implements TestConstants {
    private AxisService service;

    private OMElement data;

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
        OMText textData = new OMTextImpl(expectedDH, fac);
        subData.addChild(textData);
        data.addChild(subData);
        rpcWrapEle.addChild(data);
        return rpcWrapEle;

    }

    public void testEchoXMLSync() throws Exception {

        OMElement payload = createEnvelope();

        Options options = new Options();
        options.setTo(targetEPR);
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

        options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        ConfigurationContext configContext =
                ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                        Constants.TESTING_PATH + "commons-http-enabledRepository", null);
        ServiceClient sender = new ServiceClient(configContext, null);
        sender.setOptions(options);
        options.setTo(targetEPR);

        sender.sendReceive(payload);
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