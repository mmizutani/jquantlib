/*
 Copyright (C) 2008 

 This source code is release under the BSD License.
 
 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the JQuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 
 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */

package org.jquantlib.testsuite.calendars;

import static org.jquantlib.util.Month.JANUARY;
import static org.jquantlib.util.Month.FEBRUARY;
import static org.jquantlib.util.Month.MARCH;
import static org.jquantlib.util.Month.APRIL;
import static org.jquantlib.util.Month.MAY;
import static org.jquantlib.util.Month.JUNE;
import static org.jquantlib.util.Month.JULY;
import static org.jquantlib.util.Month.AUGUST;
import static org.jquantlib.util.Month.SEPTEMBER;
import static org.jquantlib.util.Month.OCTOBER;
import static org.jquantlib.util.Month.NOVEMBER;
import static org.jquantlib.util.Month.DECEMBER;

import java.util.List;
import java.util.Vector;

import org.jquantlib.time.Calendar;
import org.jquantlib.time.calendars.Indonesia;
import org.jquantlib.util.Date;
import org.jquantlib.util.DateFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 
 *
 *
 */

public class IndonesiaCalendarTest {

	public IndonesiaCalendarTest() {
		System.out.println("\n\n::::: "+this.getClass().getSimpleName()+" :::::");
	}
	
	@Test
    public void testIndonesia() {
                       
    	Calendar c = Indonesia.getCalendar(Indonesia.Market.BEJ);
    	
        
		// 2007 
        testIndonesiaYear2007(c);
		
		// 2006 
        testIndonesiaYear2006(c);
		
		// 2005 
        testIndonesiaYear2005(c);
		
		
    }
	// 2007 
	
	void testIndonesiaYear2007(Calendar c) {
      	int year = 2007;
      	System.out.println("Testing Indonesia's holiday list for the year " + year + "...");
    	List<Date> expectedHol = new Vector<Date>();
		//New Year 
		expectedHol.add(DateFactory.getFactory().getDate(1,JANUARY,year)); 
    	
		// Nyepi
		expectedHol.add(DateFactory.getFactory().getDate(19,MARCH,year));
		
		//Good Friday
		expectedHol.add(DateFactory.getFactory().getDate(6,APRIL,year));
		
		//Ascension Thursday
		expectedHol.add(DateFactory.getFactory().getDate(17,MAY,year)); 
		
		// National leaves
		expectedHol.add(DateFactory.getFactory().getDate(18,MAY,year)); 
		
		// Waisak
		expectedHol.add(DateFactory.getFactory().getDate(1,JUNE,year)); 
		
		// Independence Day
		expectedHol.add(DateFactory.getFactory().getDate(17,AUGUST,year)); 
		
		// National leaves
		expectedHol.add(DateFactory.getFactory().getDate(12,OCTOBER,year)); 
		expectedHol.add(DateFactory.getFactory().getDate(15,OCTOBER,year)); 
		expectedHol.add(DateFactory.getFactory().getDate(16,OCTOBER,year)); 
		expectedHol.add(DateFactory.getFactory().getDate(24,OCTOBER,year)); 
		
		// Ied Adha
		expectedHol.add(DateFactory.getFactory().getDate(20,DECEMBER,year)); 
		
		// Christmas
		expectedHol.add(DateFactory.getFactory().getDate(25,DECEMBER,year)); 
		
        // Call the Holiday Check
        CalendarUtil cbt = new CalendarUtil();
    	cbt.checkHolidayList(expectedHol, c, year);
    }
    // 2006 
	
	void testIndonesiaYear2006(Calendar c) {
      	int year = 2006;
      	System.out.println("Testing Indonesia's holiday list for the year " + year + "...");
    	List<Date> expectedHol = new Vector<Date>();
		//New Year -- weekend in yr 2006 
		//expectedHol.add(DateFactory.getFactory().getDate(1,JANUARY,year)); 
    	
		// Idul Adha
		expectedHol.add(DateFactory.getFactory().getDate(10,JANUARY,year)); 
		
		// Moslem's New Year Day
		expectedHol.add(DateFactory.getFactory().getDate(31,JANUARY,year)); 
		
		// Nyepi
		expectedHol.add(DateFactory.getFactory().getDate(30,MARCH,year));
		
		// Birthday of Prophet Muhammad SAW
		expectedHol.add(DateFactory.getFactory().getDate(10,APRIL,year));
		
		//Good Friday
		expectedHol.add(DateFactory.getFactory().getDate(14,APRIL,year));
		
		//Ascension Thursday
		expectedHol.add(DateFactory.getFactory().getDate(25,MAY,year)); 
		
		// Independence Day
		expectedHol.add(DateFactory.getFactory().getDate(17,AUGUST,year)); 
		
		// Ascension of Prophet Muhammad SAW
		expectedHol.add(DateFactory.getFactory().getDate(21,AUGUST,year)); 
		
		// National leaves
		expectedHol.add(DateFactory.getFactory().getDate(23,OCTOBER,year)); 
		
		// Idul Fitri
		expectedHol.add(DateFactory.getFactory().getDate(24,OCTOBER,year)); 
		expectedHol.add(DateFactory.getFactory().getDate(25,OCTOBER,year)); 
		
		//National Leaves
		expectedHol.add(DateFactory.getFactory().getDate(26,OCTOBER,year)); 
		expectedHol.add(DateFactory.getFactory().getDate(27,OCTOBER,year)); 
						
		// Christmas
		expectedHol.add(DateFactory.getFactory().getDate(25,DECEMBER,year)); 
        // Call the Holiday Check
        CalendarUtil cbt = new CalendarUtil();
    	cbt.checkHolidayList(expectedHol, c, year);
    }
	// 2005 
	
	void testIndonesiaYear2005(Calendar c) {
      	int year = 2005;
      	System.out.println("Testing Indonesia's holiday list for the year " + year + "...");
    	List<Date> expectedHol = new Vector<Date>();
		//New Year -- weekend in yr 2005
		//expectedHol.add(DateFactory.getFactory().getDate(1,JANUARY,year)); 
    	
		// Idul Adha
		expectedHol.add(DateFactory.getFactory().getDate(21,JANUARY,year)); 
		
		// Imlek
		expectedHol.add(DateFactory.getFactory().getDate(9,FEBRUARY,year)); 
		
		// Moslem's New Year Day
		expectedHol.add(DateFactory.getFactory().getDate(10,FEBRUARY,year)); 
		
		// Nyepi
		expectedHol.add(DateFactory.getFactory().getDate(11,MARCH,year));
		
		//Good Friday
		expectedHol.add(DateFactory.getFactory().getDate(25,MARCH,year));
		
		
		// Birthday of Prophet Muhammad SAW
		expectedHol.add(DateFactory.getFactory().getDate(22,APRIL,year));
		
		//Ascension Thursday
		expectedHol.add(DateFactory.getFactory().getDate(5,MAY,year)); 
		
		// Waisak
		expectedHol.add(DateFactory.getFactory().getDate(24,MAY,year));
		
		
		
		
		// Independence Day
		expectedHol.add(DateFactory.getFactory().getDate(17,AUGUST,year)); 
		
		// Ascension of Prophet Muhammad SAW
		expectedHol.add(DateFactory.getFactory().getDate(2,SEPTEMBER,year)); 
		
		//National Leaves
		expectedHol.add(DateFactory.getFactory().getDate(2,NOVEMBER,year)); 
		
		// Idul Fitri
		expectedHol.add(DateFactory.getFactory().getDate(3,NOVEMBER,year)); 
		expectedHol.add(DateFactory.getFactory().getDate(4,NOVEMBER,year)); 
		
		//National Leaves
		expectedHol.add(DateFactory.getFactory().getDate(7,NOVEMBER,year)); 
		expectedHol.add(DateFactory.getFactory().getDate(8,NOVEMBER,year)); 
						
		// Christmas -- weekend in yr 2005
		//expectedHol.add(DateFactory.getFactory().getDate(25,DECEMBER,year)); 
		
		//National Leaves
		expectedHol.add(DateFactory.getFactory().getDate(26,DECEMBER,year)); 
        // Call the Holiday Check
        CalendarUtil cbt = new CalendarUtil();
    	cbt.checkHolidayList(expectedHol, c, year);
    }
	
}
