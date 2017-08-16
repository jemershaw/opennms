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

import org.apache.commons.lang.StringUtils;
import org.opennms.netmgt.dao.api.EventDao;
import org.opennms.netmgt.events.api.EventConstants;
import org.opennms.netmgt.events.api.EventForwarder;
import org.opennms.netmgt.model.OnmsAlarm;
import org.opennms.netmgt.model.OnmsEvent;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.model.notifd.Argument;
import org.opennms.netmgt.model.notifd.NotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;

/**
 * Invoke the trouble ticketer using notifd instead of automations.
 * This allows tickets to be used in conjunction with path-outages and escalation paths.
 *
 * @author <a href="mailto:jwhite@datavlaet.com">Jesse White</a>
 * @version $Id: $
 */
public class TicketNotificationStrategy implements NotificationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(TicketNotificationStrategy.class);
    private EventForwarder m_eventForwarder;
    private EventDao m_eventDao;
    private TransactionOperations m_transactionTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public int send(List<Argument> arguments) {
        String eventID = null;

        // Pull the arguments we're interested in from the list.
        for (Argument arg : arguments) {
            LOG.debug("arguments: {} = {}", arg.getSwitch(), arg.getValue());
            if ("eventID".equalsIgnoreCase(arg.getSwitch())) {
                eventID = arg.getValue();
            }
        }

        // Make sure we have the arguments we need.
        if (StringUtils.isBlank(eventID)) {
            LOG.error("There is no event-id associated with the notice. Cannot create ticket.");
            return 1;
        }

        final int eventId = Integer.parseInt(eventID);
        return m_transactionTemplate.execute(status -> {
            final OnmsEvent event = m_eventDao.get(eventId);
            if (event == null) {
                LOG.error("No event found with id: {}. Aborting.", eventId);
                return 1;
            }

            final OnmsAlarm alarm = event.getAlarm();
            if (alarm == null) {
                LOG.warn("Event with id: {} is not associated with any alarm. Ignoring.", eventId);
                return 0;
            }

            // Log everything we know so far.
            LOG.info("Got event-id='{}' and alarm='{}'", eventId, alarm);
            sendCreateTicketEvent(alarm);
            return 0;
        });
    }

    public void setEventForwarder(EventForwarder eventForwarder) {
        m_eventForwarder = eventForwarder;
    }

    public void setEventDao(EventDao eventDao) {
        m_eventDao = eventDao;
    }

    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        m_transactionTemplate = transactionTemplate;
    }

    /**
     * <p>Helper function that sends the create ticket event</p>
     */
    private void sendCreateTicketEvent(OnmsAlarm alarm) {
        LOG.debug("Sending create ticket for alarm with UEI='{}' and id={}", alarm.getUei(), alarm.getId());
        EventBuilder ebldr = new EventBuilder(EventConstants.TROUBLETICKET_CREATE_UEI, getClass().getName());
        ebldr.addParam(EventConstants.PARM_ALARM_ID, alarm.getId());
        // These fields are required by the trouble ticketer, but not used
        ebldr.addParam(EventConstants.PARM_ALARM_UEI, alarm.getUei());
        ebldr.addParam(EventConstants.PARM_USER, "admin");
        m_eventForwarder.sendNow(ebldr.getEvent());
    }

}
