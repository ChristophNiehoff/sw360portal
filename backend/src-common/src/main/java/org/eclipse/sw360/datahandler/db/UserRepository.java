/*
 * Copyright Siemens AG, 2013-2017. Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.datahandler.db;

import com.google.common.base.Strings;
import org.eclipse.sw360.components.summary.SummaryType;
import org.eclipse.sw360.components.summary.UserSummary;
import org.eclipse.sw360.datahandler.couchdb.DatabaseConnector;
import org.eclipse.sw360.datahandler.couchdb.SummaryAwareRepository;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.ektorp.support.View;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * CRUD access for the User class
 *
 * @author cedric.bodet@tngtech.com
 * @author Johannes.Najjar@tngtech.com
 */

@View(name = "all", map = "function(doc) { if (doc.type == 'user') emit(null, doc._id) }")
public class UserRepository extends SummaryAwareRepository<User> {
    public UserRepository(DatabaseConnector databaseConnector) {
        super(User.class, databaseConnector, new UserSummary());
        initStandardDesignDocument();
    }

    private static final String BY_NAME_VIEW =
            "function(doc) {" +
                    "  if (doc.type == 'user') {" +
                    "    emit(doc.lastname, doc);" +
                    "  }" +
                    "}";

    @View(name = "byname", map = BY_NAME_VIEW)
    public List<User> searchByName(String name) {

        final List<User> byname;
        if (Strings.isNullOrEmpty(name)) {
            byname = queryView("byname");
        } else {
            byname = queryByPrefix("byname", name);
        }
        return makeSummaryFromFullDocs(SummaryType.SUMMARY, byname);
    }

    private List<User> removeDuplicateUsers(List<User> listUsers) {
        List<User> listUsersUnique = new ArrayList<User>();
        List<String> alreadyUsedIds = new ArrayList<String>();
        for(User user : listUsers) {
            if(!alreadyUsedIds.contains(user.getId())) {
                alreadyUsedIds.add(user.getId());
                listUsersUnique.add(user);
            }
        }
        return listUsersUnique;
    }

    private static final String BY_NAME_AND_EMAIL_VIEW =
            "function(doc) {" +
                    "  if (doc.type == 'user') {" +
                    "    emit(doc.lastname, doc);" +
                    "    emit(doc.givenname, doc);" +
                    "    emit(doc.email, doc);" +
                    "  }" +
                    "}";

    @View(name = "bynameandemail", map = BY_NAME_AND_EMAIL_VIEW)
    public List<User> searchByNameAndEmail(String name) {

        final List<User> users_bynameandemail;
        if (Strings.isNullOrEmpty(name)) {
            users_bynameandemail = queryView("bynameandemail");
        } else {
            users_bynameandemail = queryByPrefix("bynameandemail", name);
        }
        List<User> users_unique = removeDuplicateUsers(users_bynameandemail);
        return makeSummaryFromFullDocs(SummaryType.SUMMARY, users_unique);
    }

    @Override
    public List<User> get(Collection<String> ids) {
        return getConnector().get(User.class, ids, true);
    }
}
