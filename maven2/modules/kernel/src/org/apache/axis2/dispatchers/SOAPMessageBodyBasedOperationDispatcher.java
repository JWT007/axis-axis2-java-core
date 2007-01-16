/*
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

package org.apache.axis2.dispatchers;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.HandlerDescription;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SOAPMessageBodyBasedOperationDispatcher extends AbstractOperationDispatcher {

    public static final String NAME = "SOAPMessageBodyBasedOperationDispatcher";
    private static final Log log = LogFactory.getLog(SOAPMessageBodyBasedOperationDispatcher.class);
    private static final boolean isDebugEnabled = log.isDebugEnabled();

    public AxisOperation findOperation(AxisService service, MessageContext messageContext)
            throws AxisFault {
        OMElement bodyFirstChild = messageContext.getEnvelope().getBody().getFirstElement();
        QName operationName = null;
        if (bodyFirstChild == null) {
            return null;
        } else {
            if(isDebugEnabled){
            log.debug(
                    "Checking for Operation using SOAP message body's first child's local name : "
                            + bodyFirstChild.getLocalName());
            }
            operationName = new QName(bodyFirstChild.getLocalName());
        }

        AxisOperation axisOperation = service.getOperation(operationName);

        if (axisOperation == null) {
            axisOperation = service.getOperationByMessageName(bodyFirstChild.getLocalName());
        }
        return axisOperation;
    }

    public void initDispatcher() {
        init(new HandlerDescription(NAME));
    }
}
