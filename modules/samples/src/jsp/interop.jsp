<%@ page import="interop.util.Constants,
                 interop.doclit.InteropRequestHandler"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="interopBean" scope="request" class="interop.util.InteropTO" />
<jsp:setProperty name="interopBean" property="*" />


<html>
<%
/*
 * Copyright 2002,2004 The Apache Software Foundation.
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
 %>
<head>
    <title>Axis 2 -Interop Test Page</title>
    <link href="css/axis-style.css" rel="stylesheet" type="text/css">
    <script>
       function displayStringRow(){
              document.getElementById('StringRow').style.display = '';
        }
        function hideStringRow(){
              document.getElementById('StringRow').style.display = 'none';
        }

        function displayStringArrayRow(){
              document.getElementById('StringArrayRow').style.display = '';
        }
        function hideStringArrayRow(){
              document.getElementById('StringArrayRow').style.display = 'none';
        }

        function displayStructRow(){
              document.getElementById('StringStruct').style.display = '';
        }
        function hideStructRow(){
              document.getElementById('StringStruct').style.display = 'none';
        }



    </script>
</head>
	<body>
         <jsp:include page="include/header.inc"></jsp:include>

       	<h3>Welcome to Axis interop testing.</h3><br/>
        You can use this web page to do a doclit standard interop test. For more detail and test server information please visit the
        <a href="http://www.whitemesa.com/r3/interop3.html">whitemesa interop test page.</a>
    <%
      if (request.getParameter("submit") != null) {
          int type = interopBean.getType();
          switch(type){
              case Constants.InteropConstants.ECHO_STRING_SERVICE: {
                   interopBean.setStringValue((String)request.getParameter("StringValue"));
                  break;
              }
              case Constants.InteropConstants.ECHO_STRING_ARRAY_SERVICE: {
                  String [] values = new String[10];
                  for(int i =0 ; i< values.length ; i++){
                      String s = (String)request.getParameter("arryValue" + (i+1));
                      values[i] =s==null?"":s;
                  }
                  interopBean.setArraValue(values);
                  break;
              }
              case Constants.InteropConstants.ECHO_STRUCT_SERVICE : {
                  interopBean.setStructString((String)request.getParameter("structValue1"));
                  interopBean.setStructint(Integer.parseInt(request.getParameter("structValue2")));
                  interopBean.setStructfloat(Float.parseFloat(request.getParameter("structValue3")));
                  break;
              }
          }
          try {
              new InteropRequestHandler().handleInteropRequest(interopBean);
          } catch (Exception e) {
              %> <font color="red">Exception occurred during the test <br/> <%=e.getMessage()%></font> <%

          }
      }
    %>

        <form method="post" name="InteropTesting" action="interop.jsp">
         <table border="0" width="100%" cellspacing="1" cellpadding="1">
          <tr>
            <td vAlign="top" align="right" class="formText">Service Endpoint URL :</td>
            <td>
                <input name="URL" maxlength="99" size="38" type="text" id="Name" class="textBox" value="<%= (interopBean.getURL()==null)?"":interopBean.getURL()%>">&nbsp;<small><font color="red">*</font></small>
            </td>
          </tr>
          <tr>
            <td vAlign="top" align="right" class="formText">SOAP Action :</td>
            <td>
                <input name="SOAPAction" maxlength="99" size="38" type="text" id="Name" class="textBox" value="<%= (interopBean.getSOAPAction()==null)?"":interopBean.getSOAPAction()%>">&nbsp;<small><font color="red">*</font></small>
            </td>
          </tr>
          <td></td>
          <tr>
          <td></td>
          <td>
              <input type="radio"  name="type" value="<%=Constants.InteropConstants.ECHO_STRING_SERVICE%>"  onclick="displayStringRow();hideStringArrayRow();hideStructRow();"  checked>Echo String</input>

          </td>
          </tr>
           <tr>
           <td></td>
            <td>
              <input type="radio" name="type" onclick="hideStringRow(); displayStringArrayRow();hideStructRow();"  value="<%=Constants.InteropConstants.ECHO_STRING_ARRAY_SERVICE%>">Echo String Array</input>
          </td>
          </tr>
           <tr><td></td>
           <td>
              <input type="radio" name="type" onclick="hideStringRow();hideStringArrayRow();displayStructRow();" value="<%=Constants.InteropConstants.ECHO_STRUCT_SERVICE%>">Echo Struct</input>
          </td>
          </tr>
          </table>
<%--           <tr>--%>
<%--          <td>--%>
          <table border="0" width="100%" cellspacing="1" cellpadding="1">
             <tr style="display:''" id="StringRow">
               <td vAlign="top" align="right" class="formText">Enter String value :</td>
               <td>
                    <input name="StringValue" maxlength="99" size="38" type="text" class="textBox" value="<%= (interopBean.getStringValue()==null)?"":interopBean.getStringValue()%>">&nbsp;<small><font color="red">*</font></small>
               </td>
              </tr>
              <tr style="display:none" id="StringArrayRow">
           <td vAlign="top" align="right" class="formText">Enter String values for array :</td>
           <td>
            <table border="0" width="100%" cellspacing="1" cellpadding="1">
                <tr>
                   <td><input name="arryValue1" maxlength="99" size="38" type="text" class="textBox"></td>
                </tr>
                <tr>
                 <td><input name="arryValue2" maxlength="99" size="38" type="text" class="textBox"></td>
                </tr>
                <tr>
                    <td><input name="arryValue3" maxlength="99" size="38" type="text" class="textBox"></td>
                </tr>
                <tr>
                    <td><input name="arryValue4" maxlength="99" size="38" type="text" class="textBox"></td>
                </tr>
                <tr>
                    <td>  <input name="arryValue5" maxlength="99" size="38" type="text" class="textBox"></td>
                </tr>
                <tr>
                    <td> <input name="arryValue6" maxlength="99" size="38" type="text" class="textBox"></td>

                </tr>
                <tr>
                <td><input name="arryValue7" maxlength="99" size="38" type="text" class="textBox"></td>
                </tr>
                <tr>
                <td><input name="arryValue8" maxlength="99" size="38" type="text" class="textBox"></td>
                </tr><tr>
                <td><input name="arryValue9" maxlength="99" size="38" type="text" class="textBox"></td>
                </tr><tr>
                <td><input name="arryValue10" maxlength="99" size="38" type="text" class="textBox"></td>
                </tr>
            </table>
            </td>
          </tr>
          <tr style="display:none" id="StringStruct">
          <td></td>
          <td>
          <table border="0" width="100%" cellspacing="1" cellpadding="1">
          <tr><td vAlign="top" align="right" class="formText">Enter values for the SOAP Structure</td>
            <td>
              &nbsp;
            </td></tr>
          <tr>
            <td vAlign="top" align="right" class="formText">String Value</td>
            <td>
              <input name="structValue1" maxlength="99" size="38" type="text" class="textBox">&nbsp;
            </td>
          </tr>
            <tr>
            <td vAlign="top" align="right" class="formText">Integer Value</td>
            <td>
              <input name="structValue2" maxlength="99" size="38" type="text" class="textBox">&nbsp;
            </td>
          </tr>
            <tr>
            <td vAlign="top" align="right" class="formText">Float Value</td>
            <td>
              <input name="structValue3" maxlength="99" size="38" type="text" class="textBox">&nbsp;
            </td>
            </td>
          </tr>
          </table>
          </tr>

          </table>
          <table  border="0" width="100%" cellspacing="1" cellpadding="1">
          <tr>
           <td>&nbsp;</td>
           <td>&nbsp;</td>
           </tr>
                    <tr>
                <td width="40%" ></td>
                <td>
                     <input name="submit" type="submit" value=" Send " class="buttons" >
                     <input name="reset" type="reset" value=" Clear " class="buttons" >
                </td>
           </tr>
           <tr>
           <td>&nbsp;</td>
           <td>&nbsp;</td>
           </tr>
          </table>
          <table border="0" width="100%" cellspacing="1" cellpadding="1">
          <tr><td>
          <table border="0" width="100%" cellspacing="1" cellpadding="1">
             <tr>
              <td><b>Requst SOAP Message</b></td>
             </tr>
             <tr>
             <td >
              <textarea cols="55" disabled="true"  name="SOAPRequest" rows="20"><%= (interopBean.getRequest()==null) ? "" : interopBean.getRequest() %>
</textarea>
             </td>
             </tr>
          </table></td>
          <td>
            <table border="0" width="100%" cellspacing="1" cellpadding="1">
             <tr>
              <td><b>Response SOAP Message</b></td>
             </tr>
             <tr>
             <td ><textarea cols="55"  disabled="true" name="SOAPResponse" rows="20" ><%= (interopBean.getResponse()==null) ? "" : interopBean.getResponse() %>
</textarea></td>
             </tr>
          </table>
          </td>
          </tr>
        </form>

        <jsp:include page="include/link-footer.inc"></jsp:include>
        <jsp:include page="include/footer.inc"></jsp:include>
	</body>
</html>