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
package org.apache.axis.description;

import org.apache.wsdl.MessageReference;
import org.apache.wsdl.WSDLFeature;
import org.apache.wsdl.WSDLOperation;
import org.apache.wsdl.WSDLProperty;
import org.apache.wsdl.impl.WSDLOperationImpl;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;

/**
 * Class SimpleAxisOperationImpl
 */
public class SimpleAxisOperationImpl implements AxisOperation {
    /**
     * Field wsdlOperation
     */
    protected final WSDLOperation wsdlOperation;

    /**
     * Field parameters
     */
    protected final ParameterInclude parameters;

    /**
     *
     */
    public SimpleAxisOperationImpl() {
        wsdlOperation = new WSDLOperationImpl();
        parameters = new ParameterIncludeImpl();
    }

    /**
     * Constructor SimpleAxisOperationImpl
     *
     * @param name
     */
    public SimpleAxisOperationImpl(QName name) {
        wsdlOperation = new WSDLOperationImpl();
        wsdlOperation.setName(name);
        parameters = new ParameterIncludeImpl();
    }

    /**
     * @param param
     */
    public void addParameter(Parameter param) {
        parameters.addParameter(param);
    }

    /**
     * @param name
     * @return
     */
    public Parameter getParameter(String name) {
        return parameters.getParameter(name);
    }

    /**
     * @param feature
     */
    public void addFeature(WSDLFeature feature) {
        wsdlOperation.addFeature(feature);
    }

    /**
     * @param wsdlProperty
     */
    public void addProperty(WSDLProperty wsdlProperty) {
        wsdlOperation.addProperty(wsdlProperty);
    }

    /**
     * @return
     */
    public HashMap getComponentProperties() {
        return wsdlOperation.getComponentProperties();
    }

    /**
     * @param key
     * @return
     */
    public Object getComponentProperty(Object key) {
        return wsdlOperation.getComponentProperty(key);
    }

    /**
     * @return
     */
    public List getFeatures() {
        return wsdlOperation.getFeatures();
    }

    /**
     * @return
     */
    public List getInfaults() {
        return wsdlOperation.getInfaults();
    }

    /**
     * @return
     */
    public MessageReference getInputMessage() {
        return wsdlOperation.getInputMessage();
    }

    /**
     * @return
     */
    public String getMessageExchangePattern() {
        return wsdlOperation.getMessageExchangePattern();
    }

    /**
     * @return
     */
    public QName getName() {
        return wsdlOperation.getName();
    }

    /**
     * @return
     */
    public List getOutfaults() {
        return wsdlOperation.getOutfaults();
    }

    /**
     * @return
     */
    public MessageReference getOutputMessage() {
        return wsdlOperation.getOutputMessage();
    }

    /**
     * @return
     */
    public List getProperties() {
        return wsdlOperation.getProperties();
    }

    /**
     * @return
     */
    public String getStyle() {
        return wsdlOperation.getStyle();
    }

    /**
     * @return
     */
    public String getTargetnemespace() {
        return wsdlOperation.getTargetnemespace();
    }

    /**
     * @return
     */
    public boolean isSafe() {
        return wsdlOperation.isSafe();
    }

    /**
     * @param properties
     */
    public void setComponentProperties(HashMap properties) {
        wsdlOperation.setComponentProperties(properties);
    }

    /**
     * Method setMessageExchangePattern
     *
     * @param messageExchangePattern
     */
    public void setMessageExchangePattern(String messageExchangePattern) {
        wsdlOperation.setMessageExchangePattern(messageExchangePattern);
    }

    /**
     * @param key
     * @param obj
     */
    public void setComponentProperty(Object key, Object obj) {
        wsdlOperation.setComponentProperty(key, obj);
    }

    /**
     * @param infaults
     */
    public void setInfaults(List infaults) {
        wsdlOperation.setInfaults(infaults);
    }

    /**
     * @param inputMessage
     */
    public void setInputMessage(MessageReference inputMessage) {
        wsdlOperation.setInputMessage(inputMessage);
    }

    /**
     * @param name
     */
    public void setName(QName name) {
        wsdlOperation.setName(name);
    }

    /**
     * @param outfaults
     */
    public void setOutfaults(List outfaults) {
        wsdlOperation.setOutfaults(outfaults);
    }

    /**
     * @param outputMessage
     */
    public void setOutputMessage(MessageReference outputMessage) {
        wsdlOperation.setOutputMessage(outputMessage);
    }

    /**
     * @param safe
     */
    public void setSafety(boolean safe) {
        wsdlOperation.setSafety(safe);
    }

    /**
     * @param style
     */
    public void setStyle(String style) {
        wsdlOperation.setStyle(style);
    }
}
