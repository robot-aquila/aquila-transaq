package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import java.util.LinkedHashSet;
import java.util.Set;

import org.easymock.IMocksControl;
import org.ini4j.Profile.Section;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;

public class TQConnectorTest {
	private IMocksControl control;
	private JTransaqServer serverMock;
	private Section configMock;
	private TQConnector service;
	private JTransaqHandler handlerMock;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		serverMock = control.createMock(JTransaqServer.class);
		configMock = control.createMock(Section.class);
		handlerMock = control.createMock(JTransaqHandler.class);
		service = new TQConnector(configMock, serverMock, handlerMock);
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
		Set<TQSecID2> symbols = new LinkedHashSet<>();
		symbols.add(new TQSecID2("foo", "EX1"));
		symbols.add(new TQSecID2("bar", "EX2"));
		symbols.add(new TQSecID2("buz", "EX3"));
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
		
		service.subscribe(symbols, TQConnector.SUBSCR_TYPE_ALL_TRADES |
				TQConnector.SUBSCR_TYPE_QUOTATIONS | TQConnector.SUBSCR_TYPE_QUOTES);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe() throws Exception {
		Set<TQSecID2> symbols = new LinkedHashSet<>();
		symbols.add(new TQSecID2("foo", "EX1"));
		symbols.add(new TQSecID2("bar", "EX2"));
		symbols.add(new TQSecID2("buz", "EX3"));
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
		
		service.unsubscribe(symbols, TQConnector.SUBSCR_TYPE_ALL_TRADES |
				TQConnector.SUBSCR_TYPE_QUOTATIONS | TQConnector.SUBSCR_TYPE_QUOTES);
		
		control.verify();
	}

}
