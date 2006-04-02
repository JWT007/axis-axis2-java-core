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

package org.apache.axis2.security.handler;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.Handler;
import org.apache.axis2.security.util.Axis2Util;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.handler.WSHandler;

import javax.xml.namespace.QName;

/**
 * Class WSDoAllHandler
 */
public abstract class WSDoAllHandler extends WSHandler implements Handler {

    /**
     * Field EMPTY_HANDLER_METADATA
     */
    private static HandlerDescription EMPTY_HANDLER_METADATA =
            new HandlerDescription(new QName("deafult Handler"));

    private final static String WSS_PASSWORD = "password";
    
    private final static String WSS_USERNAME = "username";
    
    /**
     * Field handlerDesc
     */
    protected HandlerDescription handlerDesc;
    
    /**
     * This is used to get hold of the message context to extract the
     * configuration information (from axis2.xml and service.xml)
     * out of it 
     */
    protected RequestData reqData;
    
    /**
     * In Axis2, the user cannot set inflow and outflow parameters.
     * Therefore, we need to map the Axis2 specific inflow and outflow 
     * parameters to WSS4J params,
     * 
     * Knowledge of inhandler and out handler is used to get the mapped value.
     */
    protected boolean inHandler;
    
    /**
     * Constructor AbstractHandler.
     */
    public WSDoAllHandler() {
        handlerDesc = EMPTY_HANDLER_METADATA;
    }

    /**
     * Method getName.
     *
     * @return Returns name.
     */
    public QName getName() {
        return handlerDesc.getName();
    }

    /**
     * Method cleanup.
     *
     * @throws org.apache.axis2.AxisFault
     */
    public void cleanup() throws AxisFault {
    }

    /**
     * Method getParameter.
     *
     * @param name
     * @return Returns parameter.
     */
    public Parameter getParameter(String name) {
        return handlerDesc.getParameter(name);
    }

    /**
     * Method init.
     *
     * @param handlerdesc
     */
    public void init(HandlerDescription handlerdesc) {
        this.handlerDesc = handlerdesc;
    }

    /**
     * Gets the handler description.
     *
     * @return Returns handler description.
     */
    public HandlerDescription getHandlerDesc() {
        return handlerDesc;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        QName name = this.getName();
        return (name != null) ? name.toString() : null;
    }


    public Object getProperty(Object msgContext, String axisKey) {
    	
    	int repetition = getCurrentRepetition(msgContext);
    	
    	String key = Axis2Util.getKey(axisKey,inHandler, repetition);
        Object property = ((MessageContext)msgContext).getProperty(key);
        if(property == null) {
            //Try the description hierarchy
            Parameter parameter = ((MessageContext)msgContext).getParameter(key);
            if(parameter != null) {
                property = parameter.getValue();
            }
        }
        return property;
    }

    /**
     * Returns the repetition number from the message context
     * @param msgContext
     * @return Returns int.
     */
	protected int getCurrentRepetition(Object msgContext) {
		//get the repetition from the message context
    	int repetition = 0;
    	if(!inHandler) {//We only need to repete the out handler
    		Integer count = (Integer)((MessageContext)msgContext).getProperty(WSSHandlerConstants.CURRENT_REPETITON);
    		if(count != null) { //When we are repeting the handler
    			repetition = count.intValue();
    		}
    	}
		return repetition;
	}

    public String getPassword(Object msgContext) {
        return (String)((MessageContext)msgContext).getProperty(WSS_PASSWORD);
    }

    public void setPassword(Object msgContext, String password) {
        ((MessageContext)msgContext).setProperty(WSS_PASSWORD,password);
    }
    
    public String getUsername(Object msgContext) {
        return (String)((MessageContext)msgContext).getProperty(WSS_USERNAME);
    }

    public void setUsername(Object msgContext, String username) {
        ((MessageContext)msgContext).setProperty(WSS_USERNAME,username);
    }
    
	/**
	 * Gets optoin. Extracts the configuration values from the service.xml 
	 * and/or axis2.xml. Values set in the service.xml takes prority over 
	 * values of the axis2.xml
	 */
    public Object getOption(String axisKey) {
    	
    	MessageContext msgContext = (MessageContext)this.reqData.getMsgContext();
    	
    	int repetition  = this.getCurrentRepetition(msgContext);
    	
    	String key  = Axis2Util.getKey(axisKey,inHandler, repetition);

    	Object value = null;
    	
        Parameter param = msgContext.getParameter(key);
		value = (param== null)?null:param.getValue();

        // ---------------------------------------------------------------------
    	//If value is still null this point then the user has not set the value
    	  
    	
    	//Look in the handlerDesc for the value
    	if(value == null) {
    		Parameter parameter = this.handlerDesc.getParameter(key);
    		value = (parameter== null)?null:parameter.getValue();
    	}

    	return value;
    }

	public void setProperty(Object msgContext, String key, Object value) {
		((MessageContext)msgContext).setProperty(key, value);
	}

    /**
     * Overrides the class loader used to load the PW callback class.
     * @return Returns class loader.
     */
    public java.lang.ClassLoader getClassLoader() {
        try {
            MessageContext msgContext = (MessageContext) this.reqData.getMsgContext();
            return msgContext.getAxisService().getClassLoader();
        } catch (Throwable t) {
            return super.getClassLoader();
        }
    }
}
