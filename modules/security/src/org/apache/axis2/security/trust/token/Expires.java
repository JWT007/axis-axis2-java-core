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
package org.apache.axis2.security.trust.token;

import org.apache.axis2.om.OMElement;
import org.apache.axis2.security.trust.Constants;
import org.apache.axis2.security.trust.TrustException;

import javax.xml.namespace.QName;

public class Expires extends ValueToken {

	public static final QName TOKEN = new QName(Constants.WSU.NS,
			Constants.WSU.EXPIRES_LN, Constants.WSU.PREFIX);
	
	public Expires() {
		super();
	}

	/**
	 * @param elem
	 * @throws TrustException
	 */
	public Expires(OMElement elem) throws TrustException {
		super(elem);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.security.trust.token.AbstractToken#getToken()
	 */
	protected QName getToken() {
		return TOKEN;
	}

}
