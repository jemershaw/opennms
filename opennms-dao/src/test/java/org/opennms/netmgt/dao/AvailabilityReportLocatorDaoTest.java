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
package org.opennms.netmgt.dao;

import java.util.Date;

import org.opennms.netmgt.model.AvailabilityReportLocator;

public class AvailabilityReportLocatorDaoTest extends AbstractDaoTestCase {

    public void setUp() throws Exception {
        //setPopulate(false);
        super.setUp();
    }
    
	/*private AvailabilityReportLocatorDao m_availabilityReportLocatorDao;
        
        public AvailabilityReportLocatorDaoTest() throws MarshalException, ValidationException, IOException, PropertyVetoException, SQLException {
            
             * Note: I'm using the opennms-database.xml file in target/classes/etc
             * so that it has been filtered first.
             
            DataSourceFactory.setInstance(new C3P0ConnectionFactory("../opennms-daemon/target/classes/etc/opennms-database.xml"));
        }

	public void setAvailabilityReportLocatorDao(AvailabilityReportLocatorDao availabilityReportLocatorDao) {
		m_availabilityReportLocatorDao = availabilityReportLocatorDao;
	}*/
	
	public void testBogus() {
		// do nothing... we're here so JUnit doesn't complain
	}

	public void testFindAll() {
		
		System.out.println("going for the report locator");
		AvailabilityReportLocator locator = new AvailabilityReportLocator();
		System.out.println("got the report locator");
		locator.setAvailable(true);
		locator.setCategory("cat1");
		locator.setDate(new Date());
		locator.setFormat("HTML");
		locator.setType("Random String");
		locator.setLocation("down the back of the sofa");
		
		getAvailabilityReportLocatorDao().save(locator);
		
		
		AvailabilityReportLocator retrieved = getAvailabilityReportLocatorDao().get(locator.getId());
		
		assertEquals(retrieved.getId(), locator.getId());
		assertEquals(retrieved.getAvailable(), locator.getAvailable());
		assertEquals(retrieved.getCategory(), locator.getCategory());
	}
	
}
