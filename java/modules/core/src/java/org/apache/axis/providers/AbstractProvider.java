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
package org.apache.axis.providers;

import org.apache.axis.context.MessageContext;
import org.apache.axis.engine.Provider;

import javax.xml.namespace.QName;

/**
 * This is the Absract provider. It is just a another handler. the
 * protected abstract methods are only for the sake of breaking down the logic
 */
public abstract class AbstractProvider implements Provider {
    /**
     * Field name
     */
    protected QName name;

    /**
     * Method getName
     *
     * @return
     */
    public QName getName() {
        return name;
    }

    /**
     * Method setName
     *
     * @param name
     */
    public void setName(QName name) {
        this.name = name;
    }

    /**
     * Method revoke
     *
     * @param msgContext
     */
    public void revoke(MessageContext msgContext) {
    }
}
