package ru.prolib.aquila.transaq.remote;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.ini4j.Profile.Section;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;
import ru.prolib.aquila.transaq.remote.StdConnector;
import ru.prolib.aquila.transaq.remote.TQSecIDT;

public class StdConnectorTest {
	private IMocksControl control;
	private JTransaqServer serverMock;
	private Section configMock;
	private StdConnector service;
	private JTransaqHandler handlerMock;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		serverMock = control.createMock(JTransaqServer.class);
		configMock = control.createMock(Section.class);
		handlerMock = control.createMock(JTransaqHandler.class);
		service = new StdConnector(configMock, serverMock, handlerMock);
	}
	
	@Test
	public void testInit() throws Exception {
		expect(configMock.get("log_path")).andReturn("C:\\tmp\\logs");
		expect(configMock.get("log_level")).andReturn("3");
		serverMock.Initialize("C:\\tmp\\logs", 3);
		control.replay();
		
		service.init();
		
		control.verify();
	}
	
	@Test
	public void testConnect() throws Exception {
		expect(configMock.get("login")).andReturn("user");
		expect(configMock.get("password")).andReturn("12345");
		expect(configMock.get("host")).andReturn("123.456.789");
		expect(configMock.get("port")).andReturn("915");
		serverMock.SendCommand(new StringBuilder()
				.append("<command id=\"connect\">")
				.append("<login>user</login>")
				.append("<password>12345</password>")
				.append("<host>123.456.789</host>")
				.append("<port>915</port>")
				.append("<language>en</language>")
				.append("<autopos>true</autopos>")
				.append("<micex_registers>true</micex_registers>")
				.append("<milliseconds>true</milliseconds>")
				.append("<utc_time>false</utc_time>")
				.append("<rqdelay>1000</rqdelay>")
				.append("<push_u_limits>5</push_u_limits>")
				.append("<push_pos_equity>5</push_pos_equity>")
				.append("</command>")
				.toString());
		control.replay();

		service.connect();
		
		control.verify();
	}
	
	@Test
	public void testDisconnect() throws Exception {
		serverMock.SendCommand("<command id=\"disconnect\"/>");
		control.replay();
		
		service.disconnect();
		
		control.verify();
	}

	@Test
	public void testClose() throws Exception {
		serverMock.UnInitialize();
		expect(handlerMock.Handle("<dump_stats/>")).andReturn(true);
		handlerMock.delete();
		control.replay();
		
		service.close();
		
		control.verify();
	}
	
	@Test
	public void testSubscribe() throws Exception {
		Set<TQSecIDT> symbols = new LinkedHashSet<>();
		symbols.add(new TQSecIDT("foo", "EX1"));
		symbols.add(new TQSecIDT("bar", "EX2"));
		symbols.add(new TQSecIDT("buz", "EX3"));
		serverMock.SendCommand(
				"<command id=\"subscribe\">\n" +
		
				"\t<alltrades>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX1</board>\n" +
				"\t\t\t<seccode>foo</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX2</board>\n" +
				"\t\t\t<seccode>bar</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX3</board>\n" +
				"\t\t\t<seccode>buz</seccode>\n" +
				"\t\t</security>\n" +
				"\t</alltrades>\n" +
				
				"\t<quotations>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX1</board>\n" +
				"\t\t\t<seccode>foo</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX2</board>\n" +
				"\t\t\t<seccode>bar</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX3</board>\n" +
				"\t\t\t<seccode>buz</seccode>\n" +
				"\t\t</security>\n" +
				"\t</quotations>\n" +
				
				"\t<quotes>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX1</board>\n" +
				"\t\t\t<seccode>foo</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX2</board>\n" +
				"\t\t\t<seccode>bar</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX3</board>\n" +
				"\t\t\t<seccode>buz</seccode>\n" +
				"\t\t</security>\n" +
				"\t</quotes>\n" +
				
				"</command>"
			);
		control.replay();
		
		service.subscribe(symbols, StdConnector.SUBSCR_TYPE_ALL_TRADES |
				StdConnector.SUBSCR_TYPE_QUOTATIONS | StdConnector.SUBSCR_TYPE_QUOTES);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe() throws Exception {
		Set<TQSecIDT> symbols = new LinkedHashSet<>();
		symbols.add(new TQSecIDT("foo", "EX1"));
		symbols.add(new TQSecIDT("bar", "EX2"));
		symbols.add(new TQSecIDT("buz", "EX3"));
		serverMock.SendCommand(
				"<command id=\"unsubscribe\">\n" +
		
				"\t<alltrades>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX1</board>\n" +
				"\t\t\t<seccode>foo</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX2</board>\n" +
				"\t\t\t<seccode>bar</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX3</board>\n" +
				"\t\t\t<seccode>buz</seccode>\n" +
				"\t\t</security>\n" +
				"\t</alltrades>\n" +
				
				"\t<quotations>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX1</board>\n" +
				"\t\t\t<seccode>foo</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX2</board>\n" +
				"\t\t\t<seccode>bar</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX3</board>\n" +
				"\t\t\t<seccode>buz</seccode>\n" +
				"\t\t</security>\n" +
				"\t</quotations>\n" +
				
				"\t<quotes>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX1</board>\n" +
				"\t\t\t<seccode>foo</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX2</board>\n" +
				"\t\t\t<seccode>bar</seccode>\n" +
				"\t\t</security>\n" +
				"\t\t<security>\n" +
				"\t\t\t<board>EX3</board>\n" +
				"\t\t\t<seccode>buz</seccode>\n" +
				"\t\t</security>\n" +
				"\t</quotes>\n" +
				
				"</command>"
			);
		control.replay();
		
		service.unsubscribe(symbols, StdConnector.SUBSCR_TYPE_ALL_TRADES |
				StdConnector.SUBSCR_TYPE_QUOTATIONS | StdConnector.SUBSCR_TYPE_QUOTES);
		
		control.verify();
	}
	
	@Test
	public void testSubscribe3() throws Exception {
		Set<ISecIDT> alltrades = new LinkedHashSet<>();
		alltrades.add(new TQSecIDT("RIZ9", "FUT"));
		alltrades.add(new TQSecIDT("SBER", "EQTB"));
		alltrades.add(new TQSecIDT("LKOH", "EQTB"));
		Set<ISecIDT> quotations = new LinkedHashSet<>();
		quotations.add(new TQSecIDT("AAPL", "NYSE"));
		quotations.add(new TQSecIDT("GAZP", "EQTB"));
		Set<ISecIDT> quotes = new LinkedHashSet<>();
		quotes.add(new TQSecIDT("RIZ9", "FUT"));
		quotes.add(new TQSecIDT("SiZ9", "FUT"));
		Capture<String> my_cap = Capture.newInstance();
		serverMock.SendCommand(capture(my_cap));
		control.replay();
		
		service.subscribe(alltrades, quotations, quotes);
		
		control.verify();
		String expected = FileUtils.readFileToString(new File("fixture/connector-subscribe3.xml"), "UTF8");
		assertEquals(expected, my_cap.getValue());
	}
	
	@Test
	public void testSubscribe3_DoesNotSendIfEmptySets() throws Exception {
		control.replay();
		
		service.subscribe(new HashSet<>(), new HashSet<>(), new HashSet<>());
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe3() throws Exception {
		Set<ISecIDT> alltrades = new LinkedHashSet<>();
		alltrades.add(new TQSecIDT("RIZ9", "FUT"));
		alltrades.add(new TQSecIDT("AAPL", "NYSE"));
		alltrades.add(new TQSecIDT("SBER", "EQTB"));
		Set<ISecIDT> quotations = new LinkedHashSet<>();
		quotations.add(new TQSecIDT("LKOH", "EQTB"));
		quotations.add(new TQSecIDT("RIZ9", "FUT"));
		quotations.add(new TQSecIDT("GAZP", "EQTB"));
		Set<ISecIDT> quotes = new LinkedHashSet<>();
		quotes.add(new TQSecIDT("SiZ9", "FUT"));
		Capture<String> my_cap = Capture.newInstance();
		serverMock.SendCommand(capture(my_cap));
		control.replay();
		
		service.unsubscribe(alltrades, quotations, quotes);
		
		control.verify();
		String expected = FileUtils.readFileToString(new File("fixture/connector-unsubscribe3.xml"), "UTF8");
		assertEquals(expected, my_cap.getValue());
	}
	
	@Test
	public void testUnsubscribe3_DoesNotSendIfEmptySets() throws Exception {
		control.replay();
		
		service.unsubscribe(new HashSet<>(), new HashSet<>(), new HashSet<>());
		
		control.verify();
	}

}
