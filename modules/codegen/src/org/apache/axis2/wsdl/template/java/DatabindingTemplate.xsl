<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>

    <!-- #################################################################################  -->
    <!-- ############################   xmlbeans template   ##############################  -->
    <xsl:template match="databinders[@dbtype='xmlbeans']">

        <xsl:variable name="base64"><xsl:value-of select="base64Elements/name"/></xsl:variable>
        <xsl:if test="$base64">
            private static javax.xml.namespace.QName[] qNameArray = {
            <xsl:for-each select="base64Elements/name">
                <xsl:if test="position()>1">,</xsl:if>new javax.xml.namespace.QName("<xsl:value-of select="@ns-url"/>","<xsl:value-of select="@localName"/>")
            </xsl:for-each>
            };
        </xsl:if>

        <xsl:for-each select="param">
            <xsl:if test="@type!=''">
                private  org.apache.axiom.om.OMElement  toOM(<xsl:value-of select="@type"/> param, boolean optimizeContent){
                org.apache.axiom.om.impl.builder.StAXOMBuilder builder = new org.apache.axiom.om.impl.builder.StAXOMBuilder
                (org.apache.axiom.om.OMAbstractFactory.getOMFactory(),new org.apache.axis2.util.StreamWrapper(param.newXMLStreamReader())) ;
                 org.apache.axiom.om.OMElement documentElement = builder.getDocumentElement();

		<xsl:if test="$base64">
		if (optimizeContent) {
                         optimizeContent(documentElement,qNameArray);
		}
                </xsl:if>

                  ((org.apache.axiom.om.impl.OMNodeEx)documentElement).setParent(null);
                  return documentElement;
                }

                private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, <xsl:value-of select="@type"/> param, boolean optimizeContent){
                    org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                    if (param != null){
                        envelope.getBody().addChild(toOM(param, optimizeContent));
                    }
                    return envelope;
                }
            </xsl:if>

        </xsl:for-each>

        /**
         *  get the default envelope
         */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
            return factory.getDefaultEnvelope();
        }

        public org.apache.xmlbeans.XmlObject fromOM(
            org.apache.axiom.om.OMElement param,
            java.lang.Class type,
            java.util.Map extraNamespaces){
        try{
        <xsl:for-each select="param">
            <xsl:if test="@type!=''">
                if (<xsl:value-of select="@type"/>.class.equals(type)){
                if (extraNamespaces!=null){
                 return <xsl:value-of select="@type"/>.Factory.parse(
                       param.getXMLStreamReaderWithoutCaching(),
                       new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
                }else{
                 return <xsl:value-of select="@type"/>.Factory.parse(
                       param.getXMLStreamReaderWithoutCaching());
                }
                }
            </xsl:if>
        </xsl:for-each>
        }catch(java.lang.Exception e){
        throw new RuntimeException("Data binding error",e);
        }
        return null;
        }

    <!-- Generate the base 64 optimize methods only if the base64 items are present -->    
   <xsl:if test="$base64">
		
   private void optimizeContent(org.apache.axiom.om.OMElement element, javax.xml.namespace.QName[] qNames){
        for (int i = 0; i &lt; qNames.length; i++) {
            markElementsAsOptimized(qNames[i],element);
        }
    }

    private void markElementsAsOptimized(javax.xml.namespace.QName qName,org.apache.axiom.om.OMElement rootElt){
        if (rootElt.getQName().equals(qName)){
            //get the text node and mark it
            org.apache.axiom.om.OMNode node = rootElt.getFirstOMChild();
            if (node.getType()==org.apache.axiom.om.OMNode.TEXT_NODE){
                ((org.apache.axiom.om.OMText)node).setOptimize(true);
            }

        }
        java.util.Iterator childElements = rootElt.getChildElements();
        while (childElements.hasNext()) {
            markElementsAsOptimized(qName,(org.apache.axiom.om.OMElement)childElements.next());
        }
    }
    </xsl:if>
    </xsl:template>
    <!-- #################################################################################  -->
    <!-- ############################   jaxme template   ##############################  -->
    <xsl:template match="databinders[@dbtype='jaxme']">

        <xsl:variable name="base64"><xsl:value-of select="base64Elements/name"/></xsl:variable>
        <xsl:if test="$base64">
            private static javax.xml.namespace.QName[] qNameArray = {
            <xsl:for-each select="base64Elements/name">
                <xsl:if test="position()>1">,</xsl:if>new javax.xml.namespace.QName("<xsl:value-of select="@ns-url"/>","<xsl:value-of select="@localName"/>")
            </xsl:for-each>
            };
        </xsl:if>

        private org.apache.axiom.om.OMElement toOM(Object param) {
            try {
                javax.xml.bind.JAXBContext ctx = javax.xml.bind.JAXBContext.newInstance(param.getClass().getInterfaces()[0].getPackage()
                                                                                            .getName());
                org.apache.axiom.om.impl.builder.SAXOMBuilder builder = new org.apache.axiom.om.impl.builder.SAXOMBuilder();
                javax.xml.bind.Marshaller marshaller = ctx.createMarshaller();
                marshaller.marshal(param, builder);
                return builder.getRootElement();
            } catch (javax.xml.bind.JAXBException e) {
                throw new RuntimeException(e);
            }
        }

        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory, Object param, boolean optimizeContent) {
            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
            if (param != null){
                envelope.getBody().addChild(toOM(param));
            }

            return envelope;
        }

        public java.lang.Object fromOM(org.apache.axiom.om.OMElement param,
        java.lang.Class type,
         java.util.Map extraNamespaces){
            try{
                javax.xml.transform.Source source =
                        new javanet.staxutils.StAXSource(param.getXMLStreamReader());
                javax.xml.bind.JAXBContext ctx = javax.xml.bind.JAXBContext.newInstance(
                        type.getPackage().getName());
                javax.xml.bind.Unmarshaller u = ctx.createUnmarshaller();
                return u.unmarshal(source);
            } catch(java.lang.Exception e) {
                throw new RuntimeException("Data binding error",e);
            }
        }
    <!-- Generate the base 64 optimize methods only if the base64 items are present -->    
   <xsl:if test="$base64">
   private void optimizeContent(org.apache.axiom.om.OMElement element, javax.xml.namespace.QName[] qNames){
        for (int i = 0; i &lt; qNames.length; i++) {
            markElementsAsOptimized(qNames[i],element);
        }
    }

    private void markElementsAsOptimized(javax.xml.namespace.QName qName,org.apache.axiom.om.OMElement rootElt){
        if (rootElt.getQName().equals(qName)){
            //get the text node and mark it
            org.apache.axiom.om.OMNode node = rootElt.getFirstOMChild();
            if (node.getType()==org.apache.axiom.om.OMNode.TEXT_NODE){
                ((org.apache.axiom.om.OMText)node).setOptimize(true);
            }

        }
        java.util.Iterator childElements = rootElt.getChildElements();
        while (childElements.hasNext()) {
            markElementsAsOptimized(qName,(org.apache.axiom.om.OMElement)childElements.next());
        }
    }
    </xsl:if>
    </xsl:template>
    <!-- #################################################################################  -->
       <!-- ############################   ADB template   ##############################  -->
       <xsl:template match="databinders[@dbtype='adb']">

            <xsl:variable name="base64"><xsl:value-of select="base64Elements/name"/></xsl:variable>
            <xsl:if test="$base64">
                private static javax.xml.namespace.QName[] qNameArray = {
                <xsl:for-each select="base64Elements/name">
                    <xsl:if test="position()>1">,</xsl:if>new javax.xml.namespace.QName("<xsl:value-of select="@ns-url"/>","<xsl:value-of select="@localName"/>")
                </xsl:for-each>
                };
            </xsl:if>

            <xsl:for-each select="param">
                <xsl:if test="@type!=''">
                    private  org.apache.axiom.om.OMElement  toOM(<xsl:value-of select="@type"/> param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axiom.om.impl.builder.StAXOMBuilder builder
                                       = new org.apache.axiom.om.impl.builder.StAXOMBuilder
                            (org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
                               new org.apache.axis2.util.StreamWrapper(param.getPullParser(<xsl:value-of select="@type"/>.MY_QNAME)));
                            org.apache.axiom.om.OMElement documentElement = builder.getDocumentElement();
                            ((org.apache.axiom.om.impl.OMNodeEx) documentElement).setParent(null); // remove the parent link
                            return documentElement;
                        }else{
                           <!-- treat this as a plain bean. use the reflective bean converter -->
                           //todo finish this onece the bean serializer has the necessary methods
                            return null;
                        }
                    }

                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, <xsl:value-of select="@type"/> param, boolean optimizeContent){
                        if (param instanceof org.apache.axis2.databinding.ADBBean){
                            org.apache.axis2.databinding.ADBSOAPModelBuilder builder = new
                                    org.apache.axis2.databinding.ADBSOAPModelBuilder(param.getPullParser(<xsl:value-of select="@type"/>.MY_QNAME),
                                                                                     factory);
                            return builder.getEnvelope();
                        }else{
                           <!-- treat this as a plain bean. use the reflective bean converter -->
                           //todo finish this onece the bean serializer has the necessary methods
                            return null;
                        }
                    }
                </xsl:if>
            </xsl:for-each>

           /**
           *  get the default envelope
           */
           private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
                return factory.getDefaultEnvelope();
           }


            private  java.lang.Object fromOM(
            org.apache.axiom.om.OMElement param,
            java.lang.Class type,
            java.util.Map extraNamespaces){

                try {
                       <xsl:for-each select="param">
                              <xsl:if test="@type!=''">
                      if (<xsl:value-of select="@type"/>.class.equals(type)){
                           return <xsl:value-of select="@type"/>.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                      }
                              </xsl:if>
                     </xsl:for-each>
                } catch (Exception e) {
                     throw new RuntimeException(e);
                }

                return null;
            }

        </xsl:template>
    <!-- #################################################################################  -->
    <!-- ############################   JiBX template   ##############################  -->
    <xsl:template match="databinders[@dbtype='jibx']">

        <xsl:variable name="base64"><xsl:value-of select="base64Elements/name"/></xsl:variable>
        <xsl:if test="$base64">
            private static javax.xml.namespace.QName[] qNameArray = {
            <xsl:for-each select="base64Elements/name">
                <xsl:if test="position()>1">,</xsl:if>new javax.xml.namespace.QName("<xsl:value-of select="@ns-url"/>","<xsl:value-of select="@localName"/>")
            </xsl:for-each>
            };
        </xsl:if>
        
        <xsl:variable name="firstType"><xsl:value-of select="param[1]/@type"/></xsl:variable>
        
        <xsl:for-each select="param">
            <xsl:if test="@type!=''">
            
                <xsl:if test="position()=1">
                    private static final org.jibx.runtime.IBindingFactory bindingFactory;
                    static {
                        org.jibx.runtime.IBindingFactory factory = null;
                        try {
                            factory = org.jibx.runtime.BindingDirectory.getFactory(<xsl:value-of select="@type"/>.class);
                        } catch (Exception e) { /** intentionally empty - report error on usage attempt */ }
                        bindingFactory = factory;
                    };
                </xsl:if>
                
                private org.apache.axiom.om.OMElement toOM(<xsl:value-of select="@type"/> param, org.apache.axiom.soap.SOAPFactory factory, boolean optimizeContent) {
                    if (param instanceof org.jibx.runtime.IMarshallable){
                        if (bindingFactory == null) {
                            throw new RuntimeException("Could not find JiBX binding information for <xsl:value-of select='$firstType'/>, JiBX binding unusable");
                        }
                        org.jibx.runtime.IMarshallable marshallable =
                            (org.jibx.runtime.IMarshallable)param;
                        int index = marshallable.JiBX_getIndex();
                        org.apache.axis2.jibx.JiBXDataSource source =
                            new org.apache.axis2.jibx.JiBXDataSource(marshallable, bindingFactory);
                        org.apache.axiom.om.OMNamespace namespace = factory.createOMNamespace(bindingFactory.getElementNamespaces()[index], null);
                        return factory.createOMElement(source, bindingFactory.getElementNames()[index], namespace);
                    } else {
                        throw new RuntimeException("No JiBX &lt;mapping> defined for class <xsl:value-of select='@type'/>");
                    }
                }
  
                private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, <xsl:value-of select="@type"/> param, boolean optimizeContent) {
                    org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                    if (param != null){
                        envelope.getBody().addChild(toOM(param, factory, optimizeContent));
                    }
                    return envelope;
                }
                
            </xsl:if>
        </xsl:for-each>
  
        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory) {
            return factory.getDefaultEnvelope();
        }
        
        private java.lang.Object fromOM(
            org.apache.axiom.om.OMElement param,
            java.lang.Class type,
            java.util.Map extraNamespaces) {
            try {
                if (bindingFactory == null) {
                    throw new RuntimeException("Could not find JiBX binding information for com.sosnoski.seismic.jibxsoap.Query, JiBX binding unusable");
                }
                org.jibx.runtime.impl.UnmarshallingContext ctx =
                    (org.jibx.runtime.impl.UnmarshallingContext)bindingFactory.createUnmarshallingContext();
                org.jibx.runtime.IXMLReader reader = new org.jibx.runtime.impl.StAXReaderWrapper(param.getXMLStreamReaderWithoutCaching(), "SOAP-message", true);
                ctx.setDocument(reader);
                return ctx.unmarshalElement(type);
            } catch (Exception e) {
                 throw new RuntimeException(e);
            }
        }

    </xsl:template>
    
       <!-- #################################################################################  -->
       <!-- ############################   none template!!!   ##############################  -->
       <xsl:template match="databinders[@dbtype='none']">
           private  org.apache.axiom.om.OMElement fromOM(
                 org.apache.axiom.om.OMElement param,
                 java.lang.Class type,
                 java.util.Map extraNamespaces){
              return param;
           }

           private  org.apache.axiom.om.OMElement  toOM(org.apache.axiom.om.OMElement param){
               return param;
           }

           private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.apache.axiom.om.OMElement param, boolean optimizeContent){
                org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                envelope.getBody().addChild(param);
                return envelope;
           }

           /**
           *  get the default envelope
           */
           private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
                return factory.getDefaultEnvelope();
           }

       </xsl:template>

     </xsl:stylesheet>