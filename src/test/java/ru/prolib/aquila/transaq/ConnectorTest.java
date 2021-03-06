package ru.prolib.aquila.transaq;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;
import ru.prolib.aquila.transaq.impl.TQConnector;
import ru.prolib.aquila.transaq.impl.TQSecID2;

public class ConnectorTest {
	
	static class XHandler extends JTransaqHandler {
		
		@Override
		public boolean Handle(String data) {
			System.out.println("XHandler IN> " + data);
			return true;
		}

	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FileUtils.deleteQuietly(new File("bin"));
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	@Ignore
	public void testConnect() throws Exception {
		XHandler handler = new XHandler();
		Wini ini = new Wini(new File("fixture/transaq.ini"));
		Section config = ini.get("transaq-test");
		JTransaqServer server = new JTransaqServer(handler);
		server.Initialize(config.get("log_path"), Integer.parseInt(config.get("log_level")));
		try {
			server.SendCommand("<command id=\"connect\">"
				+ "<login>" + config.get("login") + "</login>"
				+ "<password>" + config.get("password") + "</password>"
				+ "<host>" + config.get("host") + "</host>"
				+ "<port>" + config.get("port") + "</port>"
				+ "<language>en</language>"
				+ "<autopos>true</autopos>"
				+ "<micex_registers>true</micex_registers>"
				+ "<milliseconds>true</milliseconds>"
				+ "<utc_time>false</utc_time>"
				+ "<rqdelay>1000</rqdelay>"
				+ "<push_u_limits>5</push_u_limits>"
				+ "<push_pos_equity>5</push_pos_equity>"
				+ "</command>");
		} catch ( Throwable t ) {
			System.out.println("Error: " + t);
			t.printStackTrace();
		}
		Thread.sleep(20000L);
		
		server.UnInitialize();
	}
	
	@Ignore
	@Test
	public void testConnect2() throws Exception {
		XHandler handler = new XHandler();
		Wini ini = new Wini(new File("fixture/transaq.ini"));
		Section config = ini.get("transaq-test");
		JTransaqServer server = new JTransaqServer(handler);
		TQConnector conn = new TQConnector(config, server, handler);
		conn.init();
		conn.connect();
		Thread.sleep(5000L);
		try {
			Set<TQSecID2> symbols = new LinkedHashSet<>();
			symbols.add(new TQSecID2("SBER", "TQBR"));
			symbols.add(new TQSecID2("RIZ9", "FUT"));
			conn.subscribe(symbols, TQConnector.SUBSCR_TYPE_QUOTATIONS);
			Thread.sleep(20000L);
		} finally {
			conn.disconnect();
			conn.close();
		}
	}

}
