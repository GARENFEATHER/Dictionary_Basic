package unitTest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import application.model.Word;
import application.util.RedirectNetException;
import application.util.URLParse;

public class URLParseTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException, RedirectNetException {
		Word w=URLParse.parse("there is a dog");
	}

}
