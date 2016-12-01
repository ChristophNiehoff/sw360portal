/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.datahandler.permissions.jgivens;

import org.eclipse.sw360.datahandler.TEnumToString;
import org.eclipse.sw360.datahandler.permissions.DocumentPermissions;
import org.eclipse.sw360.datahandler.permissions.PermissionUtils;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.users.RequestedAction;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.eclipse.sw360.datahandler.thrift.users.UserGroup;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import org.junit.internal.AssumptionViolatedException;

import java.util.List;

/**
 * @author johannes.najjar@tngtech.com
 */
public class WhenComputePermissions  extends Stage<WhenComputePermissions> {
    @ExpectedScenarioState
    Project project;

    @ProvidedScenarioState
    RequestedAction highestAllowedAction;

    private static String DUMMY_ID = "DAU";
    private static String DUMMY_DEP = "definitleyTheWrongDepartment YO HO HO";

    public WhenComputePermissions the_highest_allowed_action_is_computed_for_user_$_with_user_group_$(@Quoted String userEmail, @TEnumToString UserGroup userGroup) {
        final User user = new User(DUMMY_ID, userEmail, DUMMY_DEP).setUserGroup(userGroup);

        final DocumentPermissions<Project> projectDocumentPermissions = PermissionUtils.makePermission(project, user);

        highestAllowedAction = projectDocumentPermissions.getHighestAllowedPermission();
        return self();
    }
}
