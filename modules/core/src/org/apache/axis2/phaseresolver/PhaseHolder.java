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


package org.apache.axis2.phaseresolver;

import org.apache.axis2.deployment.DeploymentErrorMsgs;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.engine.Handler;
import org.apache.axis2.engine.Phase;
import org.apache.axis2.i18n.Messages;

import java.util.ArrayList;

/**
 * This class hold all the phases found in the services.xml and server.xml
 */
public class PhaseHolder {
    private ArrayList phaseList;

    public PhaseHolder() {
    }

    public PhaseHolder(ArrayList phases) {
        this.phaseList = phases;
    }

    /**
     * Method addHandler
     *
     * @param handlerDesc
     * @throws PhaseException
     */
    public void addHandler(HandlerDescription handlerDesc) throws PhaseException {
        String phaseName = handlerDesc.getRules().getPhaseName();

        if (isPhaseExist(phaseName)) {
            getPhase(phaseName).addHandler(handlerDesc);
        } else {
            throw new PhaseException(Messages.getMessage(DeploymentErrorMsgs.INVALID_PHASE,
                    phaseName, handlerDesc.getName().getLocalPart()));
        }
    }

    /**
     * This method is to build the transport phase , here load the corresponding handlers and added them
     * in to correct phase
     *
     * @param phase
     * @param handlers
     * @throws PhaseException
     */
    public void buildTransportHandlerChain(Phase phase, ArrayList handlers) throws PhaseException {
        try {
            Class handlerClass = null;
            Handler handler;

            for (int i = 0; i < handlers.size(); i++) {
                HandlerDescription description = (HandlerDescription) handlers.get(i);

                handlerClass = Class.forName(description.getClassName(), true,
                        Thread.currentThread().getContextClassLoader());
                handler = (Handler) handlerClass.newInstance();
                handler.init(description);
                description.setHandler(handler);
                phase.addHandler(description.getHandler());
            }
        } catch (ClassNotFoundException e) {
            throw new PhaseException(e);
        } catch (InstantiationException e) {
            throw new PhaseException(e);
        } catch (IllegalAccessException e) {
            throw new PhaseException(e);
        }
    }

    /**
     * this method is used to get the actual phase object given in the phase array list
     *
     * @param phaseName
     * @return
     */
    private Phase getPhase(String phaseName) {
        for (int i = 0; i < phaseList.size(); i++) {
            Phase phase = (Phase) phaseList.get(i);

            if (phase.getPhaseName().equals(phaseName)) {
                return phase;
            }
        }

        return null;
    }

    /**
     * Method isPhaseExist
     *
     * @param phaseName
     * @return
     */
    private boolean isPhaseExist(String phaseName) {
        for (int i = 0; i < phaseList.size(); i++) {
            Phase phase = (Phase) phaseList.get(i);

            if (phase.getPhaseName().equals(phaseName)) {
                return true;
            }
        }

        return false;
    }
}
