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
package org.apache.wsdl.impl;

import org.apache.wsdl.Component;
import org.apache.wsdl.WSDLConstants;
import org.w3c.dom.Document;

import java.util.HashMap;

/**
 * @author Chathura Herath
 */
public class ComponentImpl implements WSDLConstants, Component {
    /**
     * Field componentProperties
     */
    protected HashMap componentProperties = new HashMap();

    /**
     * Field documentation
     */
    protected Document documentation = null;

    /**
     * Returns the Documentation Element as a
     * <code>Document</code>.
     *
     * @return documentation
     */
    public Document getDocumentation() {
        return documentation;
    }

    /**
     * Will set the Documentation element for the Component.
     *
     * @param documentation Component Docuemntation
     */
    public void setDocumentation(Document documentation) {
        this.documentation = documentation;
    }

    /**
     * Returns the properties that are specific to this WSDL Component.
     *
     * @return
     */
    public HashMap getComponentProperties() {
        return componentProperties;
    }

    /**
     * Sets the properties of the Component if any.
     *
     * @param properties
     */
    public void setComponentProperties(HashMap properties) {
        this.componentProperties = properties;
    }

    /**
     * Will set the property keyed with the relavent key
     *
     * @param key Key in the map
     * @param obj Object to be put
     */
    public void setComponentProperty(Object key, Object obj) {
        this.componentProperties.put(key, obj);
    }

    /**
     * Gets the component property
     *
     * @param key key for the map search.
     * @return
     */
    public Object getComponentProperty(Object key) {
        return this.componentProperties.get(key);
    }
}
