/*
 * Copyright Siemens AG, 2013-2016. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.siemens.sw360.portal.tags;

import com.siemens.sw360.datahandler.common.SW360Utils;
import com.siemens.sw360.datahandler.thrift.licenses.License;

/**
 * This prints a license name and version
 *
 * @author birgit.heydenreich@tngtech.com
 */
public class LicenseName extends OutTag {
    public void setLicense(License license) {
        this.value = SW360Utils.printName(license);
    }
}
