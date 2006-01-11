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

package org.apache.wsdl.extensions.impl;

import org.apache.wsdl.extensions.ExtensionConstants;
import org.apache.wsdl.extensions.SOAPAddress;
import org.apache.wsdl.impl.WSDLExtensibilityElementImpl;

import javax.xml.namespace.QName;

/**
 *         This Extensibility Element is extended to handle particularly the
 *         SOAP Adress or the Endpoint URL.
 */
public class SOAPAddressImpl extends WSDLExtensibilityElementImpl implements ExtensionConstants,
        SOAPAddress {
    /**
     * Location of the Endpoint
     */
    private String locationURI;


    public SOAPAddressImpl() {
        this.type = SOAP_11_ADDRESS;
    }

    public SOAPAddressImpl(QName type) {
        this.type = type;
    }


    /**
     * Gets the Endpoint adress
     */
    public String getLocationURI() {
        return locationURI;
    }

    /**
     * Sets the Endpoint Address
     */
    public void setLocationURI(String locationURI) {
        this.locationURI = locationURI;
    }
}
