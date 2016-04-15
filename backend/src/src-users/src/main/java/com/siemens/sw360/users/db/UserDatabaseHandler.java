/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License Version 2.0 as published by the
 * Free Software Foundation with classpath exception.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License version 2.0 for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (please see the COPYING file); if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package com.siemens.sw360.users.db;

import com.siemens.sw360.datahandler.couchdb.DatabaseConnector;
import com.siemens.sw360.datahandler.thrift.RequestStatus;
import com.siemens.sw360.datahandler.thrift.users.RequestedAction;
import com.siemens.sw360.datahandler.thrift.users.User;
import com.siemens.sw360.mail.MailConstants;
import com.siemens.sw360.mail.MailUtil;

import java.net.MalformedURLException;
import java.util.List;

import static com.siemens.sw360.datahandler.permissions.PermissionUtils.makePermission;

/**
 * Class for accessing the CouchDB database
 *
 * @author cedric.bodet@tngtech.com
 */
public class UserDatabaseHandler {

    /**
     * Connection to the couchDB database
     */
    private DatabaseConnector db;
    private UserRepository repository;

    public UserDatabaseHandler(String url, String dbName) throws MalformedURLException {
        // Create the connector
        db = new DatabaseConnector(url, dbName);
        repository =  new UserRepository(db);
    }

    public User getByEmail(String email) {
        return db.get(User.class, email);
    }

    public RequestStatus addUser(User user) {
        // Set id to email, in order to have human readable database
        user.setId(user.getEmail());

        // Add to database
        db.add(user);

        return RequestStatus.SUCCESS;
    }

    public RequestStatus updateUser(User user) {
        // Set id to email, in order to have human readable database
        user.setId(user.getEmail());
        db.update(user);

        return RequestStatus.SUCCESS;
    }

    public RequestStatus deleteUser(User user, User adminUser) {
        if (makePermission(user, adminUser).isActionAllowed(RequestedAction.DELETE)) {
            repository.remove(user);
            return RequestStatus.SUCCESS;
        }
        return RequestStatus.FAILURE;
    }

    public RequestStatus sendMailForAcceptedModerationRequest(String userEmail) {

        MailUtil mailUtil = new MailUtil();
        mailUtil.sendMail(userEmail, MailConstants.SUBJECT_FOR_ACCEPTED_MODERATION_REQUEST, MailConstants.TEXT_FOR_ACCEPTED_MODERATION_REQUEST, true);

        return RequestStatus.SUCCESS;
    }

    public List<User> getAll() {return repository.getAll();}

    public List<User> searchUsers(String name) {
        return repository.searchByName(name);
    }
}
