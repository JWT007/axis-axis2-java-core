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

package org.apache.axis2.deployment;

import org.apache.axis2.AbstractTestCase;
import org.apache.axis2.description.ServiceDescription;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BadServiceXMLTest extends AbstractTestCase {
    /**
     * Constructor.
     */
    public BadServiceXMLTest(String testName) {
        super(testName);
    }

    public void testBadServiceXML() {
        try {
            InputStream in = new FileInputStream(
                    getTestResourceFile("deployment/BadService.xml"));

//            DeploymentParser parser = new DeploymentParser(in, null);
            ServiceDescription axisService = new ServiceDescription();
            ServiceBuilder builder = new ServiceBuilder(in,null,axisService);
            builder.populateService(builder.buildOM());
            fail(
                    "this must fail gracefully with DeploymentException or FileNotFoundException");
        } catch (FileNotFoundException e) {
            fail("File not found ");
        } catch (DeploymentException e) {
            assertTrue(true);
        } catch (XMLStreamException e) {
            fail(" XMLStreamException " + e.getMessage());
        }

    }
}
