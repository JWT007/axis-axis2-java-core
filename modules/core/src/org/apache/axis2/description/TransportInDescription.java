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

package org.apache.axis2.description;

import org.apache.axis2.AxisFault;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.engine.Phase;
import org.apache.axis2.phaseresolver.PhaseMetadata;
import org.apache.axis2.transport.TransportListener;

import javax.xml.namespace.QName;
import java.util.ArrayList;

/**
 * Represents a incoming transport deployed in AXis2
 */
public class TransportInDescription
        implements ParameterInclude {
    /**
     * Field paramInclude
     */
    protected final ParameterInclude paramInclude;

    /**
     * Field phasesInclude
     */

    /**
     * Field flowInclude
     */
    private Flow inFlow;
    /**
     * Field flowInclude
     */
    private Flow faultFlow;


    /**
     * Field name
     */
    protected QName name;


    protected TransportListener receiver;

    //to store handler in inFlow
    private Phase inPhase;
    //to store handler Fault in inFlow
    private Phase faultPhase;


    /**
     * Constructor AxisTransport
     *
     * @param name
     */
    public TransportInDescription(QName name) {
        paramInclude = new ParameterIncludeImpl();
        this.name = name;
        inPhase = new Phase(PhaseMetadata.TRANSPORT_PHASE);
        faultPhase = new Phase(PhaseMetadata.TRANSPORT_PHASE);
    }

    /**
     * Method getParameter
     *
     * @param name
     * @return
     */
    public Parameter getParameter(String name) {
        return paramInclude.getParameter(name);
    }

    public ArrayList getParameters() {
        return paramInclude.getParameters();
    }

    //to check whether the parameter is locked at any levle
    public boolean isParameterLocked(String parameterName) {
        return paramInclude.isParameterLocked(parameterName);
    }

    public void deserializeParameters(OMElement parameterElement) throws AxisFault {
        this.paramInclude.deserializeParameters(parameterElement);
    }

    /**
     * Method addParameter
     *
     * @param param
     */
    public void addParameter(Parameter param) throws AxisFault{
        paramInclude.addParameter(param);
    }

    /**
     * @return
     */
    public QName getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(QName name) {
        this.name = name;
    }

    public Flow getInFlow() {
        return inFlow;
    }

    public void setInFlow(Flow inFlow) {
        this.inFlow = inFlow;
    }

    public Flow getFaultFlow() {
        return faultFlow;
    }

    public void setFaultFlow(Flow faultFlow) {
        this.faultFlow = faultFlow;
    }

    /**
     * @return
     */
    public TransportListener getReceiver() {
        return receiver;
    }

    /**
     * @param receiver
     */
    public void setReceiver(TransportListener receiver) {
        this.receiver = receiver;
    }

    public Phase getInPhase() {
        return inPhase;
    }

    public void setInPhase(Phase inPhase) {
        this.inPhase = inPhase;
    }

    public Phase getFaultPhase() {
        return faultPhase;
    }

    public void setFaultPhase(Phase faultPhase) {
        this.faultPhase = faultPhase;
    }

}
