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
package org.apache.axis.phaseresolver;

import org.apache.axis.description.AxisGlobal;
import org.apache.axis.description.AxisModule;
import org.apache.axis.description.AxisService;
import org.apache.axis.description.AxisTransport;
import org.apache.axis.description.Flow;
import org.apache.axis.description.HandlerMetadata;
import org.apache.axis.engine.AxisFault;
import org.apache.axis.engine.EngineRegistry;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Class PhaseResolver
 */
public class PhaseResolver {
    /**
     * Field engineRegistry
     */
    private final EngineRegistry engineRegistry;

    /**
     * Field axisService
     */
    private AxisService axisService;

    /**
     * Field phaseHolder
     */
    private PhaseHolder phaseHolder;

    /**
     * default constructor , to obuild chains for AxisGlobal
     *
     * @param engineRegistry
     */
    public PhaseResolver(EngineRegistry engineRegistry) {
        this.engineRegistry = engineRegistry;
    }

    /**
     * Constructor PhaseResolver
     *
     * @param engineRegistry
     * @param axisService
     */
    public PhaseResolver(EngineRegistry engineRegistry,
                         AxisService axisService) {
        this.engineRegistry = engineRegistry;
        this.axisService = axisService;
    }

    /**
     * Method buildchains
     *
     * @throws PhaseException
     * @throws AxisFault
     */
    public void buildchains() throws PhaseException, AxisFault {
        for (int i = 1; i < 4; i++) {
            buildExcutionChains(i);
        }
    }

    /**
     * this opeartion is used to build all the three cahins ,
     * so type varible is used to difrenciate them
     * type = 1 inflow
     * type = 2 out flow
     * type = 3 fault flow
     *
     * @param type
     * @throws AxisFault
     * @throws PhaseException
     */
    private void buildExcutionChains(int type)
            throws AxisFault, PhaseException {
        int flowtype = type;
        ArrayList allHandlers = new ArrayList();

        // int count = server.getModuleCount();
        // QName moduleName;
        AxisModule module;
        Flow flow = null;

        /*
         * //adding server specific handlers  . global
         * for(int intA=0 ; intA < count; intA ++){
         * moduleName = server.getModule(intA);
         * module = engineRegistry.getModule(moduleName);
         * switch (flowtype){
         * case 1 : {
         * flow = module.getInFlow();
         * break;
         * }
         * case  2 : {
         * flow = module.getOutFlow();
         * break;
         * }
         * case 3 : {
         * flow = module.getFaultFlow();
         * break;
         * }
         * }
         * for(int j= 0 ; j < flow.getHandlerCount() ; j++ ){
         * HandlerMetaData metadata = flow.getHandler(j);
         * //todo change this in properway
         * if (metadata.getRules().getPhaseName().equals("")){
         * metadata.getRules().setPhaseName("global");
         * }
         * allHandlers.add(metadata);
         * }
         * }
         */

        // service module handlers
        Collection collection = axisService.getModules();
        Iterator itr = collection.iterator();
        while (itr.hasNext()) {
            QName moduleref = (QName) itr.next();

            // }
            // Vector modules = (Vector)axisService.getModules();
            // for (int i = 0; i < modules.size(); i++) {
            // QName moduleref = (QName) modules.elementAt(i);
            module = engineRegistry.getModule(moduleref);
            switch (flowtype) {
                case 1:
                    {
                        flow = module.getInFlow();
                        break;
                    }
                case 2:
                    {
                        flow = module.getOutFlow();
                        break;
                    }
                case 3:
                    {
                        flow = module.getFaultFlow();
                        break;
                    }
            }
            if (flow != null) {
                for (int j = 0; j < flow.getHandlerCount(); j++) {
                    HandlerMetadata metadata = flow.getHandler(j);

                    // todo change this in properway
                    if (metadata.getRules().getPhaseName().equals("")) {
                        metadata.getRules().setPhaseName("service");
                    }
                    allHandlers.add(metadata);
                }
            }
        }
        switch (flowtype) {
            case 1:
                {
                    flow = axisService.getInFlow();
                    break;
                }
            case 2:
                {
                    flow = axisService.getOutFlow();
                    break;
                }
            case 3:
                {
                    flow = axisService.getFaultFlow();
                    break;
                }
        }
        if (flow != null) {
            for (int j = 0; j < flow.getHandlerCount(); j++) {
                HandlerMetadata metadata = flow.getHandler(j);

                // todo change this in properway
                if (metadata.getRules().getPhaseName().equals("")) {
                    metadata.getRules().setPhaseName("service");
                }
                allHandlers.add(metadata);
            }
        }
        phaseHolder = new PhaseHolder(engineRegistry, axisService);
        for (int i = 0; i < allHandlers.size(); i++) {
            HandlerMetadata handlerMetaData =
                    (HandlerMetadata) allHandlers.get(i);
            phaseHolder.addHandler(handlerMetaData);
        }
        phaseHolder.getOrderedHandlers(type);
    }

    /**
     * Method buildTranspotsChains
     *
     * @throws PhaseException
     */
    public void buildTranspotsChains() throws PhaseException {
        try {
            HashMap transports = engineRegistry.getTransports();
            Collection coltrnsport = transports.values();
            for (Iterator iterator = coltrnsport.iterator();
                 iterator.hasNext();) {
                AxisTransport transport = (AxisTransport) iterator.next();
                buildTransportChains(transport);
            }
        } catch (AxisFault axisFault) {
            throw new PhaseException("AxisFault" + axisFault.getMessage());
        }
    }

    /**
     * Method buildTransportChains
     *
     * @param transport
     * @throws PhaseException
     */
    private void buildTransportChains(AxisTransport transport)
            throws PhaseException {
        Flow flow = null;
        for (int type = 1; type < 4; type++) {
            phaseHolder = new PhaseHolder(engineRegistry, null);
            switch (type) {
                case 1:
                    {
                        flow = transport.getInFlow();
                        break;
                    }
                case 2:
                    {
                        flow = transport.getOutFlow();
                        break;
                    }
                case 3:
                    {
                        flow = transport.getFaultFlow();
                        break;
                    }
            }
            if (flow != null) {
                for (int j = 0; j < flow.getHandlerCount(); j++) {
                    HandlerMetadata metadata = flow.getHandler(j);

                    // todo change this in properway
                    if (metadata.getRules().getPhaseName().equals("")) {
                        metadata.getRules().setPhaseName("transport");
                    }
                    phaseHolder.addHandler(metadata);
                }
            }
            phaseHolder.buildTransportChain(transport, type);
        }
    }

    /**
         * Method buildGlobalChains
         *
         * @param global
         * @throws AxisFault
         * @throws PhaseException
         */
    public void buildGlobalChains(AxisGlobal global)
            throws AxisFault, PhaseException {
        List modules = (List) global.getModules();
        int count = modules.size();
        QName moduleName;
        AxisModule module;
        Flow flow = null;
        for (int type = 1; type < 4; type++) {
            phaseHolder = new PhaseHolder(engineRegistry, null);
            for (int intA = 0; intA < count; intA++) {
                moduleName = (QName) modules.get(intA);
                module = engineRegistry.getModule(moduleName);
                switch (type) {
                    case 1:
                        {
                            flow = module.getInFlow();
                            break;
                        }
                    case 2:
                        {
                            flow = module.getOutFlow();
                            break;
                        }
                    case 3:
                        {
                            flow = module.getFaultFlow();
                            break;
                        }
                }
                if (flow != null) {
                    for (int j = 0; j < flow.getHandlerCount(); j++) {
                        HandlerMetadata metadata = flow.getHandler(j);

                        // todo change this in properway
                        if (metadata.getRules().getPhaseName().equals("")) {
                            metadata.getRules().setPhaseName("global");
                        }
                        phaseHolder.addHandler(metadata);
                    }
                }
            }
            phaseHolder.buildGlobalChain(global, type);
        }
    }
}
