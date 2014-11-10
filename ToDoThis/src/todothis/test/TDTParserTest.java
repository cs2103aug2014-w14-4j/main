//@author @A0115933H
package todothis.test;

import static org.junit.Assert.*;
import todothis.logic.parser.TDTParser;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;

import org.junit.Test;

public class TDTParserTest {
	
	//----------------------------------------ADD----------------------------------------------
	/**
	 * This test that the commandDetails are correct.
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
		assertEquals(parser.getDateAndTimeParts(), " at 3pm today");
	}

	/**
	 * The command word 'add' is not present. 
	 * This test that order of words does not matter. 
	 * '3:00pm on 20/11/2014' will not be included in commandDetails
	 */
	@Test
	public void testAdd2() {
		TDTParser parser = new TDTParser();
		String testInput2 = "3:00pm on 20/11/2014 buy a lot of eggs";
		parser.parse(testInput2);
		assertEquals(parser.getCommandDetails().trim(),"buy a lot of eggs");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getDateAndTimeParts(), " 3:00pm on 20/11/2014");
	}
	
	/**
	 * This test that the default command is add even when adding nothing. 
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
	 * This test that the input is of priority 
	 */
	@Test
	public void testAdd4() {
		TDTParser parser = new TDTParser();
		String testInput4 = "go shopping at 3pm today!!!!!";
		parser.parse(testInput4);
		assertEquals(parser.getCommandDetails().trim(),"go shopping");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getIsHighPriority(), true);
		assertEquals(parser.getDateAndTimeParts(), " at 3pm today");
	}
	
	/**
	 * This test that the words between quotation marks appear as commandDetails and
	 * does not get parsed as date / time / priority.
	 */
	@Test
	public void testAdd5() {
		TDTParser parser = new TDTParser();
		String testInput5 = "\"edit everything 4pm today!\" 6pm 23 nov";
		parser.parse(testInput5);
		assertEquals(parser.getCommandDetails().trim(),"edit everything 4pm today!");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getIsHighPriority(), false);
		assertEquals(parser.getDateAndTimeParts(), " 6pm 23~nov");
	}
	
	//----------------------------------------DELETE----------------------------------------------
	/**
	 * This tests that Delete valid taskID
	 */
	@Test
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
		assertEquals(parser.getTaskID(), 2);
	}
	
	/**
	 * This test that the order in which the label name and task id
	 * are placed after the command word DELETE does not matter.
	 */
	@Test
	public void testDelete2() {
		TDTParser parser = new TDTParser();
		String testInput7 = "delete 1 today";
		parser.parse(testInput7);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), 1);
		assertEquals(parser.getLabelName(), "today");
		
		String testInput8 = "delete tmr 1";
		parser.parse(testInput8);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), 1);
		assertEquals(parser.getLabelName(), "tmr");
	}
	
	/**
	 * This tests that there cannot be more than 2 fields 
	 * (label name and task id) entered for a delete command. 
	 */
	@Test
	public void testDelete3() {
		TDTParser parser = new TDTParser();
		String testInput9 = "delete 1 todothis todothisss";
		parser.parse(testInput9);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), -1);
		assertEquals(parser.getLabelName(), " ");
	}
	
	/**
	 * This tests the deletion of a label 
	 */
	@Test
	public void testDelete4() {
		TDTParser parser = new TDTParser();
		String testInput10 = "delete todothis";
		parser.parse(testInput10);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getLabelName(), "todothis");
	}
	
	//----------------------------------------LABEL----------------------------------------------
	/**
	 * Create valid label (1word)
	 */
	@Test
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
	@Test
	public void testLabel2() {
		TDTParser parser = new TDTParser();
		String testInput2 = "label school work";
		parser.parse(testInput2);
		assertEquals(parser.getCommandType(), COMMANDTYPE.LABEL);
		assertEquals(parser.getLabelName(), "school work");
	}
	
	//----------------------------------------DONE----------------------------------------------
	/**
	 * This test that the order in which the label name and task id
	 * are placed after the command word DONE does not matter.
	 */
	@Test
	public void testDone1() {
		TDTParser parser = new TDTParser();
		String testInput1 = "done todothis 1";
		parser.parse(testInput1);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DONE);
		assertEquals(parser.getLabelName(), "todothis");
		assertEquals(parser.getTaskID(), 1);
		
		String testInput3 = "done 2 todothis";
		parser.parse(testInput3);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DONE);
		assertEquals(parser.getLabelName(), "todothis");
		assertEquals(parser.getTaskID(), 2);
	}
	
	/**
	 * This tests that there cannot be more than 2 fields 
	 * (label name and task id) entered for a done command. 
	 */
	@Test
	public void testDone2() {
		TDTParser parser = new TDTParser();
		String testInput2 = "done todothis today 1";
		parser.parse(testInput2);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DONE);
		assertEquals(parser.getLabelName(), " ");
		assertEquals(parser.getTaskID(), -1);
	}
	//----------------------------------------EDIT----------------------------------------------
	/**
	 * This tests that the edit is valid without a label name 
	 */
	@Test
	public void testEdit1() {
		TDTParser parser = new TDTParser();
		String testInput2 = "edit 1 lets test edit command";
		parser.parse(testInput2);
		assertEquals(parser.getCommandType(), COMMANDTYPE.EDIT);
		assertEquals(parser.getLabelName(), "");
		assertEquals(parser.getTaskID(), 1);
		assertEquals(parser.getCommandDetails(), " lets test edit command");
	}
	
	//----------------------------------------HIDE----------------------------------------------
	/**
	 * This tests that hide command parse the label names as command details. 
	 */
	@Test
	public void testHide1() {
		TDTParser parser = new TDTParser();
		String testInput1 = "hide label1 label2";
		parser.parse(testInput1);
		assertEquals(parser.getCommandType(), COMMANDTYPE.HIDE);
		assertEquals(parser.getLabelName(), "");
		assertEquals(parser.getCommandDetails(), "label1 label2");
	}
	
	//----------------------------------------SHOW----------------------------------------------
	/**
	 * This tests that show command parse the label names as command details. 
	 */
	@Test
	public void testShow1() {
		TDTParser parser = new TDTParser();
		String testInput1 = "show label1 label2";
		parser.parse(testInput1);
		assertEquals(parser.getCommandType(), COMMANDTYPE.SHOW);
		assertEquals(parser.getLabelName(), "");
		assertEquals(parser.getCommandDetails(), "label1 label2");
	}
	
	//----------------------------------------REMIND----------------------------------------------
	/**
	 * This tests that remind command parse everything after the taskID as command details.
	 */
	@Test
	public void testRemind1() {
		TDTParser parser = new TDTParser();
		String testInput1 = "remind todothis 3 4pm";
		parser.parse(testInput1);
		assertEquals(parser.getCommandType(), COMMANDTYPE.REMIND);
		assertEquals(parser.getLabelName(), "todothis");
		assertEquals(parser.getCommandDetails(), "4pm");
		assertEquals(parser.getDateAndTimeParts(), "");
		
		String testInput2 = "remind 3 todothis 4pm";
		parser.parse(testInput2);
		assertEquals(parser.getCommandType(), COMMANDTYPE.REMIND);
		assertEquals(parser.getLabelName(), "");
		assertEquals(parser.getCommandDetails(), "todothis 4pm");
		assertEquals(parser.getDateAndTimeParts(), "");
	}
	
	//----------------------------------------SEARCH----------------------------------------------
	/**
	 * This tests that search command parse everything as command details
	 */
	@Test
	public void testSearch1() {
		TDTParser parser = new TDTParser();
		String testInput1 = "search apples @5pm next week";
		parser.parse(testInput1);
		assertEquals(parser.getCommandType(), COMMANDTYPE.SEARCH);
		assertEquals(parser.getCommandDetails(), "apples @5pm next week");
		assertEquals(parser.getDateAndTimeParts(), "");
	}
}