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

import org.apache.axis.deployment.listener.RepositoryListenerImpl;
import org.apache.axis.deployment.repository.utill.HDFileItem;
import org.apache.axis.deployment.repository.utill.UnZipJAR;
import org.apache.axis.deployment.repository.utill.WSInfo;
import org.apache.axis.deployment.scheduler.DeploymentIterator;
import org.apache.axis.deployment.scheduler.Scheduler;
import org.apache.axis.deployment.scheduler.SchedulerTask;
import org.apache.axis.description.AxisGlobal;
import org.apache.axis.description.AxisModule;
import org.apache.axis.description.AxisService;
import org.apache.axis.description.Flow;
import org.apache.axis.description.HandlerMetadata;
import org.apache.axis.description.Parameter;
import org.apache.axis.engine.AxisFault;
import org.apache.axis.engine.EngineRegistry;
import org.apache.axis.engine.EngineRegistryImpl;
import org.apache.axis.engine.Handler;
import org.apache.axis.engine.Provider;
import org.apache.axis.phaseresolver.PhaseException;
import org.apache.axis.phaseresolver.PhaseResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DeploymentEngine implements DeploymentConstants {
    private Log log = LogFactory.getLog(getClass());
    private static Scheduler scheduler;

    private boolean hotdeployment = true;   //to do hot deployment or not
    private boolean hotupdate = true;  // to do hot update or not



    /**
     * This will store all the web Services to deploy
     */
    private List wsToDeploy = new ArrayList();
    /**
     * this will store all the web Services to undeploy
     */
    private List wsToUnDeploy = new ArrayList();

    /**
     * to keep a ref to engine register
     * this ref will pass to engine when it call start()
     * method
     */
    private EngineRegistry engineRegistry;

    /**
     * this constaructor for the testing
     */

    private String folderName;

    private String serverconfigName;

    /**
     * This to keep a referance to serverMetaData object
     */
    // private static ServerMetaData server = new ServerMetaData();
    private AxisGlobal server;


    private HDFileItem currentFileItem;

    /**
     * This the constructor which is used by Engine inorder to start
     * Deploymenat module,
     *
     * @param RepositaryName is the path to which Repositary Listner should
     *                       listent.
     */

    public DeploymentEngine(String RepositaryName) throws DeploymentException {
        this(RepositaryName,"server.xml");

    }
    public DeploymentEngine(String RepositaryName, String serverXMLFile) throws DeploymentException {
        this.folderName = RepositaryName;
        File repository = new File(RepositaryName);
        if(!repository.exists()){
            repository.mkdirs();
            File servcies = new File(repository,"services");
            File modules = new File(repository,"modules");
            modules.mkdirs();
            servcies.mkdirs();
        }
        File serverConf = new File(repository,serverXMLFile);
        if(!serverConf.exists()){
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream in = cl.getResourceAsStream("org/apache/axis/deployment/server.xml");
            if(in != null){
                try {
                    serverConf.createNewFile();
                    FileOutputStream out = new FileOutputStream(serverConf);
                    int BUFSIZE = 512; // since only a test file going to load , the size has selected
                    byte[] buf = new byte[BUFSIZE];
                    int read;
                    while((read = in.read(buf)) > 0){
                        out.write(buf,0,read);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    throw new DeploymentException(e.getMessage());
                }


            } else{
                throw new DeploymentException("can not found org/apache/axis/deployment/server.xml");

            }
        }
        this.serverconfigName = RepositaryName + '/' + serverXMLFile;
    }

//    public DeploymentEngine(String RepositaryName , String configFileName) {
//        this.folderName = RepositaryName;
//        this.serverconfigName = configFileName;
//    }

    public HDFileItem getCurrentFileItem() {
        return currentFileItem;
    }


    /**
     * tio get ER
     * @return
     */
    public EngineRegistry getEngineRegistry() {
        return engineRegistry;
    }

    /**
     * This method will fill the engine registry and return it to Engine
     *
     * @return
     * @throws AxisFault
     */
    public EngineRegistry start() throws AxisFault, DeploymentException, XMLStreamException {
        //String fileName;
        if(serverconfigName == null) {
            throw new DeploymentException("path to Server.xml can not be NUll");
        }
        File tempfile = new File(serverconfigName);
        try {
            InputStream in = new FileInputStream(tempfile);
            engineRegistry = createEngineRegistry();
            DeploymentParser parser = new DeploymentParser(in, this);
            parser.procesServerXML(server);
        } catch (FileNotFoundException e) {
            throw new AxisFault("Exception at deployment",e);
        }
        setDeploymentFeatures();
        if(hotdeployment){
            startSearch(this);
        } else {
            RepositoryListenerImpl repository =
                    new RepositoryListenerImpl(folderName,this);
        }
        try {
            valideServerModule() ;
        } catch (PhaseException e) {
            log.info("Module validation failed"  + e.getMessage());
        }

        return engineRegistry;
    }

    /**
     * To set hotdeployment and hot update
     */
    private void setDeploymentFeatures() {
        String value;
        Parameter parahotdeployment = server.getParameter(HOTDEPLOYMENT);
        Parameter parahotupdate = server.getParameter(HOTUPDATE);
        if(parahotdeployment != null ){
            value = (String)parahotdeployment.getValue();
            if("false".equals(value))
                hotdeployment=  false;
        }
        if(parahotupdate != null){
            value =(String)parahotupdate.getValue();
            if("false".equals(value))
                hotupdate =false;

        }
    }

    /**
     * This methode used to check the modules referd by server.xml
     * are exist , or they have deployed
     */
    private void valideServerModule() throws AxisFault, PhaseException{
        Iterator itr= server.getModules().iterator();
        while (itr.hasNext()) {
            QName qName = (QName) itr.next();
            if(getModule(qName) == null ){
                throw new AxisFault(server + " Refer to invalid module " + qName + " has not bean deployed yet !");
            }
        }
        PhaseResolver phaseResolver = new PhaseResolver(engineRegistry);
        phaseResolver.buildGlobalChains(server);
        phaseResolver.buildTranspotsChains();

    }

    public AxisModule getModule(QName moduleName) throws AxisFault {
        AxisModule metaData = engineRegistry.getModule(moduleName);
        return metaData;
    }

    /**
     * this method use to start the Deployment engine
     * inorder to perform Hot deployment and so on..
     */
    private void startSearch(DeploymentEngine engine) {
        scheduler =new Scheduler();
        scheduler.schedule(new SchedulerTask(engine, folderName), new DeploymentIterator());
    }

    private EngineRegistry createEngineRegistry()  {
        EngineRegistry newEngineRegisty;

        server = new AxisGlobal();
        newEngineRegisty = new EngineRegistryImpl(server);

        return newEngineRegisty;
    }


    private void addnewService(AxisService serviceMetaData) throws AxisFault, PhaseException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        loadServiceClass(serviceMetaData,classLoader);

        Flow inflow = serviceMetaData.getInFlow();
        if(inflow != null ){
            addFlowHandlers(inflow,classLoader);
        }

        Flow outFlow = serviceMetaData.getOutFlow();
        if(outFlow != null){
            addFlowHandlers(outFlow,classLoader);
        }

        Flow faultFlow = serviceMetaData.getFaultFlow();
        if(faultFlow != null) {
            addFlowHandlers(faultFlow,classLoader);
        }
        PhaseResolver reolve = new PhaseResolver(engineRegistry,serviceMetaData);
        reolve.buildchains();
        engineRegistry.addService(serviceMetaData);
    }

    private void loadServiceClass(AxisService service, ClassLoader parent) throws AxisFault{
        File file = currentFileItem.getFile();
        Class serviceclass = null;
        URLClassLoader loader1 = null;
        if (file != null) {
            URL[] urlsToLoadFrom = new URL[0];
            try {
                if (!file.exists()) {
                    throw new RuntimeException("file not found !!!!!!!!!!!!!!!");
                }
                urlsToLoadFrom = new URL[]{file.toURL()};
                loader1 = new URLClassLoader(urlsToLoadFrom, parent);
                service.setClassLoader(loader1);

                String readInClass = currentFileItem.getClassName();

                if(readInClass != null && !"".equals(readInClass)){
                    serviceclass = Class.forName(currentFileItem.getClassName(), true, loader1);
                }
                service.setServiceClass(serviceclass);

                String readInProviderName = currentFileItem.getProvideName();
                if(readInProviderName != null && ! "".equals(readInProviderName)){
                    Class provider =Class.forName(currentFileItem.getProvideName(), true, loader1);
                    service.setProvider((Provider)provider.newInstance());
                }
            } catch (MalformedURLException e) {
                throw new AxisFault(e.getMessage(),e);
            } catch (Exception e) {
                throw new AxisFault(e.getMessage(),e);
            }

        }

    }


    private void addFlowHandlers(Flow flow, ClassLoader parent) throws AxisFault {
        int count = flow.getHandlerCount();
        File file = currentFileItem.getFile();
        URLClassLoader loader1 = null;
        if (file != null) {
            URL[] urlsToLoadFrom = new URL[0];
            try {
                if (!file.exists()) {
                    throw new RuntimeException("file not found !!!!!!!!!!!!!!!");
                }
                urlsToLoadFrom = new URL[]{file.toURL()};
            } catch (MalformedURLException e) {
                throw new AxisFault(e.getMessage());
            }
            loader1 = new URLClassLoader(urlsToLoadFrom, parent);
        }

        for (int j = 0; j < count; j++) {
            //todo handle exception in properway
            HandlerMetadata handlermd = flow.getHandler(j);
            Class handlerClass = null;
            Handler handler;
            handlerClass = getHandlerClass(handlermd.getClassName(), loader1);
            try {
                handler = (Handler) handlerClass.newInstance();
                handler.init(handlermd);
                handlermd.setHandler(handler);

            } catch (InstantiationException e) {
                throw new AxisFault(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new AxisFault(e.getMessage());
            }

        }
    }


    public Class getHandlerClass(String className, URLClassLoader loader1) throws AxisFault {
        Class handlerClass = null;

        try {
            handlerClass = Class.forName(className, true, loader1);
        } catch (ClassNotFoundException e) {
            throw new AxisFault(e.getMessage());
        }
        return handlerClass;
    }


    private void addNewModule(AxisModule moduelmetada) throws AxisFault {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Flow inflow = moduelmetada.getInFlow();
        addFlowHandlers(inflow,classLoader);

        Flow outFlow = moduelmetada.getOutFlow();
        addFlowHandlers(outFlow,classLoader);

        Flow faultFlow = moduelmetada.getFaultFlow();
        addFlowHandlers(faultFlow,classLoader);

        engineRegistry.addMdoule(moduelmetada);
    }


    /**
     * @param file
     */
    public void addtowsToDeploy(HDFileItem file) {
        wsToDeploy.add(file);
    }

    /**
     * @param file
     */
    public void addtowstoUnDeploy(WSInfo file) {
        wsToUnDeploy.add(file);
    }

    public void doDeploy() {
        //todo complete this
        if (wsToDeploy.size() > 0) {
            for (int i = 0; i < wsToDeploy.size(); i++) {
                currentFileItem = (HDFileItem) wsToDeploy.get(i);
                int type = currentFileItem.getType();
                UnZipJAR unZipJAR = new UnZipJAR();
                switch (type) {
                    case SERVICE:
                        try {
//
                            AxisService service = new AxisService();
                            unZipJAR.unzipService(currentFileItem.getAbsolutePath(), this, service);
                            addnewService(service);
                            log.info("Deployement WS Name  " + currentFileItem.getName());
                        } catch (DeploymentException de) {
                            log.info("Invalid service" + currentFileItem.getName() );
                            log.info("DeploymentException  " + de);
                        } catch (AxisFault axisFault) {
                            log.info("Invalid service" + currentFileItem.getName() );
                            log.info("AxisFault  " + axisFault);
                        } catch (Exception e) {
                            log.info("Invalid service" + currentFileItem.getName() );
                            log.info("Exception  " + e);
                        } finally {
                            currentFileItem = null;
                        }
                        break;
                    case MODULE:
                        try {
                            AxisModule metaData = new AxisModule();
                            unZipJAR.unzipModule(currentFileItem.getAbsolutePath(), this, metaData);
                            addNewModule(metaData);
                            log.info("Moduel WS Name  " + currentFileItem.getName() + " modulename :" + metaData.getName());
                        } catch (DeploymentException e) {
                            log.info("Invalid module" + currentFileItem.getName() );
                            log.info("DeploymentException  " + e);
                        } catch (AxisFault axisFault) {
                            log.info("Invalid module" + currentFileItem.getName() );
                            log.info("AxisFault  " + axisFault);
                        } finally {
                            currentFileItem = null;
                        }
                        break;

                }
            }
        }
        wsToDeploy.clear();
    }

    public void doUnDeploye() {
        //todo complete this
        String serviceName ="";
        try{
            if (wsToUnDeploy.size() > 0) {
                for (int i = 0; i < wsToUnDeploy.size(); i++) {
                    WSInfo wsInfo = (WSInfo) wsToUnDeploy.get(i);
                    if(wsInfo.getType()==SERVICE) {
                        serviceName = getAxisServiceName(wsInfo.getFilename());
                        engineRegistry.removeService(new QName(serviceName));
                        log.info("UnDeployement WS Name  " + wsInfo.getFilename());
                    }
                }

            }
        }catch(AxisFault e){
            log.info("AxisFault " + e);
        }
        wsToUnDeploy.clear();
    }

    public boolean isHotupdate() {
        return hotupdate;
    }

    /**
     * This method is used to retrive service name form the arechive file name
     * if the archive file name is service1.aar , then axis service name would be service1
     * @param fileName
     * @return
     */
    private String getAxisServiceName(String fileName){
        char seperator = '.';
        String value = null;
        int index = fileName.indexOf(seperator);
        if (index > 0) {
            value = fileName.substring(0 , index);
            return value;
        }
        return fileName;
    }

}
