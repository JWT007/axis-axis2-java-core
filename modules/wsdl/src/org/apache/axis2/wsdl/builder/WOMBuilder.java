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

package org.apache.axis2.wsdl.builder;

import org.apache.axis2.wsdl.WSDLVersionWrapper;

import javax.wsdl.WSDLException;
import java.io.InputStream;

public interface WOMBuilder {

	/**
	 * Builds a WOM and a WSDL4J object model given the URI of the WSDL file and
	 * returns as a wrapper object WSDLVersionWrapper.
	 * @param uri URI pointing to the WSDL document.
	 * @return Returns WSDLVersionWrapper which contains both the WSDL 2.0 and WSDL 1.1 
	 * object models.
	 * @throws WSDLException
	 */
    public WSDLVersionWrapper build(String uri) throws WSDLException;
    
    /**
	 * Builds a WOM and a WSDL4J object model given the URI of the WSDL file and
	 * returns a wrapper object WSDLVersionWrapper. A WSDL Component Factory
	 * can be passed into the builder using which the WOM component can be built.
	 * For example: The Engine uses the WOM's components in the context hierarchy but 
	 * those are extended components. 
	 * (<code>AxisService</code> extends <code>WSDLService</code>.)
	 * So when deployment build the WOM it would prefer to get a <code>AxisService</code>
	 * built in place of a <code>WSDLService</code>. This can be achieved by passing the 
	 * correct Component Factory that will instantiate the correct object for the WOM builder.
	 * @param uri URI pointing to the WSDL document.
	 * @param wsdlComponentFactory The ComponentFactory that will be used to create the
	 * WOM components out of.
	 * @return Returns WSDLVersionWrapper which contains both the WSDL 2.0 and WSDL 1.1 
	 * object models.
	 * @throws WSDLException
	 */
    public WSDLVersionWrapper build(String uri,
                                    WSDLComponentFactory wsdlComponentFactory) throws WSDLException;
    
    /**
	 * Builds a WOM and a WSDL4J object model given the URI of the WSDL file and
	 * returns a wrapper object WSDLVersionWrapper.
	 * @param in InputStream from which the WSDL document can be read in.
	 * @return Returns WSDLVersionWrapper which contains both the WSDL 2.0 and WSDL 1.1 
	 * object models.
	 * @throws WSDLException
	 */
    public WSDLVersionWrapper build(InputStream in) throws WSDLException ;
    
    /**
	 * Builds a WOM and a WSDL4J object model given the URI of the WSDL file and
	 * returns a wrapper object WSDLVersionWrapper. A WSDL Component Factory
	 * can be passed into the builder using which the WOM component can be built.
	 * For example: The Engine uses the WOM's components in the context hierarchy but 
	 * those are extended components. 
	 * (<code>AxisService</code> extends <code>WSDLService</code>.)
	 * So when deployment build the WOM it would prefer to get a <code>AxisService</code>
	 * built in place of a <code>WSDLService</code>. This can be achieved by passing the 
	 * correct Component Factory that will instanciate the correct object for the WOM builder.
	 * @param in InputStream from which the WSDL document can be read in.
	 * @param wsdlComponentFactory The ComponentFactory that will be used to create the
	 * WOm components out of.
	 * @return Returns WSDLVersionWrapper which contains both the WSDL 2.0 and WSDL 1.1 
	 * object models.
	 * @throws WSDLException
	 */
    public WSDLVersionWrapper build(InputStream in, 
			WSDLComponentFactory wsdlComponentFactory) throws WSDLException;
}
