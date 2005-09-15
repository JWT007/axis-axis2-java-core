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

package org.apache.axis2.om;

import org.apache.axis2.soap.SOAPEnvelope;

import java.io.File;

public class BadInputTest extends OMTestCase {
    File dir = new File(testResourceDir, "badsoap");

    public BadInputTest(String testName) {
        super(testName);
    }


    //done
    public void testEnvelopeMissing() throws Exception {
        try {
            SOAPEnvelope soapEnvelope =
                    (SOAPEnvelope) OMTestUtils.getOMBuilder(
                            new File(dir, "envelopeMissing.xml"))
                    .getDocumentElement();
            OMTestUtils.walkThrough(soapEnvelope);
            fail("this must failed gracefully with OMException or AxisFault");
        } catch (OMException e) {
            return;
        }

    }

    //done
    public void testHeaderBodyWrongOrder() throws Exception {
        try {
            SOAPEnvelope soapEnvelope =
                    (SOAPEnvelope) OMTestUtils.getOMBuilder(
                            new File(dir, "haederBodyWrongOrder.xml"))
                    .getDocumentElement();
            OMTestUtils.walkThrough(soapEnvelope);
            fail("this must failed gracefully with OMException or AxisFault");
        } catch (OMException e) {
            return;
        }
    }

    //done
    //    public void testNotnamespaceQualified() throws Exception {
    //        try {
    //            SOAPEnvelope soapEnvelope =
    //                    (SOAPEnvelope) OMTestUtils.getOMBuilder(new File(dir, "notnamespaceQualified.xml")).getDocumentElement();
    //            OMTestUtils.walkThrough(soapEnvelope);
    //            fail("this must failed gracefully with OMException or AxisFault");
    //        } catch (OMException e) {
    //            return;
    //        } catch (AxisFault e) {
    //            return;
    //        }
    //
    //    }
    //done
    //    public void testBodyNotQualified() throws Exception {
    //        try {
    //            SOAPEnvelope soapEnvelope =
    //                    (SOAPEnvelope) OMTestUtils.getOMBuilder(new File(dir, "bodyNotQualified.xml")).getDocumentElement();
    //            OMTestUtils.walkThrough(soapEnvelope);
    //            fail("this must failed gracefully with OMException or AxisFault");
    //        } catch (OMException e) {
    //            //we are OK!
    //            return;
    //        } catch (AxisFault e) {
    //            //we are OK here too!
    //            return;
    //        }
    //
    //    }

    //done
    public void testTwoBodymessage() throws Exception {
        try {
            SOAPEnvelope soapEnvelope =
                    (SOAPEnvelope) OMTestUtils.getOMBuilder(
                            new File(dir, "twoBodymessage.xml"))
                    .getDocumentElement();
            OMTestUtils.walkThrough(soapEnvelope);
            fail("this must failed gracefully with OMException or AxisFault");
        } catch (OMException e) {
            return;
        }

    }

    //done
    public void testTwoheaders() throws Exception {
        try {
            SOAPEnvelope soapEnvelope =
                    (SOAPEnvelope) OMTestUtils.getOMBuilder(
                            new File(dir, "twoheaders.xml"))
                    .getDocumentElement();
            OMTestUtils.walkThrough(soapEnvelope);
            fail("this must failed gracefully with OMException or AxisFault");
        } catch (OMException e) {
            return;
        }

    }

    //done
    public void testWrongSoapNs() throws Exception {
        try {
            SOAPEnvelope soapEnvelope =
                    (SOAPEnvelope) OMTestUtils.getOMBuilder(
                            new File(dir, "wrongSoapNs.xml"))
                    .getDocumentElement();
            OMTestUtils.walkThrough(soapEnvelope);
            fail("this must failed gracefully with OMException or AxisFault");
        } catch (OMException e) {
            return;
        }

    }

    


}
