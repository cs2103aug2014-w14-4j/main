package todothis.test;

import static org.junit.Assert.*;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.parser.TDTParser;
import org.junit.Test;

public class TDTParserTest {

	/**
	 * The command word 'add' is present
	 * 'at 3pm today' will not be included in commandDetails
	 */
	@Test
	public void testAdd1() {
		TDTParser parser = new TDTParser();
		String testInput1 = "add buy eggs at 3pm today";
		parser.parse(testInput1);
		assertEquals(parser.getCommandDetails().trim(),"buy eggs");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getLabelName(), "");
		assertEquals(parser.getDateAndTimeParts(), "at 3pm today");
	}

	/**
	 * The command word 'add' is not present. 
	 * Order of words does not matter. 
	 * '3:00pm on 20/11/2014' will not be included in commandDetails
	 */
	@Test
	public void testAdd2() {
		TDTParser parser = new TDTParser();
		String testInput2 = "3:00pm on 20/11/2014 buy a lot of eggs";
		parser.parse(testInput2);
		assertEquals(parser.getCommandDetails().trim(),"buy a lot of eggs");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getDateAndTimeParts(), "3:00pm on 20/11/2014");
	}
	
	/**
	 * This tests ADD without adding anything. 
	 */
	@Test
	public void testAdd3() {
		TDTParser parser = new TDTParser();
		String testInput3 = "";
		parser.parse(testInput3);
		assertEquals(parser.getCommandDetails().trim(),"");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getDateAndTimeParts(), "");
	}
	
	/**
	 * This input is of priority 
	 */
	@Test
	public void testAdd4() {
		TDTParser parser = new TDTParser();
		String testInput4 = "go shopping at 3pm today!!!!!";
		parser.parse(testInput4);
		assertEquals(parser.getCommandDetails().trim(),"go shopping");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getIsHighPriority(), true);
		assertEquals(parser.getDateAndTimeParts(), "at 3pm today");
	}
	
	/**
	 * This tests the input "command word, time, date" date time
	 */
	@Test
	public void testAdd5() {
		TDTParser parser = new TDTParser();
		String testInput5 = "\"edit everything 4pm today!\" 6pm 23 nov";
		parser.parse(testInput5);
		assertEquals(parser.getCommandDetails().trim(),"edit everything 4pm today!");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getIsHighPriority(), false);
		assertEquals(parser.getDateAndTimeParts(), "6pm 23~nov");
	}
	
	/**
	 * Delete valid taskID
	 */
	public void testDelete1() {
		TDTParser parser = new TDTParser();
		String testInput1 = "buy eggs";
		String testInput2 = "buy pets";
		String testInput3 = "buy things from somerset";
		String testInput4 = "play games";
		parser.parse(testInput1);
		parser.parse(testInput2);
		parser.parse(testInput3);
		parser.parse(testInput4);

		String testInput5 = "delete 2";
		parser.parse(testInput5);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), "2");
	}

	/**
	 * This deletes an invalid taskID but still passes the correct info. 
	 */
	public void testDelete2() {
		TDTParser parser = new TDTParser();
		String testInput6 = "delete 6";
		parser.parse(testInput6);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), "6");
	}
	
	/**
	 * The order in which words are placed after the command word DELETE does not matter.
	 */
	public void testDelete3() {
		TDTParser parser = new TDTParser();
		String testInput7 = "delete 1 today";
		parser.parse(testInput7);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), "1");
		assertEquals(parser.getLabelName(), "today");
		
		String testInput8 = "delete tmr 1";
		parser.parse(testInput8);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), "1");
		assertEquals(parser.getLabelName(), "tmr");
	}
	
	/**
	 * This is an invalid delete. 
	 */
	public void testDelete5() {
		TDTParser parser = new TDTParser();
		String testInput9 = "delete 1 todothis todothisss";
		parser.parse(testInput9);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), "");
		assertEquals(parser.getLabelName(), "");
	}
	
	/**
	 * Delete label 
	 */
	public void testDelete4() {
		TDTParser parser = new TDTParser();
		String testInput10 = "delete todothis";
		parser.parse(testInput10);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getLabelName(), "todothis");
	}
	
	/**
	 * Create valid label (1word)
	 */
	public void testLabel1() {
		TDTParser parser = new TDTParser();
		String testInput1 = "label school";
		parser.parse(testInput1);
		assertEquals(parser.getCommandType(), COMMANDTYPE.LABEL);
		assertEquals(parser.getLabelName(), "school");
	}
	
	/**
	 * Create invalid label (2words)
	 */
	public void testLabel2() {
		TDTParser parser = new TDTParser();
		String testInput2 = "label school work";
		parser.parse(testInput2);
		assertEquals(parser.getCommandType(), COMMANDTYPE.LABEL);
		assertEquals(parser.getLabelName(), "");
	}
	
	/**
	 * Valid done 
	 */
	public void testDone1() {
		TDTParser parser = new TDTParser();
		String testInput1 = "done todothis 1";
		parser.parse(testInput1);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DONE);
		assertEquals(parser.getLabelName(), "todothis");
		assertEquals(parser.getTaskID(), "1");
	}
	
	/**
	 * Invalid done 
	 */
	public void testDone2() {
		TDTParser parser = new TDTParser();
		String testInput2 = "done todothis 1 today";
		parser.parse(testInput2);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DONE);
		assertEquals(parser.getLabelName(), "");
		assertEquals(parser.getTaskID(), "");
	}
	
	/**
	 * Edit
	 */
	public void testEdit1() {
		TDTParser parser = new TDTParser();
		String testInput2 = "edit 1 ";
		parser.parse(testInput2);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DONE);
		assertEquals(parser.getLabelName(), "");
		assertEquals(parser.getTaskID(), "");
	}
}