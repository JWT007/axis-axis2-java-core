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


package org.apache.axis2.deployment;

import org.apache.axis2.AxisFault;
import org.apache.axis2.deployment.util.PhasesInfo;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisOperationFactory;
import org.apache.axis2.description.InOnlyAxisOperation;
import org.apache.axis2.description.ModuleDescription;
import org.apache.axis2.description.PolicyInclude;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.modules.Module;
import org.apache.axis2.om.OMAttribute;
import org.apache.axis2.om.OMElement;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Builds a module description from OM
 */
public class ModuleBuilder extends DescriptionBuilder {
    private AxisConfiguration axisConfig;
    private ModuleDescription module;

    public ModuleBuilder(InputStream serviceInputStream, ModuleDescription module,
                         AxisConfiguration axisConfig) {
        super(serviceInputStream, axisConfig);
        this.axisConfig = axisConfig;
        this.module = module;
    }

    private void loadModuleClass(ModuleDescription module, String moduleClassName)
            throws DeploymentException {
        Class moduleClass;

        try {
            if ((moduleClassName != null) && !"".equals(moduleClassName)) {
                moduleClass = Class.forName(moduleClassName, true, module.getModuleClassLoader());
                module.setModule((Module) moduleClass.newInstance());
            }
        } catch (Exception e) {
            throw new DeploymentException(e.getMessage(), e);
        }
    }

    public void populateModule() throws DeploymentException {
        try {
            OMElement moduleElement = buildOM();

            // Setting Module Name
            OMAttribute moduleNameAtt = moduleElement.getAttribute(new QName(ATTRIBUTE_NAME));

            if (moduleNameAtt != null) {
                String moduleName = moduleNameAtt.getAttributeValue();

                if ((moduleName != null) && !"".equals(moduleName)) {
                    module.setName(new QName(moduleName));
                }
            }

            // Setting Module Class , if it is there
            OMAttribute moduleClassAtt = moduleElement.getAttribute(new QName(TAG_CLASS_NAME));

            if (moduleClassAtt != null) {
                String moduleClass = moduleClassAtt.getAttributeValue();

                if ((moduleClass != null) && !"".equals(moduleClass)) {
                    loadModuleClass(module, moduleClass);
                }
            }

            // setting the PolicyInclude
            PolicyInclude policyInclude;

            if (axisConfig != null) {
                PolicyInclude parent = axisConfig.getPolicyInclude();
                policyInclude = new PolicyInclude(parent);

            } else {
                policyInclude = new PolicyInclude();
            }
            module.setPolicyInclude(policyInclude);

            // processing <wsp:Policy> .. </..> elements
            Iterator policyElements = moduleElement.getChildrenWithName(new QName(POLICY_NS_URI, TAG_POLICY));

            if (policyElements != null) {
                processPolicyElements(PolicyInclude.MODULE_POLICY, policyElements, module.getPolicyInclude());
            }

            // processing <wsp:PolicyReference> .. </..> elements
            Iterator policyRefElements = moduleElement.getChildrenWithName(new QName(POLICY_NS_URI, TAG_POLICY_REF));

            if (policyRefElements != null) {
                processPolicyRefElements(PolicyInclude.MODULE_POLICY, policyRefElements, module.getPolicyInclude());
            }

            // processing Parameters
            // Processing service level parameters
            Iterator itr = moduleElement.getChildrenWithName(new QName(TAG_PARAMETER));

            processParameters(itr, module, module.getParent());

            // process INFLOW
            OMElement inFlow = moduleElement.getFirstChildWithName(new QName(TAG_FLOW_IN));

            if (inFlow != null) {
                module.setInFlow(processFlow(inFlow, module));
            }

            OMElement outFlow = moduleElement.getFirstChildWithName(new QName(TAG_FLOW_OUT));

            if (outFlow != null) {
                module.setOutFlow(processFlow(outFlow, module));
            }

            OMElement inFaultFlow = moduleElement.getFirstChildWithName(new QName(TAG_FLOW_IN_FAULT));

            if (inFaultFlow != null) {
                module.setFaultInFlow(processFlow(inFaultFlow, module));
            }

            OMElement outFaultFlow = moduleElement.getFirstChildWithName(new QName(TAG_FLOW_OUT_FAULT));

            if (outFaultFlow != null) {
                module.setFaultOutFlow(processFlow(outFaultFlow, module));
            }

            // processing Operations
            Iterator op_itr = moduleElement.getChildrenWithName(new QName(TAG_OPERATION));
            ArrayList operations = processOperations(op_itr);

            for (int i = 0; i < operations.size(); i++) {
                AxisOperation operation = (AxisOperation) operations.get(i);

                module.addOperation(operation);
            }
        } catch (XMLStreamException e) {
            throw new DeploymentException(e);
        }
    }

    private ArrayList processOperations(Iterator operationsIterator) throws DeploymentException {
        ArrayList operations = new ArrayList();

        while (operationsIterator.hasNext()) {
            OMElement operation = (OMElement) operationsIterator.next();

            // /getting operation name
            OMAttribute op_name_att = operation.getAttribute(new QName(ATTRIBUTE_NAME));

            if (op_name_att == null) {
                throw new DeploymentException(
                        Messages.getMessage(
                                Messages.getMessage(
                                        DeploymentErrorMsgs.INVALID_OP, "operation name missing")));
            }

            OMAttribute op_mep_att = operation.getAttribute(new QName(TAG_MEP));
            String mepURL = null;
            AxisOperation op_descrip;

            if (op_mep_att != null) {
                mepURL = op_mep_att.getAttributeValue();
            }

            if (mepURL == null) {

                // assuming in-out mep
                op_descrip = new InOnlyAxisOperation();
            } else {
                try {
                    op_descrip = AxisOperationFactory.getOperationDescription(mepURL);
                } catch (AxisFault axisFault) {
                    throw new DeploymentException(
                            Messages.getMessage(
                                    Messages.getMessage(
                                            DeploymentErrorMsgs.OPERATION_PROCESS_ERROR,
                                            axisFault.getMessage())));
                }
            }

            String opname = op_name_att.getAttributeValue();

            op_descrip.setName(new QName(opname));

            // Operation Parameters
            Iterator parameters = operation.getChildrenWithName(new QName(TAG_PARAMETER));
            ArrayList wsamapping = processParameters(parameters, op_descrip, module);

            op_descrip.setWsamappingList(wsamapping);

            // setting the mep of the operation
            // loading the message recivers
            OMElement receiverElement = operation.getFirstChildWithName(new QName(TAG_MESSAGE_RECEIVER));

            if (receiverElement != null) {
                MessageReceiver messageReceiver =
                        loadMessageReceiver(module.getModuleClassLoader(), receiverElement);

                op_descrip.setMessageReceiver(messageReceiver);
            } else {

                // setting default message receiver
                MessageReceiver msgReceiver = loadDefaultMessageReceiver(mepURL, null);

                op_descrip.setMessageReceiver(msgReceiver);
            }

            // Process Module Refs
            Iterator modules = operation.getChildrenWithName(new QName(TAG_MODULE));

            processOperationModuleRefs(modules, op_descrip);

            // setting Operation phase
            PhasesInfo info = axisConfig.getPhasesInfo();

            try {
                info.setOperationPhases(op_descrip);
            } catch (AxisFault axisFault) {
                throw new DeploymentException(axisFault);
            }

            // adding the operation
            operations.add(op_descrip);
        }

        return operations;
    }
}
