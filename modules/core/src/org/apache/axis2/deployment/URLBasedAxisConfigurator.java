package org.apache.axis2.deployment;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisConfigurator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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

public class URLBasedAxisConfigurator implements AxisConfigurator {

	private static final Log log = LogFactory.getLog(URLBasedAxisConfigurator.class);
    private URL axis2xml;
    private URL repositoy;
    private DeploymentEngine deploymentEngine;
    private AxisConfiguration axisConfiguration;
    ;

    public URLBasedAxisConfigurator(URL axis2xml, URL repositoy) throws AxisFault {
        this.axis2xml = axis2xml;
        this.repositoy = repositoy;
    }

    public AxisConfiguration getAxisConfiguration() throws AxisFault {
        deploymentEngine = new DeploymentEngine();
        InputStream axis2xmlStream;
        try {
            if (axis2xml == null) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                axis2xmlStream = cl.getResourceAsStream(DeploymentConstants.AXIS2_CONFIGURATION_RESOURCE);
            } else {
                axis2xmlStream = axis2xml.openStream();
            }
            axisConfiguration = deploymentEngine.populateAxisConfiguration(axis2xmlStream);
            if (repositoy == null) {
                Parameter axis2repoPara = axisConfiguration.getParameter(DeploymentConstants.AXIS2_REPO);
                if (axis2repoPara != null) {
                    String repoValue = (String) axis2repoPara.getValue();
                    if (repoValue != null && !"".equals(repoValue.trim())) {
                        if (repoValue.startsWith("file://")) {
                            // we treat this case specialy , by assuming file is
                            // locate in the local machine
                            deploymentEngine.loadRepository(repoValue);
                        } else {
                            deploymentEngine.loadRepositoryFromURL(new URL(repoValue));
                        }
                    }
                } else {
                    log.info("No repository found , module will be loded using class path");
                    deploymentEngine.loadFromClassPath();
                }
            } else {
                deploymentEngine.loadRepositoryFromURL(repositoy);
            }

        } catch (IOException e) {
            throw new AxisFault(e.getMessage());
        }
        return axisConfiguration;
    }

    //to load services
    public void loadServices() {
        try {
            if (repositoy == null) {
                Parameter axis2repoPara = axisConfiguration.getParameter(DeploymentConstants.AXIS2_REPO);
                if (axis2repoPara != null) {
                    String repoValue = (String) axis2repoPara.getValue();
                    if (repoValue != null && !"".equals(repoValue.trim())) {
                        if (repoValue.startsWith("file://")) {
                            // we treat this case specialy , by assuming file is
                            // locate in the local machine
                            deploymentEngine.loadServices();
                        } else {
                            deploymentEngine.loadServicesFromUrl(new URL(repoValue));
                        }
                    }
                }
            } else {
                deploymentEngine.loadServices();
            }
        } catch (MalformedURLException e) {
            log.info(e);
        }
    }

    //To engage globally listed modules
    public void engageGlobalModules() throws AxisFault {
        deploymentEngine.engageModules();
    }
}
