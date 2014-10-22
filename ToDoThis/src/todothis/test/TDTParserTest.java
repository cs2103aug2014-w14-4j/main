package todothis.test;

import static org.junit.Assert.*;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.parser.TDTParser;
import org.junit.Test;

public class TDTParserTest {

	@Test
	public void testAdd() {
		/**
		 * The command word 'add' is present
		 * 'at 3pm today' will not be included in commandDetails
		 */
		TDTParser parser = new TDTParser();
		String testInput1 = "add buy eggs at 3pm today";
		parser.parse(testInput1);
		assertEquals(parser.getCommandDetails().trim(),"buy eggs");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getLabelName(), "");
		
		/**
		 * The command word 'add' is not present. 
		 * Order of words does not matter. 
		 * '3:00pm on 20/11/2014' will not be included in commandDetails
		 */
		String testInput2 = "3:00pm on 20/11/2014 buy a lot of eggs";
		parser.parse(testInput2);
		assertEquals(parser.getCommandDetails().trim(),"buy a lot of eggs");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		
		/**
		 * This tests ADD without adding anything. 
		 */
		String testInput3 = "";
		parser.parse(testInput3);
		assertEquals(parser.getCommandDetails().trim(),"");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		
		/**
		 * This input is of priority
		 */
		String testInput4 = "go shopping at 3pm today!!!!!";
		parser.parse(testInput4);
		assertEquals(parser.getCommandDetails().trim(),"go shopping");
		assertEquals(parser.getCommandType(), COMMANDTYPE.ADD);
		assertEquals(parser.getIsHighPriority(), true);
	}
	
	public void testDelete() {
		
		TDTParser parser = new TDTParser();
		String testInput1 = "buy eggs";
		String testInput2 = "buy pets";
		String testInput3 = "buy things from somerset";
		String testInput4 = "play games";
		parser.parse(testInput1);
		parser.parse(testInput2);
		parser.parse(testInput3);
		parser.parse(testInput4);
		/**
		 * This deletes a valid taskID
		 */
		String testInput5 = "delete 2";
		parser.parse(testInput5);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), "2");
		
		/**
		 * This deletes an invalid taskID but still passes the correct info. 
		 */
		String testInput6 = "delete 6";
		parser.parse(testInput6);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), "6");
		
		String testInput8 = "label TMR";
		String testInput9 = "lets party!";
		String testInput10 = "play games tmr";
		String testInput11 = "play more games tmr";
		parser.parse(testInput8);
		parser.parse(testInput9);
		parser.parse(testInput10);
		parser.parse(testInput11);
		
		/**
		 * The order in which words are placed after the command word DELETE does not matter.
		 */
		String testInput12 = "delete 1 today";
		parser.parse(testInput12);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), "1");
		
		String testInput13 = "delete tmr 1";
		parser.parse(testInput13);
		assertEquals(parser.getCommandType(), COMMANDTYPE.DELETE);
		assertEquals(parser.getTaskID(), "1");
	}
	
}