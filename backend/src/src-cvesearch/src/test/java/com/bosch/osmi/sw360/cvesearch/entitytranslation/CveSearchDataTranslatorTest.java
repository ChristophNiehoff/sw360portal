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
package com.bosch.osmi.sw360.cvesearch.entitytranslation;

import com.bosch.osmi.sw360.cvesearch.datasource.CveSearchApiImpl;
import com.bosch.osmi.sw360.cvesearch.datasource.CveSearchData;
import com.siemens.sw360.datahandler.common.CommonUtils;
import com.siemens.sw360.datahandler.thrift.vulnerabilities.CVEReference;
import com.siemens.sw360.datahandler.thrift.vulnerabilities.Vulnerability;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CveSearchDataTranslatorTest {

    String HOST = "https://cve.circl.lu";

    CveSearchData cveSearchData;
    String host;

    String ID = "1";
    String CVEYEAR = "2007";
    String CVENUMBER = "5432";
    String CVE = "CVE-" + CVEYEAR + "-" + CVENUMBER;

    private CveSearchDataTranslator cveSearchDataTranslator;
    private CveSearchDataToVulnerabilityTranslator cveSearchDataToVulnerabilityTranslator;

    private CveSearchData generateCveSearchData(String id) {
        return new CveSearchData() {
            @Override
            public Map<String, String> getAccess() {
                Map<String,String> access = new HashMap<>();
                access.put("access_key_a" + id, "access_value_a" + id);
                access.put("access_key_b" + id, "access_value_b" + id);
                return access;
            }

            @Override
            public String getId() {
                return "CVE-" + CVEYEAR + "-" + CVENUMBER + id;
            }

            @Override
            public String getModified() {
                return "Modified" + id;
            }

            @Override
            public Set<String> getReferences() {
                Set<String> references = new HashSet<>();
                references.add("references_a" + id);
                references.add("references_b" + id);
                return references;
            }

            @Override
            public String getPublished() {
                return "Published" + id;
            }

            @Override
            public Double getCvss() {
                return 7.5;
            }

            @Override
            public Map<String, String> getImpact() {
                Map<String,String> impact = new HashMap<>();
                impact.put("impact_key_a" + id, "impact_value_a" + id);
                impact.put("impact_key_b" + id, "impact_value_b" + id);
                return impact;
            }

            @Override
            public String getSummary() {
                return "summary" + id;
            }
        };
    }

    @Before
    public void setUp() {
        cveSearchData = generateCveSearchData("1");
        cveSearchDataTranslator = new CveSearchDataTranslator();
        cveSearchDataToVulnerabilityTranslator = new CveSearchDataToVulnerabilityTranslator();

        Properties props = CommonUtils.loadProperties(CveSearchDataTranslatorTest.class, "/cvesearch.properties");
        host = props.getProperty("cvesearch.host","http://localhost:5000");
    }

    @Test
    public void getCVEReferenceForCveSearchdataTest(){
        Set<CVEReference> cveReferences = cveSearchDataToVulnerabilityTranslator.getCVEReferencesForCVE(CVE);

        assert(1 == cveReferences.size());
        CVEReference cveReference = cveReferences.stream()
                .findAny()
                .get();
        assert(CVEYEAR.equals(cveReference.getYear()));
        assert(CVENUMBER.equals(cveReference.getNumber()));
    }

    @Test
    public void testIdTranslation() {
        Vulnerability v = cveSearchDataTranslator.apply(cveSearchData).vulnerability;

        Set<CVEReference> cveReferences = v.getCveReferences();

        assert(cveReferences.size() == 1);

        CVEReference cveReference = cveReferences.stream()
                .findAny()
                .get();

        assert(CVEYEAR.equals(cveReference.getYear()));
        assert((CVENUMBER+ID).equals(cveReference.getNumber()));
    }

    @Test
    public void testSummaryTranslation() {
        Vulnerability v = cveSearchDataTranslator.apply(cveSearchData).vulnerability;
        assert(v.getDescription().equals(cveSearchData.getSummary()));
    }

    @Test
    public void testWithRealData() throws IOException {
        List<CveSearchData> cveSearchDatas = new CveSearchApiImpl(host).search("zyxel","zywall");
        List<CveSearchDataTranslator.VulnerabilityWithRelation> vms = cveSearchDatas.stream()
                .map(cveSearchData -> cveSearchDataTranslator.apply(cveSearchData))
                .collect(Collectors.toList());
        assert(vms != null);
    }

    @Test
    public void testWithApacheData() throws IOException {
        List<CveSearchData> cveSearchDatas = new CveSearchApiImpl(host).search("apache",".*");
        List<CveSearchDataTranslator.VulnerabilityWithRelation> vms = cveSearchDatas.stream()
                .map(cveSearchData -> cveSearchDataTranslator.apply(cveSearchData))
                .collect(Collectors.toList());
        assert(vms != null);
        List<Vulnerability> vs = vms.stream()
                .map(vm -> vm.vulnerability)
                .collect(Collectors.toList());
        assert(vs.size() > 700);
    }
}
