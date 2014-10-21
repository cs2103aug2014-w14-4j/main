package todothis.test;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import todothis.logic.TDTDateAndTime;

public class TDTDateAndTimeTest {

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
		taskList[9] = new TDTDateAndTime("14/10/2014", "14/10/2014", "13:55", "13:59");
		taskList[10] = new TDTDateAndTime("14/10/2014", "15/10/2014", "13:59", "01:00");
		taskList[11] = new TDTDateAndTime("12/9/2014", "15/10/2014", "12:00", "01:00");
		taskList[12] = new TDTDateAndTime("12/9/2014", "12/9/2014", "12:01", "12:02");
		taskList[13] = new TDTDateAndTime("28/9/2014", "01/10/2014", "10:00", "12:00");
		taskList[14] = new TDTDateAndTime("28/9/2012", "01/10/2014", "10:00", "12:00");
		taskList[15] = new TDTDateAndTime("05/05/2015", "06/05/2015", "10:40", "13:22");
		taskList[16] = new TDTDateAndTime("28/9/2014", "01/10/2015", "10:00", "12:00");
		
		
//		assertTrue(taskList[0].isClash(taskList[2]));
//		assertTrue(taskList[0].isClash(taskList[4]));
//		assertFalse(taskList[0].isClash(taskList[3]));
//		assertTrue(taskList[0].isClash(taskList[5]));
//		assertFalse(taskList[5].isClash(taskList[6])); 
//		assertTrue(taskList[7].isClash(taskList[8]));
//		assertTrue(taskList[7].isClash(taskList[9]));
//		assertFalse(taskList[8].isClash(taskList[10]));
//		assertTrue(taskList[10].isClash(taskList[11]));
//		assertTrue(taskList[11].isClash(taskList[12]));
//		assertTrue(taskList[11].isClash(taskList[13]));
//		assertTrue(taskList[13].isClash(taskList[14]));
		assertTrue(taskList[16].isClash(taskList[15]));
	}
}
