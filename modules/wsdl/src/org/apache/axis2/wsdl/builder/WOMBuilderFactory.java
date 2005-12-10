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

import org.apache.axis2.wsdl.builder.wsdl4j.WSDL1ToWOMBuilder;
import org.apache.wsdl.WSDLConstants;
import org.apache.wsdl.util.Utils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class WOMBuilderFactory {


    public static WOMBuilder getBuilder(int wsdlDocumentType) throws WSDLException {

        if (wsdlDocumentType == WSDLConstants.WSDL_1_1) {
            return new WSDL1ToWOMBuilder();
        }
        if (wsdlDocumentType == WSDLConstants.WSDL_2_0) {
            return new WSDL2ToWOMBuilder();
        }
        throw new WSDLException(WSDLException.INVALID_WSDL,
                "The document type specified is not valid");
    }


    public static WOMBuilder getBuilder(InputStream in) throws WSDLException {
        // Load the wsdl as a DOM
        Document doc;
        try {
            doc = Utils.newDocument(in);
        } catch (ParserConfigurationException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser Configuration Exception",
                    e);
        } catch (IOException e1) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "WSDL Document read error",
                    e1);
        } catch (SAXException e2) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser Exception",
                    e2);
        }
        
        
        //Check the target namespace of the WSDL and determine the WSDL version.
        int version = getWSDLVersion(doc);

        if (version == WSDLConstants.WSDL_1_1) {
            return new WSDL1ToWOMBuilder();
        } else if (version == WSDLConstants.WSDL_2_0) {
            return new WSDL2ToWOMBuilder();
        }

        throw new WSDLException(WSDLException.OTHER_ERROR,
                "Unable to Figure out the WSDL vesion of the Document");
    }

    /**
     * Will return an int that will represent the wsdl version and the int will correspond to the static
     * variables defined in this class.
     *
     * @param doc
     * @return
     * @throws WSDLException If the version cannot be determined
     */
    private static int getWSDLVersion(Document doc) throws WSDLException {
        //TODO check weather the namespaces are correct and the / problem too
        if (WSDLConstants.WSDL2_0_NAMESPACE.equals(
                doc.getDocumentElement().getNamespaceURI())) {
            return WSDLConstants.WSDL_2_0;
        } else if (WSDLConstants.WSDL1_1_NAMESPACE.equals(
                doc.getDocumentElement().getNamespaceURI())) {
            return WSDLConstants.WSDL_1_1;
        }

        throw new WSDLException(WSDLException.OTHER_ERROR,
                "Unable to Figure out the WSDL vesion of the Document");
    }
}
