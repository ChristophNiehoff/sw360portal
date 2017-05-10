<%@ page import="java.util.ArrayList" %>
<%--
  ~ Copyright (c) Bosch Software Innovations GmbH 2016.
  ~
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ which accompanies this distribution, and is available at
  ~ http://www.eclipse.org/legal/epl-v10.html
  --%>

<%@include file="/html/init.jsp" %>
<script src="<%=request.getContextPath()%>/js/external/jquery.validate.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/external/additional-methods.min.js" type="text/javascript"></script>

<jsp:useBean id="customMap" type="java.util.Map<java.lang.String, java.util.Set<java.lang.String>>"  scope="request"/>
<core_rt:forEach items="<%=customMap.entrySet()%>" var="mapEntry" varStatus="loop1">
    <core_rt:forEach items="${mapEntry.value}" var="setItem" varStatus="loop2">
        <tr id="mapEditTableRow${loop1.count}_${loop2.count}" >
            <td width="46%">
                <select class="toplabelledInput" id="<%=PortalConstants.CUSTOM_MAP_KEY%>${loop1.count}_${loop2.count}"
                        name="<portlet:namespace/><%=PortalConstants.CUSTOM_MAP_KEY%>${loop1.count}_${loop2.count}"
                        style="min-width: 162px; min-height: 28px;">

                    <core_rt:forEach items="${keys}" var="key">
                        <option value="${key}" class="textlabel stackedLabel" <core_rt:if
                                test='${mapEntry.key == key}'>selected="selected"</core_rt:if>> ${key} </option>
                    </core_rt:forEach>
                </select>
            </td>
            <td width="46%">
                <input id="<%=PortalConstants.CUSTOM_MAP_VALUE%>${loop1.count}_${loop2.count}" name="<portlet:namespace/><%=PortalConstants.CUSTOM_MAP_VALUE%>${loop1.count}_${loop2.count}" type="email" class="toplabelledInput" placeholder="${inputSubtitle}" title="${inputSubtitle}"
                       value="<sw360:out value='${setItem}'/>"/>
            </td>

            <td class="deletor">
                <img src="<%=request.getContextPath()%>/images/Trash.png"
                     onclick="deleteMapItem('mapEditTableRow${loop1.count}_${loop2.count}')"
                     alt="Delete">
            </td>
        </tr>
    </core_rt:forEach>
</core_rt:forEach>
<script>
    function deleteMapItem(rowId) {
        function deleteMapItemInternal() {
            $('#' + rowId).remove();
        };

        deleteConfirmed("Do you really want to remove this item?", deleteMapItemInternal);
    }

    function addRowToMapEditTable() {
        var randomRowId = "mapItemRow" + Date.now();
        var newRowAsString =
                '<tr id="' + randomRowId + '" >'+
                '<td width="46%">'+
                '<select class="toplabelledInput" id="<%=PortalConstants.CUSTOM_MAP_KEY%>'+randomRowId+'" name="<portlet:namespace/><%=PortalConstants.CUSTOM_MAP_KEY%>'+randomRowId+'" style="min-width: 162px; min-height: 28px;">' +
                <core_rt:forEach items="${keys}" var="key">
                '<option value="${key}" class="textlabel stackedLabel"> ${key} </option>'+
                </core_rt:forEach>
                '</select>'+
                '</td>'+
                '<td width="46%">'+
                '<input id="<%=PortalConstants.CUSTOM_MAP_VALUE%>'+randomRowId+'" name="<portlet:namespace/><%=PortalConstants.CUSTOM_MAP_VALUE%>'+randomRowId+'" type="email" class="toplabelledInput" placeholder="${inputSubtitle}" title="${inputSubtitle}" />'+
                '</td>'+
                '<td class="deletor">'+
                '<img src="<%=request.getContextPath()%>/images/Trash.png" onclick="deleteMapItem(\''+ randomRowId +'\')" alt="Delete">' +
                '</td>'+
                '</tr>';
        $('#mapEditTable tr:last').after(newRowAsString);
    }
</script>
