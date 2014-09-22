package todothis;

import static org.junit.Assert.*;

import org.junit.Test;

import todothis.ITDTParser.COMMANDTYPE;

public class TDTParserTest {
	
	//Test for ADD command
	@Test
	public void parserTestADD() {
		TDTParser parser = new TDTParser();
		String testString = "add eat rice later";
		Command command = parser.parse(testString);
		
		assertEquals(COMMANDTYPE.ADD,command.getCommandType());
		assertEquals(testString,command.getCommandDetails());
		
		String testString1 = "tmr undo homework 12.34pm 22/9/2014";
		Command command1 = parser.parse(testString1);
		
		assertEquals(COMMANDTYPE.ADD,command1.getCommandType());
		assertEquals(testString1,command1.getCommandDetails());
	
	}
	
	//Test for DELETE command
	@Test
	public void parserTestDELETE() {
		TDTParser parser = new TDTParser();
		String testString = "Delete today 3";
		Command command = parser.parse(testString);
		
		assertEquals(COMMANDTYPE.DELETE,command.getCommandType());
		assertEquals("today",command.getLabelName());
		assertEquals(3,command.getTaskID());
		
	}
	
	//Test for EDIT command
	@Test
	public void parserTestEDIT() {
		TDTParser parser = new TDTParser();
		String testString = "edit today 3 buy egg later";
		Command command = parser.parse(testString);
		
		assertEquals(COMMANDTYPE.EDIT,command.getCommandType());
		assertEquals("today",command.getLabelName());
		assertEquals(3,command.getTaskID());
		assertEquals("buy egg later",command.getCommandDetails());
	}
	
	//Test for DONE command
	@Test
	public void parserTestDONE() {
		TDTParser parser = new TDTParser();
		String testString = "DONE someday 3";
		Command command = parser.parse(testString);
		
		assertEquals(COMMANDTYPE.DONE,command.getCommandType());
		assertEquals("someday",command.getLabelName());
		assertEquals(3,command.getTaskID());
		
	}
	
	//Test for HIDE command
	@Test
	public void parserTestHIDE() {
		TDTParser parser = new TDTParser();
		String testString = "HiDe someday";
		Command command = parser.parse(testString);
		
		assertEquals(COMMANDTYPE.HIDE,command.getCommandType());
		assertEquals("someday",command.getLabelName());
		
		String testString1 = "HiDe";
		Command command1 = parser.parse(testString1);
		
		assertEquals(COMMANDTYPE.HIDE,command1.getCommandType());
		assertEquals("",command1.getLabelName());
	}
	
	//Test for LABEL command
	@Test
	public void parserTestLABEL() {
		TDTParser parser = new TDTParser();
		String testString = "Label tmrthetmrdetmr";
		Command command = parser.parse(testString);
		
		assertEquals(COMMANDTYPE.LABEL,command.getCommandType());
		assertEquals("tmrthetmrdetmr",command.getLabelName());
			
		String testString1 = "Label tmrthe tmrdetmr";
		Command command1 = parser.parse(testString1);
		
		assertEquals(COMMANDTYPE.LABEL,command1.getCommandType());
		assertEquals("tmrthe tmrdetmr",command1.getLabelName());
		
	}
	
	//Test for DISPLAY command
	@Test
	public void parserTestDISPLAY() {
		TDTParser parser = new TDTParser();
		String testString = "display someday";
		Command command = parser.parse(testString);
		
		assertEquals(COMMANDTYPE.DISPLAY,command.getCommandType());
		assertEquals("someday",command.getLabelName());
		
		String testString1 = "display";
		Command command1 = parser.parse(testString1);
		
		assertEquals(COMMANDTYPE.DISPLAY,command1.getCommandType());
		assertEquals("",command1.getLabelName());
		assertEquals(-1,command1.getTaskID());
		
		String testString2 = "display someday 2";
		Command command2 = parser.parse(testString2);
		
		assertEquals(COMMANDTYPE.DISPLAY,command2.getCommandType());
		assertEquals("someday",command2.getLabelName());
		assertEquals(-1,command2.getTaskID());

	}
	
	//Test for SORT command
	@Test
	public void parserTestSORT() {
		TDTParser parser = new TDTParser();
		String testString = "Sort";
		Command command = parser.parse(testString);
		
		assertEquals(COMMANDTYPE.SORT,command.getCommandType());
		assertEquals("",command.getLabelName());
		
		String testString1 = "Sort this";
		Command command1 = parser.parse(testString1);
	
		assertEquals(COMMANDTYPE.SORT,command1.getCommandType());
		assertEquals("",command1.getLabelName());
		
	}
	
	//Test for SEARCH command
	@Test
	public void parserTestSEARCH() {
		TDTParser parser = new TDTParser();
		String testString = "Search";
		Command command = parser.parse(testString);
		
		assertEquals(COMMANDTYPE.SEARCH,command.getCommandType());
		assertEquals("",command.getLabelName());
		
		String testString1 = "Search this this this";
		Command command1 = parser.parse(testString1);
		
		assertEquals(COMMANDTYPE.SEARCH,command1.getCommandType());
		assertEquals("",command1.getLabelName());
		assertEquals("this this this", command1.getCommandDetails());
		
	}
	
	
	//ADD OTHER TEST BELOW

}