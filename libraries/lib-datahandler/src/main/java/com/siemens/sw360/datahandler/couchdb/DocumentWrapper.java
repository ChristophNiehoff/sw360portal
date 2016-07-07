/*
 * Copyright Siemens AG, 2014-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.siemens.sw360.datahandler.couchdb;

import org.ektorp.support.CouchDbDocument;

public abstract class DocumentWrapper<T> extends CouchDbDocument {
    public abstract void updateNonMetadata(T source);
}
