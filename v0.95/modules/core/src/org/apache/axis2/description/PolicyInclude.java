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

import org.apache.axis2.i18n.Messages;
import org.apache.ws.commons.om.util.UUIDGenerator;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PolicyReference;
import org.apache.ws.policy.util.PolicyRegistry;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class PolicyInclude {

	public static final int ANON_POLICY = 100;

	public static final int AXIS_POLICY = 1;
	
	public static final int AXIS_MODULE_POLICY = 2;

	public static final int AXIS_SERVICE_POLICY = 3;

	public static final int AXIS_OPERATION_POLICY = 4;

	public static final int AXIS_MESSAGE_POLICY = 5;

	public static final int SERVICE_POLICY = 6;

	public static final int PORT_POLICY = 7;

	public static final int PORT_TYPE_POLICY = 8;

	public static final int BINDING_POLICY = 9;

	public static final int OPERATION_POLICY = 10;

	public static final int BINDING_OPERATION_POLICY = 11;

	public static final int INPUT_POLICY = 12;

	public static final int OUTPUT_POLICY = 13;

	public static final int BINDING_INPUT_POLICY = 14;

	public static final int BINDING_OUTPUT_POLICY = 15;

	public static final int MESSAGE_POLICY = 16;

	private Policy policy = null;

	private Policy effectivePolicy = null;
	
	private PolicyRegistry reg;

	private AxisDescription description;
	
	// private ArrayList wrapperElements = new ArrayList();

	private boolean useCacheP = false;
	
	private boolean useCacheE = false;
	
	private Hashtable wrapperElements = new Hashtable();

	public PolicyInclude() {
		reg = new PolicyRegistry();
	}
	
	public PolicyInclude(AxisDescription axisDescription) {
		reg = new PolicyRegistry();
		setDescription(axisDescription);
		
		if (axisDescription.getParent() != null) {
			PolicyInclude parentPolicyInclude = axisDescription.getParent().getPolicyInclude();
			reg.setParent(parentPolicyInclude.getPolicyRegistry());
		}
		
	}

	public void setPolicyRegistry(PolicyRegistry reg) {
		this.reg = reg;
	}
	
	public PolicyRegistry getPolicyRegistry() {
		return reg;
	}

	public void setPolicy(Policy policy) {
		wrapperElements.clear();
		
		if (policy.getPolicyURI() != null && policy.getPolicyURI().equals("")) {
			policy.setId(UUIDGenerator.getUUID());
		}
		
		Wrapper wrapper = new Wrapper(PolicyInclude.ANON_POLICY, policy);
		wrapperElements.put(policy.getPolicyURI(), wrapper);
		
		useCacheP = false;
	}
	
	public void updatePolicy(Policy policy) {
		String policyURI = policy.getPolicyURI();
		
		if (policyURI == null && "".equals(policyURI)) {
			throw new RuntimeException(Messages.getMessage("emptypolicy"));
		}
		
		Wrapper wrapper = (Wrapper) wrapperElements.get(policyURI);
		wrapper.value = policy;
		
		useCacheP = false;		
	}
	
	public void setEffectivePolicy(Policy effectivePolicy) {
		this.effectivePolicy = effectivePolicy;
	}
	
	public void setDescription(AxisDescription description) {
		this.description = description;
	}
	
	public AxisDescription getDescription() {
		return description;
	}
	
	private PolicyInclude getParent() {

		if (description != null && description.getParent() != null) {
			return description.getParent().getPolicyInclude();
		}
		return null;
	}

	private void calculatePolicy() {

		Policy result = null;
		Iterator iterator = wrapperElements.values().iterator();

		while (iterator.hasNext()) {
			Object policyElement = ((Wrapper) iterator.next()).getValue();
			Policy p;

			if (policyElement instanceof PolicyReference) {
				p = (Policy) ((PolicyReference) policyElement)
						.normalize(getPolicyRegistry());

			} else if (policyElement instanceof Policy) {
				p = (Policy) policyElement;

			} else {
				// TODO AxisFault?
				throw new RuntimeException();
			}

			result = (result == null) ? (Policy) p.normalize(reg)
					: (Policy) result.merge(p, reg);
		}
		this.policy = result;
		useCacheP(true);
	}

	private void calculateEffectivePolicy() {
		Policy result ;
		
		if (getParent() != null) {
			Policy parentPolicy = getParent().getEffectivePolicy();
			
			if (parentPolicy == null) {
				result = getPolicy();
				
			} else {
				
				if (getPolicy() != null) {
					result = (Policy) parentPolicy.merge(getPolicy(), reg);
					
				} else {
					result = parentPolicy;
				}
			}
			
		} else {
			result = getPolicy();
		}
		setEffectivePolicy(result);
		useCacheE(true);		
	}
	
	public Policy getPolicy() {
		
		if (! useCacheP) {
			calculatePolicy();
			useCacheP(true);
		}
		return policy;
	}

	public Policy getEffectivePolicy() {
		
		if (! useCacheE) {
			calculateEffectivePolicy();		
			useCacheE(true);
		}
		return effectivePolicy;
	}

	public ArrayList getPolicyElements() {
		ArrayList policyElementsList = new ArrayList();
		Iterator policyElementIterator = wrapperElements.values().iterator();

		while (policyElementIterator.hasNext()) {
			policyElementsList
					.add(((Wrapper) policyElementIterator.next()).getValue());
		}
		return policyElementsList;
	}

	public ArrayList getPolicyElements(int type) {
		ArrayList policyElementList = new ArrayList();
		Iterator wrapperElementIterator = wrapperElements.values().iterator();
		Wrapper wrapper;

		while (wrapperElementIterator.hasNext()) {
			wrapper = (Wrapper) wrapperElementIterator.next();

			if (wrapper.getType() == type) {
				policyElementList.add(wrapper.getValue());
			}
		}
		return policyElementList;
	}

	public void registerPolicy(Policy policy) {
		reg.register(policy.getPolicyURI(), policy);
	}

	public Policy getPolicy(String policyURI) {
		return reg.lookup(policyURI);
	}

	public void addPolicyElement(int type, Policy policy) {
		if (policy.getPolicyURI() == null || policy.getPolicyURI().equals("")) {
			policy.setId(UUIDGenerator.getUUID());
		}
		Wrapper wrapper = new Wrapper(type, policy);
		wrapperElements.put(policy.getPolicyURI(), wrapper);

		if (policy.getPolicyURI() != null) {
			reg.register(policy.getPolicyURI(), policy);
		}
	}

	public void addPolicyRefElement(int type, PolicyReference policyReference) {
		Wrapper wrapper = new Wrapper(type, policyReference);
		wrapperElements.put(policyReference.getPolicyURIString(), wrapper);
	}

	public void invalidate() {
		
		if (description != null) {
//			Iterator children = description.getChildren();
//			
//			if (children != null) {
//				AxisDescription axisDescription;
//				
//				while (children.hasNext()) {
//					axisDescription = (AxisDescription) children.next();
//					axisDescription.getPolicyInclude().invalidate();
//				}				
//			}
		}
		useCache(false);
	}
	
	private void useCacheP(boolean useCache) {
		this.useCacheP = useCache;
	}
	
	private void useCacheE(boolean useCacheE) {
		this.useCacheE = useCacheE;
	}
	
	private void useCache(boolean useCache) {
		this.useCacheP = useCache;
		this.useCacheE = useCache;
	}
	
	class Wrapper {
		private int type;
		private Object value;
		
		Wrapper(int type, Object value) {
			setType(type);
			setValue(value);
		}
		
		void setType(int type) {
			this.type = type;
		}
		
		int getType() {
			return type;
		}
		
		void setValue(Object value) {
			this.value = value;
		}
		
		Object getValue() {
			return value;
		}
	}
	
	public void removePolicyElement(String policyURI) {
		wrapperElements.remove(policyURI);
	}

    public void removeAllPolicyElements(){
        wrapperElements.clear();
    }
}
