package todothis.test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import todothis.dateandtime.TDTDateAndTime;
import todothis.dateandtime.TDTDateMethods;
import todothis.dateandtime.TDTTimeMethods;

public class TDTDateAndTimeTest {
	@Test
	public void testValidTimeRange(){
		//24 hrs format
		assertFalse(TDTTimeMethods.isValidTimeRange("24:00"));//This is boundary case for hours above value 23 partition
		assertTrue(TDTTimeMethods.isValidTimeRange("10:00"));//This is boundary case for hours within the range partition
		assertFalse(TDTTimeMethods.isValidTimeRange("-1:00"));//This is boundary case for hours in negative value partition
		
		assertFalse(TDTTimeMethods.isValidTimeRange("23:60"));//This is boundary case for minutes above value 59 partition
		assertTrue(TDTTimeMethods.isValidTimeRange("23:34"));//This is boundary case for minutes within the range partition
		assertFalse(TDTTimeMethods.isValidTimeRange("23:-1"));//This is boundary case for minutes in negative value partition
		
		assertTrue(TDTTimeMethods.isValidTimeRange("null"));//This is case when start or end time is not intialised
	}
	
	@Test
	public void testValidDateRange(){
		
		assertFalse(TDTDateMethods.isValidDateRange("12/12/2013"));//This is boundary case for year lesser than 2014 partition
		assertTrue(TDTDateMethods.isValidDateRange("12/12/2050"));//This is boundary case for year within range partition
		assertFalse(TDTDateMethods.isValidDateRange("12/12/2100"));//This is boundary case for year more than 2099 partition
		
		assertFalse(TDTDateMethods.isValidDateRange("12/0/2014"));//This is boundary case for month lesser than 1 partition
		assertTrue(TDTDateMethods.isValidDateRange("12/8/2014"));//This is boundary case for month within range partition
		assertFalse(TDTDateMethods.isValidDateRange("12/13/2014"));//This is boundary case for month more than 12 partition
		
		assertFalse(TDTDateMethods.isValidDateRange("0/8/2014"));//This is boundary case for day lesser than 1 partition
		assertTrue(TDTDateMethods.isValidDateRange("12/8/2014"));//This is boundary case for day within range partition
		assertFalse(TDTDateMethods.isValidDateRange("32/8/2014"));//This is boundary case for day more than 31 partition
		
		assertTrue(TDTDateMethods.isValidDateRange("null"));//This is case when date is not intialised
		
		assertFalse(TDTDateMethods.isValidDateRange("a31/8/2014")); //This is case when date has unwanted non digits
		
	}
	
	//@Test
	public void testOverDue(){
		ArrayList<TDTDateAndTime> taskList = new ArrayList<TDTDateAndTime>();
		// TDTDateAndTime(String startDate, String endDate, String startTime, String endTime)
		//Test Done on 22/10/2014 5pm
		
		//Testing deadline task
		taskList.add(new TDTDateAndTime("null", "22/10/2014", "null", "16:59")); 
		assertTrue(taskList.get(0).isOverdue()); //This is the boundary case for dueTime before currentTime partition
		taskList.add(new TDTDateAndTime("null", "22/10/2014", "null", "17:01")); 
		assertFalse(taskList.get(1).isOverdue()); //This is the boundary case for dueTime after currentTime partition
		taskList.add(new TDTDateAndTime("null", "22/10/2014", "null", "17:00")); 
		assertFalse(taskList.get(2).isOverdue()); //This is the boundary case for dueTime on currentTime partition
		
		taskList.add(new TDTDateAndTime("null", "21/10/2014", "null", "null")); 
		assertTrue(taskList.get(3).isOverdue()); //This is the boundary case for dueDate before currentDate partition
		taskList.add(new TDTDateAndTime("null", "23/10/2014", "null", "null")); 
		assertFalse(taskList.get(4).isOverdue()); //This is the boundary case for dueDate after currentDate partition
		taskList.add(new TDTDateAndTime("null", "22/10/2014", "null", "null")); 
		assertFalse(taskList.get(5).isOverdue()); //This is the boundary case for dueDate on currentDate partition
		
		taskList.add(new TDTDateAndTime("null", "null", "null", "15:00")); 
		assertFalse(taskList.get(6).isOverdue()); //This is the case where only dueTime is keyed, taken care the whole partition
		
		
		//Testing timed task Eg: on a single day with a start and end time
		taskList.add(new TDTDateAndTime("22/10/2014", "null", "11:00", "16:59")); 
		assertTrue(taskList.get(7).isOverdue()); //This is the boundary case for endTime before currentTime partition
		taskList.add(new TDTDateAndTime("22/10/2014", "null", "11:00", "17:01")); 
		assertFalse(taskList.get(8).isOverdue()); //This is the boundary case for endTime after currentTime partition
		taskList.add(new TDTDateAndTime("22/10/2014", "null", "11:00", "17:00")); 
		assertFalse(taskList.get(9).isOverdue()); //This is the boundary case for endTime on currentTime partition
		
		//Testing timed task Eg: a span of more than 1 day with a start and end time
		taskList.add(new TDTDateAndTime("20/10/2014", "21/10/2014", "11:00", "16:59")); 
		assertTrue(taskList.get(10).isOverdue()); //This is the boundary case for endDate before currentDate partition
		taskList.add(new TDTDateAndTime("20/10/2014", "22/10/2014", "11:00", "16.59")); 
		assertTrue(taskList.get(11).isOverdue()); //This is the boundary case for endDate same currentDate partition, endTime is checked
		taskList.add(new TDTDateAndTime("20/10/2014", "23/10/2014", "11:00", "17:00")); 
		assertFalse(taskList.get(12).isOverdue()); //This is the boundary case for endDate after currentDate partition
		
	}

	@Test
	public void testClash() {

		int numTask = 10000;
		TDTDateAndTime[] taskList = new TDTDateAndTime[numTask];

		taskList[0] = new TDTDateAndTime("13/10/2014", "13/10/2014", "18:00", "20:00");
		taskList[1] = new TDTDateAndTime("13/10/2014", "13/10/2014", "20:00", "22:00");
		taskList[2] = new TDTDateAndTime("13/10/2014", "13/10/2014", "19:00", "22:00");
		taskList[3] = new TDTDateAndTime("13/10/2015", "13/10/2015", "18:00", "20:00");
		taskList[4] = new TDTDateAndTime("13/10/2014", "13/10/2014", "19:00", "21:00");
		taskList[5] = new TDTDateAndTime("13/10/2014", "13/10/2014", "18:05", "19:55");
		taskList[6] = new TDTDateAndTime("13/10/2014", "13/10/2014", "18:04", "18:05");
		taskList[7] = new TDTDateAndTime("13/10/2014", "14/10/2014", "12:00", "14:00");
		taskList[8] = new TDTDateAndTime("13/10/2014", "14/10/2014", "23:59", "13:59");
		taskList[9] = new TDTDateAndTime("14/10/2014", "14/10/2014", "13:58", "13:59");
		taskList[10] = new TDTDateAndTime("14/10/2014", "15/10/2014", "13:59", "01:00");
		taskList[11] = new TDTDateAndTime("12/9/2014", "15/10/2014", "12:00", "01:00");
		taskList[12] = new TDTDateAndTime("12/9/2014", "12/9/2014", "12:01", "23:59");
		taskList[13] = new TDTDateAndTime("28/9/2014", "01/10/2014", "10:00", "12:00");
		taskList[14] = new TDTDateAndTime("28/9/2012", "01/10/2014", "10:00", "12:00");
		taskList[15] = new TDTDateAndTime("05/05/2015", "06/05/2015", "10:40", "13:22");
		taskList[16] = new TDTDateAndTime("28/9/2014", "01/10/2015", "10:00", "12:00");
		
		
		assertFalse(taskList[0].isClash(taskList[1])); /* This is a boundary case for the 'is not clash' partition */
		assertTrue(taskList[0].isClash(taskList[4])); /* This is a case for the 'is clash' partition, where the time intersects in the middle */
		assertFalse(taskList[0].isClash(taskList[3])); /* test for different year */
		assertTrue(taskList[0].isClash(taskList[5])); /* This is a boundary case for the 'is clash' partition where the time overlaps by 5mins */
		assertFalse(taskList[5].isClash(taskList[6])); /* This is a boundary case for the 'is not clash' partition where the time difference is 1min */
		assertTrue(taskList[7].isClash(taskList[8])); /* overlaps time except for last 1min */
		assertTrue(taskList[7].isClash(taskList[9])); /* This is a boundary case for the 'is clash' partition where the time overlaps by 1min */
//		assertFalse(taskList[8].isClash(taskList[10]));
//		assertTrue(taskList[10].isClash(taskList[11]));
		assertTrue(taskList[11].isClash(taskList[12])); /* This is a boundary case for the 'is clash' partition where the time overlaps except for 1min */
//		assertTrue(taskList[11].isClash(taskList[13]));
//		assertTrue(taskList[13].isClash(taskList[14]));
		assertTrue(taskList[15].isClash(taskList[16])); /* test whether smaller time range is able to call isClash method */
	}
}
