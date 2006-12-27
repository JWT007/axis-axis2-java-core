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


package org.apache.axis2.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12Factory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.engine.Handler.InvocationResponse;
import org.apache.axis2.transport.TransportUtils;
import org.apache.axis2.util.Builder;
import org.apache.axis2.util.JavaUtils;
import org.apache.axis2.util.Utils;

public class HTTPTransportUtils {


    public static SOAPEnvelope createEnvelopeFromGetRequest(String requestUrl,
                                   Map map,ConfigurationContext configCtx) throws AxisFault {
        String[] values =
                Utils.parseRequestURLForServiceAndOperation(requestUrl,
                                                        configCtx.getServiceContextPath());
        if (values == null) {
            return new SOAP11Factory().getDefaultEnvelope();
        }

        if ((values[1] != null) && (values[0] != null)) {
            String srvice = values[0];
            AxisService service = configCtx.getAxisConfiguration().getService(srvice);
            if (service == null) {
                throw new AxisFault("service not found: " + srvice);
            }
            String operation = values[1];
            SOAPFactory soapFactory = new SOAP11Factory();
            SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
            OMNamespace omNs = soapFactory.createOMNamespace(service.getSchematargetNamespace(),
                                                             service.getSchematargetNamespacePrefix());
            soapFactory.createOMNamespace(service.getSchematargetNamespace(),
                                          service.getSchematargetNamespacePrefix());
            OMElement opElement = soapFactory.createOMElement(operation, omNs);
            Iterator it = map.keySet().iterator();

            while (it.hasNext()) {
                String name = (String) it.next();
                String value = (String) map.get(name);
                OMElement omEle = soapFactory.createOMElement(name, omNs);

                omEle.setText(value);
                opElement.addChild(omEle);
            }

            envelope.getBody().addChild(opElement);

            return envelope;
        } else {
            return null;
        }
    }

    public static boolean doWriteMTOM(MessageContext msgContext) {
        boolean enableMTOM = false;

        if (msgContext.getParameter(Constants.Configuration.ENABLE_MTOM) != null) {
            enableMTOM = JavaUtils.isTrueExplicitly(
                    msgContext.getParameter(Constants.Configuration.ENABLE_MTOM).getValue());
        }

        if (msgContext.getProperty(Constants.Configuration.ENABLE_MTOM) != null) {
            enableMTOM = JavaUtils.isTrueExplicitly(
                    msgContext.getProperty(Constants.Configuration.ENABLE_MTOM));
        }
        return enableMTOM;
    }
    
    public static boolean doWriteSwA(MessageContext msgContext) {
        boolean enableSwA = false;

        if (msgContext.getParameter(Constants.Configuration.ENABLE_SWA) != null) {
            enableSwA = JavaUtils.isTrueExplicitly(
                    msgContext.getParameter(Constants.Configuration.ENABLE_SWA).getValue());
        }

        if (msgContext.getProperty(Constants.Configuration.ENABLE_SWA) != null) {
            enableSwA = JavaUtils.isTrueExplicitly(
                    msgContext.getProperty(Constants.Configuration.ENABLE_SWA));
        }
        return enableSwA;
    }

    public static boolean processHTTPGetRequest(MessageContext msgContext,
                                                OutputStream out, String soapAction, String requestURI,
                                                ConfigurationContext configurationContext, Map requestParameters)
            throws AxisFault {
        if ((soapAction != null) && soapAction.startsWith("\"") && soapAction.endsWith("\"")) {
            soapAction = soapAction.substring(1, soapAction.length() - 1);
        }

        msgContext.setSoapAction(soapAction);
        msgContext.setTo(new EndpointReference(requestURI));
        msgContext.setProperty(MessageContext.TRANSPORT_OUT, out);
        msgContext.setServerSide(true);
        SOAPEnvelope envelope = HTTPTransportUtils.createEnvelopeFromGetRequest(requestURI,
                                                                                requestParameters, configurationContext);

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

    private static final int VERSION_UNKNOWN = 0;
    private static final int VERSION_SOAP11 = 1;
    private static final int VERSION_SOAP12 = 2;

    public static InvocationResponse processHTTPPostRequest(MessageContext msgContext, InputStream in,
                                              OutputStream out, String contentType, String soapActionHeader, String requestURI)
            throws AxisFault {

        int soapVersion = VERSION_UNKNOWN;

        InvocationResponse pi = InvocationResponse.CONTINUE;
        
        try {

            Map headers = (Map) msgContext.getProperty(MessageContext.TRANSPORT_HEADERS);
            if (headers != null) {
                if (HTTPConstants.COMPRESSION_GZIP.equals(headers.get(HTTPConstants.HEADER_CONTENT_ENCODING)) ||
                    HTTPConstants.COMPRESSION_GZIP.equals(headers.get(HTTPConstants.HEADER_CONTENT_ENCODING.toLowerCase())))
                {
                    in = new GZIPInputStream(in);
                }
            }

            // remove the starting and trailing " from the SOAP Action
            if ((soapActionHeader != null) && soapActionHeader.charAt(0) == '\"'
                && soapActionHeader.endsWith("\"")) {
                soapActionHeader = soapActionHeader.substring(1, soapActionHeader.length() - 1);
            }

            // fill up the Message Contexts
            msgContext.setSoapAction(soapActionHeader);
            msgContext.setTo(new EndpointReference(requestURI));
            msgContext.setProperty(MessageContext.TRANSPORT_OUT, out);
            msgContext.setServerSide(true);

            SOAPEnvelope envelope = null;
            boolean isMIME=false;

            // get the type of char encoding
            String charSetEnc = Builder.getCharSetEncoding(contentType);
            if(charSetEnc == null){
                charSetEnc = MessageContext.DEFAULT_CHAR_SET_ENCODING;
            }
            // Setting the value in msgCtx
            msgContext.setProperty(Constants.Configuration.CHARACTER_SET_ENCODING, charSetEnc);

            String soapNS = SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI;
            if (contentType != null) {
				if (contentType.indexOf(SOAP12Constants.SOAP_12_CONTENT_TYPE) > -1) {
					soapVersion = VERSION_SOAP12;
					soapNS = SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI;
					processContentTypeForAction(contentType, msgContext);
				} else if (contentType
						.indexOf(SOAP11Constants.SOAP_11_CONTENT_TYPE) > -1) {
					soapVersion = VERSION_SOAP11;
					soapNS = SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI;
				}
				if (JavaUtils.indexOfIgnoreCase(contentType,
						HTTPConstants.HEADER_ACCEPT_MULTIPART_RELATED) > -1) {
					// It is MIME (MTOM or SwA)
					isMIME = true;
				} else if (soapVersion == VERSION_SOAP11) {
					// Deployment configuration parameter
					Parameter enableREST = msgContext
							.getParameter(Constants.Configuration.ENABLE_REST);
					if ((soapActionHeader == null) && (enableREST != null)) {
						if (Constants.VALUE_TRUE.equals(enableREST.getValue())) {
							// If the content Type is text/xml (BTW which is the
							// SOAP 1.1 Content type ) and the SOAP Action is
							// absent it is rest !!
							msgContext.setDoingREST(true);
						}
					}
				}
			}
            envelope = TransportUtils.createSOAPMessage(msgContext,in,soapNS,isMIME,contentType,charSetEnc);
            msgContext.setEnvelope(envelope);
            AxisEngine engine = new AxisEngine(msgContext.getConfigurationContext());

            if (envelope.getBody().hasFault()) {
                pi = engine.receiveFault(msgContext);
            } else {
                pi = engine.receive(msgContext);
            }
            
            return pi;
        } catch (SOAPProcessingException e) {
            throw new AxisFault(e);
        } catch (AxisFault e) {
            throw e;
        } catch (IOException e) {
            throw new AxisFault(e);
        } catch (OMException e) {
            throw new AxisFault(e);
        } catch (XMLStreamException e) {
            throw new AxisFault(e);
        } catch (FactoryConfigurationError e) {
            throw new AxisFault(e);
        } finally {
            if ((msgContext.getEnvelope() == null) && soapVersion != VERSION_SOAP11) {
                msgContext.setEnvelope(new SOAP12Factory().getDefaultEnvelope());
            }
        }
    }

    private static void processContentTypeForAction(String contentType, MessageContext msgContext) {
        //Check for action header and set it in as soapAction in MessageContext
        int index = contentType.indexOf("action");
        if (index > -1) {
            String transientString = contentType.substring(index, contentType.length());
            int equal = transientString.indexOf("=");
            int firstSemiColon = transientString.indexOf(";");
            String soapAction; // This will contain "" in the string
            if (firstSemiColon > -1) {
                soapAction = transientString.substring(equal + 1, firstSemiColon);
            } else {
                soapAction = transientString.substring(equal + 1, transientString.length());
            }
            if ((soapAction != null) && soapAction.startsWith("\"")
                && soapAction.endsWith("\"")) {
                soapAction = soapAction
                        .substring(1, soapAction.length() - 1);
            }
            msgContext.setSoapAction(soapAction);
        }
    }

    public static boolean isDoingREST(MessageContext msgContext) {
        boolean enableREST = false;

        // check whether isDoingRest is already true in the message context
        if (msgContext.isDoingREST()) {
            return true;
        }

        Object enableRESTProperty = msgContext.getProperty(Constants.Configuration.ENABLE_REST);
        if (enableRESTProperty != null) {
            enableREST = JavaUtils.isTrueExplicitly(enableRESTProperty);
        }

        msgContext.setDoingREST(enableREST);

        return enableREST;
    }
}
