<%@page contentType="text/html;charset=UTF-8" import="java.util.*,java.io.*,eionet.cr.dao.DAOFactory"%>

<%@ include file="/pages/common/taglibs.jsp"%>	

<stripes:layout-render name="/pages/common/template.jsp" pageTitle="Harvest">
	<stripes:layout-component name="errors"/>
	<stripes:layout-component name="messages"/>
	<stripes:layout-component name="contents">
		<h1>Harvest</h1>
	    <stripes:form action="/source.action" focus="">
	    	<stripes:hidden name="harvest.harvestId"/>
	        <table>
	        	<tr>
	                <td>Harvest source:</td>
	                <td>
	                	<stripes:link href="/source.action" event="preView">
                            ${actionBean.harvestSource.name}
                            <stripes:param name="harvestSource.sourceId" value="${actionBean.harvest.harvestSourceId}"/>
                        </stripes:link>
	                </td>
	            </tr>
	            <tr>
	                <td>Type:</td>
	                <td>${actionBean.harvest.harvestType}</td>
	            </tr>
	            <tr>
	                <td>User:</td>
	                <td>${actionBean.harvest.user}</td>
	            </tr>
	            <tr>
	                <td>Status:</td>
	                <td>
	                	${actionBean.harvest.status}
	                </td>
	            </tr>
	            <tr>
	                <td>Started:</td>
	                <td><fmt:formatDate value="${actionBean.harvest.datetimeStarted}" pattern="dd MMM yy HH:mm:ss"/></td>
	            </tr>
	            <tr>
	                <td>Finished:</td>
	                <td><fmt:formatDate value="${actionBean.harvest.datetimeFinished}" pattern="dd MMM yy HH:mm:ss"/></td>
	            </tr>
	            <tr>
	                <td>Statements in total:</td>
	                <td>${actionBean.harvest.totalStatements}</td>
	            </tr>
	            <tr>
	                <td>Statements with literal objects:</td>
	                <td>${actionBean.harvest.litObjStatements}</td>
	            </tr>
	            <tr>
	                <td>Statements with resource objects:</td>
	                <td>${actionBean.harvest.totalResources}</td>
	            </tr>
	            <tr>
	                <td>Encoding schemes:</td>
	                <td>${actionBean.harvest.encodingSchemes}</td>
	            </tr>
	        </table>
	        <br/><br/>
	        <strong>Messages:</strong>
	        <table class="datatable">	        	
	        	<thead>
		        	<tr>
		        		<th scope="col">Type</th>
		        		<th scope="col">Message</th>
		        		<th scope="col">StackTrace</th>
		        	</tr>
	        	</thead>
	        	<tbody>
	        		<c:forEach items="${actionBean.harvestMessages}" var="msg" varStatus="loop">
	        			<tr>
	        				<td>${msg.type}</td>
	        				<td>${msg.message}</td>
	        				<td>${msg.stackTrace}</td>
	        			</tr>
	        		</c:forEach>
	        	</tbody>
	        </table>
	    </stripes:form>

	</stripes:layout-component>
</stripes:layout-render>
