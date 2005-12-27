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
*
*/
package org.apache.axis2.databinding.utils;


import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.typemapping.SimpleTypeMapper;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMAttribute;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.impl.llom.builder.StAXOMBuilder;
import org.apache.axis2.om.impl.llom.factory.OMXMLBuilderFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class BeanUtil {

    /**
     * To Serilize Bean object this method is used, this will create an object array using given
     * bean object
     *
     * @param beanObject
     * @param beanName
     */
    public static XMLStreamReader getPullParser(Object beanObject, QName beanName) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanObject.getClass());
            PropertyDescriptor [] propDescs = beanInfo.getPropertyDescriptors();
            ArrayList object = new ArrayList();
            for (int i = 0; i < propDescs.length; i++) {
                PropertyDescriptor propDesc = propDescs[i];
                Class ptype = propDesc.getPropertyType();
                if (propDesc.getName().equals("class")) {
                    continue;
                }
                if (SimpleTypeMapper.isSimpleType(ptype)) {
                    Object value = propDesc.getReadMethod().invoke(beanObject, null);
                    object.add(propDesc.getName());
                    object.add(value.toString());
                } else if (SimpleTypeMapper.isArrayList(ptype)) {
                    Object value = propDesc.getReadMethod().invoke(beanObject, null);
                    ArrayList objList = (ArrayList) value;
                    if (objList != null && objList.size() > 0) {
                        //this was given error , when the array.size = 0
                        // and if the array contain simple type , then the ADBPullParser asked
                        // PullParser from That simpel type
                        for (int j = 0; j < objList.size(); j++) {
                            Object o = objList.get(j);
                            if (SimpleTypeMapper.isSimpleType(o)) {
                                object.add(propDesc.getName());
                                object.add(o);
                            } else {
                                object.add(new QName(propDesc.getName()));
                                object.add(o);
                            }
                        }

                    }
                } else {
                    object.add(new QName(propDesc.getName()));
                    Object value = propDesc.getReadMethod().invoke(beanObject, null);
                    object.add(value);
                }
            }
            return ADBPullParser.createPullParser(beanName, object.toArray(), null);
            // TODO : Deepal fix this. I added another parameter to the above method in the ADBPullPrser
            // to get the attributes array. For the time being I passed null. Pass attributes array here.

        } catch (Exception e) {
            //todo has to throw this exeception
            return null;
        }
    }

    /**
     * to get the pull parser for a given bean object , generate the wrpper elemnet using class name
     *
     * @param beanObject
     */
    public static Object getPullParser(Object beanObject) {
        String className = beanObject.getClass().getName();
        if (className.indexOf(".") > 0) {
            className = className.substring(className.lastIndexOf('.') + 1, className.length());
        }
        return getPullParser(beanObject, new QName(className));
    }

    public static Object deserialize(Class beanClass, OMElement beanElement) throws AxisFault {
        Object beanObj;
        try {
            if (SimpleTypeMapper.isSimpleType(beanClass)) {
                return SimpleTypeMapper.getSimpleTypeObject(beanClass, beanElement);
            }
            HashMap properties = new HashMap();
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor [] propDescs = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propDescs.length; i++) {
                PropertyDescriptor proprty = propDescs[i];
                properties.put(proprty.getName(), proprty);
            }

            beanObj = beanClass.newInstance();
            Iterator elements = beanElement.getChildren();
            while (elements.hasNext()) {
                OMElement parts = (OMElement) elements.next();
                // if parts/@href != null then need to find element with id and deserialize. 
                // before that first check whether we already have it in the hashtable
                String partsLocalName = parts.getLocalName();
                PropertyDescriptor prty = (PropertyDescriptor) properties.get(partsLocalName.toLowerCase());
                if (prty != null) {
                    Class parameters = prty.getPropertyType();
                    if (prty.equals("class"))
                        continue;

                    Object partObj;
                    if (SimpleTypeMapper.isSimpleType(parameters)) {
                        partObj = SimpleTypeMapper.getSimpleTypeObject(parameters, parts);
                    } else if (SimpleTypeMapper.isArrayList(parameters)) {
                        //todo : Deepal , the array handling is completely wrong , this has to be
                        // improved
                        partObj = SimpleTypeMapper.getArrayList((OMElement) parts.getParent(), prty.getName());
                    } else {
                        partObj = deserialize(parameters, parts);
                    }
                    Object [] parms = new Object[]{partObj};
                    prty.getWriteMethod().invoke(beanObj, parms);
                }
            }
        } catch (InstantiationException e) {
            throw new AxisFault("InstantiationException : " + e);
        } catch (IllegalAccessException e) {
            throw new AxisFault("IllegalAccessException : " + e);
        } catch (InvocationTargetException e) {
            throw new AxisFault("InvocationTargetException : " + e);
        } catch (IntrospectionException e) {
            throw new AxisFault("IntrospectionException : " + e);
        }
        return beanObj;
    }

    public static Object deserialize(Class beanClass, OMElement beanElement, MultirefHelper helper) throws AxisFault {
        Object beanObj;
        try {
            HashMap properties = new HashMap();
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor [] propDescs = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propDescs.length; i++) {
                PropertyDescriptor proprty = propDescs[i];
                properties.put(proprty.getName(), proprty);
            }

            beanObj = beanClass.newInstance();
            Iterator elements = beanElement.getChildren();
            while (elements.hasNext()) {
                Object child = elements.next();
                OMElement parts;
                if (child instanceof OMElement) {
                    parts = (OMElement) child;
                } else {
                    continue;
                }
                String partsLocalName = parts.getLocalName();
                PropertyDescriptor prty = (PropertyDescriptor) properties.get(partsLocalName.toLowerCase());
                if (prty != null) {
                    Class parameters = prty.getPropertyType();
                    if (prty.equals("class"))
                        continue;
                    Object partObj;
                    OMAttribute attr = MultirefHelper.processRefAtt(parts);
                    if (attr != null) {
                        String refId = MultirefHelper.getAttvalue(attr);
                        partObj = helper.getObject(refId);
                        if (partObj == null) {
                            partObj = helper.processRef(parameters, refId);
                        }
                    } else {
                        partObj = SimpleTypeMapper.getSimpleTypeObject(parameters, parts);
                        if (partObj == null) {
                            partObj = deserialize(parameters, parts);
                        }
                    }
                    Object [] parms = new Object[]{partObj};
                    prty.getWriteMethod().invoke(beanObj, parms);
                }
            }
        } catch (InstantiationException e) {
            throw new AxisFault("InstantiationException : " + e);
        } catch (IllegalAccessException e) {
            throw new AxisFault("IllegalAccessException : " + e);
        } catch (InvocationTargetException e) {
            throw new AxisFault("InvocationTargetException : " + e);
        } catch (IntrospectionException e) {
            throw new AxisFault("IntrospectionException : " + e);
        }
        return beanObj;
    }


    /**
     * To get JavaObjects from XML elemnt , the element most of the time contains only one element
     * in that case that element will be converted to the JavaType specified by the javaTypes array
     * The algo is as follows, get the childerns of the response element , and if it conatian more than
     * one element then check the retuen type of that element and conver that to corresponding JavaType
     *
     * @param response  OMElement
     * @param javaTypes Array of JavaTypes
     * @return Array of objects
     * @throws AxisFault
     */
    public static Object [] deserialize(OMElement response, Object [] javaTypes) throws AxisFault {
        /**
         * Take the number of parameters in the method and , only take that much of child elements
         * from the OMElement , other are ignore , as an example
         * if the method is , foo(String a , int b)
         * and if the OMElemet
         * <foo>
         *  <arg0>Val1</arg0>
         *  <arg1>Val2</arg1>
         *  <arg2>Val3</arg2>
         *
         * only the val1 and Val2 take into account
         */
        int length = javaTypes.length;
        int count = 0;
        Object [] retObjs = new Object[length];

/**
 * If the body first child contains , then there can not be any other element withot
 * refs , so I can assume if the first child of the body first element has ref then
 * the message has to handle as mutiref message.
 * as an exmple if the body is like below
 * <foo>
 *  <arg0 href="#0"/>
 * </foo>
 *
 * then there can not be any element without refs , meaning following we are not handling
 * <foo>
 *  <arg0 href="#0"/>
 *  <arg1>absbsbs</arg1>
 * </foo>
 */
        Iterator parts = response.getChildren();
        //to handle multirefs
        //have to check the instanceof
        MultirefHelper helper = new MultirefHelper((OMElement) response.getParent());
        boolean hasRef = false;
        //to support array . if the parameter type is array , then all the omelemnts with that paramtre name
        // has to  get and add to the list
        Class classType;
        while (parts.hasNext() && count < length) {
            Object objValue = parts.next();
            OMElement omElement;
            if (objValue instanceof OMElement) {
                omElement = (OMElement) objValue;
            } else {
                continue;
            }
            classType = (Class) javaTypes[count];
            //handling refs
            OMAttribute omatribute = MultirefHelper.processRefAtt(omElement);
            String ref = null;
            if (omatribute != null) {
                hasRef = true;
                ref = MultirefHelper.getAttvalue(omatribute);
            }

            if (OMElement.class.isAssignableFrom(classType)) {
                if (hasRef) {
                    OMElement elemnt = helper.getOMElement(ref);
                    if (elemnt == null) {
                        retObjs[count] = helper.processOMElementRef(ref);
                    } else {
                        retObjs[count] = omElement;
                    }
                } else
                    retObjs[count] = omElement;
            } else {
                if (hasRef) {
                    if (helper.getObject(ref) != null) {
                        retObjs[count] = helper.getObject(ref);
                    } else {
                        retObjs[count] = helper.processRef(classType, ref);
                    }
                } else {
                    if (SimpleTypeMapper.isSimpleType(classType)) {
                        retObjs[count] = SimpleTypeMapper.getSimpleTypeObject(classType, omElement);
                    } else if (SimpleTypeMapper.isArrayList(classType)) {
                        retObjs[count] = SimpleTypeMapper.getArrayList(omElement);
                    } else {
                        retObjs[count] = BeanUtil.deserialize(classType, omElement);
                    }
                }
            }
            hasRef = false;
            count ++;
        }
        helper.clean();
        return retObjs;
    }

    public static OMElement getOMElement(QName opName, Object [] args) {
        ArrayList objects;
        objects = new ArrayList();
        int argCount = 0;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            //todo if the request parameter has name other than argi (0<i<n) , there should be a
            //way to do that , to solve that problem we need to have RPCRequestParameter
            //note that The value of request parameter can either be simple type or JavaBean
            if (SimpleTypeMapper.isSimpleType(arg)) {
                objects.add("arg" + argCount);
                objects.add(arg.toString());
            } else {
                objects.add(new QName("arg" + argCount));
                objects.add(arg);
            }
            argCount ++;
        }
        XMLStreamReader xr = ADBPullParser.createPullParser(opName, objects.toArray(), null);
        StAXOMBuilder stAXOMBuilder =
                OMXMLBuilderFactory.createStAXOMBuilder(
                        OMAbstractFactory.getSOAP11Factory(), xr);
        return stAXOMBuilder.getDocumentElement();
    }

}
