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
 
package org.apache.axis.integration;

import org.apache.axis.description.AxisService;
import org.apache.axis.engine.AxisFault;
import org.apache.axis.engine.EngineRegistry;
import org.apache.axis.engine.EngineRegistryFactory;
import org.apache.axis.transport.http.SimpleHTTPServer;

import javax.xml.namespace.QName;
import java.net.ServerSocket;

public class UtilServer {
    private static int count = 0;
    private static SimpleHTTPServer reciver;


    public static synchronized void deployService(AxisService service)
            throws AxisFault {
        reciver.getEngineReg().addService(service);
    }

    public static synchronized void unDeployService(QName service)
            throws AxisFault {
        reciver.getEngineReg().removeService(service);
    }

    public static synchronized void start() throws Exception {
        if (count == 0) {
            Class erClass = Class.forName("org.apache.axis.deployment.EngineRegistryFactoryImpl");
            EngineRegistryFactory erfac = (EngineRegistryFactory)erClass.newInstance();
            EngineRegistry er = 
                erfac.createEngineRegistry("target/test-resources/samples/");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                throw new AxisFault("Thread interuptted", e1);
            }

            reciver = new SimpleHTTPServer(er);

            ServerSocket serverSoc = null;
            serverSoc = new ServerSocket(Constants.TESTING_PORT);
            reciver.setServerSocket(serverSoc);
            Thread thread = new Thread(reciver);
            thread.setDaemon(true);

            try {
                thread.start();
                System.out.print("Server started on port " + Constants.TESTING_PORT + ".....");
            } finally {

            }
        }
        count++;
    }

    public static synchronized void stop() {
        if (count == 1) {
            reciver.stop();
            count = 0;
            System.out.print("Server stopped .....");
        } else {
            count--;
        }
    }
}
