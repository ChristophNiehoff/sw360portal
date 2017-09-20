/*
 * Copyright Bosch Software Innovations GmbH, 2016.
 * With modifications by Siemens AG, 2017.
 * Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.sw360.licenseinfo.outputGenerators;

import org.apache.log4j.Logger;
import org.eclipse.sw360.datahandler.thrift.SW360Exception;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseInfoParsingResult;

import java.util.Collection;

public class XhtmlGenerator extends OutputGenerator<String> {
    private static final Logger LOGGER = Logger.getLogger(XhtmlGenerator.class);
    private static final String XHTML_TEMPLATE_FILE = "xhtmlLicenseInfoFile.vm";

    public XhtmlGenerator() {
        super("html", "License information as XHTML", false, "application/xhtml+xml");
    }

    @Override
    public String generateOutputFile(Collection<LicenseInfoParsingResult> projectLicenseInfoResults, String projectName) throws SW360Exception {
        Collection<LicenseInfoParsingResult> renderedProjectLicenseInfoResults = renderLicenseInfoParsingResults(projectLicenseInfoResults);
        try {
            return renderTemplateWithDefaultValues(renderedProjectLicenseInfoResults, XHTML_TEMPLATE_FILE);
        } catch (Exception e) {
            LOGGER.error("Could not generate xhtml license info file", e);
            return "License information could not be generated.\nAn exception occured: " + e.toString();
        }
    }

    @Override
    protected String renderLicenseText(String licenseText) {
        return licenseText + "\n\n HtmlGenerator \n\n";
    }
}

