package ru.prolib.aquila.transaq.impl.mp;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;

public class BoardsProcessorTest {
	private IMocksControl control;
	private TQParser parserMock;
	private TQReactor reactorMock;
	private XMLStreamReader readerMock;
	private BoardsProcessor service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parserMock = control.createMock(TQParser.class);
		reactorMock = control.createMock(TQReactor.class);
		readerMock = control.createMock(XMLStreamReader.class);
		service = new BoardsProcessor(reactorMock, parserMock);
	}

	@Test
	public void testProcessMessage() throws Exception {
		List<Board> boards = new ArrayList<>();
		boards.add(control.createMock(Board.class));
		boards.add(control.createMock(Board.class));
		boards.add(control.createMock(Board.class));
		expect(parserMock.readBoards(readerMock)).andReturn(boards);
		reactorMock.updateBoards(boards);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}
