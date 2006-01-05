/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package org.apache.axis2.transport.mail;

import org.apache.axis2.AxisFault;
import org.apache.axis2.i18n.Messages;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

public class EmailReceiver {
    private String host;
    private Folder inbox;
    private String password;
    private String popPort;
    private Store store;
    private String user;

    public EmailReceiver(String user, String host, String popPort, String password)
            throws AxisFault {
        this.user = user;
        this.host = host;
        this.popPort = popPort;
        this.password = password;

        if (this.user == null) {
            throw new AxisFault(Messages.getMessage("canNotBeNull", "User"));
        }

        if (this.host == null) {
            throw new AxisFault(Messages.getMessage("canNotBeNull", "Host"));
        }

        if (this.popPort == null) {
            throw new AxisFault(Messages.getMessage("canNotBeNull", "port"));
        }

        if (this.password == null) {
            throw new AxisFault(Messages.getMessage("canNotBeNull", "Password"));
        }
    }

    public void connect() throws AxisFault {
        try {
            final PasswordAuthentication authentication = new PasswordAuthentication(user,
                    password);
            Properties props = new Properties();

            props.put("mail.user", user);
            props.put("mail.host", host);
            props.put("mail.store.protocol", "pop3");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.pop3.port", popPort);

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return authentication;
                }
            });

            store = session.getStore();
            store.connect();

            Folder root = store.getDefaultFolder();

            inbox = root.getFolder("inbox");
        } catch (NoSuchProviderException e) {
            throw new AxisFault(e);
        } catch (MessagingException e) {
            throw new AxisFault(e);
        }
    }

    public void disconnect() throws AxisFault {
        try {
            inbox.close(true);
            store.close();
        } catch (MessagingException e) {
            throw new AxisFault(e);
        }
    }

    public Message[] receive() throws AxisFault {
        try {
            inbox.open(Folder.READ_WRITE);

            Message[] msgs = inbox.getMessages();

            if (msgs.length == 0) {
                return null;
            } else {
                return msgs;
            }
        } catch (NoSuchProviderException e) {
            throw new AxisFault(e);
        } catch (MessagingException e) {
            throw new AxisFault(e);
        }
    }
}
