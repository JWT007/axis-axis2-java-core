package org.apache.axis2.description;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.wsdl.WSDLConstants;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: Deepal Jayasinghe
 * Date: Oct 3, 2005
 * Time: 2:07:15 PM
 */
public class InOutAxisOperation extends AxisOperation {
    private AxisMessage inFaultMessage;
    private AxisMessage inMessage;
    private AxisMessage outFaultMessage;
    private AxisMessage outMessage;

    public InOutAxisOperation() {
        super();
        createMessages();
    }

    public InOutAxisOperation(QName name) {
        super(name);
        createMessages();
    }

    public void addMessage(AxisMessage message, String label) {
        if (WSDLConstants.MESSAGE_LABEL_OUT_VALUE.equals(label)) {
            outMessage = message;
        } else if (WSDLConstants.MESSAGE_LABEL_IN_VALUE.equals(label)) {
            inMessage = message;
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    public void addMessageContext(MessageContext msgContext, OperationContext opContext)
            throws AxisFault {
        HashMap mep = opContext.getMessageContexts();
        MessageContext inMsgContext = (MessageContext) mep.get(MESSAGE_LABEL_IN_VALUE);
        MessageContext outmsgContext = (MessageContext) mep.get(MESSAGE_LABEL_OUT_VALUE);

        if ((inMsgContext != null) && (outmsgContext != null)) {
            throw new AxisFault("Invalid message addition , operation context completed");
        }

        if (inMsgContext == null) {
            mep.put(MESSAGE_LABEL_IN_VALUE, msgContext);
        } else {
            mep.put(MESSAGE_LABEL_OUT_VALUE, msgContext);
            opContext.setComplete(true);
            opContext.cleanup();
        }
    }

    private void createMessages() {
        inMessage = new AxisMessage();
        inMessage.setDirection(WSDLConstants.WSDL_MESSAGE_DIRECTION_IN);
        inMessage.setParent(this);
        
        inFaultMessage = new AxisMessage();
        inFaultMessage.setParent(this);
        
        outFaultMessage = new AxisMessage();
        outFaultMessage.setParent(this);
        
        outMessage = new AxisMessage();
        outMessage.setDirection(WSDLConstants.WSDL_MESSAGE_DIRECTION_OUT);
        outMessage.setParent(this);
    }

    public AxisMessage getMessage(String label) {
        if (WSDLConstants.MESSAGE_LABEL_OUT_VALUE.equals(label)) {
            return outMessage;
        } else if (WSDLConstants.MESSAGE_LABEL_IN_VALUE.equals(label)) {
            return inMessage;
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    public ArrayList getPhasesInFaultFlow() {
        return inFaultMessage.getMessageFlow();
    }

    public ArrayList getPhasesOutFaultFlow() {
        return outFaultMessage.getMessageFlow();
    }

    public ArrayList getPhasesOutFlow() {
        return outMessage.getMessageFlow();
    }

    public ArrayList getRemainingPhasesInFlow() {
        return inMessage.getMessageFlow();
    }

    public void setPhasesInFaultFlow(ArrayList list) {
        inFaultMessage.setMessageFlow(list);
    }

    public void setPhasesOutFaultFlow(ArrayList list) {
        outFaultMessage.setMessageFlow(list);
    }

    public void setPhasesOutFlow(ArrayList list) {
        outMessage.setMessageFlow(list);
    }

    public void setRemainingPhasesInFlow(ArrayList list) {
        inMessage.setMessageFlow(list);
    }
}
