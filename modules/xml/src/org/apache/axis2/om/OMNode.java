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

package org.apache.axis2.om;

import org.apache.axis2.om.impl.OMOutputImpl;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Interface OMNode
 */
public interface OMNode {
    /**
     * The node is an <code>Element</code>.
     */
    public static final short ELEMENT_NODE = 1;

    /**
     * The node is a <code>Text</code> node.
     */
    public static final short TEXT_NODE = XMLStreamConstants.CHARACTERS;

    /**
     * The node is a <code>CDATASection</code>.
     */
    public static final short CDATA_SECTION_NODE = XMLStreamConstants.CDATA;

    /**
     * The node is a <code>Comment</code>.
     */
    public static final short COMMENT_NODE = XMLStreamConstants.COMMENT;

    /**
     * This node is a <code>DTD</code>.
     */
    public static final short DTD_NODE = XMLStreamConstants.DTD;

    /**
     * This node is a <code>ProcessingInstruction</code>.
     */
    public static final short PI_NODE = XMLStreamConstants.PROCESSING_INSTRUCTION;

    /**
     * This node is a <code>Entity Reference</code>.
     */
    public static final short ENTITY_REFERENCE_NODE = XMLStreamConstants.ENTITY_REFERENCE;

    /**
     * This node is a <code>Entity Reference</code>.
     */
    public static final short SPACE_NODE = XMLStreamConstants.SPACE;

    /**
     * This method should return the immediate parent of the node.
     * Parent is always an Element
     *
     * @return
     * @throws OMException
     */
    public OMContainer getParent() throws OMException;

    /**
     * This will give the next sibling. This can be an OMAttribute for OMAttribute or OMText or OMELement for others.
     *
     * @return
     * @throws OMException
     */
    public OMNode getNextSibling() throws OMException;

    /**
     * this will indicate whether parser has parsed this information item completely or not.
     * If somethings info are not available in the item, one has to check this attribute to make sure that, this
     * item has been parsed completely or not.
     *
     * @return boolean
     */
    public boolean isComplete();

    /**
     * This will remove this information item and its children, from the model completely.
     * Important  to note that this method will detach the OMNode once it is fully built.
     * Half built nodes are not to be detached!
     *
     * @throws OMException
     */
    public OMNode detach() throws OMException;

    /**
     * Discards a Node. Discrad goes to the parser level and if the element is not
     * completely built, then it will be completely skipped at the parser level
     *
     * @throws OMException
     */
    public void discard() throws OMException;

    /**
     * This will insert a sibling just after the current information item.
     *
     * @param sibling
     * @throws OMException
     */
    public void insertSiblingAfter(OMNode sibling) throws OMException;

    /**
     * This will insert a sibling just before the current information item
     *
     * @param sibling
     * @throws OMException
     */
    public void insertSiblingBefore(OMNode sibling) throws OMException;

    /**
     * This is to get the type of node, as this is the super class of all the nodes
     *
     * @return
     * @throws OMException
     */
    public int getType() throws OMException;

    /**
     * get the previous sibling
     *
     * @return node
     */
    public OMNode getPreviousSibling();

    /**
     * Serialize the node with caching
     *
     * @param xmlWriter
     * @throws XMLStreamException
     * @see #serializeWithCache(org.apache.axis2.om.impl.OMOutputImpl)
     */
    public void serializeWithCache(XMLStreamWriter xmlWriter)
            throws XMLStreamException;

    /**
     * Serialize the node with caching
     *
     * @param omOutput
     * @throws XMLStreamException
     * @see #serializeWithCache(org.apache.axis2.om.impl.OMOutputImpl)
     */
    public void serializeWithCache(org.apache.axis2.om.impl.OMOutputImpl omOutput)
            throws XMLStreamException;

    /**
     * Serialize the node without caching
     *
     * @param xmlWriter
     * @throws XMLStreamException
     * @see #serialize(org.apache.axis2.om.impl.OMOutputImpl)
     */
    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException;

    /**
     * Serialize the node without caching
     *
     * @param omOutput
     * @throws XMLStreamException
     * @see #serialize(org.apache.axis2.om.impl.OMOutputImpl)
     */
    public void serialize(OMOutputImpl omOutput) throws XMLStreamException;

    /**
     * Builds itself
     */
    public void build();
}
