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
package org.apache.axis2.jaxws.message.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMConstants;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants.Configuration;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.jaxws.ExceptionFactory;
import org.apache.axis2.jaxws.message.Attachment;
import org.apache.axis2.jaxws.message.Message;
import org.apache.axis2.jaxws.message.MessageException;
import org.apache.axis2.jaxws.message.attachments.AttachmentUtils;
import org.apache.axis2.jaxws.message.factory.MessageFactory;
import org.apache.axis2.jaxws.registry.FactoryRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;


/**
 * Miscellaneous Utilities that may be useful inside and outside the Message
 * subcomponent.
 */
public class MessageUtils {
    
    private static final Log log = LogFactory.getLog(MessageUtils.class);
    /**
     * Get an axiom SOAPFactory for the specified element
     * @param e OMElement
     * @return SOAPFactory
     */
    public static SOAPFactory getSOAPFactory(OMElement e) {
        // Getting a factory from a SOAPEnvelope is not straight-forward.
        // Please change this code if an easier mechanism is discovered.
        
        OMXMLParserWrapper builder = e.getBuilder();
        if (builder instanceof StAXBuilder) {
            StAXBuilder staxBuilder = (StAXBuilder) builder;
            OMDocument document = staxBuilder.getDocument();
            if (document != null) {
                OMFactory factory = document.getOMFactory();
                if (factory instanceof SOAPFactory) {
                    return (SOAPFactory) factory;
                }
            }
        }
        // Flow to here indicates that the envelope does not have
        // an accessible factory.  Create a new factory based on the 
        // protocol.
        
        while (e != null && !(e instanceof SOAPEnvelope)) {
            e = (OMElement) e.getParent();
        }
        if (e instanceof SOAPEnvelope) {
            if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.
                    equals(e.getNamespace().getNamespaceURI())) {
                return OMAbstractFactory.getSOAP11Factory();
            } else {
                return OMAbstractFactory.getSOAP12Factory();
            }
        }
        return null;
    }
    
    /**
     * Create a JAXWS Message Attachment object from an SAAJ AttachmentPart
     * @param ap AttachmentPart
     * @param message Message
     * @return Attachment
     * @throws SOAPException
     */
    public static Attachment createAttachment(AttachmentPart ap, Message message) throws SOAPException {
        // Create the attachment
        Attachment a = message.createAttachment(ap.getDataHandler(), ap.getContentId());
        
        // Copy over all of the headers
        MimeHeaders mhs = new MimeHeaders();
        Iterator it =ap.getAllMimeHeaders();
        while (it.hasNext()) {
            MimeHeader mh = (MimeHeader) it.next();
            mhs.addHeader(mh.getName(), mh.getValue());
        }
        a.setMimeHeaders(mhs);
        return a;
    }
    
    /**
     * Create an SAAJ AttachmentPart from a JAXWS Attachment
     * @param a Attachment
     * @param message SOAPMessage
     * @return AttachmentPart
     */
    public static AttachmentPart createAttachmentPart(Attachment a, SOAPMessage message) {
        // Create the Attachment Part
        AttachmentPart ap = message.createAttachmentPart(a.getDataHandler());
        
        // Copy over all of the Headers
        Iterator it = a.getMimeHeaders().getAllHeaders();
        while (it.hasNext()) {
            MimeHeader mh = (MimeHeader) it.next();
            ap.addMimeHeader(mh.getName(), mh.getValue());
        }
        return ap;
    }
    
    /**
     * Create a JAX-WS Message from the
     * information on an Axis 2 Message Context
     * @param msgContext
     * @return Message
     */
    public static Message getMessageFromMessageContext(MessageContext msgContext) throws MessageException {
        if (log.isDebugEnabled()) {
            log.debug("Start getMessageFromMessageContext");
        }
        
        Message message = null;
        // If the Axis2 MessageContext that was passed in has a SOAPEnvelope
        // set on it, grab that and create a JAX-WS Message out of it.
        SOAPEnvelope soapEnv = msgContext.getEnvelope();
        if (soapEnv != null) {
            MessageFactory msgFactory = (MessageFactory) FactoryRegistry.getFactory(MessageFactory.class);
            try {
                message = msgFactory.createFrom(soapEnv);
            } catch (Exception e) {
                throw ExceptionFactory.makeWebServiceException("Could not create new Message");
            }
            
            // Add all the MimeHeaders from the Axis2 MessageContext
            MimeHeaders mhs = message.getMimeHeaders();
            HashMap headerMap = (HashMap) msgContext.getProperty(MessageContext.TRANSPORT_HEADERS);
            if (headerMap != null) {
                Iterator it = headerMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = (String) headerMap.get(key);
                    mhs.addHeader(key, value);
                }
            }
            
            
            // FIXME: This should be revisited when we re-work the MTOM support.
            //This destroys performance by forcing a double pass through the message.
            //If attachments are found on the MessageContext, then that means
            //the inbound message has more than just the normal XML payload
            Attachments as = (Attachments) msgContext.getProperty(MTOMConstants.ATTACHMENTS); 
            if (as != null) { 
                if (log.isDebugEnabled()) {
                    log.debug("Found Axis MTOM Attachments");
                }
                
                //Walk the tree and find all of the optimized binary nodes.
                ArrayList<OMText> binaryNodes = AttachmentUtils.findBinaryNodes((SOAPEnvelope) message.getAsOMElement());
                if (binaryNodes != null  && binaryNodes.size() > 0) {
                    
                    if (log.isDebugEnabled()) {
                        log.debug("Found " + binaryNodes.size() +"MTOM Binary Nodes");
                    }
                    
                    // Mark the JAX-WS Message as MTOM enabled
                    message.setMTOMEnabled(true);
                    
                    //Replace each of the nodes with it's corresponding <xop:include>
                    //element, so JAXB can process it correctly.
                    Iterator<OMText> itr = binaryNodes.iterator();
                    while (itr.hasNext()) {
                        OMText node = itr.next();
                        OMElement xop = AttachmentUtils.makeXopElement(node);
                        node.getParent().addChild(xop);
                        node.detach();
                        
                        //We have to add the individual attachments in their raw
                        //binary form, so we can access them later.
                        if (log.isDebugEnabled()) {
                            log.debug("Create MTOM Message Attachment for " + node.getContentID());
                        }
                        Attachment a = message.createAttachment(
                                (DataHandler) node.getDataHandler(), 
                                node.getContentID());
                        message.addAttachment(a);
                    }
                }
            } 
            
            // Get SWA Attachments from the Axis2 MessageContext
            Attachments attachments = msgContext.getAttachmentMap();
            if (attachments != null) {
                String[] ids = attachments.getAllContentIDs();
                if (ids != null) {
                    // Axis2 stores the SOAP Part as one of the Attachments.
                    // For now we will assume that the SOAPPart is the first attachment, and skip it.
                    for (int i = 1; i<ids.length; i++) {
                        // The Attachment may already be added as an MTOM attachment (by the processing above)
                        if (message.getAttachment(ids[i]) == null) {
                            DataHandler dh = attachments.getDataHandler(ids[i]);
                            Attachment a = message.createAttachment(dh, ids[i]);
                            message.addAttachment(a);
                            if (log.isDebugEnabled()) {
                                log.debug("Create JAXWS Attachment for SWA Attachment:" + a.getContentID() + " " + a.getContentType());
                            }
                        }
                    }
                }
            }
            
        }
        return message;
    }
    
    /**
     * Put the JAX-WS Message onto the Axis2 MessageContext
     * @param message JAX-WS Message
     * @param msgContext Axis2MessageContext
     */
    public static void putMessageOnMessageContext(Message message, MessageContext msgContext) throws AxisFault, MessageException {
        // Put the XML message on the Axis 2 Message Context
        SOAPEnvelope envelope = (SOAPEnvelope) message.getAsOMElement();
        msgContext.setEnvelope(envelope);
        
        // Put the Headers onto the MessageContext
        // TODO: Merge with latest TransportHeaders impl.
        HashMap headerMap = new HashMap();
        for (Iterator it = message.getMimeHeaders().getAllHeaders(); it.hasNext();) {
            MimeHeader mh = (MimeHeader) it.next();
            headerMap.put(mh.getName(), mh.getValue());
        }
        msgContext.setProperty(MessageContext.TRANSPORT_HEADERS, headerMap);
        
        // Enable MTOM Attachments 
        if (message.isMTOMEnabled()) {
            if (log.isDebugEnabled()) {
                log.debug("MTOM is enabled on the JAX-WS Message...look for XOP Includes");
            }
            // If we have MTOM attachments, we need to replace the <xop:include>
            // elements with OMText binary nodes.
            
            // First find all of the <xop:include> elements
            ArrayList<OMElement> xops = AttachmentUtils.findXopElements(envelope);
            
            if (xops != null && xops.size() > 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Found XOP:Include Elements");
                }
                // Enable MTOM on the Axis2 MessageContext
                Options opts = msgContext.getOptions();
                opts.setProperty(Configuration.ENABLE_MTOM, "true");
                
                QName href = new QName("","href");
                Iterator<OMElement> itr = xops.iterator();
                
                
                while (itr.hasNext()) {
                    OMElement xop = itr.next();
                    String cid = xop.getAttributeValue(href);
                    
                    // Find and remove the Attachment from the JAX-WS Message
                    // (It is removed so that it is not considered a SWA Attachment ...see below)
                    Attachment a = message.removeAttachment(cid);
                    if (log.isDebugEnabled()) {
                        log.debug("Create Binary OMNode for attachment:" + cid);
                    }
                    
                    // Convert the <xop:include> OMElement into an OMText
                    // binary node and replace it in the tree.                    
                    OMText binaryNode = AttachmentUtils.makeBinaryOMNode(xop, a);
                    xop.insertSiblingAfter(binaryNode);
                    xop.detach();
                }
            }
            
        }
        
        // Any remaining attachments must be SWA attachments
        List attachments = message.getAttachments();
        if (attachments != null && attachments.size() > 0) {
            // Indicate SWA Attachments are present
            Options opts = msgContext.getOptions();
            opts.setProperty(Configuration.ENABLE_SWA, "true");
            
            for (int i=0; i<attachments.size(); i++) {
                Attachment a = (Attachment) attachments.get(i);
                msgContext.addAttachment(a.getContentID(), a.getDataHandler());
                if (log.isDebugEnabled()) {
                    log.debug("Add SWA Attachment for:" + a.getContentID() + " " + a.getContentType());
                }
            }
        }
    }
    
    /**
     * Get a string containing the stack of the specified exception
     * @param e Throwable
     * @return String
     */
    public static String stackToString(Throwable e){
        java.io.StringWriter sw= new java.io.StringWriter(); 
        java.io.BufferedWriter bw = new java.io.BufferedWriter(sw);
        java.io.PrintWriter pw= new java.io.PrintWriter(bw); 
        e.printStackTrace(pw);
        pw.close();
        return sw.getBuffer().toString();
      }
}
