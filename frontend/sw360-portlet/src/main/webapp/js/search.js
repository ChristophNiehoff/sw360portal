/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
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
        "pagingType": "simple_numbers",
        dom: "lrtip",
        "data": data,
        "columns": [
            { "title": "Type",
                "mRender": function ( data, type, full ) {
                    return typeColumn( data, type, full );
                }
            },
            { "title": "Text" }
        ]
    });
}