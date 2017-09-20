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

import com.github.tkqubo.html2md.converters.DefaultMarkdownConverter;
import com.github.tkqubo.html2md.converters.MarkdownConverter;
import com.github.tkqubo.html2md.converters.MarkdownConverter$;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.eclipse.sw360.datahandler.thrift.SW360Exception;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseInfo;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseInfoParsingResult;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseNameWithText;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import com.github.tkqubo.html2md.Html2Markdown;
import com.github.tkqubo.html2md.converters.MarkdownConverter.*;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class TextGenerator extends OutputGenerator<String> {
    private static final Logger LOGGER = Logger.getLogger(TextGenerator.class);
    private static final String LICENSE_INFO_TEMPLATE_FILE = "textLicenseInfoFile.vm";

    private String testHTML;


    public TextGenerator() {
        super("txt", "License information as TEXT", false, "text/plain");
        testHTML = "";
        testHTML += "<em>Unordered</em>\n";
        testHTML += "<ul>\n";
        testHTML += "    <li>Item 1</li>\n";
        testHTML += "    <li>Item 2</li>\n";
        testHTML += "    <li>Item 3</li>\n";
        testHTML += "</ul>\n";
        testHTML += "<strong>Ordered</strong>\n";
        testHTML += "<ol>\n";
        testHTML += "    <li>Item 1</li>\n";
        testHTML += "    <li>Item 2</li>\n";
        testHTML += "    <li>Item 3</li>\n";
        testHTML += "</ol> \n";
        testHTML += "<p> This is a new paragraph </p> \n";

    }

    @Override
    public String generateOutputFile(Collection<LicenseInfoParsingResult> projectLicenseInfoResults, String projectName) throws SW360Exception {
        Collection<LicenseInfoParsingResult> renderedProjectLicenseInfoResults = renderLicenseInfoParsingResults(projectLicenseInfoResults);
        try {
            return renderTemplateWithDefaultValues(renderedProjectLicenseInfoResults, LICENSE_INFO_TEMPLATE_FILE);
        } catch (Exception e) {
            LOGGER.error("Could not generate text licenseinfo file", e);
            return "License information could not be generated.\nAn exception occurred: " + e.toString();
        }
    }

    @Override
    protected String renderLicenseText(String licenseText) {
        //SimpleHtmlRenderer renderer = new SimpleHtmlRenderer(licenseText);
        SimpleHtmlRenderer renderer = new SimpleHtmlRenderer(testHTML);
        return renderer.renderHtml();
    }

}

