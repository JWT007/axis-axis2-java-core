package org.apache.ws.java2wsdl.bytecode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This class retrieves function parameter names from bytecode built with
 * debugging symbols.  Used as a last resort when creating WSDL.
 */
public class ParamNameExtractor {

	private static final Log log = LogFactory.getLog(ParamNameExtractor.class);

    /**
     * Retrieves a list of function parameter names from a method. 
     * Returns null if unable to read parameter names (i.e. bytecode not
     * built with debug).
     */
    public static String[] getParameterNamesFromDebugInfo(Method method) {
        // Don't worry about it if there are no params.
        int numParams = method.getParameterTypes().length;
        if (numParams == 0)
            return null;

        // get declaring class
        Class c = method.getDeclaringClass();

        // Don't worry about it if the class is a Java dynamic proxy
        if (Proxy.isProxyClass(c)) {
            return null;
        }

        try {
            // get a parameter reader
            ParamReader pr = new ParamReader(c);
            // get the parameter names
            return pr.getParameterNames(method);
        } catch (IOException e) {
            // log it and leave
            return null;
        }
    }
}
