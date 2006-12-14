package org.apache.axis2.util;

import junit.framework.TestCase;

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
 */

public class SchemaUtilTest extends TestCase {

    String httpLocation = "?test=1&last={LastName}&firstNamePart={FirstName}";

    public void testCreateHttpLocationParameterMap() {
        Map httpLocationParameterMap = SchemaUtil.createHttpLocationParameterMap(httpLocation, "&");
        assertEquals("firstNamePart", httpLocationParameterMap.get("FirstName"));
        assertEquals("last", httpLocationParameterMap.get("LastName"));
        assertTrue(httpLocationParameterMap.size() == 2);
    }
}
