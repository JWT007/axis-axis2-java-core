package org.apache.axis2.saaj;

import org.apache.axis2.om.DOOMAbstractFactory;
import org.apache.axis2.om.impl.dom.ElementImpl;
import org.apache.axis2.soap.SOAPFaultCode;
import org.apache.axis2.soap.SOAPFaultReason;
import org.apache.axis2.soap.SOAPFaultRole;
import org.apache.axis2.soap.SOAPFaultText;
import org.apache.axis2.soap.SOAPFaultValue;
import org.apache.axis2.soap.impl.dom.soap11.SOAP11FaultDetailImpl;
import org.apache.axis2.soap.impl.dom.soap11.SOAP11FaultReasonImpl;
import org.apache.axis2.soap.impl.dom.soap11.SOAP11FaultRoleImpl;
import org.apache.axis2.soap.impl.dom.soap11.SOAP11FaultTextImpl;

import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import java.util.Locale;

public class SOAPFaultImpl extends SOAPBodyElementImpl implements SOAPFault {

    protected org.apache.axis2.soap.SOAPFault fault;

    /**
     * @param element
     */
    public SOAPFaultImpl(org.apache.axis2.soap.SOAPFault element) {
        super((ElementImpl) element);
        fault = element;
    }

    /**
     * Sets this <CODE>SOAPFault</CODE> object with the given
     * fault code.
     * <p/>
     * <P>Fault codes, which given information about the fault,
     * are defined in the SOAP 1.1 specification.</P>
     *
     * @param faultCode a <CODE>String</CODE> giving
     *                  the fault code to be set; must be one of the fault codes
     *                  defined in the SOAP 1.1 specification
     * @throws SOAPException if there was an error in
     *                       adding the <CODE>faultCode</CODE> to the underlying XML
     *                       tree.
     * @see #getFaultCode() getFaultCode()
     */
    public void setFaultCode(String faultCode) throws SOAPException {
        org.apache.axis2.soap.SOAPFactory soapFactory = DOOMAbstractFactory.getSOAP11Factory();
        SOAPFaultCode fCode = soapFactory.createSOAPFaultCode(fault);
        SOAPFaultValue fValue = soapFactory.createSOAPFaultValue(fCode);
        fCode.setValue(fValue);
        fValue.setText(faultCode);

        this.fault.setCode(fCode);
    }

    /**
     * Gets the fault code for this <CODE>SOAPFault</CODE>
     * object.
     *
     * @return a <CODE>String</CODE> with the fault code
     * @see #setFaultCode(java.lang.String) setFaultCode(java.lang.String)
     */
    public String getFaultCode() {
        return this.fault.getCode().getValue().getText();
    }

    /* (non-Javadoc)
      * @see javax.xml.soap.SOAPFault#setFaultActor(java.lang.String)
      */
    public void setFaultActor(String faultActor) throws SOAPException {
        if (this.fault.getRole() == null) {
            SOAP11FaultRoleImpl faultRoleImpl = new SOAP11FaultRoleImpl(this.fault);
            faultRoleImpl.setRoleValue(faultActor);
            this.fault.setRole(faultRoleImpl);

            /* SOAPFactory soapFactory = DOOMAbstractFactory.getSOAP11Factory();
            SOAPFaultNode fNode = soapFactory.createSOAPFaultNode(fault);
            fNode.setNodeValue(faultActor);*/

        } else {
            SOAPFaultRole role = this.fault.getRole();
            role.setRoleValue(faultActor);
        }
    }

    /* (non-Javadoc)
      * @see javax.xml.soap.SOAPFault#getFaultActor()
      */
    public String getFaultActor() {
        if (this.fault.getRole() != null) {
            return this.fault.getRole().getRoleValue();
        }
        return null;

//        return fault.getNode().getNodeValue();
    }

    /**
     * Sets the fault string for this <CODE>SOAPFault</CODE>
     * object to the given string.
     *
     * @param faultString a <CODE>String</CODE>
     *                    giving an explanation of the fault
     * @throws SOAPException if there was an error in
     *                       adding the <CODE>faultString</CODE> to the underlying XML
     *                       tree.
     * @see #getFaultString() getFaultString()
     */
    public void setFaultString(String faultString) throws SOAPException {
        if (this.fault.getReason() != null) {
            SOAPFaultReason reason = this.fault.getReason();
            if (reason.getSOAPText() != null) {
                reason.getSOAPText().setText(faultString);
            } else {
                SOAPFaultText text = new SOAP11FaultTextImpl(reason);
                text.setText(faultString);
                reason.setSOAPText(text);
            }
        } else {
            org.apache.axis2.soap.SOAPFactory soapFactory = DOOMAbstractFactory.getSOAP11Factory();
            SOAPFaultReason fReason = soapFactory.createSOAPFaultReason(fault);
            SOAPFaultText fText = soapFactory.createSOAPFaultText(fReason);
            fText.setText(faultString);
            fReason.setSOAPText(fText);
        }
    }

    /* (non-Javadoc)
      * @see javax.xml.soap.SOAPFault#getFaultString()
      */
    public String getFaultString() {
        if (this.fault.getReason() != null && this.fault.getReason().getSOAPText() != null) {
            return this.fault.getReason().getSOAPText().getText();
        }
        return null;
    }

    /* (non-Javadoc)
      * @see javax.xml.soap.SOAPFault#getDetail()
      */
    public Detail getDetail() {
        if (this.fault.getDetail() != null) {
            return new DetailImpl(this.fault.getDetail());
        }
        return null;
    }

    /* (non-Javadoc)
      * @see javax.xml.soap.SOAPFault#addDetail()
      */
    public Detail addDetail() throws SOAPException {
        SOAP11FaultDetailImpl omDetail = new SOAP11FaultDetailImpl(this.fault);
        return new DetailImpl(omDetail);
    }

    /* (non-Javadoc)
      * @see javax.xml.soap.SOAPFault#setFaultCode(javax.xml.soap.Name)
      */
    public void setFaultCode(Name name) throws SOAPException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    /* (non-Javadoc)
      * @see javax.xml.soap.SOAPFault#getFaultCodeAsName()
      */
    public Name getFaultCodeAsName() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    /* (non-Javadoc)
      * @see javax.xml.soap.SOAPFault#setFaultString(java.lang.String, java.util.Locale)
      */
    public void setFaultString(String faultString, Locale locale) throws SOAPException {
        if (this.fault.getReason() != null) {
            SOAPFaultReason reason = this.fault.getReason();
            if (reason.getSOAPText() != null) {
                reason.getSOAPText().setText(faultString);
                reason.getSOAPText().setLang(locale.getLanguage());
            } else {
                SOAPFaultText text = new SOAP11FaultTextImpl(reason);
                text.setText(faultString);
                text.setLang(locale.getLanguage());
                reason.setSOAPText(text);
            }
        } else {
            SOAPFaultReason reason = new SOAP11FaultReasonImpl(this.fault);
            SOAPFaultText text = new SOAP11FaultTextImpl(reason);
            text.setText(faultString);
            text.setLang(locale.getLanguage());
            reason.setSOAPText(text);
            this.fault.setReason(reason);
        }
    }

    /* (non-Javadoc)
      * @see javax.xml.soap.SOAPFault#getFaultStringLocale()
      */
    public Locale getFaultStringLocale() {
        //We only save the language in OM,
        //Can we construct a Locale with it :-?
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

}
