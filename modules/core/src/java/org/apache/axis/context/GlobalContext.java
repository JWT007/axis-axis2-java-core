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

import org.apache.axis.engine.EngineRegistry;

import java.util.HashMap;

/**
 * All the engine componets are stateless accross the executions and all the states should be kept in the
 * Contexts, there are three context Global, Session and Message
 */
public class GlobalContext {
    /**
     * Field registry
     */
    private EngineRegistry registry;

    /**
     * Field map
     */
    private final HashMap map = new HashMap(10);

    /**
     * Constructor GlobalContext
     *
     * @param er
     */
    public GlobalContext(EngineRegistry er) {
        this.registry = er;
    }

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
    public void put(String key, Object obj) {
        map.put(key, obj);
    }

    /**
     * @return
     */
    public EngineRegistry getRegistry() {
        return registry;
    }

    /**
     * @param registry
     */
    public void setRegistry(EngineRegistry registry) {
        this.registry = registry;
    }
}
