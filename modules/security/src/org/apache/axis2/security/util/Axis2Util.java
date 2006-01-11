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

package org.apache.axis2.security.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axis2.om.DOOMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.impl.dom.DocumentImpl;
import org.apache.axis2.om.impl.llom.builder.StAXOMBuilder;
import org.apache.axis2.security.handler.WSSHandlerConstants;
import org.apache.axis2.soap.SOAP11Constants;
import org.apache.axis2.soap.SOAP12Constants;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.axis2.soap.impl.llom.builder.StAXSOAPModelBuilder;
import org.apache.ws.security.WSSecurityException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class for the Axis2-WSS4J Module
 */
public class Axis2Util {

	/**
	 * Creates a DOM Document using the SOAP Envelope.
	 * @param env An org.apache.axis2.soap.SOAPEnvelope instance 
	 * @return Returns the DOM Document of the given SOAP Envelope.
	 * @throws Exception
	 */
	public static Document getDocumentFromSOAPEnvelope(SOAPEnvelope env)
			throws WSSecurityException {
		try {
			env.build();
			
			//Check the namespace and find SOAP version and factory
			String nsURI = null;
			SOAPFactory factory;
			if(env.getNamespace().getName().equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
				nsURI = SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI;
				factory = DOOMAbstractFactory.getSOAP11Factory();
			} else {
				nsURI = SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI;
				factory = DOOMAbstractFactory.getSOAP11Factory();
			}
			
			StAXSOAPModelBuilder stAXSOAPModelBuilder = new StAXSOAPModelBuilder(env.getXMLStreamReader(),factory, nsURI);
			SOAPEnvelope envelope = (stAXSOAPModelBuilder).getSOAPEnvelope();
			envelope.build();
			
			Element envElem = (Element)envelope;
			return envElem.getOwnerDocument();
			
		} catch (Exception e) {
			throw new WSSecurityException(
					"Error in converting SOAP Envelope to Document", e);
		}
	}

	public static SOAPEnvelope getSOAPEnvelopeFromDOOMDocument(DocumentImpl doc) {
        OMElement docElem = (OMElement)doc.getDocumentElement();
        StAXSOAPModelBuilder stAXSOAPModelBuilder = new StAXSOAPModelBuilder(docElem.getXMLStreamReader(), null);
        return stAXSOAPModelBuilder.getSOAPEnvelope();
	}
	
	
	/**
	 * Provides the appropriate key to pickup config params from the message context.
	 * This is acutally used when the outflow handler (WSDoAllSender)
	 * is repeated n number of times.
	 * @param originalKey The default key
	 * @param inHandler Whether the handler is the inflow handler or not
	 * @param repetition The current repetition number
	 * @return Returns the key to be used internally in the security module to pick
	 * up the config params.
	 */
	public static String getKey(String originalKey, boolean inHandler, int repetition) {
		
		if(repetition > 0 && !inHandler && 
				!originalKey.equals(WSSHandlerConstants.OUTFLOW_SECURITY)&&	
				!originalKey.equals(WSSHandlerConstants.SENDER_REPEAT_COUNT)) {
			
				return originalKey + repetition;
		}
		return originalKey;
	}
	
	/**
	 * Converts a given DOM Element to an OMElement.
	 * @param element
	 * @return Returns OMElement.
	 * @throws Exception
	 */
	public static OMElement toOM(Element element) throws Exception {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			XMLUtils.outputDOM(element, os, true);
			
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			XMLStreamReader reader = XMLInputFactory.newInstance()
					.createXMLStreamReader(is);

			StAXOMBuilder builder = new StAXOMBuilder(reader);
			builder.setCache(true);

			return builder.getDocumentElement();
	}
	

	/**
	 * Converts a given OMElement to a DOM Element.
	 * @param element
	 * @return Returns Element.
	 * @throws Exception
	 */
	public static Element toDOM(OMElement element) throws Exception {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			element.serialize(baos);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			return factory.newDocumentBuilder().parse(bais).getDocumentElement();
	}
}
