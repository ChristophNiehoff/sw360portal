/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.portal.users;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import javax.portlet.PortletRequest;

/**
 * Class to manage user sessions using Liferay's logged-in user
 *
 * @author cedric.bodet@tngtech.com
 */
public class LifeRayUserSession {
    /**
     * Get the email of the currently logged-in user
     *
     * @param request Java portlet render request
     */
    public static String getEmailFromRequest(PortletRequest request) {
        String email = null;

        // Logged-in user can be fetched from Liferay's ThemeDisplay
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        if (themeDisplay.isSignedIn()) {
            com.liferay.portal.model.User user = themeDisplay.getUser();

            // Get email address from user
            if (user != null) {
                email = user.getEmailAddress();
            }
        }
        return email;
    }
}
