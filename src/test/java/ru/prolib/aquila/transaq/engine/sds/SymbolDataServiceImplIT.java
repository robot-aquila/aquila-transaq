package ru.prolib.aquila.transaq.engine.sds;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;

public class SymbolDataServiceImplIT {
	
	private static String nodeToString(Node node) throws TransformerException {
		StringWriter buf = new StringWriter();
		Transformer xform = TransformerFactory.newInstance().newTransformer();
		xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		xform.transform(new DOMSource(node), new StreamResult(buf));
		return(buf.toString());
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void test() throws Exception {
		String xml = "<script><foo><zoom>2</zoom><zoom>3</zoom></foo></script>";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node result = (Node)xPath.evaluate("script/foo", doc, XPathConstants.NODE);

		System.out.println(nodeToString(result));
		
		
		//System.out.println(new Symbol("RTS-12.19", (String) null, "RUB", SymbolType.FUTURES).getExchangeID());
		//System.out.println(new Symbol("F:RTS-12.19@:RUB").getExchangeID());
		fail("Not yet implemented");
	}

}
