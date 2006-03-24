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

package org.apache.axis2.security;

import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.security.handler.WSSHandlerConstants;
import org.apache.axis2.security.handler.config.InflowConfiguration;
import org.apache.axis2.security.handler.config.OutflowConfiguration;
import org.apache.ws.security.WSConstants;

/**
 * WS-Security interop scenario 7
 */
public class Scenario7Test extends InteropTestBase {

	protected OutflowConfiguration getOutflowConfiguration() {
		OutflowConfiguration ofc = new OutflowConfiguration();
		
		ofc.setActionItems("Signature Encrypt Timestamp");
		ofc.setUser("alice");
		ofc.setEncryptionUser("bob");
		ofc.setSignaturePropFile("interop.properties");
		ofc.setEncryptionPropFile("interop.properties");
		ofc.setPasswordCallbackClass("org.apache.axis2.security.PWCallback");
		ofc.setEncryptionSymAlgorithm(WSConstants.TRIPLE_DES);
		ofc.setSignatureKeyIdentifier(WSSHandlerConstants.BST_DIRECT_REFERENCE);
		ofc.setEncryptionKeyIdentifier(WSSHandlerConstants.SKI_KEY_IDENTIFIER);
		ofc.setEmbeddedKeyCallbackClass("rg.apache.axis2.security.PWCallback");
		ofc.setSignatureParts("{}{" + 
                                SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI + 
                                "}Body;STRTransform");
		
		return ofc;
	}

	protected InflowConfiguration getInflowConfiguration() {
		InflowConfiguration ifc = new InflowConfiguration();
		
		ifc.setActionItems("Signature Encrypt Timestamp");
		ifc.setPasswordCallbackClass("org.apache.axis2.security.PWCallback");
		ifc.setSignaturePropFile("interop.properties");
		ifc.setDecryptionPropFile("interop.properties");
		
		return ifc;
	}

	protected String getClientRepo() {
		return SCENARIO7_CLIENT_REPOSITORY;
	}

	protected String getServiceRepo() {
		return SCENARIO7_SERVICE_REPOSITORY;
	}

	protected boolean isUseSOAP12InStaticConfigTest() {
		return false;
	}
}
