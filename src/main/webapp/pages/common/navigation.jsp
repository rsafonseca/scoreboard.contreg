<%@ page import="eionet.cr.web.security.CRUser" %>

<%@ include file="/pages/common/taglibs.jsp"%>

<div id="leftcolumn" class="localnav">
    <ul>
        <li><a href="documentation">Help and documentation</a></li>
        <li><a href="simpleSearch.action">Simple search</a></li>
        <c:if test='${not empty sessionScope.crUser && crfn:userHasPermission(pageContext.session, "/mergedeliveries", "v")}'>
        	<li><a href="deliverySearch.action">Merge deliveries</a></li>
        </c:if>
        <li><a href="customSearch.action">Custom search</a></li>
        <li><a href="typeSearch.action">Type search</a></li>
<!--
        <li><a href="spatialSearch.action">Spatial search</a></li>
        <li><a href="spatialSearch.action?googleEarthIntro">Google Earth link</a></li>
-->
        <li><a href="tagSearch.action">Tag search</a></li>
        <li><a href="recentUploads.action">Recent uploads</a></li>
        <li><a href="sparql">SPARQL endpoint</a></li>
        <li><a href="sources.action">Harvesting sources</a></li>
        <li><a href="harvestQueue.action">Harvest queue</a></li>
        <li><a href="home">User homes</a></li>
        <c:if test='${not empty sessionScope.crUser && crfn:userHasPermission(pageContext.session, "/sparqlclient", "v")}'>
            <li><a href="sparqlclient">Other SPARQL systems</a></li>
        </c:if>
        <c:if test="${not empty sessionScope.crUser && sessionScope.crUser.administrator}">
            <li><a href="admin" title="Administrative activities">Admin actions</a></li>
        </c:if>
        <c:if test='${not empty sessionScope.crUser && crfn:userHasPermission(pageContext.session, "/registrations", "u")}'>
            <li><a href="registerUrl.action">URL registration</a></li>
            <%-- We're sure that session has user, because we assume anonymous users --%>
            <%-- don't have permissions in "/registrations" ACL.                     --%>
            <li><a href="view.action?uri=${fn:escapeXml(sessionScope.crUser.homeUri)}">My home</a></li>
        </c:if>
    </ul>
</div>
