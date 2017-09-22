/*
 * Copyright Siemens AG, 2017.
 * Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
define('modules/expandableTreetable', ['jquery', /* jquery-plugins */ 'jquery-treetable'], function($) {

    return {
        setUpExpandableTreetable: function(tableId, ajaxUrl, parentBranchId, parentScopeIdKey, parentScopeId) {
            var tbl = $("#" + tableId);
            tbl.treetable({
                expandable: true,
                onNodeExpand: function () {
                    var node = this;
                    if (node.children.length === 0) {
                        var dataDict = {};
                        dataDict[parentBranchId] = node.id;
                        dataDict[parentScopeIdKey] = parentScopeId;

                        $.ajax({
                            type: 'POST',
                            url: ajaxUrl,
                            cache: false,
                            data: dataDict
                        }).done(function (html) {
                            var rows = $(html).filter("tr");
                            tbl.treetable("loadBranch", node, rows);
                        });
                    }
                }
            });
        }
    }
});