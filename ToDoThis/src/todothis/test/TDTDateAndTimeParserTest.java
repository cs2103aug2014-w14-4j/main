//@author A0110852R
package todothis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import todothis.commons.TDTDateAndTime;
import todothis.logic.parser.TDTDateAndTimeParser;

/**
 * 
 * This class tests the methods in TDTDateAndTimeParser.
 *
 */
public class TDTDateAndTimeParserTest {
	private TDTDateAndTime test = new TDTDateAndTime();
	private TDTDateAndTimeParser testParser = new TDTDateAndTimeParser();

	@Test
	public void testDecodeDateAndTimeDetails() {
		// Checks if the date and time info are stored correctly in the right
		// place

		// Deadline Task
		test = testParser.decodeDateAndTimeDetails("by 12/11/14 10pm");
		assertEquals(test.getStartDate(), "null");
		assertEquals(test.getStartTime(), "null");
		assertEquals(test.getEndDate(), "12/11/2014");
		assertEquals(test.getEndTime(), "22:00");

		testParser = new TDTDateAndTimeParser(); // resets the variable values
		test = testParser.decodeDateAndTimeDetails("before 2345hr");
		assertEquals(test.getStartDate(), "null");
		assertEquals(test.getStartTime(), "null");
		assertEquals(test.getEndDate(), "null");
		assertEquals(test.getEndTime(), "23:45");

		testParser = new TDTDateAndTimeParser();
		test = testParser.decodeDateAndTimeDetails("due 13/12");
		assertEquals(test.getStartDate(), "null");
		assertEquals(test.getStartTime(), "null");
		assertEquals(test.getEndDate(), "13/12/2014");
		assertEquals(test.getEndTime(), "null");

		// Floating Task
		testParser = new TDTDateAndTimeParser();
		test = testParser.decodeDateAndTimeDetails("");
		assertEquals(test.getStartDate(), "null");
		assertEquals(test.getStartTime(), "null");
		assertEquals(test.getEndDate(), "null");
		assertEquals(test.getEndTime(), "null");

		// Timed Task
		testParser = new TDTDateAndTimeParser();
		test = testParser
				.decodeDateAndTimeDetails("10/11/14 from 1200h - 1400hr");
		assertEquals(test.getStartDate(), "10/11/2014");
		assertEquals(test.getStartTime(), "12:00");
		assertEquals(test.getEndDate(), "10/11/2014");
		assertEquals(test.getEndTime(), "14:00");

		testParser = new TDTDateAndTimeParser();
		test = testParser
				.decodeDateAndTimeDetails("from 1200h - 1400hr on 12122014");
		assertEquals(test.getStartDate(), "12/12/2014");
		assertEquals(test.getStartTime(), "12:00");
		assertEquals(test.getEndDate(), "12/12/2014");
		assertEquals(test.getEndTime(), "14:00");

		testParser = new TDTDateAndTimeParser();
		test = testParser
				.decodeDateAndTimeDetails("from 10/12 9am until 15/12 10pm");
		assertEquals(test.getStartDate(), "10/12/2014");
		assertEquals(test.getStartTime(), "9:00");
		assertEquals(test.getEndDate(), "15/12/2014");
		assertEquals(test.getEndTime(), "22:00");

		testParser = new TDTDateAndTimeParser();
		test = testParser.decodeDateAndTimeDetails("on 11/12/14 9.30pm");
		assertEquals(test.getStartDate(), "11/12/2014");
		assertEquals(test.getStartTime(), "21:30");
		assertEquals(test.getEndDate(), "null");
		assertEquals(test.getEndTime(), "null");
	}

	// As this method makes use of the current date and time, this test will
	// only pass at the specific time of testing
	// @Test
	public void testDecodeSearchDetails() {
		// test search by range of dates eg. 10/10/2014 - 11/11/2014
		assertEquals(
				TDTDateAndTimeParser
						.decodeSearchDetails("28/10/2014 - 6/11/2014"),
				"28/10/2014 6/11/2014 29/10/2014 30/10/2014 31/10/2014 1/11/2014 2/11/2014 3/11/2014 4/11/2014 5/11/2014 6/11/2014");
		assertEquals(
				TDTDateAndTimeParser
						.decodeSearchDetails("28/10/2014 - 15/10/2014"),
				"28/10/2014 15/10/2014");

		// test search a string of dates eg. 10/10/2014, 16/10/2014, 5/1/2015
		assertEquals(
				TDTDateAndTimeParser
						.decodeSearchDetails("10/10/14 16/10/14 5/1/2015"),
				"10/10/2014 16/10/2014 5/1/2015");

		// test search by week month or year
		// Test done at 10/11/2014 mon
		assertEquals(TDTDateAndTimeParser.decodeSearchDetails("this week"),
				"9/11/2014 10/11/2014 11/11/2014 12/11/2014 13/11/2014 14/11/2014 15/11/2014");

		assertEquals(
				TDTDateAndTimeParser.decodeSearchDetails("next month"),
				"1/12/2014 2/12/2014 3/12/2014 4/12/2014 5/12/2014 6/12/2014 7/12/2014 8/12/2014 9/12/2014 10/12/2014 11/12/2014 12/12/2014 13/12/2014 14/12/2014 15/12/2014 16/12/2014 17/12/2014 18/12/2014 19/12/2014 20/12/2014 21/12/2014 22/12/2014 23/12/2014 24/12/2014 25/12/2014 26/12/2014 27/12/2014 28/12/2014 29/12/2014 30/12/2014 31/12/2014");

	}

	// As this method makes use of the current date and time, this test will
	// only pass at the specific time of testing
	// @Test
	public void testDecodeReminderDetails() {
		// checks if user types only the time
		// Testing date and time: 4.00pm 10/11/2014
		assertEquals(TDTDateAndTimeParser.decodeReminderDetails("6pm"),
				"10/11/2014 18:00");
		// if user input a time that is over, the reminder details will be
		// invalid
		assertEquals(TDTDateAndTimeParser.decodeReminderDetails("2pm"), "null");

		// if user input a date and a time
		assertEquals(
				TDTDateAndTimeParser.decodeReminderDetails("15/11/14 2pm"),
				"15/11/2014 14:00");
		// if user input a date and time that is over
		assertEquals(TDTDateAndTimeParser.decodeReminderDetails("9/11/14 2pm"),
				"null");
	}

}
