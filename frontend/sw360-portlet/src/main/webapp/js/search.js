/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

//This can not be document ready function as liferay definitions need to be loaded first
$(window).load(function() {
    var data = parseList(allSearchResultsString);

    createSearchTable(data);
});

function parseList(listString ){
    var result = [];

    for (var i=0; i<listString.length; ++i) {
        var id = listString[i].id;
        var row = {
            "DT_RowId": id,
            "0": listString[i].type,
            "1": listString[i].name
         };
        result.push(row);
    }

    return result;
}

function createSearchTable(data) {
    $('#searchTable').dataTable({
        "sPaginationType": "full_numbers",
        "aaData": data,
        "aoColumns": [
            { "sTitle": "Type",
                "mRender": function ( data, type, full ) {
                    return typeColumn( data, type, full );
                }
            },
            { "sTitle": "Text" }
        ]
    });
    $('#searchTable_filter').hide();
    $('#searchTable_first').hide();
    $('#searchTable_last').hide();
}