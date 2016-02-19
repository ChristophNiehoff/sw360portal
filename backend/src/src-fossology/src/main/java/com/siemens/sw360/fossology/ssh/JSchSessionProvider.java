/*
 * Copyright Siemens AG, 2015. Part of the SW360 Portal Project.
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
package com.siemens.sw360.fossology.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.siemens.sw360.datahandler.thrift.SW360Exception;
import com.siemens.sw360.fossology.config.FossologySettings;
import com.siemens.sw360.fossology.ssh.keyrepo.FossologyHostKeyRepository;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Optional;

import static org.apache.log4j.Logger.getLogger;

@Component
public class JSchSessionProvider implements DisposableBean {
    private static final Logger log = getLogger(JSchSessionProvider.class);

    static {
        JSch.setLogger(new JSchLogForwarder());
        JSch.setConfig("StrictHostKeyChecking", "yes");
    }

    private static final JSch J_SCH = new JSch();

    private final FossologyHostKeyRepository fossologyHostKeyRepository;
    private final String userName;
    private final String hostName;
    private final int hostPort;
    private final ConcurrentLinkedQueue<Session> sessions;

    @Autowired
    public JSchSessionProvider(FossologySettings fossologySettings, FossologyHostKeyRepository fossologyHostKeyRepository) {
        this.fossologyHostKeyRepository = fossologyHostKeyRepository;

        userName = fossologySettings.getFossologySshUsername();
        hostName = fossologySettings.getFossologyHost();
        hostPort = fossologySettings.getFossologyPort();

        addIdentity(userName, fossologySettings.getFossologyPrivateKey());
        sessions = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void destroy() throws Exception {
        for (Session session : sessions) {
            doCloseSession(session);
        }
    }

    private void addIdentity(String userName, byte[] privateKey) {
        synchronized (J_SCH) {
            try {
                if (!J_SCH.getIdentityNames().contains(userName)) {
                    J_SCH.addIdentity(userName, privateKey, null, null);
                }
            } catch (JSchException e) {
                log.error("cannot set Identity for " + userName, e);
            }
        }
    }

    private Optional<Session> pollCachedSession() {
        Session session = sessions.poll();
        if (session != null && session.isConnected()) {
            return Optional.of(session);
        } else {
            return Optional.empty();
        }
    }

    public Session getSession(int connectionTimeout) throws SW360Exception {
        Optional<Session> session = pollCachedSession();
        if (session.isPresent()) {
            return session.get();
        } else {
            return doGetSession(connectionTimeout);
        }
    }

    public void closeSession(Session session) {
        if (session == null) {
            return;
        }
        if (session.isConnected()) {
            if (sessions.size() < 5) { // minor race condition, not a problem
                sessions.add(session);
            } else {
                doCloseSession(session);
            }
        }
    }

    private Session doGetSession(int connectionTimeout) throws SW360Exception {
        synchronized (J_SCH) {
            try {
                Session session = J_SCH.getSession(userName, hostName, hostPort);
                session.setHostKeyRepository(fossologyHostKeyRepository);
                session.connect(connectionTimeout);
                return session;
            } catch (JSchException e) {
                String serverString = getServerString();
                log.error("cannot connect to fossology server: " + serverString, e);
                throw new SW360Exception("cannot connect to Fossology Server");
            }
        }
    }

    private void doCloseSession(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    public String getServerString() {
        return userName + "@[" + hostName + "]:[" + hostPort + "]";
    }

    private static class JSchLogForwarder implements com.jcraft.jsch.Logger {
        @Override
        public boolean isEnabled(int level) {
            return log.isEnabledFor(Level.toLevel(level));
        }

        @Override
        public void log(int level, String message) {
            log.log(Level.toLevel(level), message);
        }
    }
}
