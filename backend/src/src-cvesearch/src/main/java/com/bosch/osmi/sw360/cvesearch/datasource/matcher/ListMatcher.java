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
package com.bosch.osmi.sw360.cvesearch.datasource.matcher;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.bosch.osmi.sw360.cvesearch.datasource.matcher.ModifiedLevenshteinDistance.levenshteinMatch;

public class ListMatcher {
    private Collection<String> needleList;
    Logger log = Logger.getLogger(ListMatcher.class);

    public ListMatcher(Collection<String> needleList){
        this.needleList = needleList;
    }

    public List<Match> getMatches(String haystack){
        return needleList.stream()
                .map(needle -> levenshteinMatch(needle, haystack))
                .sorted((sm1,sm2) -> sm1.compareTo(sm2))
                .collect(Collectors.toList());
    }
}
