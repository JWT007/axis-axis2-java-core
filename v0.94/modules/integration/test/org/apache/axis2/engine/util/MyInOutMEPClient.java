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

package org.apache.axis2.engine.util;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Call;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.soap.SOAPEnvelope;

import javax.xml.namespace.QName;

public class MyInOutMEPClient extends Call {

    public MyInOutMEPClient(String clientHome) throws AxisFault {
        super(clientHome);    
    }

    public SOAPEnvelope invokeBlockingWithEnvelopeOut(String axisop,
                                                      OMElement toSend) throws AxisFault {
        AxisOperation axisOperation =
                getServiceContext().getAxisService().getOperation(new QName(axisop));
        if (axisOperation == null) {
            axisOperation = new OutInAxisOperation();
            axisOperation.setName(new QName(axisop));
            axisOperation.setRemainingPhasesInFlow(axisOperationTemplate.getRemainingPhasesInFlow());
            axisOperation.setPhasesOutFlow(axisOperationTemplate.getPhasesOutFlow());
            axisOperation.setPhasesInFaultFlow(axisOperationTemplate.getPhasesInFaultFlow());
            axisOperation.setPhasesOutFaultFlow(axisOperationTemplate.getPhasesOutFaultFlow());
            getServiceContext().getAxisService().addOperation(axisOperation);
        }
        MessageContext msgctx = prepareTheSOAPEnvelope(toSend);

        MessageContext responseContext = super.invokeBlocking(axisOperation,
                msgctx);
        return responseContext.getEnvelope();
    }


    public SOAPEnvelope invokeBlockingWithEnvelopeOut(String axisop,
                                                      SOAPEnvelope reqEnvelope) throws AxisFault {
        AxisOperation axisOperation =
                getServiceContext().getAxisService().getOperation(new QName(axisop));
        if (axisOperation == null) {
            axisOperation = new OutInAxisOperation();
            axisOperation.setName(new QName(axisop));
            axisOperation.setRemainingPhasesInFlow(axisOperationTemplate.getRemainingPhasesInFlow());
            axisOperation.setPhasesOutFlow(axisOperationTemplate.getPhasesOutFlow());
            axisOperation.setPhasesInFaultFlow(axisOperationTemplate.getPhasesInFaultFlow());
            axisOperation.setPhasesOutFaultFlow(axisOperationTemplate.getPhasesOutFaultFlow());
            getServiceContext().getAxisService().addOperation(axisOperation);
        }
        MessageContext msgctx = getMessageContext(reqEnvelope);

        MessageContext responseContext = super.invokeBlocking(axisOperation,
                msgctx);
        return responseContext.getEnvelope();
    }

    protected MessageContext getMessageContext(SOAPEnvelope envelope) throws AxisFault {
        MessageContext msgctx = new MessageContext();
        msgctx.setConfigurationContext(getServiceContext().getConfigurationContext());
        msgctx.setEnvelope(envelope);
        return msgctx;
    }
}
