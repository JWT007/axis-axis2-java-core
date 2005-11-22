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

package org.apache.wsdl.impl;

import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.wsdl.MessageReference;

import javax.xml.namespace.QName;

public class MessageReferenceImpl extends ExtensibleComponentImpl
        implements MessageReference {

    // Referes to the MEP the Message relates to.

    /**
     * Field messageLabel
     */
    private String messageLabel;

    // Can be "in" or "out" depending on the elementQName name being "input" or "output" respectively;

    /**
     * Field Direction
     */
    private String Direction;

    // TODO Do we need it "Message content model"

    /**
     * Field elementQName
     */
    private QName elementQName;
    private XmlSchemaElement elementSchema;

    /**
     * Method getDirection
     *
     * @return
     */
    public String getDirection() {
        return Direction;
    }

    /**
     * Method setDirection
     *
     * @param direction
     */
    public void setDirection(String direction) {
        Direction = direction;
    }

    /**
     * Returns an Element which refers to the actual message that will get transported. This Element
     * Abstracts all the Message Parts that was defined in the WSDL 1.1.
     *
     * @return
     */
    public QName getElementQName() {
        return elementQName;
    }

    /**
     * Sets the Element that will Abstract the actual message. All the parts defined in WSDL 1.1
     * per message should be Encapsulated in this Element.
     *
     * @param element
     */
    public void setElementQName(QName element) {
        this.elementQName = element;
    }

    /**
     * Method getMessageLabel
     *
     * @return
     */
    public String getMessageLabel() {
        return messageLabel;
    }

    /**
     * Method setMessageLabel
     *
     * @param messageLabel
     */
    public void setMessageLabel(String messageLabel) {
        this.messageLabel = messageLabel;
    }

    public XmlSchemaElement getElementSchema() {
        return elementSchema;
    }

    public void setElementSchema(XmlSchemaElement element) {
        this.elementSchema = element;
    }
}
