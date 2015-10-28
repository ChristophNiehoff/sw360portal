<%--
  ~ Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
  ~
  ~ This program is free software; you can redistribute it and/or modify it under
  ~ the terms of the GNU General Public License Version 2.0 as published by the
  ~ Free Software Foundation with classpath exception.
  ~
  ~ This program is distributed in the hope that it will be useful, but WITHOUT
  ~ ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  ~ FOR A PARTICULAR PURPOSE. See the GNU General Public License version 2.0 for
  ~ more details.
  ~
  ~ You should have received a copy of the GNU General Public License along with
  ~ this program (please see the COPYING file); if not, write to the Free
  ~ Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
  ~ 02110-1301, USA.
  --%>

<%@ page import="com.liferay.portlet.PortletURLFactoryUtil" %>
<%@include file="/html/init.jsp" %>

<%@ taglib prefix="sw360" uri="/WEB-INF/customTags.tld" %>

<portlet:defineObjects/>
<liferay-theme:defineObjects/>

<%@ page import="com.siemens.sw360.datahandler.thrift.projects.Project" %>
<%@ page import="com.siemens.sw360.datahandler.thrift.components.ReleaseLink" %>

<jsp:useBean id="releaseList" type="java.util.List<com.siemens.sw360.datahandler.thrift.components.ReleaseLink>"  scope="request"/>
<core_rt:forEach items="${releaseList}" var="releaseLink" varStatus="loop">
    <tr id="releaseLinkRow${loop.count}" >
        <td width="23%">
            <label class="textlabel stackedLabel" for="releaseVendor">Vendor name</label>
            <input id="releaseVendor" type="text" class="toplabelledInput" placeholder="Enter vendor"
                   value="${releaseLink.vendor}" readonly/>
        </td>
        <td width="23%">
            <input type="hidden" value="${releaseLink.id}" name="<portlet:namespace/><%=Project._Fields.RELEASE_ID_TO_USAGE%><%=ReleaseLink._Fields.ID%>">
            <label class="textlabel stackedLabel" for="releaseName">Release name</label>
            <input id="releaseName" type="text" class="toplabelledInput" placeholder="Enter release"
                   value="${releaseLink.name}" readonly/>
        </td>
        <td width="23%">
            <label class="textlabel stackedLabel" for="releaseVersion">Release version</label>
            <input id="releaseVersion" type="text" class="toplabelledInput" placeholder="Enter version"
                   value="${releaseLink.version}" readonly/>
        </td>
        <td width="23%">
            <label class="textlabel stackedLabel mandatory" for="releaseRelation">Release relation</label>
            <input id="releaseRelation" type="text" class="toplabelledInput" placeholder="Enter release usage"
                   value="${releaseLink.comment}" name="<portlet:namespace/><%=Project._Fields.RELEASE_ID_TO_USAGE%><%=ReleaseLink._Fields.COMMENT%>"/>
        </td>

        <td class="deletor">
            <img src="<%=request.getContextPath()%>/images/Trash.png" onclick="deleteReleaseLink('releaseLinkRow${loop.count}')" alt="Delete">
        </td>

    </tr>
</core_rt:forEach>
