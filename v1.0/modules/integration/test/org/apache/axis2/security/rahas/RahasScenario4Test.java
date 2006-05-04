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

package org.apache.axis2.security.rahas;

import org.apache.axis2.description.Parameter;
import org.apache.axis2.security.handler.config.InflowConfiguration;
import org.apache.axis2.security.handler.config.OutflowConfiguration;

/**
 * This tests the use computed keys when the requester provides entropy
 */
public class RahasScenario4Test extends TestClient {

    public RahasScenario4Test(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public Parameter getClientRahasConfiguration() {
        RahasConfiguration config = new RahasConfiguration();

        config.setCryptoPropertiesFile("sec.properties");
        config.setScope(RahasConfiguration.SCOPE_SERVICE);
        config.setPasswordCallbackClass(PWCallback.class.getName());
        config.setProvideEntropy(true);
        config.setStsEPRAddress("http://localhost:" + port + "/axis2/services/Service");

        return config.getParameter();
    }

    public OutflowConfiguration getClientOutflowConfiguration() {
        OutflowConfiguration ofc = new OutflowConfiguration();

        ofc.setActionItems("Timestamp Signature");
        ofc.setUser("alice");
        ofc.setSignaturePropFile("sec.properties");
        ofc.setPasswordCallbackClass(PWCallback.class.getName());
        return ofc;
    }

    public InflowConfiguration getClientInflowConfiguration() {
        InflowConfiguration ifc = new InflowConfiguration();

        ifc.setActionItems("Timestamp Signature Encrypt");
        ifc.setPasswordCallbackClass(PWCallback.class.getName());
        ifc.setSignaturePropFile("sec.properties");
        
        return ifc;
    }

    public String getServiceRepo() {
        return "rahas_service_repo_4";
    }


}
