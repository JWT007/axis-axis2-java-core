<%@ page import="org.apache.axis2.Constants" %>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.apache.axis2.context.ServiceContext" %>
<%@ page import="org.apache.axis2.context.ServiceGroupContext" %>
<%@ page import="org.apache.axis2.description.AxisService" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="include/adminheader.jsp"/>
<h1>Running Context Hierarchy</h1>
<%
    ConfigurationContext configContext = (ConfigurationContext) request.getSession().getAttribute(Constants.CONFIG_CONTEXT);
    Hashtable serviceGroupContextsMap = configContext.getServiceGroupContexts();
    String type = request.getParameter("TYPE");
    String sgID = request.getParameter("PID");
    String ID = request.getParameter("ID");
    ServiceGroupContext sgContext = (ServiceGroupContext) serviceGroupContextsMap.get(sgID);
    AxisService service = sgContext.getDescription().getService(ID);
    ServiceContext serviceContext = sgContext.getServiceContext(service);
    if (sgID != null && serviceContext != null) {
        if (type != null) {
            if ("VIEW".equals(type)) {
                Map perMap = serviceContext.getProperties();
                if (perMap.size() > 0) {
%>
<h4>Persistance Properties</h4><ul>
    <%
        Iterator itr = perMap.keySet().iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            Object property = perMap.get(key);
    %>
    <li><%=key%> : <%=property.toString()%></li>
    <%
        }
    %></ul>
<%
} else {
%>
<h4>No persistance properties found in the context</h4>
<%
        }
    }
} else {
%> <h4>No Service Context Found</h4><%
        }
    }
%>
<jsp:include page="include/adminfooter.jsp"/>