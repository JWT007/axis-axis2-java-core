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
 
package org.apache.axis.deployment;

import org.apache.axis.description.AxisOperation;
import org.apache.axis.description.AxisService;
import org.apache.axis.description.Flow;
import org.apache.axis.engine.EngineRegistry;
import org.apache.axis.engine.Provider;
import org.apache.axis.providers.RawXMLProvider;

import javax.xml.namespace.QName;

public class BuildERWithDeploymentTest extends AbstractTestCase {
    /**
     * @param testName
     */
    public BuildERWithDeploymentTest(String testName) {
        super(testName);
    }

    public void testDeployment() throws Exception {
        String filename = "./target/test-resources/deployment";
        DeploymentEngine deploymentEngine = new DeploymentEngine(filename);
        EngineRegistry er = deploymentEngine.start();
        assertNotNull(er);
        assertNotNull(er.getGlobal());

        AxisService service = er.getService(new QName("service2"));
        assertNotNull(service);
        Provider provider = service.getProvider();
        assertNotNull(provider);
        assertTrue(provider instanceof RawXMLProvider);
        ClassLoader cl = service.getClassLoader();
        assertNotNull(cl);
        Class.forName("Echo2", true, cl);
        assertNotNull(service.getName());
        assertEquals(service.getStyle(),"rpc");
        
        Flow flow = service.getFaultFlow();
        assertTrue(flow.getHandlerCount() > 0);
        flow = service.getInFlow();
        assertTrue(flow.getHandlerCount() > 0);
        flow = service.getOutFlow();
        assertTrue( flow.getHandlerCount() > 0);
        assertNotNull(service.getParameter("para2"));

        AxisOperation op = service.getOperation(new QName("opname"));
        assertNotNull(op);

    }
}
