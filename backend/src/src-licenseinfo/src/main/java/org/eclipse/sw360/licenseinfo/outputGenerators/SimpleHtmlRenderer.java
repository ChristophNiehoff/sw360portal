/*
 * Copyright Siemens AG, 2017. Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.licenseinfo.outputGenerators;


import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class SimpleHtmlRenderer {
    private String htmlText;

    private enum ListMode {
        NO_LIST,
        OL_LIST,
        UL_LIST
    }

    private final String NEWLINE_CHAR = System.lineSeparator();
    private final String TAB_CHAR = "\t";
    private final String ROOT_NODE = "0";
    private Map loopCounter;
    private Map tabLevel;
    private Map listMode;

    public SimpleHtmlRenderer(String htmlTextInput) {
        htmlText = htmlTextInput;

        loopCounter = new HashMap<String, Integer>();
        tabLevel = new HashMap<String, Integer>();
        listMode = new HashMap<String, ListMode>();

        loopCounter.put(ROOT_NODE, 1);
        tabLevel.put(ROOT_NODE, 0);
        listMode.put(ROOT_NODE, ListMode.NO_LIST);
    }

    public void setHtmlText(String htmlTextInput) {
        htmlText = htmlTextInput;
    }

    public String renderHtml() {
        Document doc = Jsoup.parse(htmlText);
        Element body = doc.body();

        String renderedText = renderNode(body, ROOT_NODE);
        return renderedText;
    }



    private String renderNode(Node parentNode, String listId) {

        String renderedText = "";

        for(Node node : parentNode.childNodes()) {

            // Handle lists

            if (node.outerHtml().startsWith("<ul>") || node.outerHtml().contains("<ol>")) {
                String newListId = UUID.randomUUID().toString();
                loopCounter.put(newListId, 1);
                tabLevel.put(newListId, (Integer)tabLevel.get(listId)+1);
                listMode.put(newListId, node.outerHtml().contains("<ul>") ? ListMode.UL_LIST : ListMode.OL_LIST );

                renderedText += renderNode(node, newListId);


            } else if (node.outerHtml().startsWith("<li>")) {
                String bulletPointChar = (listMode.get(listId) == ListMode.OL_LIST) ? loopCounter.get(listId).toString() + ". " : "* ";
                loopCounter.put(listId, (int)loopCounter.get(listId)+1);
                renderedText += tabs(listId) + bulletPointChar + renderNode(node, listId) + NEWLINE_CHAR;


            // Handle simple tags
            } else if (node.outerHtml().startsWith("<p>")) {
                renderedText += tabs(listId) + renderNode(node, listId);

            } else if (node.outerHtml().startsWith("<em>")) {
                renderedText += tabs(listId) + renderNode(node, listId);

            } else if (node.outerHtml().startsWith("<strong>")) {
                renderedText += tabs(listId) + "*" + renderNode(node, listId) + "*";


            // Handle text
            } else {
                renderedText += tabs(listId) + node.outerHtml() + NEWLINE_CHAR;
            }
        }
        return renderedText;
    }


    private String tabs(String listId) {
        String tabSpace = "";
        for (int i = 0; i < (int)tabLevel.get(listId); i++) {
            tabSpace += TAB_CHAR;
        }
        return tabSpace;
    }
}
