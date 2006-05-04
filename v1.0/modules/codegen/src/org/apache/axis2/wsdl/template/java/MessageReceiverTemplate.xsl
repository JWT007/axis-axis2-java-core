<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>

    <!-- include the databind supporters  -->
    <xsl:include href="databindsupporter"/>
    <!-- import the other templates for databinding
           Note  -  these names would be handled by a special
           URI resolver during the xslt transformations
       -->
       <xsl:include href="externalTemplate"/>


    <!--Template for in out message receiver -->
    <xsl:template match="/interface[@basereceiver='org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver']">

        <xsl:variable name="skeletonname"><xsl:value-of select="@skeletonInterfaceName"/></xsl:variable>
        <xsl:variable name="dbsupportpackage"><xsl:value-of select="@dbsupportpackage"/></xsl:variable>

        /**
         * <xsl:value-of select="@name"/>.java
         *
         * This file was auto-generated from WSDL
         * by the Apache Axis2 version: #axisVersion# #today#
         */
        package <xsl:value-of select="@package"/>;

        /**
         *  <xsl:value-of select="@name"/> message receiver
         */

        public class <xsl:value-of select="@name"/> extends <xsl:value-of select="@basereceiver"/>{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        //Inject the Message Context if it is asked for
        org.apache.axis2.engine.DependencyManager.configureBusinessLogicProvider(obj, msgContext.getOperationContext());

        <xsl:value-of select="$skeletonname"/> skel = (<xsl:value-of select="$skeletonname"/>)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        String methodName;
        if(op.getName() != null &amp; (methodName = op.getName().getLocalPart()) != null){

        <xsl:for-each select="method">

            <xsl:variable name="returntype"><xsl:value-of select="output/param/@type"/></xsl:variable>
            <xsl:variable name="returnvariable"><xsl:value-of select="output/param/@name"/></xsl:variable>
            <xsl:variable name="namespace"><xsl:value-of select="@namespace"/></xsl:variable>

            <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
            <xsl:variable name="style"><xsl:value-of select="@style"/></xsl:variable>


            if("<xsl:value-of select="@name"/>".equals(methodName)){


            <xsl:if test="$returntype!=''">
                <xsl:value-of select="$returntype"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$returnvariable"/> = null;
            </xsl:if>


            <xsl:choose>
                <!-- We really don't need to make a difference between these-->
                <xsl:when test="$style='document' or $style='rpc'">
                    //doc style
                    <xsl:if test="$returntype!=''"><xsl:value-of select="$returnvariable"/> =</xsl:if>
                    <xsl:variable name="paramCount"><xsl:value-of select="count(input/param[@location='body'])"/></xsl:variable>
                    <xsl:choose>
                        <xsl:when test="$paramCount &gt; 0">skel.<xsl:value-of select="@name"/>(
                            <xsl:for-each select="input/param[@location='body']">
                                <xsl:if test="@type!=''">(<xsl:value-of select="@type"/>)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    <xsl:value-of select="@type"/>.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()))<xsl:if test="position() &gt; 1">,</xsl:if></xsl:if>
                            </xsl:for-each>);
                        </xsl:when>
                        <!--No input parameters-->
                        <xsl:otherwise>skel.<xsl:value-of select="@name"/>();</xsl:otherwise>
                    </xsl:choose>


                    <xsl:choose>
                      <xsl:when test="$returntype!=''">
                        envelope = toEnvelope(getSOAPFactory(msgContext), <xsl:value-of select="$returnvariable"/>, false);
                      </xsl:when>
                      <xsl:otherwise>
                        envelope = getSOAPFactory(msgContext).getDefaultEnvelope();
                      </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>

                <xsl:otherwise>
                    //Unknown style!! No code is generated
                    throw new UnsupportedOperationException("Unknown Style");
                </xsl:otherwise>
            </xsl:choose>

            }
        </xsl:for-each>

        newMsgContext.setEnvelope(envelope);
        }
        <xsl:for-each select="fault-list/fault">
        <xsl:if test="position()=1">}</xsl:if>catch (<xsl:value-of select="@name"/> e) {
                org.apache.axis2.AxisFault f =
                    new org.apache.axis2.AxisFault("<xsl:value-of select="@shortName"/>");
                f.setDetail(toOM(e.getFaultMessage(),false));
                throw f;
            }
        </xsl:for-each>
        <!-- put the extra bracket-->
        <xsl:if test="count(fault-list/fault)=0">}</xsl:if>
            catch (Exception e) {
              throw org.apache.axis2.AxisFault.makeFault(e);
            }
        }
         <!-- Call templates recursively-->
        //<xsl:apply-templates/>

          /**
          *  A utility method that copies the namepaces from the SOAPEnvelope
          */
          private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
               java.util.Map returnMap = new java.util.HashMap();
               java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
               while (namespaceIterator.hasNext()) {
                   org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
                   returnMap.put(ns.getPrefix(),ns.getName());
               }
              return returnMap;
              }


        }//end of class
    </xsl:template>
   <!-- end of template for in-out message receiver -->

     <!-- start of in-only -->
    <xsl:template match="interface[@basereceiver='org.apache.axis2.receivers.AbstractInMessageReceiver']">
          <xsl:variable name="skeletonname"><xsl:value-of select="@skeletonname"/></xsl:variable>
        <xsl:variable name="dbsupportpackage"><xsl:value-of select="@dbsupportpackage"/></xsl:variable>

        /**
         * <xsl:value-of select="@name"/>.java
         *
         * This file was auto-generated from WSDL
         * by the Apache Axis2 version: #axisVersion# #today#
         */
        package <xsl:value-of select="@package"/>;

        /**
         *  <xsl:value-of select="@name"/> message receiver
         */

        public class <xsl:value-of select="@name"/> extends <xsl:value-of select="@basereceiver"/>{

        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext inMessage) throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(inMessage);

        //Inject the Message Context if it is asked for
        org.apache.axis2.engine.DependencyManager.configureBusinessLogicProvider(obj, inMessage.getOperationContext());

        <xsl:value-of select="$skeletonname"/> skel = (<xsl:value-of select="$skeletonname"/>)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = inMessage.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        String methodName;
        if(op.getName() != null &amp; (methodName = op.getName().getLocalPart()) != null){

        <xsl:for-each select="method">

            <xsl:variable name="namespace"><xsl:value-of select="@namespace"/></xsl:variable>
            <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
            <xsl:variable name="style"><xsl:value-of select="@style"/></xsl:variable>


            if("<xsl:value-of select="@name"/>".equals(methodName)){

            <xsl:choose>
                <xsl:when test="$style='rpc'">

                    //rpc style  -- this needs to be filled

                </xsl:when>
                <xsl:when test="$style='document'">
                    //doc style
                    <xsl:variable name="paramCount"><xsl:value-of select="count(input/param[@location='body'])"/></xsl:variable>
                    <xsl:choose>
                        <xsl:when test="$paramCount &gt; 0"> skel.<xsl:value-of select="@name"/>(
                            <xsl:for-each select="input/param[@location='body']">
                                <xsl:if test="@type!=''">(<xsl:value-of select="@type"/>)fromOM(
                                    inMessage.getEnvelope().getBody().getFirstElement(),
                                    <xsl:value-of select="@type"/>.class,
                                    getEnvelopeNamespaces(inMessage.getEnvelope())
                                    )<xsl:if test="position() &gt; 1">,</xsl:if></xsl:if>
                            </xsl:for-each>);
                        </xsl:when>
                        <xsl:otherwise>skel.<xsl:value-of select="@name"/>();</xsl:otherwise>
                    </xsl:choose>
                </xsl:when>

                <xsl:otherwise>
                    //Unknown style!! No code is generated
                    throw new UnsupportedOperationException("Unknown Style");
                </xsl:otherwise>
            </xsl:choose>

            }
        </xsl:for-each>

        }
        } catch (Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
        }


         <!-- Call templates recursively-->
        //<xsl:apply-templates/>



        /**
          *  A utility method that copies the namepaces from the SOAPEnvelope
          */
          private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
               java.util.Map returnMap = new java.util.HashMap();
               java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
               while (namespaceIterator.hasNext()) {
                   org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
                   returnMap.put(ns.getPrefix(),ns.getName());
               }
              return returnMap;
              }


        }//end of class

    </xsl:template>

</xsl:stylesheet>
