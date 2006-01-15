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

package org.apache.axis2.wsdl.codegen.extension;

import com.ibm.wsdl.util.xml.DOM2Writer;
import org.apache.axis2.wsdl.databinding.DefaultTypeMapper;
import org.apache.axis2.wsdl.databinding.JavaTypeMapper;
import org.apache.axis2.wsdl.i18n.CodegenMessages;
import org.apache.axis2.wsdl.util.ConfigPropertyFileLoader;
import org.apache.ws.jaxme.generator.impl.GeneratorImpl;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.wsdl.WSDLBinding;
import org.apache.wsdl.WSDLBindingMessageReference;
import org.apache.wsdl.WSDLBindingOperation;
import org.apache.wsdl.WSDLConstants;
import org.apache.wsdl.WSDLExtensibilityElement;
import org.apache.wsdl.WSDLTypes;
import org.apache.wsdl.extensions.ExtensionConstants;
import org.apache.wsdl.extensions.SOAPBody;
import org.apache.wsdl.extensions.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class JaxMeExtension extends AbstractDBProcessingExtension {
    public static final String SCHEMA_FOLDER = "schemas";

    public static String MAPPINGS = "mappings";
    public static String MAPPING = "mapping";
    public static String MESSAGE = "message";
    public static String JAVA_NAME = "javaclass";

    public static final String MAPPING_FOLDER = "Mapping";
    public static final String MAPPER_FILE_NAME = "mapper";
    public static final String SCHEMA_PATH = "/org/apache/axis2/wsdl/codegen/schema/";

    boolean debug = false;
    
    public void engage() {

        //test the databinding type. If not just fall through
        if (testFallThrough(configuration.getDatabindingType())) {
            return;
        }

        //check the comptibilty
        checkCompatibility();

        try {
            //get the types from the types section
            WSDLTypes typesList = configuration.getWom().getTypes();

            //check for the imported types. Any imported types are supposed to be here also
            if (typesList == null) {
                //there are no types to be code generated
                //However if the type mapper is left empty it will be a problem for the other
                //processes. Hence the default type mapper is set to the configuration
                this.configuration.setTypeMapper(new DefaultTypeMapper());
                return;
            }

            List typesArray = typesList.getExtensibilityElements();
            //this a list that keeps the already processed schemas
            List processedSchemas = new ArrayList();

            WSDLExtensibilityElement extensiblityElt;

            Vector xmlObjectsVector = new Vector();
            //create the type mapper
            JavaTypeMapper mapper = new JavaTypeMapper();

            for (int i = 0; i < typesArray.size(); i++) {
                extensiblityElt = (WSDLExtensibilityElement) typesArray.get(i);
                Schema schema;

                if (ExtensionConstants.SCHEMA.equals(extensiblityElt.getType())) {
                    schema = (Schema) extensiblityElt;

//                    TODO: FIXME                    
//                    Map namespaceMap = configuration.getWom().getNamespaces();
//                    if (namespaceMap != null && !namespaceMap.isEmpty()) {
//                        Iterator nameSpaces = namespaceMap.keySet().iterator();
//                        String nsPrefix;
//                        String nsURI;
//                        while (nameSpaces.hasNext()) {
//                            nsPrefix = (String) nameSpaces.next();
//                            nsURI = namespaceMap.get(nsPrefix).toString();
//                            parser.addImport(nsURI, null);
//                        }
//                    }

                    Stack importedSchemaStack = schema.getImportedSchemaStack();
                    File schemaFolder = null;
                    if(debug) {
                        schemaFolder = new File(configuration.getOutputLocation(), SCHEMA_FOLDER);
                        schemaFolder.mkdir();
                    }
                    //compile these schemas
                    while (!importedSchemaStack.isEmpty()) {
                        Element element = (Element) importedSchemaStack.pop();
                        String tagetNamespace = element.getAttribute("targetNamespace");
                        if (!processedSchemas.contains(tagetNamespace)) {

                            String s = DOM2Writer.nodeToString(element);
                            xmlObjectsVector.add(new InputSource(new StringReader(s)));
                            processedSchemas.add(tagetNamespace);
                        }
                    }
                }
            }


//            TODO: FIXME            
//            Element[] additionalSchemas = loadAdditionalSchemas();
//            // Need to add the third party schemas
//            for (int i = 0; i < additionalSchemas.length; i++) {
//                String s = DOM2Writer.nodeToString(additionalSchemas[i]);
//                xmlObjectsVector.add(new InputSource(new StringReader(s)));
//            }

            File outputDir = new File(configuration.getOutputLocation(), "src");

            JAXBSchemaReader reader = new JAXBSchemaReader();
            reader.setSupportingExtensions(true);

            GeneratorImpl generator = new GeneratorImpl();
            generator.setTargetDirectory(outputDir);
            generator.setForcingOverwrite(false);
            generator.setSchemaReader(reader);

            for (int i = 0; i < xmlObjectsVector.size(); i++) {
                SchemaSG sg = generator.generate((InputSource) xmlObjectsVector.elementAt(i));
                ObjectSG[] elements = sg.getElements();
                for(int j=0;j<elements.length;j++){
                    XsQName qName = elements[j].getName();
                    JavaQName name = elements[j].getClassContext().getXMLInterfaceName();
                    mapper.addTypeMappingName(new QName(qName.getNamespaceURI(), qName.getLocalName()), 
                                            name.getPackageName() + '.' + name.getClassName());
                }
                TypeSG[] types = sg.getTypes();
                for(int j=0;j<types.length;j++){
                    XsQName qName = types[j].getName();
                    JavaQName name = types[j].getRuntimeType();
                    mapper.addTypeMappingName(new QName(qName.getNamespaceURI(), qName.getLocalName()), 
                                            name.getPackageName() + '.' + name.getClassName());
                }
                GroupSG[] groups = sg.getGroups();
                for(int j=0;j<groups.length;j++){
                    XsQName qName = groups[j].getName();
                    JavaQName name = groups[j].getClassContext().getXMLInterfaceName();
                    mapper.addTypeMappingName(new QName(qName.getNamespaceURI(), qName.getLocalName()), 
                                            name.getPackageName() + '.' + name.getClassName());
                }
            }

//            TODO: FIXME                    
//            // prune the generated schema type system and add the list of base64 types
//            FindBase64Types(sts);
//
//            //get the schematypes and add the document types to the type mapper
//            SchemaType[] schemaType = sts.documentTypes();
//            SchemaType type;
//            for (int j = 0; j < schemaType.length; j++) {
//                type = schemaType[j];
//                mapper.addTypeMappingName(type.getDocumentElementName(),
//                        type.getFullJavaName());
//            }

            //set the type mapper to the config
            configuration.setTypeMapper(mapper);

            if(debug) {
                // write the mapper to a file for later retriival
                writeMappingsToFile(mapper.getAllMappedNames());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void writeMappingsToFile(Map typeMappings) throws IOException {

        File typeMappingFolder = new File(configuration.getOutputLocation(), MAPPING_FOLDER);
        if (!typeMappingFolder.exists()) {
            typeMappingFolder.mkdir();
        }

        File typeMappingFile = File.createTempFile(MAPPER_FILE_NAME, ".xml", typeMappingFolder);
        BufferedWriter out = new BufferedWriter(new FileWriter(typeMappingFile));
        out.write("<" + MAPPINGS + ">");

        Iterator iterator = typeMappings.keySet().iterator();
        while (iterator.hasNext()) {
            QName qName = (QName) iterator.next();
            String fullJavaName = (String) typeMappings.get(qName);
            out.write("<" + MAPPING + ">");
            out.write("<" + MESSAGE + ">" + qName.getLocalPart() + "</" + MESSAGE + ">");
            out.write("<" + JAVA_NAME + ">" + fullJavaName + "</" + JAVA_NAME + ">");
            out.write("</" + MAPPING + ">");
        }
        out.write("</" + MAPPINGS + ">");
        out.close();

    }

//   TODO: FIXME    
//    /**
//     * Populate the base64 types
//     * The algo is to look for simpletypes that have base64 content, and then step out of that
//     * onestep and get the element. For now there's an extended check to see whether the simple type
//     * is related to the Xmime:contentType!
//     *
//     * @param sts
//     */
//    private void FindBase64Types(SchemaTypeSystem sts) {
//        List allSeenTypes = new ArrayList();
//        List base64ElementQNamesList = new ArrayList();
//        SchemaType outerType;
//        //add the document types and global types
//        allSeenTypes.addAll(Arrays.asList(sts.documentTypes()));
//        allSeenTypes.addAll(Arrays.asList(sts.globalTypes()));
//        for (int i = 0; i < allSeenTypes.size(); i++) {
//            SchemaType sType = (SchemaType) allSeenTypes.get(i);
//
//            if (sType.getContentType() == SchemaType.SIMPLE_CONTENT && sType.getPrimitiveType() != null) {
//                if (XSLTConstants.BASE_64_CONTENT_QNAME.equals(sType.getPrimitiveType().getName())) {
//                    outerType = sType.getOuterType();
//                    //check the outer type further to see whether it has the contenttype attribute from
//                    //XMime namespace
//                    SchemaProperty[] properties = sType.getProperties();
//                    for (int j = 0; j < properties.length; j++) {
//                        if (XSLTConstants.XMIME_CONTENT_TYPE_QNAME.equals(properties[j].getName())) {
//                            base64ElementQNamesList.add(outerType.getDocumentElementName());
//                            break;
//                        }
//                    }
//                }
//            }
//            //add any of the child types if there are any
//            allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
//        }
//
//        configuration.putProperty(XSLTConstants.BASE_64_PROPERTY_KEY, base64ElementQNamesList);
//    }

    /**
     * Loading the external schemas.
     *
     * @return element array consisting of the the DOM element objects that represent schemas
     */
    private Element[] loadAdditionalSchemas() {
        //load additional schemas
        String[] schemaNames = ConfigPropertyFileLoader.getThirdPartySchemaNames();
        Element[] schemaElements;

        try {
            ArrayList additionalSchemaElements = new ArrayList();
            DocumentBuilder documentBuilder = getNamespaceAwareDocumentBuilder();
            for (int i = 0; i < schemaNames.length; i++) {
                //the location for the third party schema;s is hardcoded
                if (!"".equals(schemaNames[i].trim())) {
                    InputStream schemaStream = this.getClass().getResourceAsStream(SCHEMA_PATH + schemaNames[i]);
                    Document doc = documentBuilder.parse(schemaStream);
                    additionalSchemaElements.add(doc.getDocumentElement());
                }
            }

            //Create the Schema element array
            schemaElements = new Element[additionalSchemaElements.size()];
            for (int i = 0; i < additionalSchemaElements.size(); i++) {
                schemaElements[i] = (Element) additionalSchemaElements.get(i);

            }
        } catch (Exception e) {
            throw new RuntimeException(CodegenMessages.getMessage("extension.additionalSchemaFailure"), e);
        }

        return schemaElements;
    }

    private DocumentBuilder getNamespaceAwareDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        return documentBuilderFactory.newDocumentBuilder();
    }


    /**
     * Checking the compatibilty has to do with generating RPC/encoded stubs.
     * If the XMLBeans bindings are used encoded binding cannot be done.
     */
    private void checkCompatibility() {
        Map bindingMap = this.configuration.getWom().getBindings();
        Collection col = bindingMap.values();

        for (Iterator iterator = col.iterator(); iterator.hasNext();) {
            WSDLBinding b = (WSDLBinding) iterator.next();
            HashMap bindingOps = b.getBindingOperations();
            Collection bindingOpsCollection = bindingOps.values();
            for (Iterator iterator1 = bindingOpsCollection.iterator(); iterator1.hasNext();) {
                foo((WSDLBindingOperation) iterator1.next());
            }

        }
    }

    protected void foo(WSDLBindingOperation bindingOp) {
        WSDLBindingMessageReference input = bindingOp.getInput();
        if (input != null) {
            Iterator extIterator = input.getExtensibilityElements()
                    .iterator();
            while (extIterator.hasNext()) {
                WSDLExtensibilityElement element = (WSDLExtensibilityElement) extIterator.next();
                if (ExtensionConstants.SOAP_11_BODY.equals(element.getType()) ||
                        ExtensionConstants.SOAP_12_BODY.equals(element.getType())) {
                    if (WSDLConstants.WSDL_USE_ENCODED.equals(
                            ((SOAPBody) element).getUse())) {
                        throw new RuntimeException(
                                CodegenMessages.getMessage("extension.encodedNotSupported"));
                    }
                }
            }
        }
    }
}

