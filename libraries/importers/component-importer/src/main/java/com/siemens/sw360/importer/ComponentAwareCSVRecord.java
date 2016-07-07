/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.siemens.sw360.importer;


import com.siemens.sw360.datahandler.common.SW360Utils;

/**
 * @author johannes.najjar@tngtech.com
 */
public abstract class ComponentAwareCSVRecord implements CustomizedCSVRecord {
    protected final String componentName;
    protected final String releaseName;
    protected final String releaseVersion;

    protected ComponentAwareCSVRecord(String componentName, String releaseName, String releaseVersion) {
        this.componentName = componentName;
        this.releaseName = releaseName;
        this.releaseVersion = releaseVersion;
    }

    public String getReleaseIdentifier() {
        return SW360Utils.getVersionedName(releaseName, releaseVersion);
    }

    public String getComponentName() {
        return componentName;
    }
}
