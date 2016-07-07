/*
 * Copyright Siemens AG, 2014-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.siemens.sw360.datahandler.permissions;

import com.google.common.collect.Sets;
import com.siemens.sw360.datahandler.common.CommonUtils;
import com.siemens.sw360.datahandler.thrift.components.Release;
import com.siemens.sw360.datahandler.thrift.users.RequestedAction;
import com.siemens.sw360.datahandler.thrift.users.User;

import java.util.Map;
import java.util.Set;

import static com.siemens.sw360.datahandler.common.CommonUtils.toSingletonSet;

/**
 * Created by bodet on 16/02/15.
 *
 * @author cedric.bodet@tngtech.com
 */
public class ReleasePermissions extends DocumentPermissions<Release> {

    private final Set<String> moderators;
    private final Set<String> contributors;

    protected ReleasePermissions(Release document, User user) {
        super(document, user);

        moderators = Sets.union(toSingletonSet(document.createdBy), CommonUtils.nullToEmptySet(document.moderators));
        contributors = Sets.union(moderators, CommonUtils.nullToEmptySet(document.contacts));

    }

    @Override
    public void fillPermissions(Release other, Map<RequestedAction, Boolean> permissions) {
        other.permissions = permissions;
    }

    @Override
    public boolean isActionAllowed(RequestedAction action) {
        return getStandardPermissions(action);
    }

    @Override
    protected Set<String> getContributors() {
        return contributors;
    }

    @Override
    protected Set<String> getModerators() {
        return moderators;
    }
}
