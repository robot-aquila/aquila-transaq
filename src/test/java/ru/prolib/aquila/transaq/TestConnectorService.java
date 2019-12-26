package ru.prolib.aquila.transaq;

import static org.junit.Assert.*;
import static ru.prolib.aquila.transaq.TestServer.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ini4j.Profile.Section;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.ini4j.Wini;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;
import ru.prolib.aquila.transaq.TestServer.ClientAction;
import ru.prolib.aquila.transaq.TestServer.ScriptPhase;
import ru.prolib.aquila.transaq.remote.StdConnectorFactory;

public class TestConnectorService extends StdConnectorFactory {
	
	static class FakeHandler extends JTransaqHandler {
		private JTransaqHandler target;
		
		public synchronized void setHandler(JTransaqHandler target) {
			this.target = target;
		}
		
		@Override
		public synchronized boolean Handle(String data) {
			if ( target != null ) {
				return target.Handle(data);
			}
			return true;
		}
		
		@Override
		public synchronized void delete() {
			if ( target != null ) {
				target.delete();
			}
			super.delete();
		}
		
	}
	
	private final FakeHandler fakeHandler = new FakeHandler();
	private final List<ScriptPhase> scriptPhases = new ArrayList<>();
	private final Section config;
	private TestServer recentlyCreatedServer;

	public TestConnectorService(Section config) {
		super(config);
		this.config = config;
	}
	
	public TestConnectorService() {
		this(new Wini().add("dummy_section"));
	}
	
	public TestConnectorService loadConfig(String ini_file, String section_name) throws Exception {
		config.putAll(new Wini(new File(ini_file)).get(section_name));
		return this;
	}
	
	public TestConnectorService loadConfig(String ini_file) throws Exception {
		return loadConfig(ini_file, "main");
	}
	
	public TestConnectorService addScriptPhase(ScriptPhase phase) {
		scriptPhases.add(phase);
		return this;
	}
	
	public JTransaqHandler getHandlerProxy() {
		return fakeHandler;
	}
	
	private String nodeToString(Node node) throws TransformerException {
		StringWriter buf = new StringWriter();
		Transformer xform = TransformerFactory.newInstance().newTransformer();
		xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		xform.transform(new DOMSource(node), new StreamResult(buf));
		return buf.toString();
	}
	
	private String getChildAsString(Element elem) throws Exception {
		NodeList nodes = elem.getChildNodes();
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < nodes.getLength(); i ++ ) {
			Node n = nodes.item(i);
			if ( n.getNodeType() == Node.ELEMENT_NODE ) {
				sb.append(nodeToString(n));
			}
		}
		return sb.toString();
	}
	
	private ClientAction parseExpectation(Element phase_elem) throws Exception {
		NodeList nodes = phase_elem.getElementsByTagName("expectation");
		if ( nodes.getLength() != 1 ) {
			throw new IllegalArgumentException("Expected single \"expectation\" but: " + nodes.getLength());
		}
		Element elem = (Element) nodes.item(0);
		String type = elem.getAttribute("type");
		if ( type == null ) {
			throw new IllegalArgumentException("Expectation \"type\" attribute not found");
		}
		switch ( type ) {
		case "Initialize":
		{
			String log_path, log_level;
			assertNotNull("log_path attribute not defined", log_path = elem.getAttribute("log_path"));
			assertNotNull("log_level attribute not defined", log_level = elem.getAttribute("log_level"));
			return new ClientCalledInitialize(log_path, Integer.parseInt(log_level));
		}
		case "SetLogLevel":
		{
			String log_level;
			assertNotNull("log_level attribute not defined", log_level = elem.getAttribute("log_level"));
			return new ClientCalledSetLogLevel(Integer.parseInt(log_level));
		}	
		case "UnInitialize":
			return new ClientCalledUnInitialize();
		case "SendCommand":
			return new ClientCalledSendCommand(getChildAsString(elem));
		case "ExplicitCall":
			return new ClientCalledExplicitCall();
		default:
			throw new IllegalArgumentException("Unsupported expectation type: " + type);
		}		
	}
	
	private Runnable parseAction(Element elem) throws Exception {
		String type = elem.getAttribute("type");
		if ( type == null ) {
			throw new IllegalArgumentException("Expectation \"type\" attribute not found");
		}
		switch ( type ) {
		case "SendXml":
			return new Action.SendXml(fakeHandler, getChildAsString(elem));
		default:
			throw new IllegalArgumentException("Unsupported action type: " + type);
		}
	}
	
	private List<Runnable> parseActions(Element elem) throws Exception {
		List<Runnable> result = new ArrayList<>();
		NodeList nodes = elem.getElementsByTagName("actions");
		if ( nodes.getLength() == 0 ) {
			return result;
		} else if ( nodes.getLength() > 1 ) {
			throw new IllegalArgumentException("Expected single \"actions\" but: " + nodes.getLength());
		}
		nodes = ((Element) nodes.item(0)).getElementsByTagName("action");
		for ( int i = 0; i < nodes.getLength(); i ++ ) {
			result.add(parseAction((Element) nodes.item(i)));
		}
		return result;
	}
	
	private ScriptPhase parseScriptPhase(Element phase_elem) throws Exception {
		ClientAction expectation = parseExpectation(phase_elem);
		ScriptPhase sp = new ScriptPhase(expectation);
		for ( Runnable action : parseActions(phase_elem) ) {
			sp.addResponseAction(action);
		}
		return sp;
	}
	
	public TestConnectorService addScript(String xml_file, Runnable final_action) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = dbf.newDocumentBuilder().parse(new InputSource(new BufferedReader(new FileReader(xml_file))));
		Element elem = doc.getDocumentElement();
		if ( "script".equals(elem.getTagName()) == false ) {
			throw new IllegalArgumentException("Document element expecte to be \"script\" but " + elem.getTagName());
		}
		NodeList nodes = elem.getChildNodes();
		for ( int i = 0; i < nodes.getLength(); i ++ ) {
			Node node = nodes.item(i);
			if ( node.getNodeType() == Node.ELEMENT_NODE ) {
				elem = (Element) node;
				switch ( elem.getTagName() ) {
				case "phase":
				{
					ScriptPhase phase = parseScriptPhase(elem);
					if ( final_action != null ) {
						phase.addResponseAction(final_action);
					}
					addScriptPhase(phase);
					break;
				}
				case "appendix":
				{
					ScriptPhase phase = scriptPhases.get(scriptPhases.size() - 1);
					for ( Runnable action : parseActions(elem) ) {
						phase.addResponseAction(action);
					}
					break;
				}
				default:
					throw new IllegalArgumentException("Unsupported script element: " + elem.getTagName());
				}
			}
		}
		return this;
	}
	
	public TestConnectorService addScript(String xml_file) throws Exception {
		return addScript(xml_file, null);
	}
	
	/**
	 * Make an explicit call on recently created test server.
	 */
	public void ExplicitCall() {
		recentlyCreatedServer.ExplicitCall();
	}
	
	@Override
	protected JTransaqServer createServer(JTransaqHandler handler) throws Exception {
		fakeHandler.setHandler(handler);
		return (recentlyCreatedServer = new TestServer(fakeHandler, scriptPhases));
	}

}
