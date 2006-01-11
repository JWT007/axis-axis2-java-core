package org.apache.axis2.deployment;

import junit.framework.TestCase;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.util.HostConfiguration;
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
*/

public class HostCongigurationTest extends TestCase {
    AxisConfiguration ar;
    String repo ="./test-resources/deployment/hostConfigrepo";



    protected void setUp() throws Exception {
        ConfigurationContextFactory builder = new ConfigurationContextFactory();
        ar = builder.createConfigurationContextFromFileSystem(repo).getAxisConfiguration();
    }

    public void testHostConfig() throws AxisFault {
        HostConfiguration  hc = ar.getHostConfiguration();
        assertNotNull(hc);
        assertEquals(5555,hc.getPort());
    }
}