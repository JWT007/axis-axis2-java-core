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
package org.apache.ws.security.policy1.parser.processors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.ws.security.policy1.Constants;
import org.apache.ws.security.policy1.WSSPolicyException;
import org.apache.ws.security.policy1.model.TokenWrapper;
import org.apache.ws.security.policy1.model.X509Token;
import org.apache.ws.security.policy1.parser.SecurityPolicy;
import org.apache.ws.security.policy1.parser.SecurityPolicyToken;
import org.apache.ws.security.policy1.parser.SecurityProcessorContext;

import javax.xml.namespace.QName;


public class X509TokenProcessor {
    
	private static final Log log = LogFactory.getLog(X509TokenProcessor.class);
    
	private boolean initializedX509Token = false;

	/**
	 * Intialize the X509 complex token.
	 * 
	 * This method creates a copy of the X509Token token and sets the handler
	 * object to the copy. Then it creates copies of the child tokens that are
	 * allowed for X509Token. These tokens are:
	 * 
	 * These copies are also initialized with the handler object and then set as
	 * child tokens of X509Token.
	 * 
	 * <p/> The handler object that must contain the methods
	 * <code>doX509Token</code>.
	 * 
	 * @param spt
	 *            The token that will hold the child tokens.
	 * @throws NoSuchMethodException
	 */
	private void initializeX509Token(SecurityPolicyToken spt)
			throws NoSuchMethodException {

		SecurityPolicyToken tmpSpt = SecurityPolicy.requireKeyIdentifierReference
				.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.requireIssuerSerialReference.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.requireEmbeddedTokenReference.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.requireThumbprintReference.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.wssX509V1Token10.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.wssX509V3Token10.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.wssX509Pkcs7Token10.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.wssX509PkiPathV1Token10.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.wssX509V1Token11.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.wssX509V3Token11.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.wssX509Pkcs7Token11.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);

		tmpSpt = SecurityPolicy.wssX509PkiPathV1Token11.copy();
		tmpSpt.setProcessTokenMethod(this);
		spt.setChildToken(tmpSpt);
	}

	public Object doX509Token(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);

		SecurityPolicyToken spt = spc.readCurrentSecurityToken();

		switch (spc.getAction()) {

		case SecurityProcessorContext.START:
			if (!initializedX509Token) {
				try {
					initializeX509Token(spt);
                    X509Token token = (X509Token)spc.readCurrentPolicyEngineData();
                    //Get the includeToken attr info
                    String includetokenUri = spc.getAssertion().getAttribute(
                            new QName(Constants.SP_NS,
                                    Constants.ATTR_INCLUDE_TOKEN));
                    try {
                        if(includetokenUri != null) { //since its optional
                            token.setInclusion(includetokenUri);
                        }
                        ((TokenWrapper)spc.readPreviousPolicyEngineData()).setToken(token);
                    } catch (WSSPolicyException e) {
                        log.error(e.getMessage(), e);
                        return Boolean.FALSE;
                    }
					initializedX509Token = true;
				} catch (NoSuchMethodException e) {
                    log.error(e.getMessage(), e);
					return Boolean.FALSE;
				}
			}
			PrimitiveAssertion pa = spc.getAssertion();
			String text = pa.getStrValue();
			if (text != null) {
				text = text.trim();
				log.debug("Value: '" + text.toString() + "'");
			}
		case SecurityProcessorContext.COMMIT:
			break;
		case SecurityProcessorContext.ABORT:
			break;
		}
		return Boolean.TRUE;
	}

	public Object doRequireKeyIdentifierReference(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doRequireIssuerSerialReference(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doRequireEmbeddedTokenReference(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doRequireThumbprintReference(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doWssX509V1Token10(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doWssX509V3Token10(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doWssX509Pkcs7Token10(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doWssX509PkiPathV1Token10(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doWssX509V1Token11(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doWssX509V3Token11(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doWssX509Pkcs7Token11(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

	public Object doWssX509PkiPathV1Token11(SecurityProcessorContext spc) {
		log.debug("Processing "
				+ spc.readCurrentSecurityToken().getTokenName() + ": "
				+ SecurityProcessorContext.ACTION_NAMES[spc.getAction()]);
		return Boolean.TRUE;
	}

}
