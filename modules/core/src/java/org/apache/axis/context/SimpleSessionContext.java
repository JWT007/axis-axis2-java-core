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
package org.apache.axis.context;

import java.util.HashMap;

/**
 * Class SimpleSessionContext
 */
public class SimpleSessionContext implements SessionContext {
    /**
     * Field map
     */
    private HashMap map = new HashMap();

    /**
     * Method get
     *
     * @param key
     * @return
     */
    public Object get(Object key) {
        return map.get(key);
    }

    /**
     * Method put
     *
     * @param key
     * @param obj
     */
    public void put(Object key, Object obj) {
        map.put(key, obj);
    }
}
