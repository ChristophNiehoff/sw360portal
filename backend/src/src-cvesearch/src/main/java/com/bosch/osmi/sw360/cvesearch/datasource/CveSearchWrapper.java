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
package com.bosch.osmi.sw360.cvesearch.datasource;

import com.siemens.sw360.datahandler.thrift.components.Release;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.log4j.Logger;

public class CveSearchWrapper {

    private CveSearchApi cveSearchApi;
    Logger log = Logger.getLogger(CveSearchWrapper.class);

    private List<SearchLevel> searchLevels;

    @FunctionalInterface
    private interface SearchLevel {
        List<CveSearchData> apply(Release release) throws IOException;
    }

    private void searchLevelAdd(Function<Release,Boolean> isAplicable,
                                Function<Release,String> queryGenerator) {
        Function<String,String> cveWrapper = c -> {
            if (c.contains("cpe:")){
                return "cpe:2.3:.*:" + c + ".*";
            }
            return c;
        };
        searchLevels.add(r -> {
            if(isAplicable.apply(r)){
                return cveSearchApi.cvefor(cveWrapper.apply(queryGenerator.apply(r)));
            }
            return null;
        });

    }

    private void setSearchLevels() {
        // Search patterns are:
        //    1. search by full cpe
        //    2. search by: VENDOR_FULL_NAME:NAME::VERSION
        //    3. search by: VENDOR_SHORT_NAME:NAME:VERSION
        //    4. search by: VENDOR_FULL_NAME:NAME
        //    5. search by: VENDOR_SHORT_NAME:NAME
        //    6. search by: .*:NAME:VERSION
        //    7. search by: .*:NAME
        searchLevels = new ArrayList<>();
        searchLevels.add(r -> {
            if (r.isSetCpeid() &&
                    r.getCpeid().toLowerCase().startsWith("cpe:")){
                return cveSearchApi.cvefor(r.getCpeid());
            }
            return null;
        });
        searchLevels.add(r -> {
            if (r.isSetVersion() && r.isSetVendor() && r.getVendor().isSetFullname()){
                return cveSearchApi.search(r.getVendor().getFullname(), r.getName() + ":" + r.getVersion());
            }
            return null;
        });
        searchLevels.add(r -> {
            if (r.isSetVersion() && r.isSetVendor() && r.getVendor().isSetShortname()){
                return cveSearchApi.search(r.getVendor().getShortname(), r.getName() + ":" + r.getVersion());
            }
            return null;
        });
        searchLevels.add(r -> {
            if (r.isSetVendor() && r.getVendor().isSetFullname()){
                return cveSearchApi.search(r.getVendor().getFullname(), r.getName());
            }
            return null;
        });
        searchLevels.add(r -> {
            if (r.isSetVendor() && r.getVendor().isSetShortname()){
                return cveSearchApi.search(r.getVendor().getShortname(), r.getName());
            }
            return null;
        });
        searchLevels.add(r -> {
            if (r.isSetVersion()) {
                return cveSearchApi.search(null, r.getName()+ ":" + r.getVersion());
            }
            return null;
        });
        searchLevels.add(r -> cveSearchApi.search(null, r.getName()));
    }

    public CveSearchWrapper(CveSearchApi cveSearchApi){
        this.cveSearchApi=cveSearchApi;
        setSearchLevels();
    }

    public List<CveSearchData> searchForRelease(Release release, int maxDepth) {
        List<CveSearchData> result = null;
        int level = 0;
        for(SearchLevel searchLevel : searchLevels){
            level++;
            try {
                result = searchLevel.apply(release);
            } catch (IOException e) {
                log.error("IOException in searchlevel=" + level + " for release with id=" + release.getId() + " with msg=" + e.getMessage());
            }
            if(null != result && result.size() > 0){
                return result;
            }
            if(level == maxDepth){
                break;
            }
        }
        return new ArrayList<>();
    }

    public List<CveSearchData> searchForRelease(Release release) {
        return searchForRelease(release, 0);
    }
}
