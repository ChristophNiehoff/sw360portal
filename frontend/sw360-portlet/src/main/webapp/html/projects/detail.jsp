<%--
  ~ Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
  ~
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ which accompanies this distribution, and is available at
  ~ http://www.eclipse.org/legal/epl-v10.html
  --%>
<%@include file="/html/init.jsp"%>



<portlet:defineObjects />
<liferay-theme:defineObjects />

<jsp:useBean id="usingProjects" type="java.util.Set<com.siemens.sw360.datahandler.thrift.projects.Project>" scope="request"/>
<jsp:useBean id="project" class="com.siemens.sw360.datahandler.thrift.projects.Project" scope="request" />
<jsp:useBean id="selectedTab" class="java.lang.String" scope="request" />
<jsp:useBean id="numberOfUncheckedVulnerabilities" type="java.lang.Integer" scope="request"/>
<jsp:useBean id="numberOfVulnerabilities" type="java.lang.Integer" scope="request"/>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/sw360.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/external/jquery-ui.css">
<script src="<%=request.getContextPath()%>/js/external/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/external/jquery.validate.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/external/additional-methods.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/external/jquery-ui.min.js"></script>
<script src="<%=request.getContextPath()%>/js/external/jquery.dataTables.js" type="text/javascript"></script>

<jsp:include page="/html/utils/includes/attachmentsDelete.jsp" />

<core_rt:set var="dontDisplayDeleteButton" value="true" scope="request"/>
<div id="header"></div>
<p class="pageHeader"><span class="pageHeaderBigSpan">Project: <sw360:ProjectName project="${project}"/></span>
    <span class="pull-right">
        <input type="button" onclick="editProject()" id="edit" value="Edit" class="addButton">
    </span>
</p>

<core_rt:set var="inProjectDetailsContext" value="true" scope="request"/>
<%@include file="/html/projects/detailOverview.jspf"%>

<script>
    var tabView;
    var Y = YUI().use(
            'aui-tabview',
            function(Y) {
                tabView = new Y.TabView(
                        {
                            srcNode: '#myTab',
                            stacked: true,
                            type: 'tab'
                        }
                ).render();
            }
    );

    function editProject() {
        window.location ='<portlet:renderURL ><portlet:param name="<%=PortalConstants.PROJECT_ID%>" value="${project.id}"/><portlet:param name="<%=PortalConstants.PAGENAME%>" value="<%=PortalConstants.PAGENAME_EDIT%>"/></portlet:renderURL>'
    }

</script>
