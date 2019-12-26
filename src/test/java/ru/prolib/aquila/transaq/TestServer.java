package ru.prolib.aquila.transaq;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;

public class TestServer extends JTransaqServer {
	protected static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TestServer.class);
	}
	
	static void debug_msg(String msg, int max_len) {
		msg = msg.replaceAll("(\r|\n)", "");
		if ( msg.length() > max_len ) {
			msg = msg.substring(0, max_len);
		}
		logger.debug(msg);		
	}
	
	static void debug_msg(String msg) {
		debug_msg(msg, 64);
	}
	
	public interface ClientAction {
		void checkExpectation(ClientAction actual);
	}
	
	public static class ClientCalledInitialize implements ClientAction {
		private final String logPath;
		private final int logLevel;
		
		public ClientCalledInitialize(String log_path, int log_level) {
			this.logPath = log_path;
			this.logLevel = log_level;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != ClientCalledInitialize.class ) {
				return false;
			}
			ClientCalledInitialize o = (ClientCalledInitialize) other;
			return new EqualsBuilder()
					.append(o.logPath, logPath)
					.append(o.logLevel, logLevel)
					.build();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(logPath)
					.append(logLevel)
					.build();
		}
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
		
		@Override
		public void checkExpectation(ClientAction actual) {
			assertNotNull(actual);
			assertEquals(this, actual);
		}
		
	}
	
	public static class ClientCalledSetLogLevel implements ClientAction {
		private final int logLevel;
		
		public ClientCalledSetLogLevel(int log_level) {
			this.logLevel = log_level;
		}

		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != ClientCalledSetLogLevel.class ) {
				return false;
			}
			ClientCalledSetLogLevel o = (ClientCalledSetLogLevel) other;
			return new EqualsBuilder()
					.append(o.logLevel, logLevel)
					.build();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(logLevel)
					.build();
		}
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}

		@Override
		public void checkExpectation(ClientAction actual) {
			assertNotNull(actual);
			assertEquals(this, actual);
		}
		
	}
	
	public static class ClientCalledUnInitialize implements ClientAction {
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != ClientCalledUnInitialize.class ) {
				return false;
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			return 12345;
		}
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
		
		@Override
		public void checkExpectation(ClientAction actual) {
			assertNotNull(actual);
			assertEquals(this, actual);
		}
		
	}
	
	public static class ClientCalledSendCommand implements ClientAction {
		private final String xml;
		
		public ClientCalledSendCommand(String xml) {
			this.xml = xml;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null | other.getClass() != ClientCalledSendCommand.class ) {
				return false;
			}
			ClientCalledSendCommand o = (ClientCalledSendCommand) other;
			return new EqualsBuilder()
					.append(o.xml, xml)
					.build();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(xml)
					.build();
		}
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}

		@Override
		public void checkExpectation(ClientAction actual) {
			assertNotNull(actual);
			assertEquals("Expected " + this + " but " + actual, ClientCalledSendCommand.class, actual.getClass());
			ClientCalledSendCommand other = (ClientCalledSendCommand) actual;
			
			XMLUnit.setIgnoreWhitespace(true);
			XMLUnit.setIgnoreAttributeOrder(true);
			DetailedDiff diff;
			try {
				diff = new DetailedDiff(XMLUnit.compareXML(xml, other.xml));
			} catch ( Exception e ) {
				throw new RuntimeException(e);
			}
	        assertEquals("Differences found: " + diff.toString(), 0, diff.getAllDifferences().size());
		}
		
	}
	
	public static class ClientCalledExplicitCall implements ClientAction {
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != ClientCalledExplicitCall.class ) {
				return false;
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			return 34567;
		}
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
		
		@Override
		public void checkExpectation(ClientAction actual) {
			assertNotNull(actual);
			assertEquals(this, actual);
		}
		
	}
	
	public static class Action {
		
		public static class SendXml implements Runnable {
			private final JTransaqHandler handler;
			private final String xml;
			
			public SendXml(JTransaqHandler handler, String xml) {
				this.handler = handler;
				this.xml = xml;
			}
			
			@Override
			public void run() {
				handler.Handle(xml);
			}
			
			@Override
			public String toString() {
				return new StringBuilder().append(getClass().getSimpleName()).append("[xml=").append(xml).append("]").toString();
			}	
			
		}
		
		public static class CountDown implements Runnable {
			private final CountDownLatch latch;
			
			public CountDown(CountDownLatch latch) {
				this.latch = latch;
			}

			@Override
			public void run() {
				latch.countDown();
			}
		}
		
	}
	
	public static class ScriptPhase {
		private final ClientAction expectedAction;
		private final List<Runnable> responseActions;
		
		public ScriptPhase(ClientAction expected_action) {
			this.expectedAction = expected_action;
			this.responseActions = new ArrayList<>();
		}
		
		
		public ScriptPhase addResponseAction(Runnable action) {
			responseActions.add(action);
			return this;
		}
		
		public ClientAction getExpectedAction() {
			return expectedAction;
		}
		
		public List<Runnable> getResponseActions() {
			return responseActions;
		}
	}
	
	private final List<ScriptPhase> phases;
	private boolean failure = false;

	public TestServer(JTransaqHandler handler, List<ScriptPhase> phases) throws Exception {
		super(handler);
		this.phases = phases;
	}
	
	private synchronized void nextScriptPhase(ClientAction actual_action) {
		//debug_msg(actual_action.toString());
		if ( failure ) {
			return; // just skip
		}
		if ( phases.size() == 0 ) {
			failure = true;
			throw new IllegalStateException("Unexpected action: " + actual_action);
		}
		ScriptPhase phase = phases.get(0);
		try {
			phase.getExpectedAction().checkExpectation(actual_action);
		} catch ( AssertionError e ) {
			failure = true;
			throw new IllegalStateException(e);
		}
		for ( Runnable action : phase.getResponseActions() ) {
			//debug_msg(action.toString());
			action.run();
		}
		phases.remove(0);
	}
	
	public void ExplicitCall() {
		nextScriptPhase(new ClientCalledExplicitCall());
	}
	
	@Override
	public void Initialize(String log_path, int log_level) throws Exception {
		nextScriptPhase(new ClientCalledInitialize(log_path, log_level));
	}
	
	@Override
	public void SetLogLevel(int log_level) throws Exception {
		nextScriptPhase(new ClientCalledSetLogLevel(log_level));
	}
	
	@Override
	public void UnInitialize() throws Exception {
		nextScriptPhase(new ClientCalledUnInitialize());
	}
	
	@Override
	public void SendCommand(String data) throws Exception {
		nextScriptPhase(new ClientCalledSendCommand(data));
	}

}
