//@author A0110852R
package todothis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import todothis.commons.TDTDateMethods;
/**
 * This class tests methods in TDTDateMethods class.
 *
 */

public class TDTDateMethodsTest {
	@Test
	public void testCheckDate(){
		//Check for date format: DDMMYY
		
		//This is boundary case for date of length 6
		assertTrue(TDTDateMethods.checkDate("120814")); 
		//This is boundary case for date of length more than 6
		assertFalse(TDTDateMethods.checkDate("1208143")); 
		 //This is boundary case for date of length less than 6
		assertFalse(TDTDateMethods.checkDate("12814"));
		//This is case when date consist of not all digits
		assertFalse(TDTDateMethods.checkDate("2a0814")); 
		
		//Check for date format: DDMMYYYY 
		//Similar to DDMMYY testing OMITTED
		
		//Check for date format: DD-MM-YYYY, DD-MM
		
		 //This is a case that follows the date format
		assertTrue(TDTDateMethods.checkDate("12-2-14"));
		//This is a case that follows the date format
		assertTrue(TDTDateMethods.checkDate("12-1")); 
		//This is a boundary case for having more than 2 dashes
		assertFalse(TDTDateMethods.checkDate("12-1-14-1")); 
		//this is a case when date consist of not all digits
		assertFalse(TDTDateMethods.checkDate("1a-12-14")); 
		
		//Check for date format: DD/MM/YYYY, DD/MM
		//Similar to DD-MM-YYYY OMITTED
	}
	
	@Test
	public void testAddDaysToDate(){
		//This cases test adding and subtracting of less than 31 days within the month
		assertEquals(TDTDateMethods.addDaysToCurrentDate(10, 11, 2014, 10), "20/11/2014");
		assertEquals(TDTDateMethods.addDaysToCurrentDate(10, 11, 2014, -9), "1/11/2014");
		
		//This cases test adding and subtracting of more than 31 days
		assertEquals(TDTDateMethods.addDaysToCurrentDate(10, 11, 2014, 30), "10/12/2014");
		assertEquals(TDTDateMethods.addDaysToCurrentDate(10, 11, 2014, -30), "11/10/2014");
		
		//This cases test adding and subtracting of days over the year
		assertEquals(TDTDateMethods.addDaysToCurrentDate(31, 12, 2014, 2), "2/1/2015");
		assertEquals(TDTDateMethods.addDaysToCurrentDate(1, 1, 2014, -2), "30/12/2013");
	}
	
	@Test 
	public void testChangeToDayOfWeek(){
		assertEquals(TDTDateMethods.changeToDayOfWeek("10/11/2014"),"Mon");
	}
	@Test 
	public void testChangeDateFormat(){
		assertEquals(TDTDateMethods.changeDateFormat("01/11/2014"),"1/11/2014");
	}
	@Test 
	public void testChangeDateFormatDisplay(){
		assertEquals(TDTDateMethods.changeDateFormatDisplay("01/11/2014"),"1 Nov 2014");
	}
	@Test
	public void testValidDateRange(){
		//This is boundary case for year lesser than 2014 partition
		assertFalse(TDTDateMethods.isValidDateRange("12/12/2013"));
		//This is boundary case for year within range partition
		assertTrue(TDTDateMethods.isValidDateRange("12/12/2050"));
		//This is boundary case for year more than 2099 partition
		assertFalse(TDTDateMethods.isValidDateRange("12/12/2100"));
		
		//This is boundary case for month lesser than 1 partition
		assertFalse(TDTDateMethods.isValidDateRange("12/0/2014"));
		//This is boundary case for month within range partition
		assertTrue(TDTDateMethods.isValidDateRange("12/8/2014"));
		//This is boundary case for month more than 12 partition
		assertFalse(TDTDateMethods.isValidDateRange("12/13/2014"));
		
		//This is boundary case for day lesser than 1 partition
		assertFalse(TDTDateMethods.isValidDateRange("0/8/2014"));
		//This is boundary case for day within range partition
		assertTrue(TDTDateMethods.isValidDateRange("12/8/2014"));
		//This is boundary case for day more than 31 partition
		assertFalse(TDTDateMethods.isValidDateRange("32/8/2014"));
		
		//This is case when date is not initialized
		assertTrue(TDTDateMethods.isValidDateRange("null"));
		//This is case when date has unwanted non digits
		assertFalse(TDTDateMethods.isValidDateRange("a31/8/2014")); 
		
	}
	@Test
	public void testCompareToDate(){
		//This are cases that test within the same month
		assertEquals(TDTDateMethods.compareToDate("10/11/2014", "10/11/2014"), 0);
		assertEquals(TDTDateMethods.compareToDate("10/11/2014", "11/11/2014"), 1);
		assertEquals(TDTDateMethods.compareToDate("11/11/2014", "10/11/2014"), -1);
		
		//This are cases that test within the same year
		assertEquals(TDTDateMethods.compareToDate("10/10/2014", "11/11/2014"), 1);
		assertEquals(TDTDateMethods.compareToDate("1/12/2014", "10/11/2014"), -1);
		
		//This are cases that test dates of different years
		assertEquals(TDTDateMethods.compareToDate("10/10/2013", "11/11/2014"), 1);
		assertEquals(TDTDateMethods.compareToDate("1/12/2019", "10/11/2014"), -1);
	}
}
