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
package org.apache.axis2.soap.impl.dom.factory;

import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.om.OMXMLParserWrapper;
import org.apache.axis2.om.impl.dom.DocumentImpl;
import org.apache.axis2.om.impl.dom.factory.OMDOMFactory;
import org.apache.axis2.soap.SOAPBody;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.axis2.soap.SOAPFault;
import org.apache.axis2.soap.SOAPFaultCode;
import org.apache.axis2.soap.SOAPFaultDetail;
import org.apache.axis2.soap.SOAPFaultNode;
import org.apache.axis2.soap.SOAPFaultReason;
import org.apache.axis2.soap.SOAPFaultRole;
import org.apache.axis2.soap.SOAPFaultSubCode;
import org.apache.axis2.soap.SOAPFaultText;
import org.apache.axis2.soap.SOAPFaultValue;
import org.apache.axis2.soap.SOAPHeader;
import org.apache.axis2.soap.SOAPHeaderBlock;
import org.apache.axis2.soap.SOAPMessage;
import org.apache.axis2.soap.SOAPProcessingException;
import org.apache.axis2.soap.impl.dom.SOAPEnvelopeImpl;
import org.apache.axis2.soap.impl.dom.SOAPMessageImpl;

public class DOMSOAPFactory extends OMDOMFactory implements SOAPFactory {

	public DOMSOAPFactory() {}
	
	public DOMSOAPFactory(DocumentImpl doc) {
		super(doc);
	}
	
	public SOAPMessage createSOAPMessage(OMXMLParserWrapper builder) {
		SOAPMessageImpl messageImpl = new SOAPMessageImpl(builder);
		this.document = messageImpl;
		return messageImpl;
	}

	public SOAPMessage createSOAPMessage(SOAPEnvelope envelope, OMXMLParserWrapper parserWrapper) {
		SOAPMessageImpl messageImpl = new SOAPMessageImpl(envelope, parserWrapper);
		this.document = messageImpl;
		return messageImpl;
	}

	public SOAPEnvelope createSOAPEnvelope(OMXMLParserWrapper builder) {
		return new SOAPEnvelopeImpl((DocumentImpl)this.createOMDocument(), builder, this);
	}

	public SOAPEnvelope createSOAPEnvelope() throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPHeader createSOAPHeader(SOAPEnvelope envelope) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPHeader createSOAPHeader(SOAPEnvelope envelope, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns, SOAPHeader parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns, SOAPHeader parent, OMXMLParserWrapper builder) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFault createSOAPFault(SOAPBody parent, Exception e) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFault createSOAPFault(SOAPBody parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFault createSOAPFault(SOAPBody parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPBody createSOAPBody(SOAPEnvelope envelope) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPBody createSOAPBody(SOAPEnvelope envelope, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultCode createSOAPFaultCode(SOAPFault parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultCode createSOAPFaultCode(SOAPFault parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultReason createSOAPFaultReason(SOAPFault parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultReason createSOAPFaultReason(SOAPFault parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultNode createSOAPFaultNode(SOAPFault parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultNode createSOAPFaultNode(SOAPFault parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultRole createSOAPFaultRole(SOAPFault parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultRole createSOAPFaultRole(SOAPFault parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent) throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent, OMXMLParserWrapper builder) {
		throw new UnsupportedOperationException();
	}

	public SOAPEnvelope getDefaultEnvelope() throws SOAPProcessingException {
		throw new UnsupportedOperationException();
	}

	public SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException {
        SOAPEnvelope defaultEnvelope = getDefaultEnvelope();
        SOAPFault fault = createSOAPFault(defaultEnvelope.getBody());

        SOAPFaultCode faultCode = createSOAPFaultCode(fault);
        createSOAPFaultValue(faultCode);

        SOAPFaultReason reason = createSOAPFaultReason(fault);
        createSOAPFaultText(reason);

        createSOAPFaultNode(fault);
        createSOAPFaultRole(fault);
        createSOAPFaultDetail(fault);

        return defaultEnvelope;
	}

}
