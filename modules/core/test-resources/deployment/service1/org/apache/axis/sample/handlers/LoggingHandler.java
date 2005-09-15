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
 
package org.apache.axis2.sample.handlers;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author chathura@opensource.lk
 * 
 */
public class LoggingHandler extends AbstractHandler {

	private Log log = LogFactory.getLog(getClass());
	/* (non-Javadoc)
	 * @see org.apache.axis2.engine.Handler#invoke(org.apache.axis2.context.MessageContext)
	 */
	
	
	
	public void invoke(MessageContext msgContext) throws AxisFault {
		log.info("Incomming message Frrom "+msgContext.getTo().getAddress());
	}
	
	public void revoke(MessageContext msgContext){
		log.info("Incomming message Revovked at the server "+msgContext.getTo().getAddress() );
	}

}
