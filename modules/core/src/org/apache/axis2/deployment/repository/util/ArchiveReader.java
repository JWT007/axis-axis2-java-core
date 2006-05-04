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


package org.apache.axis2.deployment.repository.util;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.deployment.*;
import org.apache.axis2.deployment.resolver.AARBasedWSDLLocator;
import org.apache.axis2.deployment.resolver.AARFileBasedURIResolver;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.WSDL2AxisServiceBuilder;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.i18n.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ArchiveReader implements DeploymentConstants {
	private static final Log log = LogFactory.getLog(ArchiveReader.class);

    private ArrayList buildServiceGroup(InputStream zin, DeploymentEngine engine,
                                        AxisServiceGroup axisServiceGroup, HashMap wsdlServices,
                                        AxisConfiguration axisConfig)
            throws XMLStreamException, AxisFault {

        DescriptionBuilder builder = new DescriptionBuilder(zin, axisConfig);
        OMElement rootElement = builder.buildOM();
        String elementName = rootElement.getLocalName();

        if (TAG_SERVICE.equals(elementName)) {
            AxisService axisService = null;
//            OMAttribute serviceNameatt = rootElement.getAttribute(new QName(ATTRIBUTE_NAME));
            String serviceName = DescriptionBuilder.getShortFileName(engine.getCurrentFileItem().getName());
            if (serviceName != null) {
                axisService = (AxisService) wsdlServices.get(serviceName);
            }
            if (axisService == null) {
                axisService = (AxisService) wsdlServices.get(
                        DescriptionBuilder.getShortFileName(
                                engine.getCurrentFileItem().getName()));
            }
            if (axisService == null) {
                axisService = new AxisService(serviceName);
            } else {
                axisService.setWsdlfound(true);
            }

            axisService.setParent(axisServiceGroup);
            axisService.setClassLoader(engine.getCurrentFileItem().getClassLoader());

            ServiceBuilder serviceBuilder = new ServiceBuilder(axisConfig, axisService);
            AxisService service = serviceBuilder.populateService(rootElement);

            ArrayList serviceList = new ArrayList();
            serviceList.add(service);
            return serviceList;
        } else if (TAG_SERVICE_GROUP.equals(elementName)) {
            ServiceGroupBuilder groupBuilder = new ServiceGroupBuilder(rootElement, wsdlServices,
                    axisConfig);
            return groupBuilder.populateServiceGroup(axisServiceGroup);
        }
        throw new AxisFault("In valid services.xml found");
    }

    /**
     * Extracts Service XML files and builds the service groups.
     *
     * @param filename
     * @param engine
     * @param axisServiceGroup
     * @param extractService
     * @param wsdls
     * @param axisConfig
     * @return Returns ArrayList.
     * @throws DeploymentException
     */
    public ArrayList processServiceGroup(String filename, DeploymentEngine engine,
                                         AxisServiceGroup axisServiceGroup,
                                         boolean extractService,
                                         HashMap wsdls,
                                         AxisConfiguration axisConfig)
            throws AxisFault {
        // get attribute values
        if (!extractService) {
            ZipInputStream zin = null;
            try {
                zin = new ZipInputStream(new FileInputStream(filename));
                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    if (entry.getName().equalsIgnoreCase(SERVICES_XML)) {
                        axisServiceGroup.setServiceGroupName(
                                DescriptionBuilder.getShortFileName(
                                        engine.getCurrentFileItem().getName()));
                        return buildServiceGroup(zin, engine, axisServiceGroup, wsdls, axisConfig);
                    }
                }
                throw new DeploymentException(
                        Messages.getMessage(DeploymentErrorMsgs.SERVICE_XML_NOT_FOUND, filename));
            } catch (Exception e) {
                throw new DeploymentException(e);
            } finally {
                if (zin != null) {
                    try {
                        zin.close();
                    } catch (IOException e) {
                        log.info(Messages.getMessage("errorininputstreamclose"));
                    }
                }
            }
        } else {
            File file = new File(filename, SERVICES_XML);
            if (!file.exists()) {
                // try for meta-inf
                file = new File(filename, SERVICES_XML.toLowerCase());
            }
            if (file.exists()) {
                InputStream in = null;
                try {
                    in = new FileInputStream(file);
                    axisServiceGroup.setServiceGroupName(engine.getCurrentFileItem().getName());
                    return buildServiceGroup(in, engine, axisServiceGroup, wsdls, axisConfig);
                } catch (FileNotFoundException e) {
                    throw new DeploymentException(
                            Messages.getMessage(DeploymentErrorMsgs.FILE_NOT_FOUND, e.getMessage()));
                } catch (XMLStreamException e) {
                    throw new DeploymentException(
                            Messages.getMessage(DeploymentErrorMsgs.XML_STREAM_EXCEPTION, e.getMessage()));
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            log.info(Messages.getMessage("errorininputstreamclose"));
                        }
                    }
                }
            } else {
                throw new DeploymentException(
                        Messages.getMessage(DeploymentErrorMsgs.SERVICE_XML_NOT_FOUND));
            }
        }
    }

    /**
     * Creats AxisService.
     *
     * @param in
     * @return Returns AxisService.
     * @throws DeploymentException
     */
    private AxisService processWSDLFile(InputStream in, File serviceArchiveFile,
                                        boolean isArchive) throws DeploymentException {
        try {
            WSDL2AxisServiceBuilder wsdl2AxisServiceBuilder =
                    new WSDL2AxisServiceBuilder(in, null, null);
            if (serviceArchiveFile != null && isArchive) {
                wsdl2AxisServiceBuilder.setCustomResolver(
                        new AARFileBasedURIResolver(serviceArchiveFile));
                wsdl2AxisServiceBuilder.setCustomWSLD4JResolver(
                        new AARBasedWSDLLocator(serviceArchiveFile, in)
                );
            } else {
                if (serviceArchiveFile != null) {
                    wsdl2AxisServiceBuilder.setBaseUri(
                            serviceArchiveFile.getParentFile().getAbsolutePath());
                }
            }
            return wsdl2AxisServiceBuilder.populateService();
        } catch (AxisFault axisFault) {
            throw new DeploymentException(axisFault);
        }
    }

    /**
     * Creates service objects from wsdl file inside a service archive file.
     *
     * @param file      <code>ArchiveFileData</code>
     * @param depengine <code>DeploymentEngine</code>
     * @throws DeploymentException <code>DeploymentException</code>
     */
    public HashMap processWSDLs(ArchiveFileData file, DeploymentEngine depengine)
            throws DeploymentException {
        File serviceFile = file.getFile();
        // to store service come from wsdl files
        HashMap servicesMap = new HashMap();
        boolean isDirectory = serviceFile.isDirectory();
        if (isDirectory) {
            try {
                File meta_inf = new File(serviceFile, META_INF);

                if (!meta_inf.exists()) {
                    throw new DeploymentException(
                            Messages.getMessage(
                                    DeploymentErrorMsgs.META_INF_MISSING, serviceFile.getName()));
                }
                File files[] = meta_inf.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file1 = files[i];
                    if (file1.getName().toLowerCase().endsWith(SUFFIX_WSDL)) {
                        InputStream in = new FileInputStream(file1);
                        AxisService service = processWSDLFile(in, file1, false);

                        servicesMap.put(service.getName(), service);
                        try {
                            in.close();
                        } catch (IOException e) {
                            log.info(e);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                throw new DeploymentException(e);
            } catch (IOException e) {
                throw new DeploymentException(e);
            }
        } else {
            ZipInputStream zin;

            try {
                zin = new ZipInputStream(new FileInputStream(serviceFile));

                ZipEntry entry;
                byte[]                buf = new byte[1024];
                int read;
                ByteArrayOutputStream out;
                while ((entry = zin.getNextEntry()) != null) {
                    String entryName = entry.getName().toLowerCase();
                    if (entryName.startsWith(META_INF.toLowerCase())
                            && entryName.endsWith(SUFFIX_WSDL)) {
                        out = new ByteArrayOutputStream();

                        while ((read = zin.read(buf)) > 0) {
                            out.write(buf, 0, read);
                        }

                        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                        AxisService service = processWSDLFile(in, serviceFile, true);
                        servicesMap.put(service.getName(), service);
                    }
                }
                try {
                    zin.close();
                } catch (IOException e) {
                    log.info(e);
                }
            } catch (FileNotFoundException e) {
                throw new DeploymentException(e);
            } catch (IOException e) {
                throw new DeploymentException(e);
            }
        }
        return servicesMap;
    }

    public void readModuleArchive(String filename, DeploymentEngine engine,
                                  AxisModule module, boolean explodedDir,
                                  AxisConfiguration axisConfig)
            throws DeploymentException {

        // get attribute values
        boolean foundmoduleXML = false;
        if (!explodedDir) {
            ZipInputStream zin;
            try {
                zin = new ZipInputStream(new FileInputStream(filename));
                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    if (entry.getName().equalsIgnoreCase(MODULE_XML)) {
                        foundmoduleXML = true;
                        ModuleBuilder builder = new ModuleBuilder(zin, module, axisConfig);
                        // setting module name
                        module.setName(
                                new QName(
                                        DescriptionBuilder.getShortFileName(
                                                engine.getCurrentFileItem().getServiceName())));
                        builder.populateModule();
                        break;
                    }
                }
                zin.close();
                if (!foundmoduleXML) {
                    throw new DeploymentException(
                            Messages.getMessage(
                                    DeploymentErrorMsgs.MODULE_XML_MISSING, filename));
                }
            } catch (Exception e) {
                throw new DeploymentException(e);
            }
        } else {
            File file = new File(filename, MODULE_XML);

            if (file.exists() || (file = new File(filename, MODULE_XML.toLowerCase())).exists()) {
                InputStream in;
                try {
                    in = new FileInputStream(file);
                    ModuleBuilder builder = new ModuleBuilder(in, module, axisConfig);
                    // setting module name
                    module.setName(
                            new QName(
                                    DescriptionBuilder.getShortFileName(
                                            engine.getCurrentFileItem().getServiceName())));
                    builder.populateModule();
                } catch (FileNotFoundException e) {
                    throw new DeploymentException(
                            Messages.getMessage(DeploymentErrorMsgs.FILE_NOT_FOUND, e.getMessage()));
                }
            } else {
                throw new DeploymentException(
                        Messages.getMessage(
                                DeploymentErrorMsgs.MODULE_XML_MISSING, filename));
            }
        }
    }
}
