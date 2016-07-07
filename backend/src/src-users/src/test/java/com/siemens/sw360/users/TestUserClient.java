/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.siemens.sw360.users;

import com.siemens.sw360.datahandler.thrift.users.UserService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;

import java.io.IOException;

/**
 * Small client for testing a service
 *
 * @author cedric.bodet@tngtech.com
 */
public class TestUserClient {

    public static void main(String[] args) throws TException, IOException {
        THttpClient thriftClient = new THttpClient("http://127.0.0.1:8080/users/thrift");
        TProtocol protocol = new TCompactProtocol(thriftClient);
        UserService.Iface client = new UserService.Client(protocol);

        System.out.println(client.getByEmail("cedric.bodet@tngtech.com"));
    }
}
