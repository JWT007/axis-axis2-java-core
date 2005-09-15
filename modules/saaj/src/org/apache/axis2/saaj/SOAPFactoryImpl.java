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
package org.apache.axis2.saaj;

import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;

import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 * @author shaas02
 *         <p/>
 *
 */
public class SOAPFactoryImpl extends javax.xml.soap.SOAPFactory {

    /**
     * @see javax.xml.soap.SOAPFactory#createElement(javax.xml.soap.Name)
     */
    public SOAPElement createElement(Name name) throws SOAPException {
    	String localName = name.getLocalName();
    	String prefix = name.getPrefix();
    	String uri = name.getURI();
    	OMElement newOMElement = OMAbstractFactory.getOMFactory()
        .createOMElement(localName, uri, prefix);
    	return new SOAPElementImpl(newOMElement);
    }

    /**
     * @see javax.xml.soap.SOAPFactory#createElement(java.lang.String)
     */
    public SOAPElement createElement(String localName) throws SOAPException {
    	OMNamespace ns = OMAbstractFactory.getOMFactory()
    	.createOMNamespace(null, null);
    	OMElement newOMElement = OMAbstractFactory.getOMFactory()
        .createOMElement(localName, ns);
    	return new SOAPElementImpl(newOMElement);
    }

    /**
     * @see javax.xml.soap.SOAPFactory#createElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public SOAPElement createElement(String localName,
                                     String prefix,
                                     String uri)
            throws SOAPException {
        OMElement newOMElement = OMAbstractFactory.getOMFactory()
                .createOMElement(localName, uri, prefix);
        return new SOAPElementImpl(newOMElement);
    }

    /**
     * @see javax.xml.soap.SOAPFactory#createDetail()
     */
    public Detail createDetail() throws SOAPException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see javax.xml.soap.SOAPFactory#createName(java.lang.String, java.lang.String, java.lang.String)
     */
    public Name createName(String localName, String prefix, String uri)
            throws SOAPException {
        return new PrefixedQName(uri, localName, prefix);
    }

    /**
     * @see javax.xml.soap.SOAPFactory#createName(java.lang.String)
     */
    public Name createName(String localName) throws SOAPException {
    	return new PrefixedQName(null, localName, null);
    }

}
