package org.apache.axis2.engine.util.profiling;

import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.engine.util.TestConstants;
import org.apache.axis2.integration.TestingUtils;
import org.apache.axis2.om.OMElement;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

public class ContextMemoryHandlingUtil implements TestConstants {

//    public static final EndpointReference targetEPR = new EndpointReference(
//            "http://192.168.1.219:" + 8080
//                    + "/axis2/services/echo/echoOMElement");
//
//    public static final QName serviceName = new QName("echo");
//    public static final QName operationName = new QName("echoOMElement");

    public ContextMemoryHandlingUtil() {
    }

    public void runOnce() throws Exception {
        OMElement payload = TestingUtils.createDummyOMElement();
        Options options = new Options();
        options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
        ConfigurationContextFactory factory = new ConfigurationContextFactory();
        ConfigurationContext configContext =
                factory.createConfigurationContextFromFileSystem("target/test-resources/integrationRepo");
        ServiceClient sender = new ServiceClient(configContext, null);
        sender.setOptions(options);
        options.setTo(targetEPR);
        OMElement result = sender.sendReceive(payload);

        TestingUtils.campareWithCreatedOMElement(result);
        sender.finalizeInvoke();
    }


    public static void main(String[] args) throws Exception {
        ContextMemoryHandlingUtil contextMemoryHandlingTest = new ContextMemoryHandlingUtil();

        try {
            long initialMemory = Runtime.getRuntime().freeMemory();
            System.out.println("initialMemory = " + initialMemory);
            int numberOfTimes = 0;

            while (true) {
                System.out.println("Iterations # = " + ++numberOfTimes);
                contextMemoryHandlingTest.runOnce();
                System.out.println("Memory Usage = " + (initialMemory - Runtime.getRuntime().freeMemory()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
