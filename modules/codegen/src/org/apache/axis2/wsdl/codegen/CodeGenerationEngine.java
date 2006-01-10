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

package org.apache.axis2.wsdl.codegen;

import org.apache.axis2.wsdl.builder.WOMBuilderFactory;
import org.apache.axis2.wsdl.codegen.emitter.Emitter;
import org.apache.axis2.wsdl.codegen.extension.CodeGenExtension;
import org.apache.axis2.wsdl.databinding.TypeMapper;
import org.apache.axis2.wsdl.i18n.CodegenMessages;
import org.apache.axis2.wsdl.util.CommandLineOption;
import org.apache.axis2.wsdl.util.CommandLineOptionConstants;
import org.apache.axis2.wsdl.util.CommandLineOptionParser;
import org.apache.axis2.wsdl.util.ConfigPropertyFileLoader;
import org.apache.wsdl.WSDLDescription;

import javax.wsdl.WSDLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeGenerationEngine {
    private List moduleEndpoints = new ArrayList();


    private CodeGenConfiguration configuration;

    public CodeGenerationEngine(CodeGenConfiguration configuration) throws CodeGenerationException {
        this.configuration = configuration;
        loadExtensions();
    }

    public CodeGenerationEngine(CommandLineOptionParser parser) throws CodeGenerationException {
        WSDLDescription wom;
        Map allOptions = parser.getAllOptions();
        try {

            CommandLineOption option = (CommandLineOption)allOptions.get(CommandLineOptionConstants.WSDL_LOCATION_URI_OPTION);
            wom = this.getWOM(option.getOptionValue());
        } catch (WSDLException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.wsdlParsingException"), e);
        }

        configuration = new CodeGenConfiguration(wom, allOptions);
        loadExtensions();
    }

    private void loadExtensions() throws CodeGenerationException {

        String[] extensions = ConfigPropertyFileLoader.getExtensionClassNames();
        for (int i = 0; i < extensions.length; i++) {
            //load the Extension class
            addExtension((CodeGenExtension) getObjectFromClassName(extensions[i]));
        }

    }

    private void addExtension(CodeGenExtension ext) {
        ext.init(configuration);
        moduleEndpoints.add(ext);
    }


    public void generate() throws CodeGenerationException {
        try {
            for (int i = 0; i < moduleEndpoints.size(); i++) {
                ((CodeGenExtension) moduleEndpoints.get(i)).engage();
            }

            Emitter emitter;


            TypeMapper mapper = configuration.getTypeMapper();
            if (mapper == null) {
                // this check is redundant here. The default databinding extension should
                // have already figured this out and thrown an error message. However in case the
                // users decides to mess with the config it is safe to keep this check in order to throw
                // a meaningful error message
                throw new CodeGenerationException(CodegenMessages.getMessage("engine.noProperDatabindingException"));
            }

            Map emitterMap = ConfigPropertyFileLoader.getLanguageEmitterMap();
            String className = emitterMap.get(configuration.getOutputLanguage()).toString();
            if (className != null) {
                emitter = (Emitter) getObjectFromClassName(className);
                emitter.setCodeGenConfiguration(configuration);
                emitter.setMapper(mapper);
            } else {
                throw new Exception(CodegenMessages.getMessage("engine.emitterMissing"));
            }


            if (configuration.isServerSide()) {
                emitter.emitSkeleton();
            }

            if (!configuration.isServerSide() || configuration.isWriteTestCase()) {
                emitter.emitStub();
            }

        } catch (ClassCastException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.wrongEmitter"), e);

        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }


    }


    private WSDLDescription getWOM(String uri) throws WSDLException {
        //assume that the builder is always WSDL 1.1 - later we'll have to edit this to allow
        //WSDL ersion to be passed
        return WOMBuilderFactory.getBuilder(org.apache.wsdl.WSDLConstants.WSDL_1_1).build(uri)
                .getDescription();
    }


    /**
     * gets a object from the class
     *
     * @param className
     * @return
     */
    private Object getObjectFromClassName(String className) throws CodeGenerationException {
        try {
            Class extensionClass = getClass().getClassLoader().loadClass(className);
            return extensionClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.extensionLoadProblem"), e);
        } catch (InstantiationException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.extensionInstantiationProblem"), e);
        } catch (IllegalAccessException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.illegalExtension"), e);
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }

    }
}
