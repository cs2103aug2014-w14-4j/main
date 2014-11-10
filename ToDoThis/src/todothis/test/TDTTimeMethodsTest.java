//@author @A0115933H
package todothis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import todothis.commons.TDTTimeMethods;

public class TDTTimeMethodsTest {

	@Test
	public void testValidTimeRange() {
		//24 hrs format
		//This is boundary case for hours above value 23 partition
		assertFalse(TDTTimeMethods.isValidTimeRange("24:00"));
		//This is boundary case for hours within the range partition
		assertTrue(TDTTimeMethods.isValidTimeRange("10:00"));
		//This is boundary case for hours in negative value partition
		assertFalse(TDTTimeMethods.isValidTimeRange("-1:00"));
		//This is boundary case for minutes above value 59 partition
		assertFalse(TDTTimeMethods.isValidTimeRange("23:60"));
		//This is boundary case for minutes within the range partition
		assertTrue(TDTTimeMethods.isValidTimeRange("23:34"));
		//This is boundary case for minutes in negative value partition
		assertFalse(TDTTimeMethods.isValidTimeRange("23:-1"));
		//This is case when start or end time is not initialized
		assertTrue(TDTTimeMethods.isValidTimeRange("null"));
	}
	
	@Test
	public void testChangeTimeFormatDisplay() {
		assertEquals(TDTTimeMethods.changeTimeFormatDisplay("00:00"), "12:00 AM");
		assertEquals(TDTTimeMethods.changeTimeFormatDisplay("12:00"), "12:00 PM");
		
	}
	
	@Test
	public void testCheckTime() {
		//24hours format
		assertTrue(TDTTimeMethods.checkTime("00:00"));  //Boundary case
		assertTrue(TDTTimeMethods.checkTime("13:00"));  //Within range partition
		assertFalse(TDTTimeMethods.checkTime("24:00")); //Boundary case
		assertTrue(TDTTimeMethods.checkTime("0000h")); 

		
		//24hours format with AM/PM
		assertTrue(TDTTimeMethods.checkTime("00:00AM"));  //Boundary case
		assertTrue(TDTTimeMethods.checkTime("00:00PM"));  //Boundary case
		assertFalse(TDTTimeMethods.checkTime("24:00PM")); //Boundary case
		assertFalse(TDTTimeMethods.checkTime("24:00AM")); //Boundary case
		assertFalse(TDTTimeMethods.checkTime("13:00AM")); //Boundary case
		assertTrue(TDTTimeMethods.checkTime("12:34PM"));  //Within range partition
		
		//12hours format with AM/PM
		assertFalse(TDTTimeMethods.checkTime("0AM"));  //Boundary case
		assertTrue(TDTTimeMethods.checkTime("8AM"));   //Within range partition
		assertFalse(TDTTimeMethods.checkTime("13PM")); //Boundary case
		
	}
	
	@Test
	public void testCompareToTime() {
		assertEquals(TDTTimeMethods.compareToTime("00:00", "12:00"), 1 );  
		assertEquals(TDTTimeMethods.compareToTime("12:00", "12:00"), 0 );  
		assertEquals(TDTTimeMethods.compareToTime("23:59", "00:00"), -1 );  
		
	}
	
	//As this method uses the current date and time, this test passed and 
	//will only pass at the specific time of testing.
	//@Test
	public void testCalculateRemainingTime() {
		// Test time : 4:10pm Test date: 10/11/14
		assertEquals(TDTTimeMethods.calculateRemainingTime("10/11/2014 16:11"), 60);  
		
		// Test time : 4:34pm Test date : 11/11/14
		assertEquals(TDTTimeMethods.calculateRemainingTime("11/11/2014 16:34"), 86400);	
	}
}
