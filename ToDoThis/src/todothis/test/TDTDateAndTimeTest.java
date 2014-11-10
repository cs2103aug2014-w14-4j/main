//@author A0110852R
package todothis.test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import todothis.commons.TDTDateAndTime;

/**
 * 
 * This class tests the TDTDateAndTime class methods.
 *
 */
public class TDTDateAndTimeTest {
	// As this method makes use of the current date and time, this test will
	// only pass at the specific time of testing
	//@Test
	public void testOverDue(){
		ArrayList<TDTDateAndTime> taskList = new ArrayList<TDTDateAndTime>();
		// TDTDateAndTime(String startDate, String endDate, String startTime, String endTime)
		//Test Done on 22/10/2014 5pm
		
		//Testing deadline task
		taskList.add(new TDTDateAndTime("null", "22/10/2014", "null", "16:59"));
		//This is the boundary case for dueTime before currentTime partition
		assertTrue(taskList.get(0).isOverdue()); 
		
		taskList.add(new TDTDateAndTime("null", "22/10/2014", "null", "17:01")); 
		//This is the boundary case for dueTime after currentTime partition
		assertFalse(taskList.get(1).isOverdue()); 
		
		taskList.add(new TDTDateAndTime("null", "22/10/2014", "null", "17:00")); 
		//This is the boundary case for dueTime on currentTime partition
		assertFalse(taskList.get(2).isOverdue()); 
		
		taskList.add(new TDTDateAndTime("null", "21/10/2014", "null", "null")); 
		//This is the boundary case for dueDate before currentDate partition
		assertTrue(taskList.get(3).isOverdue()); 
		
		taskList.add(new TDTDateAndTime("null", "23/10/2014", "null", "null")); 
		//This is the boundary case for dueDate after currentDate partition
		assertFalse(taskList.get(4).isOverdue()); 
		
		taskList.add(new TDTDateAndTime("null", "22/10/2014", "null", "null")); 
		//This is the boundary case for dueDate on currentDate partition
		assertFalse(taskList.get(5).isOverdue()); 
		
		taskList.add(new TDTDateAndTime("null", "null", "null", "15:00")); 
		//This is the case where only dueTime is keyed, taken care the whole partition
		assertFalse(taskList.get(6).isOverdue()); 
		
		
		//Testing timed task Eg: on a single day with a start and end time
		taskList.add(new TDTDateAndTime("22/10/2014", "null", "11:00", "16:59")); 
		//This is the boundary case for endTime before currentTime partition
		assertTrue(taskList.get(7).isOverdue()); 
		
		taskList.add(new TDTDateAndTime("22/10/2014", "null", "11:00", "17:01")); 
		//This is the boundary case for endTime after currentTime partition
		assertFalse(taskList.get(8).isOverdue());
		
		taskList.add(new TDTDateAndTime("22/10/2014", "null", "11:00", "17:00")); 
		//This is the boundary case for endTime on currentTime partition
		assertFalse(taskList.get(9).isOverdue()); 
		
		
		//Testing timed task Eg: a span of more than 1 day with a start and end time
		taskList.add(new TDTDateAndTime("20/10/2014", "21/10/2014", "11:00", "16:59")); 
		//This is the boundary case for endDate before currentDate partition
		assertTrue(taskList.get(10).isOverdue()); 
		
		taskList.add(new TDTDateAndTime("20/10/2014", "22/10/2014", "11:00", "16.59")); 
		//This is the boundary case for endDate same currentDate partition, endTime is checked
		assertTrue(taskList.get(11).isOverdue()); 
		
		taskList.add(new TDTDateAndTime("20/10/2014", "23/10/2014", "11:00", "17:00")); 
		//This is the boundary case for endDate after currentDate partition
		assertFalse(taskList.get(12).isOverdue()); 
		
	}
	
	@Test
	public void testCompareTo(){
		int numTask = 100;
		TDTDateAndTime[] taskList = new TDTDateAndTime[numTask];
		
		//compareTo 2 timed tasks
		//tasks with same start date but different or same timing
		taskList[0] = new TDTDateAndTime("13/10/2014", "13/10/2014", "18:00", "20:00");
		taskList[1] = new TDTDateAndTime("13/10/2014", "15/10/2014", "18:00", "22:00");
		assertEquals(taskList[0].compareTo(taskList[1]), 0);
		
		taskList[2] = new TDTDateAndTime("13/10/2014", "13/10/2014", "18:04", "18:05");
		taskList[3] = new TDTDateAndTime("13/10/2014", "14/10/2014", "12:00", "14:00");
		assertEquals(taskList[2].compareTo(taskList[3]), 1);
		
		taskList[4] = new TDTDateAndTime("13/10/2014", "14/10/2014", "12:59", "13:59");
		taskList[5] = new TDTDateAndTime("13/10/2014", "14/10/2014", "23:58", "13:59");
		assertEquals(taskList[4].compareTo(taskList[5]), -1);
		
		//tasks with different start date, time doesn't matter
		taskList[6] = new TDTDateAndTime("15/10/2014", "20/10/2014", "19:00", "22:00");
		taskList[7] = new TDTDateAndTime("13/10/2014", "15/10/2015", "18:00", "20:00");
		assertEquals(taskList[6].compareTo(taskList[7]), 1);
		
		taskList[8] = new TDTDateAndTime("12/10/2014", "13/10/2014", "19:00", "21:00");
		taskList[9] = new TDTDateAndTime("13/10/2014", "13/10/2014", "18:05", "19:55");
		assertEquals(taskList[8].compareTo(taskList[9]), -1);
		
		//compareTo 2 deadline tasks of same date
		taskList[10] = new TDTDateAndTime("null", "15/10/2014", "null", "02:00");
		taskList[11] = new TDTDateAndTime("null", "15/10/2014", "null", "01:00");
		assertEquals(taskList[10].compareTo(taskList[11]), 1);
		
		taskList[12] = new TDTDateAndTime("null", "15/10/2014", "null", "02:00");
		taskList[13] = new TDTDateAndTime("null", "15/10/2014", "null", "09:00");
		assertEquals(taskList[12].compareTo(taskList[13]), -1);
		
		//compareTo a timed task and a deadline task
		taskList[14] = new TDTDateAndTime("null", "15/10/2014", "null", "02:00");
		taskList[15] = new TDTDateAndTime("14/10/2014", "15/10/2014", "01:00", "09:00");
		assertEquals(taskList[14].compareTo(taskList[15]), 1);
		
		taskList[16] = new TDTDateAndTime("null", "15/10/2014", "null", "02:00");
		taskList[17] = new TDTDateAndTime("16/10/2014", "20/10/2014", "01:00", "09:00");
		assertEquals(taskList[16].compareTo(taskList[17]), -1);
		
		//compareTo a timed task and a floating task
		taskList[16] = new TDTDateAndTime("null", "null", "null", "null");
		taskList[17] = new TDTDateAndTime("16/10/2014", "21/10/2014", "01:00", "09:00");
		assertEquals(taskList[16].compareTo(taskList[17]), 1);
		
		//compareTo a deadline task and a floating task
		taskList[18] = new TDTDateAndTime("null", "null", "null", "null");
		taskList[19] = new TDTDateAndTime("null", "21/10/2014", "null", "09:00");
		assertEquals(taskList[18].compareTo(taskList[19]), 1);
	}
	//@author A0111211L
	@Test
	public void testClash() {
		int numTask = 100;
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
		
		/* This is a boundary case for the 'is not clash' partition */
		assertFalse(taskList[0].isClash(taskList[1])); 
		/* This is a case for the 'is clash' partition, where the time intersects in the middle */
		assertTrue(taskList[0].isClash(taskList[4]));
		/* test for different year */
		assertFalse(taskList[0].isClash(taskList[3])); 
		/* This is a boundary case for the 'is clash' partition where the time overlaps by 5mins */
		assertTrue(taskList[0].isClash(taskList[5])); 
		/* This is a boundary case for the 'is not clash' partition where the time difference is 1min */
		assertFalse(taskList[5].isClash(taskList[6])); 
		/* overlaps time except for last 1min */
		assertTrue(taskList[7].isClash(taskList[8])); 
		/* This is a boundary case for the 'is clash' partition where the time overlaps by 1min */
		assertTrue(taskList[7].isClash(taskList[9])); 
		/* This is a boundary case for the 'is clash' partition where the time overlaps except for 1min */
		assertTrue(taskList[11].isClash(taskList[12])); 
		/* test whether smaller time range is able to call isClash method */
		assertTrue(taskList[15].isClash(taskList[16])); 
	}
}
