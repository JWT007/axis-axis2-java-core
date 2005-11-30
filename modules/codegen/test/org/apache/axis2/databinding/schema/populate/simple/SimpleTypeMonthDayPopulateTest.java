package org.apache.axis2.databinding.schema.populate.simple;
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

public class SimpleTypeMonthDayPopulateTest extends AbstractSimplePopulater{
    private String xmlString[] = {
            "<monthDayParam>--05-12</monthDayParam>",
            "<monthDayParam>--06-30</monthDayParam>",
            "<monthDayParam>--12-31+05:00</monthDayParam>"
    };
    // force others to implement this method
    public void testPopulate() throws Exception {
        process(xmlString[0],"org.soapinterop.monthDayParam");
        process(xmlString[1],"org.soapinterop.monthDayParam");
        process(xmlString[2],"org.soapinterop.monthDayParam");
    }
}
