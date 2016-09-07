/*
 * Copyright (c) Bosch Software Innovations GmbH 2016.
 * Part of the SW360 Portal Project.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License Version 2.0 as published by the
 * Free Software Foundation with classpath exception.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License version 2.0 for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (please see the COPYING file); if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package com.siemens.sw360.portal.portlets.vulnerabilities;

import com.siemens.sw360.datahandler.thrift.users.User;
import com.siemens.sw360.datahandler.thrift.vulnerabilities.Vulnerability;
import com.siemens.sw360.datahandler.thrift.vulnerabilities.VulnerabilityService;
import com.siemens.sw360.portal.portlets.Sw360Portlet;
import com.siemens.sw360.portal.users.UserCacheHolder;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.siemens.sw360.datahandler.common.SW360Utils.printName;
import static com.siemens.sw360.portal.common.PortalConstants.*;

/**
 *Vulnerabilities portlet implementation
 *
 * @author birgit.heydenreich@tngtech.com
 */
public class VulnerabilitiesPortlet extends Sw360Portlet{

    private static final Logger log = Logger.getLogger(VulnerabilitiesPortlet.class);

    //Helper methods
    private void addVulnerabilityBreadcrumb(RenderRequest request, RenderResponse response, Vulnerability vulnerability) {
        PortletURL url = response.createRenderURL();
        url.setParameter(PAGENAME, PAGENAME_DETAIL);
        url.setParameter(VULNERABILITY_ID, vulnerability.getExternalId());

        addBreadcrumbEntry(request, printName(vulnerability), url);
    }

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        String pageName = request.getParameter(PAGENAME);
        if (PAGENAME_DETAIL.equals(pageName)) {
            prepareDetailView(request, response);
            include("/html/vulnerabilities/detail.jsp", request, response);
        } else {
            prepareStandardView(request);
            super.doView(request, response);
        }
    }

    private void prepareStandardView(RenderRequest request) throws IOException {

        List<Vulnerability> allVulnerabilities;

        try {
            final User user = UserCacheHolder.getUserFromRequest(request);
            VulnerabilityService.Iface vulnerabilityClient = thriftClients.makeVulnerabilityClient();
            allVulnerabilities = vulnerabilityClient.getVulnerabilities(user);
            shortenTimeStampsToDates(allVulnerabilities);

        } catch (TException e) {
            log.error("Could not search vulnerabilities in backend ", e);
            allVulnerabilities = Collections.emptyList();
        }
        request.setAttribute(VULNERABILITY_LIST, allVulnerabilities);
    }

    private void shortenTimeStampsToDates(List<Vulnerability> vulnerabilities){
        vulnerabilities.stream().forEach(v-> {
            if (v.getPublishDate().matches("\\d\\d\\d\\d-\\d\\d-\\d\\d.*")) {
                v.setPublishDate(v.getPublishDate().substring(0,10));
            }
            if (v.getLastExternalUpdate().matches("\\d\\d\\d\\d-\\d\\d-\\d\\d.*")) {
                v.setLastExternalUpdate(v.getLastExternalUpdate().substring(0,10));
            }
            if (v.isSetCvssTime() && v.getCvssTime().matches("\\d\\d\\d\\d-\\d\\d-\\d\\d.*")) {
                v.setCvssTime(v.getCvssTime().substring(0,10));
            }
        });
    }

    private void prepareDetailView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        User user = UserCacheHolder.getUserFromRequest(request);
        String externalId = request.getParameter(VULNERABILITY_ID);
        if (externalId != null) {
            try {
                VulnerabilityService.Iface client = thriftClients.makeVulnerabilityClient();
                Vulnerability vulnerability = client.getVulnerabilityByExternalId(externalId, user);
                request.setAttribute(VULNERABILITY, vulnerability);
                request.setAttribute(DOCUMENT_ID, externalId);

                addVulnerabilityBreadcrumb(request, response, vulnerability);

            } catch (TException e) {
                log.error("Error fetching vulnerability from backend!", e);
            }
        }
    }
}
