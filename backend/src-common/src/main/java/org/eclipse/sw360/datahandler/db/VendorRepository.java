/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
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
import org.eclipse.sw360.datahandler.couchdb.DatabaseConnector;
import org.eclipse.sw360.datahandler.couchdb.DatabaseRepository;
import org.eclipse.sw360.datahandler.thrift.RequestStatus;
import org.eclipse.sw360.datahandler.thrift.SW360Exception;
import org.eclipse.sw360.datahandler.thrift.components.Release;
import org.eclipse.sw360.datahandler.thrift.users.RequestedAction;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.eclipse.sw360.datahandler.thrift.vendors.Vendor;
import org.ektorp.support.View;

import static org.eclipse.sw360.datahandler.common.SW360Assert.assertNotNull;
import static org.eclipse.sw360.datahandler.permissions.PermissionUtils.makePermission;

/**
 * CRUD access for the Vendor class
 *
 * @author cedric.bodet@tngtech.com
 * @author Johannes.Najjar@tngtech.com
 */
@View(name = "all", map = "function(doc) { if (doc.type == 'vendor') emit(null, doc._id) }")
public class VendorRepository extends DatabaseRepository<Vendor> {

    public VendorRepository(DatabaseConnector db) {
        super(Vendor.class, db);

        initStandardDesignDocument();
    }



    public RequestStatus deleteVendor(String id, User user) throws SW360Exception {
        Vendor vendor = get(id);
        assertNotNull(vendor);

        if (makePermission(vendor, user).isActionAllowed(RequestedAction.DELETE)) {
            remove(id);
            return RequestStatus.SUCCESS;
        } else {
            log.error("User is not allowed to delete!");
            return RequestStatus.FAILURE;
        }


    }

    public RequestStatus updateVendor(Vendor vendor, User user) {
        if (makePermission(vendor, user).isActionAllowed(RequestedAction.WRITE)) {
            update(vendor);
            return RequestStatus.SUCCESS;
        } else {
            log.error("User is not allowed to delete!");
            return RequestStatus.FAILURE;
        }
    }

    public void fillVendor(Release release) {
        if (release.isSetVendorId()) {
            final String vendorId = release.getVendorId();
            if (!Strings.isNullOrEmpty(vendorId)) {
                final Vendor vendor = get(vendorId);
                if (vendor != null)
                    release.setVendor(vendor);
            }
            release.unsetVendorId();
        }
    }
}
