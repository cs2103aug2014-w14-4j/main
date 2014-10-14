package todothis;

import static org.junit.Assert.*;

import org.junit.Test;

public class TDTDateAndTimeTest {

	@Test
	public void testClash() {

		int numTask = 10;
		TDTDateAndTime[] taskList = new TDTDateAndTime[numTask];

		taskList[0] = new TDTDateAndTime("13/10/2014", "13/10/2014", "18:00", "20:00");
		taskList[1] = new TDTDateAndTime("13/10/2014", "13/10/2014", "20:00", "22:00");
		taskList[2] = new TDTDateAndTime("13/10/2014", "13/10/2014", "18:00", "22:00");
		taskList[3] = new TDTDateAndTime("13/10/2015", "13/10/2015", "18:00", "20:00");
		taskList[4] = new TDTDateAndTime("13/10/2014", "13/10/2014", "19:00", "21:00");
		

		// assertTrue(taskList[0].isClash(taskList[2]));
		// assertTrue(taskList[0].isClash(taskList[4]));
		assertFalse(taskList[0].isClash(taskList[3]));


	}
}
