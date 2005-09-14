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
package org.apache.axis2.addressing;

import javax.xml.namespace.QName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class AnyContentType
 */
public class AnyContentType implements Serializable {
    /**
     * Field anyContentTypeName
     */
    private String anyContentTypeName;

    /**
     * Field valueHolder
     */
    private HashMap valueHolder;

    /**
     * Constructor AnyContentType
     */
    public AnyContentType() {
        valueHolder = new HashMap(5);
    }

    /**
     * Method addReferenceValue
     *
     * @param name
     * @param value
     */
    public void addReferenceValue(QName name, String value) {
        valueHolder.put(name, value);
    }

    /**
     * Method getReferenceValue
     *
     * @param name
     * @return
     */
    public String getReferenceValue(QName name) {
        return (String) valueHolder.get(name);
    }

    public Iterator getKeys() {
        return valueHolder.keySet().iterator();
    }
}
