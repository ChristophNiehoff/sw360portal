/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.siemens.sw360.moderation;

import com.siemens.sw360.datahandler.common.DatabaseSettings;
import com.siemens.sw360.datahandler.thrift.ModerationState;
import com.siemens.sw360.datahandler.thrift.RequestStatus;
import com.siemens.sw360.datahandler.thrift.SW360Exception;
import com.siemens.sw360.datahandler.thrift.components.Component;
import com.siemens.sw360.datahandler.thrift.components.Release;
import com.siemens.sw360.datahandler.thrift.licenses.License;
import com.siemens.sw360.datahandler.thrift.moderation.ModerationRequest;
import com.siemens.sw360.datahandler.thrift.moderation.ModerationService;
import com.siemens.sw360.datahandler.thrift.projects.Project;
import com.siemens.sw360.datahandler.thrift.users.User;
import com.siemens.sw360.moderation.db.ModerationDatabaseHandler;
import org.apache.thrift.TException;

import java.net.MalformedURLException;
import java.util.List;

import static com.siemens.sw360.datahandler.common.SW360Assert.*;


/**
 * Implementation of the Thrift service
 *
 * @author cedric.bodet@tngtech.com
 * @author Johannes.Najjar@tngtech.com
 */
public class ModerationHandler implements ModerationService.Iface {

    private final ModerationDatabaseHandler handler;
    /*private final DocumentDatabaseHandler documentHandler;*/

    public ModerationHandler() throws MalformedURLException {
        handler = new ModerationDatabaseHandler(DatabaseSettings.COUCH_DB_URL, DatabaseSettings.COUCH_DB_DATABASE, DatabaseSettings.COUCH_DB_ATTACHMENTS);
        /*documentHandler = new DocumentDatabaseHandler(DatabaseSettings.COUCH_DB_URL, DatabaseSettings.COUCH_DB_DATABASE);*/
    }

    @Override
    public RequestStatus createComponentRequest(Component component, User user) throws TException {
        assertUser(user);
        assertNotNull(component);

        return handler.createRequest(component, user, false);
    }

    @Override
    public RequestStatus createReleaseRequest(Release release, User user) throws TException {
        assertUser(user);
        assertNotNull(release);

        return handler.createRequest(release, user, false);
    }

    @Override
    public RequestStatus createProjectRequest(Project project, User user) throws TException {
        assertUser(user);
        assertNotNull(project);

        return handler.createRequest(project, user, false);
    }

    @Override
    public RequestStatus createLicenseRequest(License license, User user) throws TException {
        assertUser(user);
        assertNotNull(license);

        return handler.createRequest(license, user.getEmail(), user.getDepartment());
    }

    @Override
    public void createUserRequest(User user) throws TException {
        assertUser(user);

        handler.createRequest(user);
    }

    @Override
    public void createComponentDeleteRequest(Component component, User user) throws TException {
        assertUser(user);
        assertNotNull(component);

        handler.createRequest(component, user, true);
    }

    @Override
    public void createReleaseDeleteRequest(Release release, User user) throws TException {
        assertUser(user);
        assertNotNull(release);

        handler.createRequest(release, user, true);
    }

    @Override
    public void createProjectDeleteRequest(Project project, User user) throws TException {
        assertUser(user);
        assertNotNull(project);

        handler.createRequest(project, user, true);
    }

    @Override
    public List<ModerationRequest> getModerationRequestByDocumentId(String documentId) throws TException {
        assertId(documentId);

        return handler.getRequestByDocumentId(documentId);
    }

    @Override
    public RequestStatus updateModerationRequest(ModerationRequest moderationRequest) throws TException {
        handler.updateModerationRequest(moderationRequest);
        return RequestStatus.SUCCESS;
    }

    @Override
    public ModerationRequest getModerationRequestById(String id) throws TException {
        return handler.getRequest(id);
    }

    @Override
    public void refuseRequest(String requestId) throws TException {
        handler.refuseRequest(requestId);
    }

    @Override
    public void removeUserFromAssignees(String requestId, User user) throws TException {
        ModerationRequest request = handler.getRequest(requestId);
        request.getModerators().remove(user.getEmail());
        request.setModerationState(ModerationState.PENDING);
        request.unsetReviewer();
        handler.updateModerationRequest(request);
    }

    @Override
    public void cancelInProgress(String requestId) throws TException {
        ModerationRequest request = handler.getRequest(requestId);
        request.setModerationState(ModerationState.PENDING);
        request.unsetReviewer();
        handler.updateModerationRequest(request);
    }

    @Override
    public void setInProgress(String requestId, User user) throws TException {
        ModerationRequest request = handler.getRequest(requestId);
        request.setModerationState(ModerationState.INPROGRESS);
        request.setReviewer(user.getEmail());
        handler.updateModerationRequest(request);
    }

    @Override
    public void deleteRequestsOnDocument(String documentId) throws TException {
        assertId(documentId);

        handler.deleteRequestsOnDocument(documentId);
    }

    @Override
    public RequestStatus deleteModerationRequest(String id, User user) throws SW360Exception{
        assertUser(user);

        return handler.deleteModerationRequest(id,user);
    }

    @Override
    public List<ModerationRequest> getRequestsByModerator(User user) throws TException {
        assertUser(user);

        return handler.getRequestsByModerator(user.getEmail());
    }

    @Override
    public List<ModerationRequest> getRequestsByRequestingUser(User user) throws TException {
        assertUser(user);

        return handler.getRequestsByRequestingUser(user.getEmail());
    }

}
