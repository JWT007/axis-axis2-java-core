<%@ page import="org.apache.axis2.Constants,
                 org.apache.axis2.description.AxisModule"%>
<%@ page import="org.apache.axis2.description.AxisOperation"%>
<%@ page import="org.apache.axis2.description.AxisService"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="java.util.Iterator"%>
<%
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
         *
         */

/**
 * Author : Deepal Jayasinghe
 * Date: May 26, 2005
 * Time: 7:14:26 PM
 */
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="include/adminheader.jsp" />

  <h1>Available Services</h1>
    <%  String prifix = request.getAttribute("frontendHostUrl") + "services/";
        String restprefix = request.getAttribute("frontendHostUrl") + "rest/";
    %>
     <%
         HashMap serviceMap = (HashMap)request.getSession().getAttribute(Constants.SERVICE_MAP);
         request.getSession().setAttribute(Constants.SERVICE_MAP,null);
         Hashtable errornessservice =(Hashtable)request.getSession().getAttribute(Constants.ERROR_SERVICE_MAP);
         boolean status = false;
         if (serviceMap!=null && !serviceMap.isEmpty()){
             Iterator operations;
             String serviceName ;
             Collection servicecol = serviceMap.values();
             for (Iterator iterator = servicecol.iterator(); iterator.hasNext();) {
                 AxisService axisService = (AxisService) iterator.next();
                 operations = axisService.getOperations();
                 serviceName = axisService.getName();
     %><h2><font color="blue"><a href="<%=prifix + axisService.getName()%>?wsdl"><%=serviceName%></a></font></h2>
     <font color="blue">Service EPR : <font color="black"><%=prifix + axisService.getName()%></font><br>
         <font color="blue">Service REST epr : <font color="black"><%=restprefix + axisService.getName()%></font>
     <h4>Service Description : <font color="black"><%=axisService.getServiceDescription()%></h4>
     <i><font color="blue">Service Status : <%=axisService.isActive()?"Active":"InActive"%></font></i><br>
           <%
                 Collection engagedModules = axisService.getEngagedModules();
                 String moduleName;
		boolean modules_present=false;
                 if(engagedModules.size() >0){
           %>
           <i>Engaged modules for the service</i>
                <%
                     for (Iterator iteratorm = engagedModules.iterator(); iteratorm.hasNext();) {
                         AxisModule axisOperation = (AxisModule) iteratorm.next();
                         moduleName = axisOperation.getName().getLocalPart();
			if (!modules_present) {
				modules_present=true;
%>
				<ul>
<%			}
                %><li><%=moduleName%></li>
                         <br>
                         <%
                     }
			if (modules_present) {%>
				</ul>
			<%}
                 }
                 if (operations.hasNext()) {
                %><br><i>Available operations</i><%
                 } else {
                %><i> There are no Operations specified</i><%
                 }
                %><ul><%
         operations = axisService.getOperations();
                 while (operations.hasNext()) {
                     AxisOperation axisOperation = (AxisOperation) operations.next();
                %><li><%=axisOperation.getName().getLocalPart()%></li>
<%--                 <br>Operation EPR : <%=prifix + axisService.getName().getLocalPart() + "/"+ axisOperation.getName().getLocalPart()%>--%>
                 <%
                     engagedModules = axisOperation.getEngagedModules();
                     if(engagedModules.size() >0){
                %>
                <br><i>Engaged Modules for the Operation</i><ul>
                <%
                         for (Iterator iterator2 = engagedModules.iterator(); iterator2.hasNext();) {
                             AxisModule moduleDecription = (AxisModule) iterator2.next();
                             moduleName = moduleDecription.getName().getLocalPart();
                %><li><%=moduleName%></li><br><%
                         }
                         %></ul><%
                     }

                 }
                %></ul>
           <%
                 status = true;
             }
         }
               if(errornessservice != null){
                   if(errornessservice.size() > 0){
                       request.getSession().setAttribute(Constants.IS_FAULTY,Constants.IS_FAULTY);
           %>
           <h3><font color="red">Faulty Services</font></h3>
                <%
                       Enumeration faultyservices = errornessservice.keys();
                       while (faultyservices.hasMoreElements()) {
                           String faultyserviceName = (String) faultyservices.nextElement();
                %><h3><font color="blue"><a href="listSingleService.jsp?serviceName=<%=faultyserviceName%>">
                    <%=faultyserviceName%></a></font></h3>
                    <%
                       }
                   }
                   status = true;
               }if(!status){
                    %> There seems to be no services listed! Try hitting refresh <%
               }
                    %>
<jsp:include page="include/adminfooter.jsp"></jsp:include>
