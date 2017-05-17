/*
 * Copyright Bosch Software Innovations GmbH, 2016-2017.
 * Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.licenseinfo.parsers;

import org.eclipse.sw360.datahandler.common.SW360Constants;
import org.eclipse.sw360.datahandler.common.UncheckedSW360Exception;
import org.eclipse.sw360.datahandler.couchdb.AttachmentConnector;
import org.eclipse.sw360.datahandler.thrift.Visibility;
import org.eclipse.sw360.datahandler.thrift.attachments.Attachment;
import org.eclipse.sw360.datahandler.thrift.attachments.AttachmentContent;
import org.eclipse.sw360.datahandler.thrift.attachments.AttachmentType;
import org.eclipse.sw360.datahandler.thrift.components.Release;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseInfo;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseInfoParsingResult;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseNameWithText;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.spdx.rdfparser.model.SpdxDocument;
import org.spdx.rdfparser.SPDXDocumentFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import static org.eclipse.sw360.licenseinfo.TestHelper.*;
import static org.eclipse.sw360.licenseinfo.parsers.SPDXParser.FILETYPE_SPDX_INTERNAL;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author: maximilian.huber@tngtech.com
 */
@RunWith(DataProviderRunner.class)
public class SPDXParserTest {

    private User dummyUser = new User().setEmail("dummy@some.domain");

    private SPDXParser parser;

    private AttachmentContentStore attachmentContentStore;

    @Mock
    private AttachmentConnector connector;

    public static final String spdxExampleFile = "SPDXRdfExample-v2.0.rdf";
    public static final String spdx11ExampleFile = "SPDXRdfExample-v1.1.rdf";
    public static final String spdx12ExampleFile = "SPDXRdfExample-v1.2.rdf";

    @DataProvider
    public static Object[][] dataProviderAdd() {
        // @formatter:off
        return new Object[][] {
                { spdxExampleFile,
                        Arrays.asList("Apache-2.0", "LGPL-2.0", "LicenseRef-1", "GPL-2.0", "CyberNeko License", "LicenseRef-2"),
                        4,
                        "Copyright 2008-2010 John Smith" },
                { spdx11ExampleFile,
                        Arrays.asList("LicenseRef-4", "LicenseRef-1", "Apache-2.0", "LicenseRef-2", "Apache-1.0", "MPL-1.1", "CyberNeko License"),
                        2,
                        "Hewlett-Packard Development Company, LP" },
                { spdx12ExampleFile,
                        Arrays.asList("LicenseRef-4", "LicenseRef-1", "Apache-2.0", "LicenseRef-2", "Apache-1.0", "MPL-1.1", "CyberNeko License"),
                        3,
                        "Hewlett-Packard Development Company, LP" },
        };
        // @formatter:on
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        attachmentContentStore = new AttachmentContentStore(connector);

        parser = new SPDXParser(connector, attachmentContentStore.getAttachmentContentProvider());

        attachmentContentStore.put(spdxExampleFile);
        attachmentContentStore.put(spdx11ExampleFile);
        attachmentContentStore.put(spdx12ExampleFile);
    }

    private void assertIsResultOfExample(LicenseInfo result, String exampleFile, List<String> expectedLicenses, int numberOfCoyprights, String exampleCopyright){
        assertLicenseInfo(result);

        assertThat(result.getFilenames().size(), is(1));
        assertThat(result.getFilenames().get(0), is(exampleFile));

        assertThat(result.getLicenseNamesWithTextsSize(), is(expectedLicenses.size()));
        expectedLicenses.stream()
                .forEach(licenseId -> assertThat(result.getLicenseNamesWithTexts().stream()
                        .map(LicenseNameWithText::getLicenseName)
                        .anyMatch(licenseId::equals), is(true)));
        assertThat(result.getLicenseNamesWithTexts().stream()
                        .map(lt -> lt.getLicenseText())
                        .anyMatch(t -> t.contains("The CyberNeko Software License, Version 1.0")),
                is(true));

        assertThat(result.getCopyrightsSize(), is(numberOfCoyprights));
        assertThat(result.getCopyrights().stream()
                        .anyMatch(c -> c.contains(exampleCopyright)),
                is(true));
    }

    @Test
    @UseDataProvider("dataProviderAdd")
    public void testAddSPDXContentToCLI(String exampleFile, List<String> expectedLicenses, int numberOfCoyprights, String exampleCopyright) throws Exception {
        AttachmentContent attachmentContent = new AttachmentContent()
                .setFilename(exampleFile);

        InputStream input = makeAttachmentContentStream(exampleFile);
        SpdxDocument spdxDocument = SPDXDocumentFactory.createSpdxDocument(input,
                parser.getUriOfAttachment(attachmentContentStore.get(exampleFile)),
                FILETYPE_SPDX_INTERNAL);

        LicenseInfoParsingResult result = SPDXParserTools.getLicenseInfoFromSpdx(attachmentContent, spdxDocument);
        assertIsResultOfExample(result.getLicenseInfo(), exampleFile, expectedLicenses, numberOfCoyprights, exampleCopyright);
    }

    @Test
    @UseDataProvider("dataProviderAdd")
    public void testGetLicenseInfo(String exampleFile, List<String> expectedLicenses, int numberOfCoyprights, String exampleCopyright) throws Exception {

        Attachment attachment = makeAttachment(exampleFile,
                Arrays.stream(AttachmentType.values())
                        .filter(at -> SW360Constants.LICENSE_INFO_ATTACHMENT_TYPES.contains(at))
                        .findAny()
                        .get());

        LicenseInfoParsingResult result = parser.getLicenseInfos(attachment, dummyUser,
                                            new Project()
                                                    .setVisbility(Visibility.ME_AND_MODERATORS)
                                                    .setCreatedBy(dummyUser.getEmail())
                                                    .setAttachments(Collections.singleton(new Attachment().setAttachmentContentId(attachment.getAttachmentContentId()))))
                .stream()
                .findFirst()
                .orElseThrow(()->new RuntimeException("Parser returned empty LisenceInfoParsingResult list"));

        assertLicenseInfoParsingResult(result);
        assertIsResultOfExample(result.getLicenseInfo(), exampleFile, expectedLicenses, numberOfCoyprights, exampleCopyright);
    }
}
