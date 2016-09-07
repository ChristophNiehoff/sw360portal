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

import com.siemens.sw360.datahandler.common.CommonUtils;
import com.siemens.sw360.datahandler.thrift.components.Release;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.log4j.Logger;

public class CveSearchWrapper {

    private CveSearchApi cveSearchApi;
    Logger log = Logger.getLogger(CveSearchWrapper.class);

    private List<SearchLevel> searchLevels;

    protected boolean isCpe(String potentialCpe){
        if(null == potentialCpe){
            return false;
        }
        return potentialCpe.startsWith("cpe:") && potentialCpe.length() > 10;
    }

    @FunctionalInterface
    private interface SearchLevel {
        List<CveSearchData> apply(Release release) throws IOException;
    }

    private SearchLevel mkSearchLevel(Function<Release,String> genNeedle){
        Function<String,String> cveWrapper = needle -> {
            if (isCpe(needle)){
                return needle;
            }
            return "cpe:2.3:.*:.*" + CommonUtils.nullToEmptyString(needle).replace(" ", ".*").toLowerCase() + ".*";
        };
        return r -> {
            String needle = genNeedle.apply(r);
            if (needle != null){
                return cveSearchApi.cvefor(cveWrapper.apply(needle));
            }
            return null;
        };
    }

    protected Function<Release, String> implode(Function<Release,String> prt, Function<Release,String> ... prts){
        return Arrays.stream(prts)
                .reduce(prt,
                        (s1,s2) -> r -> s1.apply(r) + ".*" + s2.apply(r));
    }

    private void addSearchLevel(Function<Release,Boolean> isPossible, Function<Release,String> prt, Function<Release,String> ... prts){
        Function<Release,String> implodedParts = implode(prt, prts);

        searchLevels.add(mkSearchLevel(r -> {
            if(isPossible.apply(r)){
                return implodedParts.apply(r);
            }
            return null;
        }));
    }

    private void setSearchLevels() {
        searchLevels = new ArrayList<>();

        // Level 1. search by full cpe
        addSearchLevel(r -> r.isSetCpeid() && isCpe(r.getCpeid().toLowerCase()),
                Release::getCpeid);

        // Level 2. search by: VENDOR_FULL_NAME:NAME:VERSION
        addSearchLevel(r -> r.isSetVersion() && r.isSetVendor() && r.getVendor().isSetFullname(),
                r -> r.getVendor().getFullname(),
                Release::getName,
                Release::getVersion);

        // Level 3. search by: VENDOR_SHORT_NAME:NAME:VERSION
        addSearchLevel(r -> r.isSetVersion() && r.isSetVendor() && r.getVendor().isSetShortname(),
                r ->r.getVendor().getShortname(),
                Release::getName,
                Release::getVersion);

        // Level 4. search by: VENDOR_FULL_NAME:NAME
        addSearchLevel(r -> r.isSetVendor() && r.getVendor().isSetFullname(),
                r ->r.getVendor().getFullname(),
                Release::getName);

        // Level 5. search by: VENDOR_SHORT_NAME:NAME
        addSearchLevel(r -> r.isSetVendor() && r.getVendor().isSetShortname(),
                r -> r.getVendor().getShortname(),
                Release::getName);

        // Level 6. search by: .*:NAME:VERSION
        addSearchLevel(r -> r.isSetVersion(),
                Release::getName,
                Release::getVersion);

        // Level 7. search by: .*:NAME
        addSearchLevel(r -> true,
                Release::getName);
    }

    public CveSearchWrapper(CveSearchApi cveSearchApi){
        this.cveSearchApi=cveSearchApi;
        setSearchLevels();
    }

    public List<CveSearchData> searchForRelease(Release release, int maxDepth) {
        List<CveSearchData> result = null;
        int level = 0;

        // use the basic search levels
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
