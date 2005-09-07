package org.apache.axis2.wsdl.codegen;

import javax.xml.namespace.QName;

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
*   Constants for the XSLT related items
*/

public interface XSLTConstants {
    String DEFAULT_PACKAGE_NAME = "codegen";
    QName BASE_64_CONTENT_QNAME= new QName("http://www.w3.org/2001/XMLSchema","base64Binary");
    String BASE_64_PROPERTY_KEY = "base64map";
    /**
     * Language constants
     */
    public interface LanguageTypes {

        public static final int JAVA = 1;
        public static final int C_SHARP = 2;
        public static final int C_PLUS_PLUS = 3;
        public static final int VB_DOT_NET = 4;
    }

    public interface DataBindingTypes {

        public static final int NONE = 0;
        public static final int XML_BEANS = 1;
        public static final int JAXB = 2;

    }

    public interface CodegenStyle{
        public static final int AUTOMATIC = 0;
        public static final int INTERFACE = 1;
        public static final int BINDING = 2;
    }
    /**
     * Interface templates
     */
    public interface XSLTInterfaceTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/InterfaceTemplate.xsl";
        public static final String CSHARP_TEMPLATE = "/org/apache/axis2/wsdl/template/csharp/InterfaceTemplate.xsl";
    }

    /**
     * Interface implementation templates
     */
    public interface XSLTInterfaceImplementationTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/InterfaceImplementationTemplate.xsl";
        public static final String CSHARP_TEMPLATE = "/org/apache/axis2/wsdl/template/csharp/InterfaceImplementationTemplate.xsl";
    }

    /**
     * Interface bean templates
     */
    public interface XSLTBeanTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/BeanTemplate.xsl";
        public static final String CSHARP_TEMPLATE = "/org/apache/axis2/wsdl/template/csharp/BeanTemplate.xsl";
    }

    /**
     * Interface CallbackHanlder Template
     */
    public interface XSLTCallbackHandlerTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/CallbackHandlerTemplate.xsl";
        public static final String CSHARP_TEMPLATE = "/org/apache/axis2/wsdl/template/csharp/CallbackHandlerTemplate.xsl";
    }

    /**
     * Interface skeletons
     */
    public interface XSLTSkeletonTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/SkeletonTemplate.xsl";
        public static final String CSHARP_TEMPLATE = "/org/apache/axis2/wsdl/template/csharp/SkeletonTemplate.xsl";
    }

    public interface XSLTTestClassTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/TestClassTemplate.xsl";
        public static final String CSHARP_TEMPLATE = "/org/apache/axis2/wsdl/template/csharp/TestClassTemplate.xsl";
    }

    public interface XSLTDatabindingSupporterTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/XMLBeansSupporterTemplate.xsl";
        public static final String JAXB_TEMPLATE = "/org/apache/axis2/wsdl/template/java/JAXBSupporterTemplate.xsl";
        public static final String DEFAULT_TEMPLATE = "/org/apache/axis2/wsdl/template/java/DefaultDataBindingSupporterTemplate.xsl";
        //public static final String CSHARP_TEMPLATE = "/org/apache/axis2/wsdl/template/csharp/TestClassTemplate.xsl";
    }

    public interface XSLTLocalTestClassTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/TestClassTemplate.xsl";
        public static final String CSHARP_TEMPLATE = "/org/apache/axis2/wsdl/template/csharp/LocalTestClassTemplate.xsl";
    }

    public interface XSLTTestSkeletonImplTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/TestSkeletonImplTemplate.xsl";
    }

    public interface XSLTServiceXMLTemplates {
        public static final String GENERAL_SERVICE_TEMPLATE = "/org/apache/axis2/wsdl/template/general/ServiceXMLTemplate.xsl";
    }

    public interface XSLTMessageReceiverTemplates {
        public static final String JAVA_TEMPLATE = "/org/apache/axis2/wsdl/template/java/MessageReceiverTemplate.xsl";
    }


}
