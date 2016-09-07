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

import com.bosch.osmi.sw360.cvesearch.datasource.heuristics.Heuristic;
import com.bosch.osmi.sw360.cvesearch.datasource.heuristics.searchlevels.BasicSearchLevels;
import com.bosch.osmi.sw360.cvesearch.datasource.heuristics.searchlevels.GuessingSearchLevels;
import com.bosch.osmi.sw360.cvesearch.datasource.heuristics.searchlevels.SearchLevelGenerator;
import com.siemens.sw360.datahandler.thrift.components.Release;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

public class CveSearchWrapper {

    private CveSearchApi cveSearchApi;
    Logger log = Logger.getLogger(CveSearchWrapper.class);

    private Heuristic heuristic;

    public CveSearchWrapper(CveSearchApi cveSearchApi) {
        this.cveSearchApi=cveSearchApi;

        if (true) {
            heuristic = new Heuristic(new BasicSearchLevels(), cveSearchApi);
        }else {
            SearchLevelGenerator searchLevelGenerator = new GuessingSearchLevels(cveSearchApi)
                    .setVendorThreshold(1)
                    .setProductThreshold(0);
            heuristic = new Heuristic(searchLevelGenerator, cveSearchApi, true);
        }
    }

    public Optional<List<CveSearchData>> searchForRelease(Release release) {
        try {
            return Optional.of(heuristic.run(release));
        } catch (IOException e) {
            log.error("Was not able to search for release with name=" + release.getName() + " and id=" + release.getId(), e);
        }
        return Optional.empty();
    }
}
