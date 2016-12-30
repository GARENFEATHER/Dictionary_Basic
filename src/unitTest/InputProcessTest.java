package unitTest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import application.util.InputProcess;

public class InputProcessTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test1() {
		String s="There is a friend";
		s=InputProcess.processInvalid(s);
		assertEquals("there is a friend", s);
	}
	
	@Test
	public void test2() {
		String s="There   is a frie23nd";
		s=InputProcess.processInvalid(s);
		assertEquals("there is a friend", s);
	}
	
	@Test
	public void test3() {
		String s="The2@@re  % &*&(8 is a frie23nd";
		s=InputProcess.processInvalid(s);
		assertEquals("there is a friend", s);
	}

	@Test
	public void test4() {
		String s="There   is a frie2	=3nd";
		s=InputProcess.processInvalid(s);
		assertEquals("there is a friend", s);
	}

	@Test
	public void test5() {
		String s="";
		s=InputProcess.processInvalid(s);
		assertEquals(null, s);
	}

	@Test
	public void test6() {
		String s="%^( 988  ";
		s=InputProcess.processInvalid(s);
		assertEquals(null, s);
	}
}
