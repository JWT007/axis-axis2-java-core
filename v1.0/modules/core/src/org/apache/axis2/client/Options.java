package org.apache.axis2.client;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.RelatesTo;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.transport.TransportListener;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

/**
 * The proposal related for this is here :
 * http://marc.theaimsgroup.com/?l=axis2-dev&m=113320384108037&w=2 Client can
 * fill this options and give to any class extending from MEPClient. All those
 * classes will be getting parameters using this.
 */
public class Options {

    public static final String COPY_PROPERTIES = "CopyProperties";

    public static final int DEFAULT_TIMEOUT_MILLISECONDS = 5000;

    private Options parent;

    private Map properties = new HashMap();

    // ==========================================================================
    //                  Parameters that can be set via Options
    // ==========================================================================
    private String soapVersionURI; // defaults to
    // SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI;

    private Boolean isExceptionToBeThrownOnSOAPFault; // defaults to true;

    private long timeOutInMilliSeconds = -1; // =
    // DEFAULT_TIMEOUT_MILLISECONDS;

    private Boolean useSeparateListener; // defaults to false

    // Addressing specific properties
    private String action;

    private EndpointReference faultTo;

    private EndpointReference from;

    private TransportListener listener;

    private TransportInDescription transportIn;

    private String transportInProtocol;

    private String messageId;

    // Array of RelatesTo objects
    private List relationships;

    private EndpointReference replyTo;

    private ArrayList referenceParameters;

    /**
     * This is used for sending and receiving messages.
     */
    protected TransportOutDescription transportOut;

    private String senderTransportProtocol;

    private EndpointReference to;

    //To control , session managment , default is set to true , if user wants he can set that to true
    // There operation clinet will manage session using ServiceGroupID if it is there in the response
    private boolean manageSession = false;

    /**
     * Default constructor
     */
    public Options() {
    }

    /**
     * In normal mode operation, this options will try to fullfil the request
     * from its values. If that is not possible, this options will request those
     * information from its parent.
     *
     * @param parent
     */
    public Options(Options parent) {
        this.parent = parent;
    }

    public String getAction() {
        if (action == null && parent != null) {
            return parent.getAction();
        }
        return action;
    }

    public EndpointReference getFaultTo() {
        if (faultTo == null && parent != null) {
            return parent.getFaultTo();
        }
        return faultTo;
    }

    public EndpointReference getFrom() {
        if (from == null && parent != null) {
            return parent.getFrom();
        }
        return from;
    }

    public TransportListener getListener() {
        if (listener == null && parent != null) {
            return parent.getListener();
        }
        return listener;
    }

    public TransportInDescription getTransportIn() {
        if (transportIn == null && parent != null) {
            return parent.getTransportIn();
        }
        return transportIn;
    }

    public String getTransportInProtocol() {
        if (transportInProtocol == null && parent != null) {
            return parent.getTransportInProtocol();
        }
        return transportInProtocol;
    }

    public String getMessageId() {
        if (messageId == null && parent != null) {
            return parent.getMessageId();
        }

        return messageId;
    }

    public Map getProperties() {
        if (properties.size() == 0 && parent != null) {
            Map properties = parent.getProperties();

            if (properties.size() > 0) {
                HashMap ret = new HashMap(properties);
                ret.putAll(properties);
                return ret;
            }
        }
        return properties;
    }

    /**
     * @param key
     * @return the value realeted to this key. Null, if not found.
     */
    public Object getProperty(String key) {
        Object myPropValue = properties.get(key);
        if (myPropValue == null && parent != null) {
            return parent.getProperty(key);
        }
        return myPropValue;
    }

    public RelatesTo getRelatesTo(String type) {
        if (relationships == null && parent != null) {
            return parent.getRelatesTo(type);
        }
        for(int i=0;relationships != null && i<relationships.size();i++) {
            RelatesTo relatesTo = (RelatesTo) relationships.get(i);
            String relationshipType = relatesTo.getRelationshipType();
            if((type.equals(AddressingConstants.Final.WSA_DEFAULT_RELATIONSHIP_TYPE) ||
                    type.equals(AddressingConstants.Submission.WSA_RELATES_TO_RELATIONSHIP_TYPE_DEFAULT_VALUE))
                    && relationshipType == null) {
                return relatesTo;
            }
            if(relationshipType != null && relationshipType.equals(type)) {
                return relatesTo;
            }
        }
        return null;
    }

    /**
     *
     * @return the relates to which has the type http://www.w3.org/2005/08/addressing/reply
     */
    public RelatesTo getRelatesTo() {
        if (relationships == null && parent != null) {
            return parent.getRelatesTo();
        }
        for(int i=0;relationships != null && i<relationships.size();i++) {
            RelatesTo relatesTo = (RelatesTo) relationships.get(i);
            String relationshipType = relatesTo.getRelationshipType();
            if(relationshipType == null) {
                return relatesTo;
            }else if (relationshipType.equals(AddressingConstants.Final.WSA_DEFAULT_RELATIONSHIP_TYPE)
                    || relationshipType.equals(AddressingConstants.Submission.WSA_RELATES_TO_RELATIONSHIP_TYPE_DEFAULT_VALUE)) {
                return relatesTo;
            }
        }
        return null;
    }

    public RelatesTo[] getRelationships() {
        if (relationships == null && parent != null) {
            return parent.getRelationships();
        }
        if(relationships == null) {
            return null;                                 
        }
        return (RelatesTo[]) relationships.toArray(new RelatesTo[relationships.size()]);
    }

    public void setRelationships(RelatesTo[] list) {
        relationships = list == null ? null : Arrays.asList(list);
    }

    public EndpointReference getReplyTo() {
        if (replyTo == null && parent != null) {
            return parent.getReplyTo();
        }
        return replyTo;
    }

    public TransportOutDescription getTransportOut() {
        if (transportOut == null && parent != null) {
            return parent.getTransportOut();
        }

        return transportOut;
    }

    public String getSenderTransportProtocol() {
        if (senderTransportProtocol == null && parent != null) {
            return parent.getSenderTransportProtocol();
        }

        return senderTransportProtocol;
    }

    public String getSoapVersionURI() {
        if (soapVersionURI == null && parent != null) {
            return parent.getSoapVersionURI();
        }

        return soapVersionURI == null ? SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI
                : soapVersionURI;
    }

    /**
     * Gets the wait time after which a client times out in a blocking scenario.
     * The default is Options#DEFAULT_TIMEOUT_MILLISECONDS
     *
     * @return timeOutInMilliSeconds
     */
    public long getTimeOutInMilliSeconds() {
        if (timeOutInMilliSeconds == -1 && parent != null) {
            return parent.getTimeOutInMilliSeconds();
        }

        return timeOutInMilliSeconds == -1 ? DEFAULT_TIMEOUT_MILLISECONDS
                : timeOutInMilliSeconds;
    }

    public EndpointReference getTo() {
        if (to == null && parent != null) {
            return parent.getTo();
        }

        return to;
    }

    /**
     * If there is a SOAP Fault in the body of the incoming SOAP Message, system
     * can be configured to throw an exception with the details extracted from
     * the information from the fault message. This boolean variable will enable
     * that facility. If this is false, the response message will just be
     * returned to the application, irrespective of whether it has a Fault or
     * not.
     */
    public boolean isExceptionToBeThrownOnSOAPFault() {
        if (isExceptionToBeThrownOnSOAPFault == null && parent != null) {
            isExceptionToBeThrownOnSOAPFault = parent.isExceptionToBeThrownOnSOAPFault;
        }

        return isExceptionToBeThrownOnSOAPFault == null
                || isExceptionToBeThrownOnSOAPFault.booleanValue();
    }

    public boolean isUseSeparateListener() {
        if (useSeparateListener == null && parent != null) {
            useSeparateListener = parent.useSeparateListener;
        }

        return useSeparateListener != null
                && useSeparateListener.booleanValue();
    }

    public Options getParent() {
        return parent;
    }

    public void setParent(Options parent) {
        this.parent = parent;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * If there is a SOAP Fault in the body of the incoming SOAP Message, system
     * can be configured to throw an exception with the details extracted from
     * the information from the fault message. This boolean variable will enable
     * that facility. If this is false, the response message will just be
     * returned to the application, irrespective of whether it has a Fault or
     * not.
     *
     * @param exceptionToBeThrownOnSOAPFault
     */
    public void setExceptionToBeThrownOnSOAPFault(
            boolean exceptionToBeThrownOnSOAPFault) {
        isExceptionToBeThrownOnSOAPFault = Boolean
                .valueOf(exceptionToBeThrownOnSOAPFault);
    }

    public void setFaultTo(EndpointReference faultTo) {
        this.faultTo = faultTo;
    }

    public void setFrom(EndpointReference from) {
        this.from = from;
    }

    public void setListener(TransportListener listener) {
        this.listener = listener;
    }

    public void setTransportIn(TransportInDescription transportIn) {
        this.transportIn = transportIn;
    }

    public void setTransportInProtocol(String transportInProtocol) {
        this.transportInProtocol = transportInProtocol;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * This will set the properties to the context. But in setting that one may
     * need to "copy" all the properties from the source properties to the
     * target properties. To enable this we introduced a property
     * (org.apache.axis2.client.Options#COPY_PROPERTIES) so that if set to
     * Boolean(true), this code will copy the whole thing, without just
     * referencing to the source.
     *
     * @param properties
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    /**
     * Properties you need to pass in to the message context must be set via
     * this. If there is a method to the set this property, within this class,
     * its encouraged to use that method, without duplicating stuff or making
     * room for bugs.
     *
     * @param propertyKey
     * @param property
     */
    public void setProperty(String propertyKey, Object property) {
        properties.put(propertyKey, property);
    }

    public void addRelatesTo(RelatesTo relatesTo) {
        if(relationships == null) {
            relationships = new ArrayList(5);
        }
        relationships.add(relatesTo);
    }

    public void setReplyTo(EndpointReference replyTo) {
        this.replyTo = replyTo;
    }

    public void setTransportOut(TransportOutDescription transportOut) {
        this.transportOut = transportOut;
    }

    /**
     * Sets the transport to be used for sending the SOAP Message
     *
     * @param senderTransport
     * @throws AxisFault if the transport is not found
     */
    public void setSenderTransport(String senderTransport,
                                   AxisConfiguration axisConfiguration) throws AxisFault {
        this.transportOut = axisConfiguration.getTransportOut(new QName(
                senderTransport));

        if (senderTransport == null) {
            throw new AxisFault(Messages.getMessage("unknownTransport",
                    senderTransport));
        }
    }

    public void setSoapVersionURI(String soapVersionURI) {
        this.soapVersionURI = soapVersionURI;
    }

    /**
     * This is used in blocking scenario. Client will time out after waiting
     * this amount of time. The default is 2000 and must be provided in
     * multiples of 100.
     *
     * @param timeOutInMilliSeconds
     */
    public void setTimeOutInMilliSeconds(long timeOutInMilliSeconds) {
        this.timeOutInMilliSeconds = timeOutInMilliSeconds;
    }

    public void setTo(EndpointReference to) {
        this.to = to;
    }

    /**
     * Sets transport information to the call. The senarios supported are as
     * follows: <blockquote>
     * <p/>
     * <pre>
     *  [transportOut, transportIn, useSeparateListener]
     *  http, http, true
     *  http, http, false
     *  http,smtp,true
     *  smtp,http,true
     *  smtp,smtp,true
     * </pre>
     * <p/>
     * </blockquote>
     *
     * @param senderTransport
     * @param listenerTransport
     * @param useSeparateListener
     * @throws AxisFault
     * @deprecated Use setTransportInProtocol(String) and
     *             useSeparateListener(boolean) instead. You do not need to
     *             setSenderTransportProtocol(String) as sender transport can be
     *             inferred from the to EPR. But still you can
     *             setTransportOut(TransportOutDescription).
     */
    public void setTransportInfo(String senderTransport,
                                 String listenerTransport, boolean useSeparateListener)
            throws AxisFault {

        // here we check for a legal combination, for and example if the
        // sendertransport is http and listner
        // transport is smtp the invocation must using separate transport
        if (!useSeparateListener) {
            boolean isTransportsEqual = senderTransport
                    .equals(listenerTransport);
            boolean isATwoWaytransport = Constants.TRANSPORT_HTTP
                    .equals(senderTransport)
                    || Constants.TRANSPORT_TCP.equals(senderTransport);

            if ((!isTransportsEqual || !isATwoWaytransport)) {
                throw new AxisFault(Messages
                        .getMessage("useSeparateListenerLimited"));
            }
        } else {
            setUseSeparateListener(useSeparateListener);
        }

        setTransportInProtocol(listenerTransport);
        this.senderTransportProtocol = senderTransport;
    }

    /**
     * Used to specify whether the two SOAP Messages are be sent over same
     * channel or over separate channels.The value of this variable depends on
     * the transport specified. For e.g., if the transports are different this
     * is true by default. HTTP transport supports both cases while SMTP
     * transport supports only two channel case.
     *
     * @param useSeparateListener
     */
    public void setUseSeparateListener(boolean useSeparateListener) {
        this.useSeparateListener = Boolean.valueOf(useSeparateListener);
    }

    public void addReferenceParameter(OMElement referenceParameter) {
        if (referenceParameters == null) {
            referenceParameters = new ArrayList(5);
        }

        referenceParameters.add(referenceParameter);
    }

    public boolean isManageSession() {
        return manageSession;
    }

    public void setManageSession(boolean manageSession) {
        this.manageSession = manageSession;
    }
}
