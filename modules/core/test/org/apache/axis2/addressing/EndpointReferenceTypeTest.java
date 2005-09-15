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

import junit.framework.TestCase;

import javax.xml.namespace.QName;


public class EndpointReferenceTypeTest extends TestCase {

    EndpointReference endpointReference;
    private String address = "htttp://wwww.openource.lk/~chinthaka";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(EndpointReferenceTypeTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        endpointReference = new EndpointReference(address);
    }

    public void testGetAndSetAddress() {
        assertEquals("Address not set properly in the constructor",
                address,
                endpointReference.getAddress());

        String newAddress = "http://www.axis2.com";
        endpointReference.setAddress(newAddress);
        assertEquals("Address not set properly in the setter method",
                newAddress,
                endpointReference.getAddress());
    }

    public void testGetAndSetPortType() {
        QName portType = new QName("www.someport.com", "port");
        endpointReference.setInterfaceName(portType);
        assertEquals("PortType not set/get properly",
                portType,
                endpointReference.getInterfaceName());
    }

    public void testGetAndSetReferenceProperties() {
        AnyContentType anyContentType = new AnyContentType();
        for (int i = 0; i < 10; i++) {
            anyContentType.addReferenceValue(
                    new QName("http://www.opensouce.lk/" + i, "" + i),
                    "value " + i * 100);
        }
        endpointReference.setReferenceProperties(anyContentType);

        AnyContentType retrievedAnyContentType = endpointReference.getReferenceProperties();
        for (int i = 0; i < 10; i++) {
            String value = retrievedAnyContentType.getReferenceValue(
                    new QName("http://www.opensouce.lk/" + i, "" + i));
            assertEquals(
                    "Input value differs from what is taken out from AnyContentType",
                    value,
                    "value " + i * 100);
        }

    }

    public void testGetAndSetReferenceParameters() {
        AnyContentType anyContentType = new AnyContentType();
        for (int i = 0; i < 10; i++) {
            anyContentType.addReferenceValue(
                    new QName("http://www.opensouce.lk/" + i, "" + i),
                    "value " + i * 50);
        }
        endpointReference.setReferenceParameters(anyContentType);

        AnyContentType retrievedAnyContentType = endpointReference.getReferenceParameters();
        for (int i = 0; i < 10; i++) {
            String value = retrievedAnyContentType.getReferenceValue(
                    new QName("http://www.opensouce.lk/" + i, "" + i));
            assertEquals(
                    "Input value differs from what is taken out from AnyContentType",
                    value,
                    "value " + i * 50);
        }
    }

    public void testGetAndSetServiceName() {
        ServiceName serviceName = new ServiceName(
                new QName("www.someservicename.org", "service"));
        endpointReference.setServiceName(serviceName);
        ServiceName retrievedServiceName = endpointReference.getServiceName();
        assertEquals("ServiceName name has not been get/set properly",
                serviceName.getName(),
                retrievedServiceName.getName());

        serviceName =
                new ServiceName(
                        new QName("www.someservicename.org", "service"),
                        "portName");
        endpointReference.setServiceName(serviceName);
        retrievedServiceName = endpointReference.getServiceName();
        assertEquals("ServiceName name has not been get/set properly",
                serviceName.getName(),
                retrievedServiceName.getName());
        assertEquals("ServiceName portName has not been get/set properly",
                serviceName.getEndpointName(),
                retrievedServiceName.getEndpointName());
    }

}
