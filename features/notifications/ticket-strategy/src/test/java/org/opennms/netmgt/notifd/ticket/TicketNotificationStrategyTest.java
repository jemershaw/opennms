/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.notifd.ticket;

import org.junit.Before;
import org.junit.Test;
import org.opennms.core.test.MockLogAppender;
import org.opennms.netmgt.dao.mock.MockEventDao;
import org.opennms.netmgt.dao.mock.MockEventIpcManager;
import org.opennms.netmgt.dao.mock.MockTransactionTemplate;
import org.opennms.netmgt.events.api.EventConstants;
import org.opennms.netmgt.events.api.EventIpcManagerFactory;
import org.opennms.netmgt.model.OnmsAlarm;
import org.opennms.netmgt.model.OnmsEvent;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.model.notifd.Argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Basic test cases for the TicketNotificationStrategy.
 *
 * @author <a href="mailto:jwhite@datavalet.com">Jesse White</a>
 * @version $Id: $
 */
public class TicketNotificationStrategyTest {

    private MockEventIpcManager m_eventIpcManager;
    private MockEventDao m_eventDao;
    private TicketNotificationStrategy m_ticketNotificationStrategy;

    @Before
    public void setUp() throws Exception {
        m_eventIpcManager = new MockEventIpcManager();
        m_eventIpcManager.setSynchronous(true);
        EventIpcManagerFactory.setIpcManager(m_eventIpcManager);

        m_eventDao = new MockEventDao();

        MockTransactionTemplate transactionTemplate = new MockTransactionTemplate();
        transactionTemplate.afterPropertiesSet();

        MockLogAppender.setupLogging();
        m_ticketNotificationStrategy = new TicketNotificationStrategy();
        m_ticketNotificationStrategy.setEventForwarder(m_eventIpcManager);
        m_ticketNotificationStrategy.setEventDao(m_eventDao);
        m_ticketNotificationStrategy.setTransactionTemplate(transactionTemplate);
    }

    @Test
    public void testNoticeWithNoEventID() {
        assertEquals("Strategy should fail if no event id is given.", 1,
                m_ticketNotificationStrategy.send(Collections.emptyList()));
    }

    @Test
    public void testNoticeWithNoAlarmID() {
        final OnmsEvent event = new OnmsEvent();
        event.setId(1);
        event.setEventUei(EventConstants.NODE_DOWN_EVENT_UEI);
        m_eventDao.save(event);

        final List<Argument> arguments = buildArguments(event);
        assertEquals("Strategy should fail silently if the event has no alarm id.", 0, m_ticketNotificationStrategy.send(arguments));
        assertTrue("Strategy should log a warning if the event has no alarm id.", !MockLogAppender.noWarningsOrHigherLogged());
    }

    @Test
    public void testCreateTicket() {
        // Setup the event anticipator
        EventBuilder newSuspectBuilder = new EventBuilder(EventConstants.TROUBLETICKET_CREATE_UEI, m_ticketNotificationStrategy.getClass().getName());
        newSuspectBuilder.setParam(EventConstants.PARM_ALARM_ID, "1");
        newSuspectBuilder.setParam(EventConstants.PARM_ALARM_UEI, EventConstants.NODE_DOWN_EVENT_UEI);
        newSuspectBuilder.setParam(EventConstants.PARM_USER, "admin");
        m_eventIpcManager.getEventAnticipator().anticipateEvent(newSuspectBuilder.getEvent());

        final OnmsEvent event = new OnmsEvent();
        event.setId(1);
        event.setEventUei(EventConstants.NODE_DOWN_EVENT_UEI);

        final OnmsAlarm alarm = new OnmsAlarm();
        alarm.setId(1);
        event.setAlarm(alarm);
        m_eventDao.save(event);

        List<Argument> arguments = buildArguments(event);
        assertEquals(0, m_ticketNotificationStrategy.send(arguments));
        assertTrue("Expected events not forthcoming", m_eventIpcManager.getEventAnticipator().waitForAnticipated(0).isEmpty());
        assertEquals("Received unexpected events", 0, m_eventIpcManager.getEventAnticipator().getUnanticipatedEvents().size());
    }

    private List<Argument> buildArguments(OnmsEvent event) {
        final List<Argument> arguments = new ArrayList<>();
        arguments.add(new Argument("eventID", null, event.getId().toString(), false));
        arguments.add(new Argument("eventUEI", null, event.getEventUei(), false));
        return arguments;
    }
}
