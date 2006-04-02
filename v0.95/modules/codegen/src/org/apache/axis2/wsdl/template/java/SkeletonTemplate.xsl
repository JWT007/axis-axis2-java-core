<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>
    <xsl:template match="/interface">
    /**
     * <xsl:value-of select="@name"/>.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: #axisVersion# #today#
     */
    package <xsl:value-of select="@package"/>;
    /**
     *  <xsl:value-of select="@name"/> java skeleton for the axisService
     */
    public class <xsl:value-of select="@name"></xsl:value-of> {
     <xsl:for-each select="method">
         <xsl:variable name="outputtype"><xsl:value-of select="output/param/@type"></xsl:value-of></xsl:variable>
		 <!-- regardless of the sync or async status, the generated method signature would be just a usual
	           java method -->
        /**
         * Auto generated method signature
         <!--  select only the body parameters  -->
          <xsl:for-each select="input/param[@location='body']">
            <xsl:if test="@type!=''">* @param <xsl:value-of select="@name"></xsl:value-of><xsl:text>
         </xsl:text></xsl:if></xsl:for-each>
         */
        public  <xsl:if test="$outputtype=''">void</xsl:if><xsl:if test="$outputtype!=''"><xsl:value-of select="$outputtype"/></xsl:if><xsl:text> </xsl:text><xsl:value-of select="@name"/>
                  (<xsl:for-each select="input/param[@location='body']">
            <xsl:if test="@type!=''"><xsl:if test="position()>1">,</xsl:if><xsl:value-of select="@type"/><xsl:text> </xsl:text><xsl:value-of select="@name"/></xsl:if>
                   </xsl:for-each> ) throws Exception {
                //Todo fill this with the necessary business logic
                <xsl:if test="$outputtype!=''">throw new  java.lang.UnsupportedOperationException();</xsl:if>
        }
     </xsl:for-each>
    }
    </xsl:template>
 </xsl:stylesheet>