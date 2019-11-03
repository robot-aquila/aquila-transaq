package ru.prolib.aquila.transaq.engine.mp;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.mp.DefaultMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQParser;

public class DefaultMessageProcessorTest {
	private static XMLInputFactory factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		factory = XMLInputFactory.newInstance();
	}
	
	private ServiceLocator services;
	private Map<String, Integer> dataStub;
	private DefaultMessageProcessor service;

	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		services.setParser(TQParser.getInstance());
		dataStub = new LinkedHashMap<>();
		service = new DefaultMessageProcessor(dataStub, services);
	}

	@Test
	public void testProcessMessage() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/default_processor.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				if ( ! sr.getLocalName().equals("document") ) {
					service.processMessage(sr);
				}
				break;
			}
		}
		
		Map<String, Integer> expected = new LinkedHashMap<>();
		expected.put("zulu",	4);
		expected.put("foo",		2);
		expected.put("bar",		4);
		expected.put("tutuma",	1);
		assertEquals(expected, dataStub);
	}
	
	@Test
	public void testProcessMessage_DumpStats() throws Exception {
		dataStub.put("foo", 15);
		dataStub.put("bar", 97);
		dataStub.put("buz", 34);
		InputStream is = new ByteArrayInputStream("<document><dump_stats/></document>".getBytes(StandardCharsets.UTF_8));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				if ( ! sr.getLocalName().equals("document") ) {
					service.processMessage(sr);
				}
				break;
			}
		}
		
		assertEquals(new LinkedHashMap<>(), dataStub);
	}

}
