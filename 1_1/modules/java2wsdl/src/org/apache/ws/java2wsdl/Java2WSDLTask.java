package org.apache.ws.java2wsdl;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOption;

import java.util.HashMap;
import java.util.Map;
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

public class Java2WSDLTask extends Task implements Java2WSDLConstants {

    private String className = null;
    private String outputLocation = null;
    private String targetNamespace = null;
    private String targetNamespacePrefix = null;
    private String schemaTargetNamespace = null;
    private String schemaTargetNamespacePrefix = null;
    private String serviceName = null;
    private String outputFileName = null;
    private Path classpath = null;

    /**
     *
     */
    public Java2WSDLTask() {
        super();
    }

    /**
     * Fills the option map. This map is passed onto
     * the code generation API to generate the code.
     */
    private Map fillOptionMap() {
        Map optionMap = new HashMap();

        // Check that critical options exist
        if (className == null) {
            throw new BuildException(
                    "You must specify a classname");
        }

        ////////////////////////////////////////////////////////////////

        // Classname
        addToOptionMap(optionMap,
                Java2WSDLConstants.CLASSNAME_OPTION,
                className);

        // Output location
        addToOptionMap(optionMap,
                Java2WSDLConstants.OUTPUT_LOCATION_OPTION,
                outputLocation);

        // Target namespace
        addToOptionMap(optionMap,
                Java2WSDLConstants.TARGET_NAMESPACE_OPTION,
                targetNamespace);

        // Target namespace prefix
        addToOptionMap(optionMap,
                Java2WSDLConstants.TARGET_NAMESPACE_PREFIX_OPTION,
                targetNamespacePrefix);

        // Schema target namespace
        addToOptionMap(optionMap,
                Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_OPTION,
                schemaTargetNamespace);

        // Schema target namespace prefix
        addToOptionMap(optionMap,
                Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_PREFIX_OPTION,
                schemaTargetNamespacePrefix);

        // Service name
        addToOptionMap(optionMap,
                Java2WSDLConstants.SERVICE_NAME_OPTION,
                serviceName);

        // Output file name
        addToOptionMap(optionMap,
                Java2WSDLConstants.OUTPUT_FILENAME_OPTION,
                outputFileName);

        return optionMap;
    }

    /**
     * Utility method to convert a string into a single item string[]
     *
     * @param value
     * @return Returns String[].
     */
    private String[] getStringArray(String value) {
        String[] values = new String[1];
        values[0] = value;
        return values;
    }

    /**
     * Function to put arguments in the option map.
     * This functions skips adding of options that have a null value.
     *
     * @param map    The option map into which the option is to be added
     * @param option The option name
     * @param value  The value of the option
     */
    private void addToOptionMap(Map map, String option, String value) {
        if (value != null) {
            map.put(option,
                    new Java2WSDLCommandLineOption(option, getStringArray(value)));
        }
    }

    public void execute() throws BuildException {
        try {

            Map commandLineOptions = this.fillOptionMap();

            AntClassLoader cl = new AntClassLoader(getClass().getClassLoader(),
                    getProject(),
                    classpath == null ? createClasspath() : classpath,
                    false);

            commandLineOptions.put(Java2WSDLConstants.CLASSPATH_OPTION, new Java2WSDLCommandLineOption(Java2WSDLConstants.CLASSPATH_OPTION, classpath.list()));

            Thread.currentThread().setContextClassLoader(cl);

            if (outputLocation != null) cl.addPathElement(outputLocation);

            new Java2WSDLCodegenEngine(commandLineOptions).generate();

        } catch (Throwable e) {
            throw new BuildException(e);
        }

    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setOutputLocation(String outputLocation) {
        this.outputLocation = outputLocation;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public void setTargetNamespacePrefix(String targetNamespacePrefix) {
        this.targetNamespacePrefix = targetNamespacePrefix;
    }

    public void setSchemaTargetNamespace(String schemaTargetNamespace) {
        this.schemaTargetNamespace = schemaTargetNamespace;
    }

    public void setSchemaTargetNamespacePrefix(String schemaTargetNamespacePrefix) {
        this.schemaTargetNamespacePrefix = schemaTargetNamespacePrefix;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /**
     * Set the optional classpath
     *
     * @param classpath the classpath to use when loading class
     */
    public void setClasspath(Path classpath) {
        createClasspath().append(classpath);
    }

    /**
     * Set the optional classpath
     *
     * @return a path instance to be configured by the Ant core.
     */
    public Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
            classpath = classpath.concatSystemClasspath();
        }
        return classpath.createPath();
    }

    /**
     * Set the reference to an optional classpath
     *
     * @param r the id of the Ant path instance to act as the classpath
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

}

