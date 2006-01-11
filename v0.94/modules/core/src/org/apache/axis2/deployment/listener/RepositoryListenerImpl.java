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


package org.apache.axis2.deployment.listener;

import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.deployment.repository.util.ArchiveFileData;
import org.apache.axis2.deployment.repository.util.WSInfoList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class RepositoryListenerImpl implements RepositoryListener, DeploymentConstants {
    protected Log log = LogFactory.getLog(getClass());
    private DeploymentEngine deploymentEngine;

    /**
     * The parent directory of the modules and services directories
     */
    private String folderName;

    /**
     * Reference to a WSInfoList
     */
    private WSInfoList wsInfoList;

    /**
     * This constructor takes two arguments, a folder name and a reference to Deployment Engine
     * First, it initializes the system, by loading all the modules in the /modules directory
     * and then creates a WSInfoList to store information about available modules and services.
     *
     * @param folderName    path to parent directory that the listener should listen to
     * @param deploymentEngine reference to engine registry for updates
     */
    public RepositoryListenerImpl(String folderName, DeploymentEngine deploymentEngine) {
        this.folderName = folderName;
        wsInfoList = new WSInfoList(deploymentEngine);
        this.deploymentEngine = deploymentEngine;
        init();
    }

    /**
     * Finds a list of modules in the folder and adds to wsInfoList.
     */
    public void checkModules() {
        String modulepath = folderName + MODULE_PATH;
        File root = new File(modulepath);
        File[] files = root.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                if (!file.isDirectory()) {
                    if (ArchiveFileData.isModuleArchiveFile(file.getName())) {
                        wsInfoList.addWSInfoItem(file, TYPE_MODULE);
                    }
                } else {
                    if (!"lib".equalsIgnoreCase(file.getName())) {
                        wsInfoList.addWSInfoItem(file, TYPE_MODULE);
                    }
                }
            }
        }
    }

    /**
     * Finds a list of services in the folder and adds to wsInfoList.
     */
    public void checkServices() {
        String modulepath = folderName + SERVICE_PATH;

        findServicesInDirectory(modulepath);
        update();
    }

    /**
     * First initializes the WSInfoList, then calls checkModule to load all the modules
     * and calls update() to update the Deployment engine and engine registry.
     */
    public void init() {
        wsInfoList.init();
        checkModules();
        deploymentEngine.doDeploy();
    }

    /**
     * Searches a given folder for jar files and adds them to a list in the 
     * WSInfolist class.
     */
    private void findServicesInDirectory(String folderName) {
        File root = new File(folderName);
        File[] files = root.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                if (!file.isDirectory()) {
                    if (ArchiveFileData.isServiceArchiveFile(file.getName())) {
                        wsInfoList.addWSInfoItem(file, TYPE_SERVICE);
                    }
                } else {
                    if (!"lib".equalsIgnoreCase(file.getName())) {
                        wsInfoList.addWSInfoItem(file, TYPE_SERVICE);
                    }
                }
            }
        }
    }

    /**
     * Method invoked from the scheduler to start the listener.
     */
    public void startListener() {
        checkServices();
        update();
    }

    /**
     * Updates WSInfoList object.
     */
    public void update() {
        wsInfoList.update();
    }
}
