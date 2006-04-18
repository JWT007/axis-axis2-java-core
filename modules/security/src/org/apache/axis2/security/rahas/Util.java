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

package org.apache.axis2.security.rahas;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.security.trust.Constants;
import org.apache.axis2.security.trust.Token;
import org.apache.axis2.security.trust.TrustException;
import org.apache.axis2.util.Base64;
import org.apache.axis2.util.Loader;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.token.SecurityContextToken;
import org.apache.ws.security.processor.EncryptedKeyProcessor;
import org.w3c.dom.Element;

import javax.security.auth.callback.CallbackHandler;
import javax.xml.namespace.QName;

import java.util.Vector;

public class Util {

    /**
     * Returns the crypto instance of this configuration. If one is not
     * availabale then it will try to create a <code>Crypto</code> instance
     * using available configuration information and will set it as the
     * <code>Crypto</code> instance of the configuration.
     * 
     * @param config
     * @return
     * @throws RahasException
     */
    public static Crypto getCryptoInstace(RahasConfiguration config)
            throws RahasException {
        if (config.getCrypto() != null) {
            return config.getCrypto();
        } else {
            Crypto crypto = null;
            if (config.getCryptoClassName() != null
                    && config.getCryptoProperties() != null) {
                crypto = CryptoFactory.getInstance(config.getCryptoClassName(),
                        config.getCryptoProperties());
            } else if (config.getCryptoPropertiesFile() != null) {
                if (config.getClassLoader() != null) {
                    crypto = CryptoFactory
                            .getInstance(config.getCryptoPropertiesFile(),
                                    config.getClassLoader());
                } else {
                    crypto = CryptoFactory.getInstance(config
                            .getCryptoPropertiesFile());
                }
            } else {
                throw new RahasException("cannotCrateCryptoInstance");
            }
            config.setCrypto(crypto);
            return crypto;
        }
    }

    public static void processRSTR(OMElement rstr, RahasConfiguration config)
            throws Exception {
        // Extract the SecurityContextToken
        OMElement rstElem = rstr.getFirstChildWithName(new QName(
                Constants.WST_NS, Constants.REQUESTED_SECURITY_TOKEN_LN));
        Token token = null;
        if (rstElem != null) {
            OMElement sctElem = rstElem
                    .getFirstChildWithName(SecurityContextToken.TOKEN);
            if (sctElem != null) {
                SecurityContextToken sct = new SecurityContextToken(
                        (Element) sctElem);
                token = new Token(sct.getIdentifier(), sctElem);
                config.resgisterContext(sct.getIdentifier());
            } else {
                throw new RahasException("sctMissingInResponse");
            }
        } else {
            throw new TrustException("reqestedSecTokMissing");
        }

        // Process RequestedProofToken and extract the secret
        byte[] secret = null;
        OMElement rpt = rstr.getFirstChildWithName(new QName(Constants.WST_NS,
                Constants.REQUESTED_PROOF_TOKEN_LN));
        if (rpt != null) {
            OMElement elem = rpt.getFirstElement();

            if (WSConstants.ENC_KEY_LN.equals(elem.getLocalName())
                    && WSConstants.ENC_NS.equals(elem.getNamespace().getName())) {
                // Handle the xenc:EncryptedKey case
                EncryptedKeyProcessor processor = new EncryptedKeyProcessor();
                processor.handleToken((Element) elem, null, Util
                        .getCryptoInstace(config),
                        getCallbackHandlerInstance(config), null, new Vector(),
                        null);
                secret = processor.getDecryptedBytes();
            } else if (Constants.BINARY_SECRET.equals(elem.getLocalName())
                    && Constants.WST_NS.equals(elem.getNamespace().getName())) {
                // Handle the wst:BinarySecret case
                secret = Base64.decode(elem.getText());
            } else {
                throw new TrustException("notSupported", new String[] { "{"
                        + elem.getNamespace().getName() + "}"
                        + elem.getLocalName() });
            }
        } else {
            throw new TrustException("rptMissing");
        }

        // Check for attached ref
        OMElement reqAttElem = rstr.getFirstChildWithName(new QName(
                Constants.WST_NS, Constants.REQUESTED_ATTACHED_REFERENCE));
        OMElement reqAttRef = reqAttElem == null ? null : reqAttElem
                .getFirstElement();

        OMElement reqUnattElem = rstr.getFirstChildWithName(new QName(
                Constants.WST_NS, Constants.REQUESTED_UNATTACHED_REFERENCE));
        OMElement reqUnattRef = reqUnattElem == null ? null : reqUnattElem
                .getFirstElement();

        token.setAttachedReference(reqAttRef);
        token.setUnattachedReference(reqUnattRef);
        token.setSecret(secret);
        config.getTokenStore().add(token);
    }
    
    private static CallbackHandler getCallbackHandlerInstance(
            RahasConfiguration config) throws Exception {
        if (config.getPasswordCallbackRef() != null) {
            return config.getPasswordCallbackRef();
        } else if (config.getPasswordCallbackClass() != null) {
            if (config.getClassLoader() != null) {
                Class clazz = Loader.loadClass(config.getClassLoader(), config
                        .getPasswordCallbackClass());
                return (CallbackHandler) clazz.newInstance();
            } else {
                Class clazz = Loader.loadClass(config
                        .getPasswordCallbackClass());
                return (CallbackHandler) clazz.newInstance();
            }
        } else {
            throw new RahasException("noInfoForCBhandler");
        }
    }

}
