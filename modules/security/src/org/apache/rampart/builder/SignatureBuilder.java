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

package org.apache.rampart.builder;

import org.apache.axis2.context.MessageContext;
import org.apache.rampart.RampartException;
import org.apache.rampart.RampartMessageData;
import org.apache.rampart.util.RampartUtil;
import org.apache.ws.secpolicy.model.Token;
import org.apache.ws.secpolicy.model.X509Token;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.WSSecDKSign;
import org.apache.ws.security.message.WSSecSignature;

import javax.security.auth.callback.CallbackHandler;

/**
 *
 */
public class SignatureBuilder {

    public void build(RampartMessageData messageData) throws RampartException {
        
        Token initiatorToken = messageData.getPolicyData().getInitiatorToken();
        
        //TODO:Handle different initiator token types here
        
        //Create message
        if(!initiatorToken.isDerivedKeys() && initiatorToken instanceof X509Token ) {
            WSSecSignature wsSign = new WSSecSignature();
            wsSign.setWsConfig(messageData.getConfig());
    
            int keyIdentifier = RampartUtil.getKeyIdentifier((X509Token)initiatorToken);
            if (keyIdentifier != 0) {
                wsSign.setKeyIdentifierType(keyIdentifier);
            }
            String asymmSignature = messageData.getPolicyData()
                    .getAlgorithmSuite().getAsymmetricSignature();
            
            if (asymmSignature != null) {
                wsSign.setSignatureAlgorithm(asymmSignature);
            }
    
            MessageContext msgCtx = messageData.getMsgContext();
            
            CallbackHandler handler = RampartUtil.getPasswordCB(msgCtx
                    .getAxisService().getClassLoader(), messageData
                    .getPolicyData().getRampartConfig().getPwCbClass());
            
            if(handler == null) {
                //If not found then try to find a call back handler instance located in the message context
                
                handler = (CallbackHandler)msgCtx.getProperty(WSHandlerConstants.PW_CALLBACK_REF);
            }
            
            if(handler == null) {
                throw new RampartException("missingCallbackHandler");
            }
            
            String user = messageData.getPolicyData().getRampartConfig()
                    .getUser();
            wsSign.setUserInfo(user, RampartUtil.performCallback(handler, user,
                    WSPasswordCallback.SIGNATURE).getPassword());
            
            if (messageData.getSignatureParts().size() > 0) {
                wsSign.setParts(messageData.getSignatureParts());
            }
        } else {
            WSSecDKSign dkSig = new WSSecDKSign();
            //TODO: Handle derieved key signature
            
        }
    }

}
