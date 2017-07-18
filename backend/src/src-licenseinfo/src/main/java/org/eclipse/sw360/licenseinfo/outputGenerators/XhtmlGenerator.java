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
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.sw360.datahandler.thrift.SW360Exception;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseInfo;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseInfoParsingResult;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseNameWithText;

import java.io.StringWriter;
import java.util.*;
import java.util.function.Function;

import static org.eclipse.sw360.licenseinfo.LicenseInfoHandler.*;

public class XhtmlGenerator extends OutputGenerator<String> {

    public static final String XHTML_TEMPLATE_FILE = "xhtmlFile.vm";
    Logger log = Logger.getLogger(XhtmlGenerator.class);

    public XhtmlGenerator() {
        super("html", "License information as XHTML", false, "application/xhtml+xml");
    }

    @Override
    public String generateOutputFile(Collection<LicenseInfoParsingResult> projectLicenseInfoResults, String projectName) throws SW360Exception {
        try {
            VelocityContext vc = getConfiguredVelocityContext();

            SortedMap<String, LicenseInfoParsingResult> sortedLicenseInfos = getSortedLicenseInfos(projectLicenseInfoResults);
            vc.put(LICENSE_INFO_RESULTS_CONTEXT_PROPERTY, sortedLicenseInfos);

            List<LicenseNameWithText> licenseNamesWithTexts = getSortedLicenseNameWithTexts(projectLicenseInfoResults);
            int id = 1;
            for(LicenseNameWithText licenseNameWithText : licenseNamesWithTexts){
                licenseNameWithText.setId(id++);
            }
            sortLicenseNamesWithinEachLicenseInfoById(projectLicenseInfoResults);

            vc.put(ALL_LICENSE_NAMES_WITH_TEXTS, licenseNamesWithTexts);

            SortedMap<String, Set<String>> acknowledgements = getSortedAcknowledgements(sortedLicenseInfos);
            vc.put(ACKNOWLEDGEMENTS_CONTEXT_PROPERTY, acknowledgements);

            StringWriter sw = new StringWriter();
            Velocity.mergeTemplate(XHTML_TEMPLATE_FILE, "utf-8", vc, sw);
            sw.close();
            return sw.toString();
        } catch (Exception e) {
            log.error("Could not generate xhtml file", e);
            return "License information could not be generated.\nAn exception occured: " + e.toString();
        }
    }

    private void sortLicenseNamesWithinEachLicenseInfoById(Collection<LicenseInfoParsingResult> licenseInfoResults) {
        licenseInfoResults
                .stream()
                .map(LicenseInfoParsingResult::getLicenseInfo)
                .filter(Objects::nonNull)
                .forEach((LicenseInfo li) -> li.setLicenseNamesWithTexts(sortSet(li.getLicenseNamesWithTexts(), LicenseNameWithText::getId)));
    }

    private static <U, K extends Comparable<K>> SortedSet<U> sortSet(Set<U> unsorted, Function<U, K> keyExtractor) {
        if (unsorted == null || unsorted.isEmpty()) {
            return Collections.emptySortedSet();
        }
        SortedSet<U> sorted = new TreeSet<>(Comparator.comparing(keyExtractor));
        sorted.addAll(unsorted);
        if (sorted.size() != unsorted.size()){
            // there were key collisions and some data was lost -> throw away the sorted set and sort by U's natural order
            sorted = new TreeSet<>();
            sorted.addAll(unsorted);
        }
        return sorted;
    }
}

