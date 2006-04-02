package org.apache.wsdl.extensions.impl;

import org.apache.wsdl.extensions.SOAPHeader;

import javax.xml.namespace.QName;

public class SOAPHeadeImpl extends SOAPBodyImpl implements SOAPHeader {

    private QName messageName = null;
    private String part = null;
    private QName element = null;

    public SOAPHeadeImpl() {
        this.type = SOAP_11_HEADER;
    }

    public SOAPHeadeImpl(QName type) {
        this.type = type;
    }

    public QName getMessage() {
        return messageName;
    }

    public void setMessage(QName message) {
        this.messageName = message;
    }

    public String part() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public QName getElement() {
        return element;
    }

    public void setElement(QName element) {
        this.element = element;
    }
}
