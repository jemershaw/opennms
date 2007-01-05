//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2006 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
package org.opennms.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opennms.web.command.DistributedStatusDetailsCommand;
import org.opennms.web.svclayer.DistributedStatusService;
import org.opennms.web.svclayer.SimpleWebTable;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class DistributedStatusDetailsController extends AbstractCommandController {
    
    private DistributedStatusService m_distributedStatusService;
    private String m_successView;

    @Override
    protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        DistributedStatusDetailsCommand cmd = (DistributedStatusDetailsCommand) command;
        SimpleWebTable table = m_distributedStatusService.createStatusTable(cmd, errors);
        return new ModelAndView(getSuccessView(), "webTable", table);
    }

    public DistributedStatusService getDistributedStatusService() {
        return m_distributedStatusService;
    }

    public void setDistributedStatusService(DistributedStatusService statusService) {
        m_distributedStatusService = statusService;
    }

    public String getSuccessView() {
        return m_successView;
    }

    public void setSuccessView(String successView) {
        m_successView = successView;
    }
}
