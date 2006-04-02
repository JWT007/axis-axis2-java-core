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

package org.apache.axis2.wsdl.builder.wsdl4j;

import org.apache.axis2.util.XMLUtils;
import org.apache.axis2.wsdl.WSDLVersionWrapper;
import org.apache.axis2.wsdl.builder.WOMBuilder;
import org.apache.axis2.wsdl.builder.WSDLComponentFactory;
import org.apache.wsdl.WSDLDescription;
import org.apache.wsdl.impl.WSDLDescriptionImpl;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class WSDL1ToWOMBuilder implements WOMBuilder {

    /**
     * Builds a WOM and a WSDL4J object model given the URI of the WSDL file and
     * returns a wrapper object WSDLVersionWrapper.
     * @param in InputStream from which the WSDL document can be read in.
     * @return Returns WSDLVersionWrapper which contains both the WSDL 2.0 and WSDL 1.1
     * object models.
     * @throws WSDLException
     */
    public WSDLVersionWrapper build(InputStream in) throws WSDLException {
        return build(in, null);
    }

    /**
     * Builds a WOM and a WSDL4J object model given the URI of the WSDL file and
     * returns a wrapper object WSDLVersionWrapper. A WSDL Component Factory
     * can be passed into the builder using which the WOM component can be built out of.
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
                                    WSDLComponentFactory wsdlComponentFactory) throws WSDLException {
        if(null == wsdlComponentFactory){
            wsdlComponentFactory = new WSDLDescriptionImpl();
        }
        WSDLDescription wsdlDescription = wsdlComponentFactory.createDescription();

        Definition wsdl1Definition = this.readInTheWSDLFile(in);
        WSDLPump pump = new WSDLPump(wsdlDescription, wsdl1Definition);
        pump.pump();


        return new WSDLVersionWrapper(wsdlDescription, wsdl1Definition);
    }


    /**
     * Builds a WOM and a WSDL4J object model given the URI of the WSDL file and
     * returns a wrapper object WSDLVersionWrapper.
     * @param uri URI pointing to the WSDL document.
     * @return Returns WSDLVersionWrapper which contains both the WSDL 2.0 and WSDL 1.1
     * object models.
     * @throws WSDLException
     */
    public WSDLVersionWrapper build(String uri) throws WSDLException {
        return build(uri, null);
    }

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
     * @param uri URI pointing to the WSDL document.
     * @param wsdlComponentFactory The ComponentFactory that will be used to create the
     * WOm components out of.
     * @return Returns WSDLVersionWrapper which contains both the WSDL 2.0 and WSDL 1.1
     * object models.
     * @throws WSDLException
     */
    public WSDLVersionWrapper build(String uri,
                                    WSDLComponentFactory wsdlComponentFactory) throws WSDLException {
        if(null == wsdlComponentFactory){
            wsdlComponentFactory = new WSDLDescriptionImpl();
        }
        WSDLDescription wsdlDescription = wsdlComponentFactory.createDescription();

        Definition wsdl1Definition = this.readInTheWSDLFile(uri);
        WSDLPump pump = new WSDLPump(wsdlDescription,
                wsdl1Definition,
                wsdlComponentFactory);
        pump.pump();

        //put the debugging serializer code here!

        return new WSDLVersionWrapper(wsdlDescription, wsdl1Definition);

    }

    /**
     * 
     * @param uri
     * @return
     * @throws WSDLException
     */
    private Definition readInTheWSDLFile(String uri) throws WSDLException {

        WSDLReader reader =
                WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", true);

        File file = new File(uri);
        String baseURI;
        
        if (uri.startsWith("http://")){
             baseURI = uri;
        } else{
            baseURI = file.getParentFile()!=null?file.getParentFile().toURI().toString():null;
        }


        Document doc;
        try {
            doc = XMLUtils.newDocument(uri);
        } catch (ParserConfigurationException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser Configuration Error",
                    e);
        } catch (SAXException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser SAX Error",
                    e);

        } catch (IOException e) {
            throw new WSDLException(WSDLException.INVALID_WSDL, "IO Error", e);
        }

        return reader.readWSDL(baseURI, doc);
    }

    /**
     *
     * @param in
     * @return
     * @throws WSDLException
     */
    private Definition readInTheWSDLFile(InputStream in) throws WSDLException {

        WSDLReader reader =
                WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", true);
        Document doc;
        try {
            doc = XMLUtils.newDocument(in);
        } catch (ParserConfigurationException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser Configuration Error",
                    e);
        } catch (SAXException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser SAX Error",
                    e);

        } catch (IOException e) {
            throw new WSDLException(WSDLException.INVALID_WSDL, "IO Error", e);
        }

        return reader.readWSDL(null, doc);
    }


}
