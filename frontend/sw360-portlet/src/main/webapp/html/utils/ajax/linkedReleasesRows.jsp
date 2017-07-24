<%--
  ~ Copyright Siemens AG, 2017. Part of the SW360 Portal Project.
  ~
  ~ SPDX-License-Identifier: EPL-1.0
  ~
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ which accompanies this distribution, and is available at
  ~ http://www.eclipse.org/legal/epl-v10.html
--%>
<jsp:useBean id="releaseList" type="java.util.List<org.eclipse.sw360.datahandler.thrift.components.ReleaseLink>" scope="request"/>
<jsp:useBean id="parent_branch_id" class="java.lang.String" scope="request"/>
<jsp:useBean id="parentScopeGroupId" class="java.lang.String" scope="request"/>

<%@include file="/html/utils/ajax/linkedReleasesRows.jspf"%>
