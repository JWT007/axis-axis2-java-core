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

import java.util.HashMap;

/**
 * Class ParameterIncludeImpl
 */
public class ParameterIncludeImpl implements ParameterInclude {
    /**
     * Field parmeters
     */
    protected final HashMap parmeters;

    /**
     * Constructor ParameterIncludeImpl
     */
    public ParameterIncludeImpl() {
        parmeters = new HashMap();
    }

    /**
     * Method addParameter
     *
     * @param param
     */
    public void addParameter(Parameter param) {
        if (param != null) {
            parmeters.put(param.getName(), param);
        }
    }

    /**
         * Method getParameter
         *
         * @param name
         * @return
         */
    public Parameter getParameter(String name) {
        return (Parameter) parmeters.get(name);
    }
}
