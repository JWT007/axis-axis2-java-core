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
package org.apache.axis.om;

public class OMEnvelopeTest extends OMTestCase {
    public OMEnvelopeTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetHeader1() {
        SOAPHeader header = soapEnvelope.getHeader();
        assertTrue("Header information retrieved not correct", (header != null && header.getLocalName().equalsIgnoreCase("Header")));
    }

    public void testGetBody1() {
        SOAPBody body = soapEnvelope.getBody();
        assertTrue("Header information retrieved not correct", (body != null && body.getLocalName().equalsIgnoreCase("Body")));
    }

    private SOAPEnvelope getSecondEnvelope() throws Exception {
        return (SOAPEnvelope) OMTestUtils.getOMBuilder(getTestResourceFile("soap/sample1.xml")).getDocumentElement();
    }

    public void testGetHeader2() throws Exception {
        SOAPHeader header = getSecondEnvelope().getHeader();
        assertTrue("Header information retrieved not correct", (header != null && header.getLocalName().equalsIgnoreCase("Header")));
    }

    public void testGetBody2() throws Exception {
        SOAPBody body = getSecondEnvelope().getBody();
        assertTrue("Header information retrieved not correct", (body != null && body.getLocalName().equalsIgnoreCase("Body")));
    }

    public void testDefaultEnveleope() {
        SOAPEnvelope env = OMFactory.newInstance().getDefaultEnvelope();
        assertNotNull(env);
        assertNotNull("Header should not be null", env.getHeader());
        assertNotNull("Body should not be null", env.getBody());

    }

}
