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
package com.siemens.sw360.portal.tags;

import com.google.common.base.Strings;
import com.siemens.sw360.datahandler.thrift.users.User;
import com.siemens.sw360.portal.users.UserCacheHolder;
import com.siemens.sw360.portal.users.UserUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * This displays a user
 *
 * @author Johannes.Najjar@tngtech.com
 */
public class DisplayUserEmail extends SimpleTagSupport {
    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    public void doTag() throws JspException, IOException {
        User user = null;

        if (!Strings.isNullOrEmpty(email)) {
            user = UserCacheHolder.getUserFromEmail(email);
        } else {
            email = "";
        }

        getJspContext().getOut().print(UserUtils.displayUser(email, user));
    }

}
