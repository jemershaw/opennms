/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.poller.remote.gwt.client;

import org.opennms.features.poller.remote.gwt.client.remoteevents.MapRemoteEvent;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

public class TestServer extends AbstractTestServer {

    private RemoteEventListener m_userSpecificListener;
    private RemoteEventListener m_domainListener;

    @Override
    public void addListener(Domain aDomain, RemoteEventListener aRemoteListener) {
        if(aDomain == null) {
            m_userSpecificListener = aRemoteListener;
        }else {
            m_domainListener = aRemoteListener;
        }
    }

    @Override
    public void start(AsyncCallback<Void> anAsyncCallback) {
        anAsyncCallback.onSuccess(null);
    }

    public void sendUserSpecificEvent( MapRemoteEvent remoteEvent ) {
        m_userSpecificListener.apply(remoteEvent);
    }
    
    public void sendDomainEvent( MapRemoteEvent remoteEvent) {
        m_domainListener.apply(remoteEvent);
    }

}
