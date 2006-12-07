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
package org.apache.axis2.jaxws;

import java.util.Hashtable;
import java.util.Map;

import javax.xml.ws.Binding;

import org.apache.axis2.jaxws.binding.SOAPBinding;
import org.apache.axis2.jaxws.client.PropertyValidator;
import org.apache.axis2.jaxws.description.EndpointDescription;
import org.apache.axis2.jaxws.i18n.Messages;
import org.apache.axis2.jaxws.spi.ServiceDelegate;
import org.apache.axis2.transport.http.HTTPConstants;

public class BindingProvider implements org.apache.axis2.jaxws.spi.BindingProvider {

	protected Map<String, Object> requestContext;
    protected Map<String, Object> responseContext;
    protected EndpointDescription endpointDesc;
    protected ServiceDelegate serviceDelegate;
    
    private Binding binding;  // force subclasses to use the lazy getter
    
    public BindingProvider(ServiceDelegate svcDelegate, EndpointDescription epDesc) {
        endpointDesc = epDesc;
        serviceDelegate = svcDelegate;

        initialize();
    }

    /*
     * Initialize any objects needed by the BindingProvider
     */
    private void initialize() {
        requestContext = new ValidatingClientContext();
        responseContext = new ValidatingClientContext();
        
        // Setting standard property defaults for the request context
        requestContext.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, new Boolean(false));
        requestContext.put(BindingProvider.SOAPACTION_USE_PROPERTY, new Boolean(true));
        
        // Set the endpoint address
        String endpointAddress = endpointDesc.getEndpointAddress();
        if (endpointAddress != null) {
            requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);            
        }
    }
    
    public ServiceDelegate getServiceDelegate() {
        return serviceDelegate;
    }
    
    public EndpointDescription getEndpointDescription() {
        return endpointDesc;
    }
    
    public Binding getBinding() {
        
        // TODO support HTTP binding when available
        
        // The default Binding is the SOAPBinding
        if (binding == null) {
            String bindingType = endpointDesc.getBindingType();
            if (bindingType == null) { // we must be on the client
                bindingType = endpointDesc.getClientBindingID();
            }
            binding = new SOAPBinding(bindingType);
        }
        return binding;
    }

    public Map<String, Object> getRequestContext() {
        return requestContext;
    }

    public Map<String, Object> getResponseContext() {
        return responseContext;
    }
    
    /*
     * Ensure that the next request context contains the session value returned
     * from previous request
     */
    protected void setupSessionContext(Map<String, Object> properties){
        String sessionKey = null;
        String sessionValue = null;
        
        if(properties == null){
            return;
        }

        if(properties.containsKey(HTTPConstants.HEADER_LOCATION)){
            sessionKey = HTTPConstants.HEADER_LOCATION;
            sessionValue = (String)properties.get(sessionKey);
            if(sessionValue != null && !"".equals(sessionValue)){
                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,sessionValue);
            }
        }
        else if(properties.containsKey(HTTPConstants.HEADER_COOKIE)){
            sessionKey = HTTPConstants.HEADER_COOKIE;
            sessionValue = (String)properties.get(sessionKey);
            if(sessionValue != null && !"".equals(sessionValue)){
                requestContext.put(HTTPConstants.COOKIE_STRING,sessionValue);
            }
        }
        else if(properties.containsKey(HTTPConstants.HEADER_COOKIE2)){
            sessionKey = HTTPConstants.HEADER_COOKIE2;
            sessionValue = (String)properties.get(sessionKey);
            if(sessionValue != null && !"".equals(sessionValue)){
                requestContext.put(HTTPConstants.COOKIE_STRING,sessionValue);
            }
        }
        else {
            throw ExceptionFactory.makeWebServiceException(Messages.getMessage("NoMaintainSessionProperty"));
        }

        if(sessionValue == null){
            throw ExceptionFactory.makeWebServiceException(Messages.getMessage("NullValueForMaintainSessionProperty",sessionKey));
        }
    }
    
    /**
     * Returns a boolean value representing whether or not a SOAPAction header
     * should be sent with the request.
     */
    protected boolean useSoapAction() {
        //TODO: Add some bit of validation for this property so that we know
        // it is actually a Boolean and not a String.
        Boolean use = (Boolean) requestContext.get(BindingProvider.SOAPACTION_USE_PROPERTY); 
        if (use != null) {
            if (use.booleanValue()) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            // If the value is not set, then just default to sending a SOAPAction
            return true;
        }
    }
    
    /*
     * An inner class used to validate properties as they are set by the client.
     */
    class ValidatingClientContext extends Hashtable<String, Object> {

        @Override
        public synchronized Object put(String key, Object value) {
            if (PropertyValidator.validate(key, value)) {
                return super.put(key, value);
            }
            else {
                throw ExceptionFactory.makeWebServiceException(
                        Messages.getMessage("invalidPropValue", key, value.getClass().getName(), 
                                PropertyValidator.getExpectedValue(key).getName()));
            }
        }
    }
}
