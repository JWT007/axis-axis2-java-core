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
package org.apache.axis.description;

import org.apache.axis.engine.AxisFault;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>This holds the information shown in the global scope. The information are all
 * that not goes in to the Transport or a Service. This has two types of Info. </p>
 * <ol>
 * <li>parameters<li>
 * <li>ordered phases<li>
 * <li>names of modules that are ref by the server.xml file, real modues are in the
 * Registry.<li>
 * <ol>
 * <p>Note: handlers in the server.xml file are not suported for M1, only way to put a
 * global handler is via a modules</p>
 */
public class AxisGlobal implements ParameterInclude, PhasesInclude {
    /**
     * Field paramInclude
     */
    protected final ParameterInclude paramInclude;

    /**
     * Field phasesInclude
     */
    protected final PhasesInclude phasesInclude;

    /**
     * Field modules
     */
    protected final List modules;

    // TODO provide a way to store name (name attribute value server.xml)

    /**
     * Constructor AxisGlobal
     */
    public AxisGlobal() {
        paramInclude = new ParameterIncludeImpl();
        phasesInclude = new PhasesIncludeImpl();
        modules = new ArrayList();
    }

    /**
     * Method addModule
     *
     * @param moduleref
     */
    public void addModule(QName moduleref) {
        modules.add(moduleref);
    }

    /**
     * Method getModules
     *
     * @return
     */
    public Collection getModules() {
        return modules;
    }

    /**
     * Method getParameter
     *
     * @param name
     * @return
     */
    public Parameter getParameter(String name) {
        return paramInclude.getParameter(name);
    }

    /**
     * Method addParameter
     *
     * @param param
     */
    public void addParameter(Parameter param) {
        paramInclude.addParameter(param);
    }

    /**
     * @param flow
     * @return
     * @throws AxisFault
     */
    public ArrayList getPhases(int flow) throws AxisFault {
        return phasesInclude.getPhases(flow);
    }

    /**
     * @param phases
     * @param flow
     * @throws AxisFault
     */
    public void setPhases(ArrayList phases, int flow) throws AxisFault {
        phasesInclude.setPhases(phases, flow);
    }
}
