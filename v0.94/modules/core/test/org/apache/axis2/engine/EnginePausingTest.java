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

package org.apache.axis2.engine;

import junit.framework.TestCase;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.InOutAxisOperation;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.axis2.transport.http.CommonsHTTPTransportSender;

import javax.xml.namespace.QName;
import java.util.ArrayList;

public class EnginePausingTest extends TestCase {

    private QName serviceName = new QName("NullService");
    private QName operationName = new QName("DummyOp");
    private ConfigurationContext configConetxt;

    private TransportOutDescription transportOut;
    private TransportInDescription transportIn;
    private MessageContext mc;
    private ArrayList executedHandlers;

    public EnginePausingTest(String arg0) {
        super(arg0);
        executedHandlers = new ArrayList();
        AxisConfiguration engineRegistry = new AxisConfiguration();
        configConetxt = new ConfigurationContext(engineRegistry);
        transportOut = new TransportOutDescription(new QName("null"));
        transportOut.setSender(new CommonsHTTPTransportSender());
        transportIn = new TransportInDescription(new QName("null"));

    }

    protected void setUp() throws Exception {

        AxisService service = new AxisService(serviceName.getLocalPart());
        configConetxt.getAxisConfiguration().addService(service);

        AxisOperation axisOp = new InOutAxisOperation(operationName);
        axisOp.setMessageReceiver(new MessageReceiver() {
            public void receive(MessageContext messageCtx) {

            }
        });
        service.addOperation(axisOp);
        service.mapActionToOperation(operationName.getLocalPart(), axisOp);

        mc = new MessageContext();
        mc.setConfigurationContext(configConetxt);
        mc.setTransportIn(transportIn);
        mc.setTransportOut(transportOut);

        mc.setTransportOut(transportOut);
        mc.setServerSide(true);
        mc.setProperty(MessageContext.TRANSPORT_OUT, System.out);
        SOAPFactory omFac = OMAbstractFactory.getSOAP11Factory();
        mc.setEnvelope(omFac.getDefaultEnvelope());

        Phase phase1 = new Phase("1");
        phase1.addHandler(new TempHandler(1));
        phase1.addHandler(new TempHandler(2));
        phase1.addHandler(new TempHandler(3));
        phase1.addHandler(new TempHandler(4));
        phase1.addHandler(new TempHandler(5));
        phase1.addHandler(new TempHandler(6));
        phase1.addHandler(new TempHandler(7));
        phase1.addHandler(new TempHandler(8));
        phase1.addHandler(new TempHandler(9));

        Phase phase2 = new Phase("2");
        phase2.addHandler(new TempHandler(10));
        phase2.addHandler(new TempHandler(11));
        phase2.addHandler(new TempHandler(12));
        phase2.addHandler(new TempHandler(13));
        phase2.addHandler(new TempHandler(14));
        phase2.addHandler(new TempHandler(15, true));
        phase2.addHandler(new TempHandler(16));
        phase2.addHandler(new TempHandler(17));
        phase2.addHandler(new TempHandler(18));

        Phase phase3 = new Phase("3");
        phase3.addHandler(new TempHandler(19));
        phase3.addHandler(new TempHandler(20));
        phase3.addHandler(new TempHandler(21));
        phase3.addHandler(new TempHandler(22));
        phase3.addHandler(new TempHandler(23));
        phase3.addHandler(new TempHandler(24));
        phase3.addHandler(new TempHandler(25));
        phase3.addHandler(new TempHandler(26));
        phase3.addHandler(new TempHandler(27));

        //TODO
        axisOp.getRemainingPhasesInFlow().add(phase1);
        axisOp.getRemainingPhasesInFlow().add(phase2);
        axisOp.getRemainingPhasesInFlow().add(phase3);

        mc.setWSAAction(operationName.getLocalPart());
        mc.setSoapAction(operationName.getLocalPart());
        System.out.flush();

    }

    public void testReceive() throws Exception {
        mc.setTo(
                new EndpointReference("axis2/services/NullService"));
        mc.setWSAAction("DummyOp");
        AxisEngine engine = new AxisEngine(configConetxt);
        engine.receive(mc);
        assertEquals(14, executedHandlers.size());
        for (int i = 0; i < 14; i++) {
            assertEquals(((Integer) executedHandlers.get(i)).intValue(),
                    i + 1);
        }
        engine.resume(mc);

        assertEquals(27, executedHandlers.size());
        for (int i = 15; i < 27; i++) {
            assertEquals(((Integer) executedHandlers.get(i)).intValue(),
                    i + 1);
        }

    }

    public class TempHandler extends AbstractHandler {
        private static final long serialVersionUID = 6201231320871883424L;
		private Integer index;
        private boolean pause = false;

        public TempHandler(int index, boolean pause) {
            this.index = new Integer(index);
            this.pause = pause;
            init(new HandlerDescription(new QName("handler" + index)));
        }

        public TempHandler(int index) {
            this.index = new Integer(index);
            init(new HandlerDescription(new QName("handler" + index)));
        }

        public void invoke(MessageContext msgContext) throws AxisFault {
            if (pause) {
                msgContext.pause();
                pause = false;
            } else {
                executedHandlers.add(index);
            }
        }

    }
}
