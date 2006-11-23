/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * Copyright 2006 International Business Machines Corp.
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

package org.apache.axis2.jaxws.description;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.axis2.description.AxisOperation;
/**
 * An OperationDescripton corresponds to a method on an SEI.  That SEI could be explicit
 * (i.e. WebService.endpointInterface=sei.class) or implicit (i.e. public methods on the service implementation
 * are the contract and thus the implicit SEI).  Note that while OperationDescriptions are created on both the client
 * and service side, implicit SEIs will only occur on the service side.
 * 
 * OperationDescriptons contain information that is only relevent for and SEI-based service, i.e. one that is invoked via specific
 * methods.  This class does not exist for Provider-based services (i.e. those that specify WebServiceProvider)
 * 
 * <pre>
 * <b>OperationDescription details</b>
 * 
 *     CORRESPONDS TO:      A single operation on an SEI (on both Client and Server)      
 *         
 *     AXIS2 DELEGATE:      AxisOperation
 *     
 *     CHILDREN:            0..n ParameterDescription
 *                          0..n FaultDescription (Note: Not fully implemented)
 *     
 *     ANNOTATIONS:
 *         WebMethod [181]
 *         SOAPBinding [181]
 *         Oneway [181]
 *         WebResult [181]
 *         RequestWrapper [224]
 *         ResponseWrapper [224]
 *     
 *     WSDL ELEMENTS:
 *         operation
 *         
 *  </pre>       
 */
public interface OperationDescription {
    public EndpointInterfaceDescription getEndpointInterfaceDescription();
    public FaultDescription[] getFaultDescriptions();
    public FaultDescription resolveFaultByExceptionName(String exceptionClassName);
    public FaultDescription resolveFaultByFaultBeanName(String faultBeanName);
    public ParameterDescription getParameterDescription(int parameterNumber);
    public ParameterDescription getParameterDescription(String parameterName);
    public ParameterDescription[] getParameterDescriptions();
    
    public abstract AxisOperation getAxisOperation();
    
    public String getJavaMethodName();
    public String[] getJavaParameters();
    public Method getSEIMethod();
    
    public QName getName();
    public String getOperationName();
    public String getAction();
    public boolean isOneWay();
    public boolean isExcluded();
    public boolean isOperationReturningResult();

    public String getResultName();
    public String getResultTargetNamespace();
    public String getResultPartName();
    public boolean isResultHeader();
    
    public String getRequestWrapperClassName();
    public String getRequestWrapperTargetNamespace();
    public String getRequestWrapperLocalName();

    public String getResponseWrapperClassName();
    public String getResponseWrapperTargetNamespace();
    public String getResponseWrapperLocalName();
    
    public String[] getParamNames();
    
    // TODO: These should return Enums defined on this interface, not from the Annotation
    public javax.jws.soap.SOAPBinding.ParameterStyle getSoapBindingParameterStyle();
    public javax.jws.soap.SOAPBinding.Style getSoapBindingStyle();
    public javax.jws.soap.SOAPBinding.Use getSoapBindingUse();

}