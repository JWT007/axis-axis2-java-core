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
package org.apache.wsdl;

import org.w3c.dom.Element;

/**
 * @author chathura@opensource.lk
 */
public interface ExtensionElement {
    /**
     * Method getElement
     *
     * @return
     */
    public Element getElement();

    /**
     * Method setElement
     *
     * @param element
     */
    public void setElement(Element element);

    /**
     * Method isRequired
     *
     * @return
     */
    public boolean isRequired();

    /**
     * Method setRequired
     *
     * @param required
     */
    public void setRequired(boolean required);
}
