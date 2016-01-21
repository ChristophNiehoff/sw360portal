/*
 * Copyright Siemens AG, 2015. Part of the SW360 Portal Project.
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
package com.siemens.sw360.portal.common.datatables.data;


import com.google.common.base.Optional;

/**
 * @author daniele.fognini@tngtech.com
 */
public class DataTablesColumn {

    private final Optional<DataTablesSearch> search;

    public DataTablesColumn(DataTablesSearch search) {
        this.search = Optional.of(search);
    }

    public DataTablesSearch getSearch() {
        return search.get();
    }

    public boolean isSearchable() {
        return search.isPresent();
    }
}
