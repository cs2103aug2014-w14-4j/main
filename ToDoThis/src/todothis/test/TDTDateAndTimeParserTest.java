//@author A0110852R
package todothis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import todothis.commons.TDTDateAndTime;
import todothis.logic.parser.TDTDateAndTimeParser;

public class TDTDateAndTimeParserTest {
	private TDTDateAndTime test = new TDTDateAndTime();
	private TDTDateAndTimeParser testParser = new TDTDateAndTimeParser();
	
	@Test
	public void testDecodeDateAndTimeDetails() {
		//Checks if the date and time info are stored correctly in the right place
		
		//Deadline Task
		test = testParser.decodeDateAndTimeDetails("by 12/11/14 10pm");
		assertEquals(test.getStartDate(), "null");
		assertEquals(test.getStartTime(), "null");
		assertEquals(test.getEndDate(), "12/11/2014");
		assertEquals(test.getEndTime(), "22:00");
		
		testParser =  new TDTDateAndTimeParser(); //resets the variable values
		test = testParser.decodeDateAndTimeDetails("before 2345hr");
		assertEquals(test.getStartDate(), "null");
		assertEquals(test.getStartTime(), "null");
		assertEquals(test.getEndDate(), "null");
		assertEquals(test.getEndTime(), "23:45");
		
		testParser =  new TDTDateAndTimeParser(); 
		test = testParser.decodeDateAndTimeDetails("due 13/12");
		assertEquals(test.getStartDate(), "null");
		assertEquals(test.getStartTime(), "null");
		assertEquals(test.getEndDate(), "13/12/2014");
		assertEquals(test.getEndTime(), "null");
		
		//Floating Task
		testParser =  new TDTDateAndTimeParser(); 
		test = testParser.decodeDateAndTimeDetails("");
		assertEquals(test.getStartDate(), "null");
		assertEquals(test.getStartTime(), "null");
		assertEquals(test.getEndDate(), "null");
		assertEquals(test.getEndTime(), "null");
		
		//Timed Task
		testParser =  new TDTDateAndTimeParser(); 
		test = testParser.decodeDateAndTimeDetails("10/11/14 from 1200h - 1400hr");
		assertEquals(test.getStartDate(), "10/11/2014");
		assertEquals(test.getStartTime(), "12:00");
		assertEquals(test.getEndDate(), "10/11/2014");
		assertEquals(test.getEndTime(), "14:00");
		
		testParser =  new TDTDateAndTimeParser(); 
		test = testParser.decodeDateAndTimeDetails("from 1200h - 1400hr on 12122014");
		assertEquals(test.getStartDate(), "12/12/2014");
		assertEquals(test.getStartTime(), "12:00");
		assertEquals(test.getEndDate(), "12/12/2014");
		assertEquals(test.getEndTime(), "14:00");
		
		testParser =  new TDTDateAndTimeParser(); 
		test = testParser.decodeDateAndTimeDetails("from 10/12 9am until 15/12 10pm");
		assertEquals(test.getStartDate(), "10/12/2014");
		assertEquals(test.getStartTime(), "9:00");
		assertEquals(test.getEndDate(), "15/12/2014");
		assertEquals(test.getEndTime(), "22:00");
		
		testParser =  new TDTDateAndTimeParser(); 
		test = testParser.decodeDateAndTimeDetails("on 11/12/14 9.30pm");
		assertEquals(test.getStartDate(), "11/12/2014");
		assertEquals(test.getStartTime(), "21:30");
		assertEquals(test.getEndDate(), "null");
		assertEquals(test.getEndTime(), "null");
	}

}
