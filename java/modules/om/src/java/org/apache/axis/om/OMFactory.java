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
package org.apache.axis.om;

import javax.xml.namespace.QName;

/**
 * Class OMFactory
 */
public abstract class OMFactory {
    /**
     * @param localName
     * @param ns
     * @return
     */
    public abstract OMElement createOMElement(String localName, OMNamespace ns);

    /**
     * @param localName
     * @param ns
     * @param parent
     * @param builder
     * @return
     */
    public abstract OMElement createOMElement(String localName, OMNamespace ns,
                                              OMElement parent,
                                              OMXMLParserWrapper builder);

    /**
     * This is almost the same as as createOMElement(localName,OMNamespace) method above.
     * But some people may, for some reason, need to use the conventional method of putting a namespace.
     * Or in other words people might not want to use the new OMNamespace.
     * Well, this is for those people.
     *
     * @param localName
     * @param namespaceURI
     * @param namespacePrefix
     * @return
     */
    public abstract OMElement createOMElement(String localName,
                                              String namespaceURI,
                                              String namespacePrefix);

    /**
     * QName(localPart),
     * QName(namespaceURI, localPart) - a prefix will be assigned to this
     * QName(namespaceURI, localPart, prefix)
     *
     * @param qname
     * @param parent
     * @return
     * @throws OMException
     */
    public abstract OMElement createOMElement(QName qname, OMElement parent)
            throws OMException;

    /**
     * @param uri
     * @param prefix
     * @return
     */
    public abstract OMNamespace createOMNamespace(String uri, String prefix);

    /**
     * @param parent
     * @param text
     * @return
     */
    public abstract OMText createText(OMElement parent, String text);

    /**
     * @param s
     * @return
     */
    public abstract OMText createText(String s);

    /**
     * @param envelope
     * @return
     */
    public abstract SOAPBody createSOAPBody(SOAPEnvelope envelope);

    /**
     * @param envelope
     * @param builder
     * @return
     */
    public abstract SOAPBody createSOAPBody(SOAPEnvelope envelope,
                                            OMXMLParserWrapper builder);

    /**
     * @param ns
     * @param builder
     * @return
     */
    public abstract SOAPEnvelope createSOAPEnvelope(OMNamespace ns,
                                                    OMXMLParserWrapper builder);

    /**
     * @param ns
     * @return
     */
    public abstract SOAPEnvelope createSOAPEnvelope(OMNamespace ns);

    /**
     * @param envelope
     * @return
     */
    public abstract SOAPHeader createSOAPHeader(SOAPEnvelope envelope);

    /**
     * @param envelope
     * @param builder
     * @return
     */
    public abstract SOAPHeader createSOAPHeader(SOAPEnvelope envelope,
                                                OMXMLParserWrapper builder);

    /**
     * @param localName
     * @param ns
     * @return
     */
    public abstract SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                          OMNamespace ns);

    /**
     * @param localName
     * @param ns
     * @param parent
     * @param builder
     * @return
     */
    public abstract SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                          OMNamespace ns, OMElement parent, OMXMLParserWrapper builder);

    /**
     * @param parent
     * @param e
     * @return
     */
    public abstract SOAPFault createSOAPFault(SOAPBody parent, Exception e);

    /**
     * @param ns
     * @param parent
     * @param builder
     * @return
     */
    public abstract SOAPFault createSOAPFault(OMNamespace ns, SOAPBody parent,
                                              OMXMLParserWrapper builder);

    // make the constructor protected

    /**
     * Constructor OMFactory
     */
    protected OMFactory() {
    }

    /**
     * Method newInstance
     *
     * @return
     */
    public static OMFactory newInstance() {
        return FactoryFinder.findFactory(null);
    }

    /**
     * Method getDefaultEnvelope
     *
     * @return
     */
    public abstract SOAPEnvelope getDefaultEnvelope();
}
