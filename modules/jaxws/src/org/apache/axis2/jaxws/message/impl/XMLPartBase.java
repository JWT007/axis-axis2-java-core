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
package org.apache.axis2.jaxws.message.impl;

import java.util.Iterator;

import javax.jws.soap.SOAPBinding.Style;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axis2.jaxws.ExceptionFactory;
import org.apache.axis2.jaxws.i18n.Messages;
import org.apache.axis2.jaxws.message.Block;
import org.apache.axis2.jaxws.message.Message;
import org.apache.axis2.jaxws.message.MessageException;
import org.apache.axis2.jaxws.message.Protocol;
import org.apache.axis2.jaxws.message.XMLFault;
import org.apache.axis2.jaxws.message.XMLPart;
import org.apache.axis2.jaxws.message.factory.BlockFactory;
import org.apache.axis2.jaxws.message.factory.SOAPEnvelopeBlockFactory;
import org.apache.axis2.jaxws.message.util.XMLFaultUtils;
import org.apache.axis2.jaxws.util.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * XMLPartBase class for an XMLPart
 * An XMLPart is an abstraction of the xml portion of the message.
 * The actual representation can be in one of three different forms:
 *    * An OM tree
 *    * A SAAJ SOAPEnvelope
 *    * An XMLSpine (an optimized representation of the message)
 * The representation is stored in the private variable (content)
 * 
 * The representation changes as the Message flows through the JAX-WS 
 * framework.  For example, here is a typical flow on the inbound case:
 *    a) Message is built from OM                           (representation: OM)
 *    b) Message flows into SOAP Handler chain              (representation: OM->SOAPEnvelope)
 *    c) Message flows out of the SOAP Handler chain 
 *    d) Message flows into the logical dispatch processing (representation: SOAPEnvelope->XMLSpine)
 * 
 * The key to performance is the implementation of the transformations between 
 * OM, SAAJ SOAPEnvelope and XMLSpine.   This base class defines all of the methods
 * that are required on an XMLPart, the actual transformations are provided by the 
 * derived class.  This division of work allows the derived class to concentrate on the
 * optimization of the transformations.  For example, the derived class may implement
 * XMLSpine -> OM using OMObjectWrapperElement constructs...thus avoid expensive parsing.
 * 
 * Here are the methods that the derived XMLPart should implement. 
 *   OMElement _convertSE2OM(SOAPEnvelope se)
 *   OMElement _convertSpine2OM(XMLSpine spine)
 *   SOAPEnvelope _convertOM2SE(OMElement om)
 *   SOAPEnvelope _convertSpine2SE(XMLSpine spine)
 *   XMLSpine _convertOM2Spine(OMElement om)
 *   XMLSpine _convertSE2Spine(SOAPEnvelope se)
 *   XMLSpine _createSpine(Protocol protocol)
 * 
 * @see org.apache.axis2.jaxws.message.XMLPart
 * @see org.apache.axis2.jaxws.message.impl.XMLPartImpl
 * 
 */
public abstract class XMLPartBase implements XMLPart {

	private static Log log = LogFactory.getLog(XMLPartBase.class);
	
	Protocol protocol = Protocol.unknown;  // Protocol defaults to unknown
    Style style = Style.DOCUMENT;          // Style defaults to document
    
	
	// The actual xml representation is always one of the following
	//   OM if the content is an OM tree
	//   SOAPENVELOPE if the content is a SOAPEnvelope
	//   SPINE if the content is a OM "spine" + Blocks
	Object content = null;
	int contentType = UNKNOWN;
	
	static final int UNKNOWN = 0;
	static final int OM = 1;
	static final int SOAPENVELOPE = 2;
	static final int SPINE = 3;
    
	boolean consumed = false;
    
    Message parent;
	
	
	/**
	 * XMLPart should be constructed via the XMLPartFactory.
	 * This constructor constructs an empty XMLPart with the specified protocol
	 * @param protocol
	 * @throws MessageException
	 */
	XMLPartBase(Protocol protocol) throws MessageException {
		super();
		this.protocol = protocol;
		if (protocol.equals(Protocol.unknown)) {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("ProtocolIsNotKnown"));
		} else if (protocol.equals(Protocol.rest)) {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("RESTIsNotSupported"));
		}
		content = _createSpine(protocol);
		contentType = SPINE;
	}
	
	/**
	 * XMLPart should be constructed via the XMLPartFactory.
	 * This constructor creates an XMLPart from the specified root.
	 * @param root
	 * @throws MessageException
	 */
	XMLPartBase(OMElement root) throws MessageException {
		content = root;
		contentType = OM;
		QName qName = root.getQName();
		if (qName.getNamespaceURI().equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
			protocol = Protocol.soap11;
		} else if (qName.getNamespaceURI().equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
			protocol = Protocol.soap12;
		} else {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("RESTIsNotSupported"));
		}
	}
	
	/**
	 * XMLPart should be constructed via the XMLPartFactory.
	 * This constructor creates an XMLPart from the specified root.
	 * @param root
	 * @throws MessageException
	 */
	XMLPartBase(SOAPEnvelope root) throws MessageException {
		content = root;
		contentType = SOAPENVELOPE;
		String ns = root.getNamespaceURI();
		if (ns.equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
			protocol = Protocol.soap11;
		} else if (ns.equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
			protocol = Protocol.soap12;
		} else {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("RESTIsNotSupported"));
		}
	}
	
	private void setContent(Object content, int contentType) {
		this.content = content;
		this.contentType = contentType;
	}
	
	private OMElement getContentAsOMElement() throws MessageException {
		
		OMElement om = null;
		switch (contentType) {
		case (OM):
		 	om = (OMElement) content;
			break;
		case (SPINE):
			om = _convertSpine2OM((XMLSpine) content);
			break;
		case (SOAPENVELOPE):
			om = _convertSE2OM((SOAPEnvelope) content);
			break;
		default:
			throw ExceptionFactory.makeMessageInternalException(Messages.getMessage("XMLPartImplErr2"), null);
		}
		setContent(om, OM);
		return om;
	}
		
	private SOAPEnvelope getContentAsSOAPEnvelope() throws MessageException {
		SOAPEnvelope se = null;
		switch (contentType) {
		case (SOAPENVELOPE):
		 	se = (SOAPEnvelope) content;
			break;
		case (SPINE):
			se = _convertSpine2SE((XMLSpine) content);
			break;
		case (OM):
			se = _convertOM2SE((OMElement) content);
			break;
		default:
			throw ExceptionFactory.makeMessageInternalException(Messages.getMessage("XMLPartImplErr2"), null);
		}
		setContent(se, SOAPENVELOPE);
		return se;
	}
	
	private XMLSpine getContentAsXMLSpine() throws MessageException {
		XMLSpine spine = null;
		switch (contentType) {
		case (SPINE):
		 	spine = (XMLSpine) content;
			break;
		case (SOAPENVELOPE):
			spine = _convertSE2Spine((SOAPEnvelope) content);
			break;
		case (OM):
			spine = _convertOM2Spine((OMElement) content);
			break;
		default:
			throw ExceptionFactory.makeMessageInternalException(Messages.getMessage("XMLPartImplErr2"), null);
		}
        setContent(spine, SPINE);
		return spine;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getAsOMElement()
	 */
	public OMElement getAsOMElement() throws MessageException {
		return getContentAsOMElement();
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getAsSOAPEnvelope()
	 */
	public SOAPEnvelope getAsSOAPEnvelope() throws MessageException {
		return getContentAsSOAPEnvelope();
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getAsBlock(java.lang.Object, org.apache.axis2.jaxws.message.factory.BlockFactory)
	 */
	public Block getAsBlock(Object context, BlockFactory blockFactory) throws MessageException, XMLStreamException {
		
		// Get the content as the specfied block.  There is some optimization here to prevent unnecessary copies.
		// More optimization may be added later.
		Block block = null;
		if (contentType == OM) {
			block = blockFactory.createFrom((OMElement) content, context, null);
		} else if (contentType == SOAPENVELOPE && 
			blockFactory instanceof SOAPEnvelopeBlockFactory)	{
			block = blockFactory.createFrom((SOAPEnvelope) content, null, null );
		} else {
			block = blockFactory.createFrom(getAsOMElement(), null, null);
		}
		return block;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getProtocol()
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getStyle()
	 */
	public Style getStyle() {
        return style;
    }

    /* (non-Javadoc)
     * @see org.apache.axis2.jaxws.message.XMLPart#setStyle(javax.jws.soap.SOAPBinding.Style)
     */
    public void setStyle(Style style) throws MessageException {
        if (this.style != style) {
            if (contentType == SPINE) {
                // Must switch to something other than XMLSpine
                getContentAsOMElement();
            }
        }
        this.style = style;
    }

    public QName getOperationElement() throws MessageException {
        try {
            if (style != Style.RPC) {
                return null;
            }
            switch (contentType) {
            case OM:
                return ((org.apache.axiom.soap.SOAPEnvelope) content).getBody().
                getFirstElement().getQName();
            case SPINE:
                return ((XMLSpine) content).getOperationElement();
            case SOAPENVELOPE:
                Iterator it =((SOAPEnvelope) content).getBody().getChildElements();
                while (it.hasNext()) {
                    Node node = (Node) it.next();
                    if (node instanceof SOAPElement) {
                        Name name =((SOAPElement) node).getElementName();
                        return new QName(name.getURI(), name.getLocalName(), name.getPrefix());
                    }
                }
            }
            return null;
        } catch (SOAPException se) {
            throw ExceptionFactory.makeMessageException(se);
        }
    }

    public void setOperationElement(QName operationQName) throws MessageException {
        if (this.style == Style.RPC) {
            this.getContentAsXMLSpine().setOperationElement(operationQName);
        }
    }

    /* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getXMLPartContentType()
	 */
	public String getXMLPartContentType() {
        switch (contentType) {
        case OM:
            return "OM";
        case SOAPENVELOPE:
            return "SOAPENVELOPE";
        case SPINE:
            return "SPINE";
        default:
            return "UNKNOWN";
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis2.jaxws.message.XMLPart#getXMLStreamReader(boolean)
     */
    public XMLStreamReader getXMLStreamReader(boolean consume) throws MessageException {
		if (consumed) {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("XMLPartImplErr1"));
		}
		XMLStreamReader reader = null;
		if (contentType == SPINE) {
			reader = getContentAsXMLSpine().getXMLStreamReader(consume);
		} else {
			OMElement omElement = getContentAsOMElement();
			if (consume) {
				reader = omElement.getXMLStreamReaderWithoutCaching();
			} else {
				reader = omElement.getXMLStreamReader();
			}
		}
		setConsumed(consume);
		return reader;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getXMLFault()
	 */
	public XMLFault getXMLFault() throws MessageException {
        
        XMLFault xmlFault = null;
        
        if (isFault()) {
            // An XMLFault represents the detail elements as Blocks, so 
            // it makes sense to convert the representation to a Spine since
            // that is what we do in other situations where we need a Block.
            // Of course there is some additional optimization that could be done.
            // If we can determine that there are no detail elements, then we could
            // build the fault from an OM or SOAPEnvelope.  
            XMLSpine spine = getContentAsXMLSpine();
            xmlFault = spine.getXMLFault();
        } 
        return xmlFault;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#setXMLFault(org.apache.axis2.jaxws.message.XMLFault)
	 */
	public void setXMLFault(XMLFault xmlFault) throws MessageException {
		// Since the XMLFault contains detail Blocks it makes sence to convert
        // to an XMLSpine first (since that is what we do in the other calls that set Blocks).
        // Of course if the XMLFault did not have detail Blocks we might be able to 
        // do this without a transformation of the data.  
        XMLSpine spine = getContentAsXMLSpine();
        spine.setXMLFault(xmlFault);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#isFault()
	 */
	public boolean isFault() throws MessageException {
        if (consumed) {
            throw ExceptionFactory.makeMessageException(Messages.getMessage("XMLPartImplErr1"));
        }

        try {
            switch (contentType) {
            case OM:
                return XMLFaultUtils.isFault(
                        (org.apache.axiom.soap.SOAPEnvelope) getContentAsOMElement());
            case SOAPENVELOPE:
                return XMLFaultUtils.isFault(getContentAsSOAPEnvelope());
            case SPINE:
                return getContentAsXMLSpine().isFault();
            }
        } catch (SOAPException se) {
            throw ExceptionFactory.makeMessageException(se);
        }
        return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#isConsumed()
	 */
	public boolean isConsumed() {
		return consumed;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#outputTo(javax.xml.stream.XMLStreamWriter, boolean)
	 */
	public void outputTo(XMLStreamWriter writer, boolean consume) throws XMLStreamException, MessageException {
		if (consumed) {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("XMLPartImplErr1"));
		}
		if (contentType == SPINE) {
			getContentAsXMLSpine().outputTo(writer, consume);
		} else {
			OMElement omElement = getContentAsOMElement();
			if (consume) {
				omElement.serializeAndConsume(writer);
			} else {
				omElement.serialize(writer);
			}
		}
		setConsumed(consume);
		return;
		
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#traceString(java.lang.String)
	 */
	public String traceString(String indent) {
        // TODO
	    return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getBodyBlock(int, java.lang.Object, org.apache.axis2.jaxws.message.factory.BlockFactory)
	 */
	public Block getBodyBlock(int index, Object context, BlockFactory blockFactory) throws MessageException {
		return getContentAsXMLSpine().getBodyBlock(index, context, blockFactory);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getHeaderBlock(java.lang.String, java.lang.String, java.lang.Object, org.apache.axis2.jaxws.message.factory.BlockFactory)
	 */
	public Block getHeaderBlock(String namespace, String localPart, Object context, BlockFactory blockFactory) throws MessageException {
		return getContentAsXMLSpine().getHeaderBlock(namespace, localPart, context, blockFactory);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getNumBodyBlocks()
	 */
	public int getNumBodyBlocks() throws MessageException {
		return getContentAsXMLSpine().getNumBodyBlocks();
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#getNumHeaderBlocks()
	 */
	public int getNumHeaderBlocks() throws MessageException {
		return getContentAsXMLSpine().getNumHeaderBlocks();
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#removeBodyBlock(int)
	 */
	public void removeBodyBlock(int index) throws MessageException {
		getContentAsXMLSpine().removeBodyBlock(index);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#removeHeaderBlock(java.lang.String, java.lang.String)
	 */
	public void removeHeaderBlock(String namespace, String localPart) throws MessageException {
		getContentAsXMLSpine().removeHeaderBlock(namespace, localPart);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#setBodyBlock(int, org.apache.axis2.jaxws.message.Block)
	 */
	public void setBodyBlock(int index, Block block) throws MessageException {
		getContentAsXMLSpine().setBodyBlock(index, block);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.XMLPart#setHeaderBlock(java.lang.String, java.lang.String, org.apache.axis2.jaxws.message.Block)
	 */
	public void setHeaderBlock(String namespace, String localPart, Block block) throws MessageException {
		getContentAsXMLSpine().setHeaderBlock(namespace, localPart, block);
	}
    
    /*
     * (non-Javadoc)
     * @see org.apache.axis2.jaxws.message.XMLPart#getParent()
     */
    public Message getParent() {
        return parent;
    }

    /*
     * Set the backpointer to this XMLPart's parent Message
     */
    public void setParent(Message p) {
        parent = p;
    }
    
	/**
	 * Convert SOAPEnvelope into an OM tree
	 * @param se SOAPEnvelope
	 * @return OM
	 * @throws MessageException
	 */
	protected abstract OMElement _convertSE2OM(SOAPEnvelope se) throws MessageException;
	
	/**
	 * Convert XMLSpine into an OM tree
	 * @param spine XMLSpine
	 * @return OM
	 * @throws MessageException
	 */
	protected abstract OMElement _convertSpine2OM(XMLSpine spine) throws MessageException;
	
	/**
	 * Convert OM tree into a SOAPEnvelope
	 * @param om
	 * @return SOAPEnvelope
	 * @throws MessageException
	 */
	protected abstract SOAPEnvelope _convertOM2SE(OMElement om) throws MessageException;
	
	/**
	 * Convert XMLSpine into a SOAPEnvelope
	 * @param spine
	 * @return SOAPEnvelope
	 * @throws MessageException
	 */
	protected abstract SOAPEnvelope _convertSpine2SE(XMLSpine spine) throws MessageException;
	
	/**
	 * Convert OM into XMLSpine
	 * @param om
	 * @return
	 * @throws MessageException
	 */
	protected abstract XMLSpine _convertOM2Spine(OMElement om) throws MessageException;
	
	/**
	 * Convert SOAPEnvelope into XMLSPine
	 * @param SOAPEnvelope
	 * @return XMLSpine
	 * @throws MessageException
	 */
	protected abstract XMLSpine _convertSE2Spine(SOAPEnvelope se) throws MessageException;
	
	/**
	 * Create an empty, default spine for the specificed protocol
	 * @param protocol
	 * @return 
	 * @throws MessageException
	 */
	protected XMLSpine _createSpine(Protocol protocol) throws MessageException {
		// Default implementation is to simply construct the spine. 
		// Derived classes may wish to construct a different kind of XMLSpine
		return new XMLSpineImpl(protocol, getStyle());
	}
	
	private void setConsumed(boolean consume) { 
		if (consume) {
			this.consumed = true;
			if (log.isDebugEnabled()) {
				log.debug("Debug Monitoring When Block is Consumed");
				log.debug(JavaUtils.stackToString());
			}
		} else {
			consumed = false;
		}
	}
	
	
}
