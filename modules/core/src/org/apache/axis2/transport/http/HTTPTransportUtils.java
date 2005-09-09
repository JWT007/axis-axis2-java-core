/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Runtime state of the engine
 */
package org.apache.axis2.transport.http;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.attachments.MIMEHelper;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.om.*;
import org.apache.axis2.om.impl.MTOMConstants;
import org.apache.axis2.om.impl.llom.OMNamespaceImpl;
import org.apache.axis2.om.impl.llom.builder.StAXBuilder;
import org.apache.axis2.om.impl.llom.builder.StAXOMBuilder;
import org.apache.axis2.om.impl.llom.mtom.MTOMStAXSOAPModelBuilder;
import org.apache.axis2.soap.SOAP11Constants;
import org.apache.axis2.soap.SOAP12Constants;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.axis2.soap.impl.llom.SOAPProcessingException;
import org.apache.axis2.soap.impl.llom.builder.StAXSOAPModelBuilder;
import org.apache.axis2.soap.impl.llom.soap11.SOAP11Factory;
import org.apache.axis2.soap.impl.llom.soap12.SOAP12Factory;
import org.apache.axis2.util.Utils;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class HTTPTransportUtils {

    public static void processHTTPPostRequest(
        MessageContext msgContext,
        InputStream in,
        OutputStream out,
        String contentType,
        String soapActionHeader,
        String requestURI,
        ConfigurationContext configurationContext)
        throws AxisFault {
        boolean soap11 = false;
        try {

            //remove the starting and trailing " from the SOAP Action
            if (soapActionHeader != null
                && soapActionHeader.startsWith("\"")
                && soapActionHeader.endsWith("\"")) {

                soapActionHeader =
                    soapActionHeader.substring(
                        1,
                        soapActionHeader.length() - 1);
            }
            //fill up the Message Contexts
            msgContext.setWSAAction(soapActionHeader);
            msgContext.setSoapAction(soapActionHeader);
            msgContext.setTo(new EndpointReference(requestURI));
            msgContext.setProperty(MessageContext.TRANSPORT_OUT, out);
            msgContext.setServerSide(true);

            SOAPEnvelope envelope = null;
            StAXBuilder builder = null;
            if (contentType != null) {
                if (contentType
                    .indexOf(HTTPConstants.HEADER_ACCEPT_MULTIPART_RELATED)
                    > -1) {
                    //It is MTOM
                    builder = selectBuilderForMIME(msgContext, in, contentType);
                    envelope = (SOAPEnvelope) builder.getDocumentElement();
                } else {
                    Reader reader = new InputStreamReader(in);

                    XMLStreamReader xmlreader;
                    //Figure out the char set encoding and create the reader

                    //If charset is not specified
                    if ( getCharSetEncoding(contentType) == null ) {
                        xmlreader =
                            XMLInputFactory
                                .newInstance()
                                .createXMLStreamReader(
                                in,
                                MessageContext.DEFAULT_CHAR_SET_ENCODING);
                        //Set the encoding scheme in the message context
                        msgContext.setProperty(
                            MessageContext.CHARACTER_SET_ENCODING,
                            MessageContext.DEFAULT_CHAR_SET_ENCODING);
                    } else {
                        //get the type of char encoding
                        String charSetEnc = getCharSetEncoding(contentType);
                        xmlreader =
                            XMLInputFactory
                                .newInstance()
                                .createXMLStreamReader(
                                in,
                                charSetEnc);

                        //Setting the value in msgCtx
                        msgContext.setProperty(
                            MessageContext.CHARACTER_SET_ENCODING,
                            charSetEnc);

                    }
                    if (contentType
                        .indexOf(SOAP12Constants.SOAP_12_CONTENT_TYPE)
                        > -1) {
                        soap11 = false;
                        //it is SOAP 1.2
                        builder =
                            new StAXSOAPModelBuilder(
                                xmlreader,
                                SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
                        envelope = (SOAPEnvelope) builder.getDocumentElement();
                    } else if (
                        contentType.indexOf(
                            SOAP11Constants.SOAP_11_CONTENT_TYPE)
                            > -1) {
                        soap11 = true;
                        //it is SOAP 1.1
                        Object enable =
                            msgContext.getProperty(
                                Constants.Configuration.ENABLE_REST);
                        if ((soapActionHeader == null
                            || soapActionHeader.length() == 0)
                            && Constants.VALUE_TRUE.equals(enable)) {
                            //If the content Type is text/xml (BTW which is the SOAP 1.1 Content type ) and
                            //the SOAP Action is absent it is rest !!
                            msgContext.setDoingREST(true);

                            SOAPFactory soapFactory = new SOAP11Factory();
                            builder = new StAXOMBuilder(xmlreader);
                            builder.setOmbuilderFactory(soapFactory);
                            envelope = soapFactory.getDefaultEnvelope();
                            envelope.getBody().addChild(
                                builder.getDocumentElement());
                        } else {
                            builder =
                                new StAXSOAPModelBuilder(
                                    xmlreader,
                                    SOAP11Constants
                                        .SOAP_ENVELOPE_NAMESPACE_URI);
                            envelope =
                                (SOAPEnvelope) builder.getDocumentElement();
                        }
                    }

                }

            }

            String charsetEncoding = builder.getDocument().getCharsetEncoding();
            if(charsetEncoding != null && !"".equals(charsetEncoding) &&
                    !((String)msgContext.getProperty(MessageContext.CHARACTER_SET_ENCODING))
                            .equalsIgnoreCase(charsetEncoding)){
                String faultCode;
                if(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(envelope.getNamespace().getName())){
                   faultCode = SOAP12Constants.FAULT_CODE_SENDER;
                }else {
                    faultCode = SOAP11Constants.FAULT_CODE_SENDER;
                }
                throw new AxisFault("Character Set Encoding from " +
                        "transport information do not match with " +
                        "character set encoding in the received SOAP message", faultCode);
            }


            msgContext.setEnvelope(envelope);
            AxisEngine engine = new AxisEngine(configurationContext);
            if (envelope.getBody().hasFault()) {
                engine.receiveFault(msgContext);
            } else {
                engine.receive(msgContext);
            }
        } catch (SOAPProcessingException e) {
            throw new AxisFault(e);

        } catch (AxisFault e) {
            throw new AxisFault(e);
        } catch (OMException e) {
            throw new AxisFault(e);
        } catch (XMLStreamException e) {
            throw new AxisFault(e);
        } catch (FactoryConfigurationError e) {
            throw new AxisFault(e);
        } catch (UnsupportedEncodingException e) {
        	throw new AxisFault(e);
        } finally {
            if (msgContext.getEnvelope() == null && !soap11) {
                msgContext.setEnvelope(
                    new SOAP12Factory().createSOAPEnvelope());
            }

        }
    }

    /**
     * Extracts and returns the character set encoding from the
     * Content-type header
     * Example:
     * Content-Type: text/xml; charset=utf-8
     * @param contentType
     */
    private static String getCharSetEncoding(String contentType) {
        int index = contentType.indexOf(HTTPConstants.CHAR_SET_ENCODING);
        if(index == -1) { //Charset encoding not found in the contect-type header
        	//Using the default UTF-8
        	return MessageContext.DEFAULT_CHAR_SET_ENCODING;
        }

        //If there are spaces around the '=' sign
        int indexOfEq = contentType.indexOf("=", index);
        //There can be situations where "charset" is not the last parameter of the Content-Type header
        int indexOfSemiColon = contentType.indexOf(";", indexOfEq);
        String value;
        if (indexOfSemiColon > 0) {
            value = (contentType.substring(indexOfEq + 1, indexOfSemiColon));
        } else {
            value = (contentType.substring(indexOfEq + 1, contentType.length()))
                    .trim();
        }

        //There might be "" around the value - if so remove them
        value = value.replaceAll("\"", "");

        if("null".equalsIgnoreCase(value)){
            return null;
        }

        return value.trim();

    }

    public static boolean processHTTPGetRequest(
        MessageContext msgContext,
        InputStream in,
        OutputStream out,
        String contentType,
        String soapAction,
        String requestURI,
        ConfigurationContext configurationContext,
        Map requestParameters)
        throws AxisFault {
        if (soapAction != null
            && soapAction.startsWith("\"")
            && soapAction.endsWith("\"")) {
            soapAction = soapAction.substring(1, soapAction.length() - 1);
        }
        msgContext.setWSAAction(soapAction);
        msgContext.setSoapAction(soapAction);
        msgContext.setTo(new EndpointReference(requestURI));
        msgContext.setProperty(MessageContext.TRANSPORT_OUT, out);
        msgContext.setServerSide(true);
        SOAPEnvelope envelope =
            HTTPTransportUtils.createEnvelopeFromGetRequest(
                requestURI,
                requestParameters);
        if (envelope == null) {
            return false;
        } else {
            msgContext.setDoingREST(true);
            msgContext.setEnvelope(envelope);
            AxisEngine engine = new AxisEngine(configurationContext);
            engine.receive(msgContext);
            return true;
        }
    }

    public static final SOAPEnvelope createEnvelopeFromGetRequest(
        String requestUrl,
        Map map) {
        String[] values =
            Utils.parseRequestURLForServiceAndOperation(requestUrl);

        if (values[1] != null && values[0] != null) {
            String operation = values[1];
            SOAPFactory soapFactory = new SOAP11Factory();
            SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();

            OMNamespace omNs =
                soapFactory.createOMNamespace(values[0], "services");
            OMNamespace defualtNs = new OMNamespaceImpl("", null);

            OMElement opElement = soapFactory.createOMElement(operation, omNs);

            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                String value = (String) map.get(name);
                OMElement omEle = soapFactory.createOMElement(name, defualtNs);
                omEle.setText(value);
                opElement.addChild(omEle);
            }

            envelope.getBody().addChild(opElement);
            return envelope;
        } else {
            return null;
        }
    }

    public static StAXBuilder selectBuilderForMIME(
        MessageContext msgContext,
        InputStream inStream,
        String contentTypeString)
        throws OMException,
			XMLStreamException, FactoryConfigurationError,
			UnsupportedEncodingException {
        StAXBuilder builder = null;


        Parameter parameter_cache_attachment = msgContext.getParameter(
                Constants.Configuration.CACHE_ATTACHMENTS);
         boolean fileCacheForAttachments ;
        if(parameter_cache_attachment == null){
            fileCacheForAttachments = false;
        }  else {
           fileCacheForAttachments =
            (Constants
                .VALUE_TRUE
                .equals(
                    parameter_cache_attachment.getValue()));
        }
        String attachmentRepoDir = null;
        String attachmentSizeThreshold = null;
        Parameter parameter;
        if (fileCacheForAttachments) {
            parameter = msgContext.getParameter(Constants.Configuration.ATTACHMENT_TEMP_DIR);
            attachmentRepoDir = parameter==null?"":parameter.getValue().toString();

            parameter = msgContext
                    .getParameter(Constants.Configuration.FILE_SIZE_THRESHOLD);
            attachmentSizeThreshold = parameter==null?"":parameter.getValue().toString();
        }

        MIMEHelper mimeHelper = new MIMEHelper(inStream, contentTypeString,
                fileCacheForAttachments, attachmentRepoDir,attachmentSizeThreshold);

        String charSetEncoding = getCharSetEncoding(mimeHelper.getSOAPPartContentType());
        XMLStreamReader reader;
        if(charSetEncoding == null || "null".equalsIgnoreCase(charSetEncoding)){
             reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(
                        new BufferedReader(new InputStreamReader(mimeHelper
                                .getSOAPPartInputStream(),
                                charSetEncoding)));
            msgContext.setProperty(MessageContext.CHARACTER_SET_ENCODING, charSetEncoding);

        }else {
            reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(
                        new BufferedReader(new InputStreamReader(mimeHelper
                                .getSOAPPartInputStream())));
            msgContext.setProperty(MessageContext.CHARACTER_SET_ENCODING, MessageContext.UTF_8);

        }


        /*
		 * put a reference to Attachments in to the message context
		 */
        msgContext.setProperty(MTOMConstants.ATTACHMENTS, mimeHelper);
        if (mimeHelper.getAttachmentSpecType().equals(MTOMConstants.MTOM_TYPE)) {
            /*
             * Creates the MTOM specific MTOMStAXSOAPModelBuilder
             */
            builder =
                new MTOMStAXSOAPModelBuilder(
                    reader,
                    mimeHelper,
                    null);
        } else if (
            mimeHelper.getAttachmentSpecType().equals(MTOMConstants.SWA_TYPE)) {
            builder =
                new StAXSOAPModelBuilder(
                    reader,
                    SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        }
        return builder;
    }

    public static boolean checkEnvelopeForOptimise(SOAPEnvelope envelope) {
        return isOptimised(envelope);
    }

    private static boolean isOptimised(OMElement element) {
        Iterator childrenIter = element.getChildren();
        boolean isOptimized = false;
        while (childrenIter.hasNext() && !isOptimized) {
            OMNode node = (OMNode) childrenIter.next();
            if (OMNode.TEXT_NODE == node.getType()
                && ((OMText) node).isOptimized()) {
                isOptimized = true;
            } else if (OMNode.ELEMENT_NODE == node.getType()) {
                isOptimized = isOptimised((OMElement) node);
            }
        }
        return isOptimized;
    }

    public static boolean doWriteMTOM(MessageContext msgContext) {
        boolean enableMTOM = false;
        if (msgContext.getParameter(Constants.Configuration.ENABLE_MTOM)
            != null) {
            enableMTOM =
                Constants.VALUE_TRUE.equals(
                    msgContext.getParameter(
                        Constants.Configuration.ENABLE_MTOM).getValue());
        } else if(msgContext.getProperty(Constants.Configuration.ENABLE_MTOM) != null) {
            enableMTOM =
                Constants.VALUE_TRUE.equals(
                    msgContext.getProperty(
                        Constants.Configuration.ENABLE_MTOM));
        }
        boolean envelopeContainsOptimise =
            HTTPTransportUtils.checkEnvelopeForOptimise(
                msgContext.getEnvelope());
        boolean doMTOM = enableMTOM && envelopeContainsOptimise;
        msgContext.setDoingMTOM(doMTOM);
        return doMTOM;
    }
}
