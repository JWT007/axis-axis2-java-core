/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * Copyright 2006 International Business Machines Corp.
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
package org.apache.axis2.jaxws.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebFault;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceProvider;

import org.apache.axis2.jaxws.i18n.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains static Class utility methods related to method parameter/argument
 * marshalling.
 */
public class ClassUtils {

	private static Log log = LogFactory.getLog(ClassUtils.class);
	
	
	/**
	 * Gets the RootCause for an throwable.
	 * The root cause is defined as the first non-InvocationTargetException.
	 * @param e Throwable
	 * @return Throwable root cause
	 */
	public static Throwable getRootCause(Throwable e) {
        Throwable t = null;
        
        if (e != null) {
            if (e instanceof InvocationTargetException) {
                t = ((InvocationTargetException) e).getTargetException();
            } else {
                t = null;
            }
            
            if (t != null) {
                e = getRootCause(t);
            }
        }
        return e;
    }
	
	private static HashMap loadClassMap = new HashMap();
    static {
        loadClassMap.put("byte", byte.class);
        loadClassMap.put("int", int.class);
        loadClassMap.put("short", short.class);
        loadClassMap.put("long", long.class);
        loadClassMap.put("float", float.class);
        loadClassMap.put("double", double.class);
        loadClassMap.put("boolean", boolean.class);
        loadClassMap.put("char", char.class);
        loadClassMap.put("void", void.class);
    }
    
    /**
     * Converts text of the form
     * Foo[] to the proper class name for loading [LFoo
     */
    private static HashMap loadableMap = new HashMap();
    static {
        loadableMap.put("byte",    "B");
        loadableMap.put("char",    "C");
        loadableMap.put("double",  "D");
        loadableMap.put("float",   "F");
        loadableMap.put("int",     "I");
        loadableMap.put("long",    "J");
        loadableMap.put("short",   "S");
        loadableMap.put("boolean", "Z");
    }
    
    /**
     * @param text String
     * @return String that can be used for Class.forName
     */
    public static String getLoadableClassName(String text) {
        int bracket = text.indexOf("[");
        if (text == null || 
            bracket < 0 || // no array
            bracket == 0) { // or already loadable
            return text;
        }
        String className = text;

        // Get the className without any array brackets
        if (bracket > 0) {
            className = className.substring(0, bracket);
        }

        // Now get the loadable name from the map or 
        // its L<className>;
        String loadClass = (String) loadableMap.get(className);
        if (loadClass == null) {
            loadClass = "L" + className + ";";
        }
        
        // Now prepend [ for each array dimension
        if (bracket > 0) {
            int i = text.indexOf("]");
            while (i > 0) {
                loadClass = "[" + loadClass;
                i = text.indexOf("]", i+1);
            }
        }
        return loadClass;
    }

    /**
     * Converts text of the form
     * [LFoo to the Foo[]
     */
    public static String getTextClassName(String text) {
        if (text == null ||
            text.indexOf("[") != 0)
            return text;
        String className = "";
        int index = 0;
        while(index < text.length() &&
              text.charAt(index) == '[') {
            index ++;
            className += "[]";
        }
        if (index < text.length()) {
            if (text.charAt(index)== 'B')
                className = "byte" + className;
            else if (text.charAt(index) == 'C')
                className = "char" + className;
            else if (text.charAt(index) == 'D')
                className = "double" + className;
            else if (text.charAt(index) == 'F')
                className = "float" + className;
            else if (text.charAt(index) == 'I')
                className = "int" + className;
            else if (text.charAt(index) == 'J')
                className = "long" + className;
            else if (text.charAt(index) == 'S')
                className = "short" + className;
            else if (text.charAt(index) == 'Z')
                className = "boolean" + className;
            else if (text.equals("void"))
                className = "void";
            else {
                className = text.substring(index+1, text.indexOf(";")) + className;
            }
        }
        return className;
    }
    
    /**
     * @param primitive
     * @return java wrapper class or null
     */
    public static Class getWrapperClass(Class primitive)
    {
        if (primitive == int.class)
            return java.lang.Integer.class;
        else if (primitive == short.class)
            return java.lang.Short.class;
        else if (primitive == boolean.class)
            return java.lang.Boolean.class;
        else if (primitive == byte.class)
            return java.lang.Byte.class;
        else if (primitive == long.class)
            return java.lang.Long.class;
        else if (primitive == double.class)
            return java.lang.Double.class;
        else if (primitive == float.class)
            return java.lang.Float.class;
        else if (primitive == char.class)
            return java.lang.Character.class;
        
        return null;
    }
    

    /**
     * @param wrapper
     * @return primitive clas or null
     */
    public static Class getPrimitiveClass(Class wrapper)
    {
        if (wrapper == java.lang.Integer.class)
            return int.class;
        else if (wrapper == java.lang.Short.class)
            return short.class;
        else if (wrapper == java.lang.Boolean.class)
            return boolean.class;
        else if (wrapper == java.lang.Byte.class)
            return byte.class;
        else if (wrapper == java.lang.Long.class)
            return long.class;
        else if (wrapper == java.lang.Double.class)
            return double.class;
        else if (wrapper == java.lang.Float.class)
            return float.class;
        else if (wrapper == java.lang.Character.class)
            return char.class;
        
        return null;
    }
    
    /**
	 * This method will return all the Class names excluding the interfaces from a given package. 
	 * @param pkg Package
	 * @return
	 * @throws ClassNotFoundException
	 */
    public static List<Class> getAllClassesFromPackage(Package pkg) throws ClassNotFoundException {
        if (pkg == null) {
            return new ArrayList<Class>();
        }   
        // This will hold a list of directories matching the pckgname. There may be more than one if a package is split over multiple jars/paths
        String pckgname = pkg.getName();
        ArrayList<File> directories = new ArrayList<File>();
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                if(log.isDebugEnabled()){
                    log.debug("Unable to get class loader");
                }
                throw new ClassNotFoundException(Messages.getMessage("ClassUtilsErr1"));
            }
            String path = pckgname.replace('.', '/');
            // Ask for all resources for the path
            Enumeration<URL> resources = cld.getResources(path);
            while (resources.hasMoreElements()) {
                directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
            }
        } catch (UnsupportedEncodingException e) {
            if(log.isDebugEnabled()){
                log.debug(pckgname + " does not appear to be a valid package (Unsupported encoding)");
            }
            throw new ClassNotFoundException(Messages.getMessage("ClassUtilsErr2", pckgname));
        } catch (IOException e) {
            if(log.isDebugEnabled()){
                log.debug("IOException was thrown when trying to get all resources for "+ pckgname);
            }
            throw new ClassNotFoundException(Messages.getMessage("ClassUtilsErr3", pckgname));
        }
        
        ArrayList<Class> classes = new ArrayList<Class>();
        // For every directory identified capture all the .class files
        for (File directory : directories) {
            if (log.isDebugEnabled()) {
                log.debug("Adding classes from: " + directory.getName());
            }
            if (directory.exists()) {
                // Get the list of the files contained in the package
                String[] files = directory.list();
                for (String file : files) {
                    // we are only interested in .class files
                    if (file.endsWith(".class")) {
                        // removes the .class extension
                        // TODO Java2 Sec
                        try {
                            Class clazz = Class.forName(pckgname + '.' + file.substring(0, file.length() - 6), 
                                    false, 
                                    Thread.currentThread().getContextClassLoader());
                            // Don't add any interfaces or JAXWS specific classes.  
                            // Only classes that represent data and can be marshalled 
                            // by JAXB should be added.
                            if(!clazz.isInterface() 
                                    && getDefaultPublicConstructor(clazz) != null
                                    && !isJAXWSClass(clazz)){
                                if (log.isDebugEnabled()) {
                                    log.debug("Adding class: " + file);
                                }
                                classes.add(clazz);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                    }
                }
            }
        }
        return classes;
    }
    
	private static final Class[] noClass=new Class[] {};
	/**
	 * Get the default public constructor
	 * @param clazz
	 * @return Constructor or null
	 */
	public static Constructor getDefaultPublicConstructor(Class clazz) {
		try {
            return clazz.getConstructor(noClass);
		} catch (Exception e) {
			return null;
		}
	}
    
    /**
     * @param cls
     * @return true if this is a JAX-WS or JAX-WS generated class
     */
    public static final boolean isJAXWSClass(Class cls) {
        // Kinds of generated classes: Service, Provider, Impl, Exception, Holder
        // Or the class is in the jaxws.xml.ws package
        
        // Check for Impl
        WebService wsAnn = (WebService) cls.getAnnotation(WebService.class);
        if (wsAnn != null) {
            return true;
        }
        
        // Check for service
        WebServiceClient wscAnn = (WebServiceClient) cls.getAnnotation(WebServiceClient.class);
        if (wscAnn != null) {
            return true;
        }
        
        // Check for provider
        WebServiceProvider wspAnn = (WebServiceProvider) cls.getAnnotation(WebServiceProvider.class);
        if (wspAnn != null) {
            return true;
        }
        
        // Check for Exception
        WebFault wfAnn = (WebFault) cls.getAnnotation(WebFault.class);
        if (wfAnn != null) {
            return true;
        }
        
        // Check for Holder
        if (Holder.class.isAssignableFrom(cls)) {
            return true;
        }
        
        if (cls.getPackage() != null && cls.getPackage().getName().startsWith("javax.xml.ws")) {
            return true;
        }
        return false;
    }

    
    /**
     * @param clazz
     * @return namespace of root element qname or null if this is not object does not represent a root element
     */
    public static QName getXmlRootElementQName(Object obj){
        
        // A JAXBElement stores its name
        if (obj instanceof JAXBElement) {
            return ((JAXBElement) obj).getName();
        }
        
        Class clazz = obj.getClass();
        
        // If the clazz is a primitive, then it does not have a corresponding root element.
        if (clazz.isPrimitive() ||
                getWrapperClass(clazz) != null) {
            return null;
        }
        
        // See if the object represents a root element
        XmlRootElement root = (XmlRootElement) clazz.getAnnotation(XmlRootElement.class);
        if (root == null) {
            return null;
        }
        
        String namespace = root.namespace();
        String localPart = root.name();
        
        // The namespace may need to be defaulted
        if (namespace == null || namespace.length() == 0 || namespace.equals("##default")) {
            Package pkg = clazz.getPackage();
            XmlSchema schema = (XmlSchema) pkg.getAnnotation(XmlSchema.class);
            if (schema != null) {
                namespace = schema.namespace();
            } else {
                return null;
            }
        }
        return new QName(namespace, localPart);
    }
    
    /**
     * @param clazz
     * @return true if this class has a corresponding xml root element
     */
    public static boolean isXmlRootElementDefined(Class clazz){
        // If the clazz is a primitive, then it does not have a corresponding root element.
        if (clazz.isPrimitive() ||
                getWrapperClass(clazz) != null) {
            return false;
        }
        // TODO We could also prune out other known classes that will not have root elements defined.
        // java.util.Date, arrays, java.math.BigInteger.
        
        XmlRootElement root = (XmlRootElement) clazz.getAnnotation(XmlRootElement.class);
        return root !=null;
    }
}

