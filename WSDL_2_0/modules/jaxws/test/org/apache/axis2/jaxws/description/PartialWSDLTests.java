/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis2.jaxws.description;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;
import javax.wsdl.Definition;

import org.apache.axis2.jaxws.description.builder.DescriptionBuilderComposite;
import org.apache.axis2.jaxws.description.builder.MethodDescriptionComposite;
import org.apache.axis2.jaxws.description.builder.ParameterDescriptionComposite;
import org.apache.axis2.jaxws.description.builder.WebServiceAnnot;

import junit.framework.TestCase;

/**
 * 
 */
public class PartialWSDLTests extends TestCase {
    
    /**
     * Tests the binding, service, and port not specified in the WSDL.
     * 
     * This test is based on the FVT test AddNumbersImplPartial1
     *
     */
    public void testPartialWSDL1() {
        String wsdlRelativeLocation = "test-resources/wsdl/";
        String wsdlFileName = "PartialWSDL1.wsdl"; 
        
        String targetNamespace = "http://serverPartial1.checkexception.webfault.annotations/";
        String wsdlLocation = wsdlRelativeLocation + wsdlFileName;

        // Build up a DBC, including the WSDL Definition and the annotation information for the impl class.
        DescriptionBuilderComposite dbc = new DescriptionBuilderComposite();
        
        URL wsdlURL = DescriptionTestUtils.getWSDLURL(wsdlFileName);
        Definition wsdlDefn = DescriptionTestUtils.createWSDLDefinition(wsdlURL);
        assertNotNull(wsdlDefn);
        
        WebServiceAnnot webServiceAnnot = WebServiceAnnot.createWebServiceAnnotImpl();
        assertNotNull(webServiceAnnot);
        webServiceAnnot.setWsdlLocation(wsdlLocation);
        webServiceAnnot.setTargetNamespace(targetNamespace);
        
        MethodDescriptionComposite mdc = new MethodDescriptionComposite();
        mdc.setMethodName("addTwoNumbers");
        mdc.setReturnType("int");
        
        ParameterDescriptionComposite pdc1 = new ParameterDescriptionComposite();
        pdc1.setParameterType("int");
        ParameterDescriptionComposite pdc2 = new ParameterDescriptionComposite();
        pdc1.setParameterType("int");
        
        mdc.addParameterDescriptionComposite(pdc1);
        mdc.addParameterDescriptionComposite(pdc2);
        
        dbc.addMethodDescriptionComposite(mdc);
        dbc.setWebServiceAnnot(webServiceAnnot);
        dbc.setClassName(AddNumbersImplPartial1.class.getName());
        dbc.setWsdlDefinition(wsdlDefn);
        dbc.setwsdlURL(wsdlURL);
        
        HashMap<String, DescriptionBuilderComposite> dbcMap = new HashMap<String, DescriptionBuilderComposite>();
        dbcMap.put(AddNumbersImplPartial1.class.getName(), dbc);
        
        List<ServiceDescription> serviceDescList = DescriptionFactory.createServiceDescriptionFromDBCMap(dbcMap);
        assertEquals(1, serviceDescList.size());
        ServiceDescription sd = serviceDescList.get(0);
        assertNotNull(sd);

        EndpointDescription[] edArray = sd.getEndpointDescriptions();
        assertNotNull(edArray);
        assertEquals(1, edArray.length);
        EndpointDescription ed = edArray[0];
        assertNotNull(ed);
        
        EndpointInterfaceDescription eid = ed.getEndpointInterfaceDescription();
        assertNotNull(eid);
        
        OperationDescription[] odArray = eid.getOperations();
        assertNotNull(odArray);
        assertEquals(1, odArray.length);
        OperationDescription od = odArray[0];
    }
    /**
     * Tests the binding, service, and port not specified in the WSDL.
     * 
     * This test is based on the FVT test AddNumbersImplPartial2
     *
     */
    public void testPartialWSDL2() {
        String wsdlRelativeLocation = "test-resources/wsdl/";
        String wsdlFileName = "PartialWSDL2.wsdl"; 
        
        String targetNamespace = "http://serverPartial1.checkexception.webfault.annotations/";
        String wsdlLocation = wsdlRelativeLocation + wsdlFileName;

        // Build up a DBC, including the WSDL Definition and the annotation information for the impl class.
        DescriptionBuilderComposite dbc = new DescriptionBuilderComposite();
        
        URL wsdlURL = DescriptionTestUtils.getWSDLURL(wsdlFileName);
        Definition wsdlDefn = DescriptionTestUtils.createWSDLDefinition(wsdlURL);
        assertNotNull(wsdlDefn);
        
        WebServiceAnnot webServiceAnnot = WebServiceAnnot.createWebServiceAnnotImpl();
        assertNotNull(webServiceAnnot);
        webServiceAnnot.setWsdlLocation(wsdlLocation);
        webServiceAnnot.setTargetNamespace(targetNamespace);
        
        MethodDescriptionComposite mdc = new MethodDescriptionComposite();
        mdc.setMethodName("addTwoNumbers");
        mdc.setReturnType("int");
        
        ParameterDescriptionComposite pdc1 = new ParameterDescriptionComposite();
        pdc1.setParameterType("int");
        ParameterDescriptionComposite pdc2 = new ParameterDescriptionComposite();
        pdc1.setParameterType("int");
        
        mdc.addParameterDescriptionComposite(pdc1);
        mdc.addParameterDescriptionComposite(pdc2);
        
        dbc.addMethodDescriptionComposite(mdc);
        dbc.setWebServiceAnnot(webServiceAnnot);
        dbc.setClassName(AddNumbersImplPartial1.class.getName());
        dbc.setWsdlDefinition(wsdlDefn);
        dbc.setwsdlURL(wsdlURL);
        
        HashMap<String, DescriptionBuilderComposite> dbcMap = new HashMap<String, DescriptionBuilderComposite>();
        dbcMap.put(AddNumbersImplPartial1.class.getName(), dbc);
        
        List<ServiceDescription> serviceDescList = DescriptionFactory.createServiceDescriptionFromDBCMap(dbcMap);
        assertEquals(1, serviceDescList.size());
        ServiceDescription sd = serviceDescList.get(0);
        assertNotNull(sd);

        EndpointDescription[] edArray = sd.getEndpointDescriptions();
        assertNotNull(edArray);
        assertEquals(1, edArray.length);
        EndpointDescription ed = edArray[0];
        assertNotNull(ed);
        
        EndpointInterfaceDescription eid = ed.getEndpointInterfaceDescription();
        assertNotNull(eid);
        
        OperationDescription[] odArray = eid.getOperations();
        assertNotNull(odArray);
        assertEquals(1, odArray.length);
        OperationDescription od = odArray[0];
    }
}

@WebService(wsdlLocation = "test-resources/wsdl/PartialWSDL2.wsdl",
            targetNamespace = "http://serverPartial1.checkexception.webfault.annotations/")
class AddNumbersImplPartial1 {
    public int addTwoNumbers(int number1, int number2) {
        return number1 + number2;
    }
}
