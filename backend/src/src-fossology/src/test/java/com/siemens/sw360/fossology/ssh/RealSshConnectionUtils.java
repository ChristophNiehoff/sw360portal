/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.siemens.sw360.fossology.ssh;

import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.Session;
import com.siemens.sw360.fossology.config.FossologySettings;
import com.siemens.sw360.fossology.db.FossologyFingerPrintRepository;
import com.siemens.sw360.fossology.ssh.keyrepo.FossologyHostKeyRepository;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author daniele.fognini@tngtech.com
 */
public class RealSshConnectionUtils {

    public static void assumeCanOpenSshSessions() {
        JSchSessionProvider trustingJSchSessionProvider = getTrustingJSchSessionFactory();
        try {
            assumeCanOpenSshSessions(trustingJSchSessionProvider);
        } finally {
            try {
                trustingJSchSessionProvider.destroy();
            } catch (Exception e) {
                assumeNoException(e);
            }
        }
    }

    static void assumeCanOpenSshSessions(JSchSessionProvider jSchSessionProvider) {
        try {
            Session session = jSchSessionProvider.getSession(2000);
            assumeThat(session, notNullValue());
            jSchSessionProvider.closeSession(session);
        } catch (Exception e) {
            assumeNoException(e);
        }
    }

    static JSchSessionProvider getTrustingJSchSessionFactory() {
        FossologyFingerPrintRepository keyConnector = mock(FossologyFingerPrintRepository.class);
        FossologyHostKeyRepository hostKeyRepository = spy(new FossologyHostKeyRepository(keyConnector));

        doReturn(HostKeyRepository.OK).when(hostKeyRepository).check(anyString(), any(byte[].class));
        return new JSchSessionProvider(new FossologySettings(), hostKeyRepository);
    }
}
