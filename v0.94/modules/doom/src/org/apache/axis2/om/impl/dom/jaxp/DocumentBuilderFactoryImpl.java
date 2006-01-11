package org.apache.axis2.om.impl.dom.jaxp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    public DocumentBuilderFactoryImpl() {
        super();
    }

    public DocumentBuilder newDocumentBuilder()
            throws ParserConfigurationException {
        return new DocumentBuilderImpl();
    }

    public Object getAttribute(String arg0) throws IllegalArgumentException {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setAttribute(String arg0, Object arg1)
            throws IllegalArgumentException {
        // // TODO
        // throw new UnsupportedOperationException("TODO");
    }

    public static DocumentBuilderFactory newInstance() {
        return new DocumentBuilderFactoryImpl();
    }

    public void setFeature(String arg0, boolean arg1)
            throws ParserConfigurationException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public boolean getFeature(String arg0) throws ParserConfigurationException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }
}
