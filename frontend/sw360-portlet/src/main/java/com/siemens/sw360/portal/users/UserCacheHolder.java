/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.siemens.sw360.portal.users;

import com.siemens.sw360.datahandler.thrift.users.User;
import org.apache.log4j.Logger;

import javax.portlet.PortletRequest;
import java.util.concurrent.ExecutionException;

/**
 * User cache singleton
 *
 * @author cedric.bodet@tngtech.com
 */
public class UserCacheHolder {

    private static final Logger log = Logger.getLogger(UserCacheHolder.class);
    public static final User EMPTY_USER = new User().setId("").setEmail("").setExternalid("").setDepartment("").setLastname("").setGivenname("");

    private static UserCacheHolder instance = null;

    UserCache cache;

    private UserCacheHolder() {
        cache = new UserCache();
    }

    private static synchronized UserCacheHolder getInstance() {
        if (instance == null) {
            instance = new UserCacheHolder();
        }
        return instance;
    }


    private User getCurrentUser(PortletRequest request) {
        String email = LifeRayUserSession.getEmailFromRequest(request);

        return loadUserFromEmail(email);
    }

    private User loadUserFromEmail(String email, boolean refresh) {
        if (log.isTraceEnabled()) log.trace("Fetching user with email: " + email);

        // Get user from cache
        try {
            if(refresh) return cache.getRefreshed(email);
            return cache.get(email);
        } catch (ExecutionException e) {
            log.error("Unable to fetch user...", e);
            return getEmptyUser();
        }
    }

    private User loadUserFromEmail(String email) {
        return  loadUserFromEmail(email, false);
    }

    private User loadRefreshedUserFromEmail(String email) {
        return  loadUserFromEmail(email, true);
    }

    public static User getUserFromRequest(PortletRequest request) {
        return getInstance().getCurrentUser(request);
    }

    public static User getUserFromEmail(String email) {
        return getInstance().loadUserFromEmail(email);
    }

    public static User getRefreshedUserFromEmail(String email) {
        return getInstance().loadRefreshedUserFromEmail(email);
    }

    /**
     * Returns an empty user object
     */
    private static User getEmptyUser() {
        return EMPTY_USER;
    }
}
