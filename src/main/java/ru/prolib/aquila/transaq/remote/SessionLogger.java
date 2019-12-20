package ru.prolib.aquila.transaq.remote;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionLogger implements MessageInterceptor {
	private static final Logger logger;
	private static final String TYPE_OUT = "SEND";
	private static final String TYPE_IN = "RECV";
	
	static {
		logger = LoggerFactory.getLogger(SessionLogger.class);
	}
	
	private final ZoneId zone;
	private final BufferedWriter writer;
	private boolean closed = false;
	private Instant prev_time;
	
	public SessionLogger(BufferedWriter writer, ZoneId zone_id) {
		this.writer = writer;
		this.zone = zone_id;
	}
	
	public SessionLogger(BufferedWriter writer) {
		this(writer, ZoneId.of("Europe/Moscow"));
	}
	
	public SessionLogger(File file) throws IOException {
		this(new BufferedWriter(new FileWriter(file)));
		logger.info("Session log started: {}", file);
	}
	
	public SessionLogger() {
		this(new BufferedWriter(new OutputStreamWriter(System.out)));
		logger.info("Session log started: STDOUT");
	}
	
	private String formatTime(Instant time) {
		return ZonedDateTime.ofInstant(time, zone).toString();
	}
	
	private synchronized void write(String type, String message) {
		if ( closed ) {
			return;
		}
		
		Instant curr_time = Instant.now();
		long offset = prev_time == null ? 0 : ChronoUnit.MILLIS.between(prev_time, curr_time);
		prev_time = curr_time;
		String fs = " ", ls = System.lineSeparator();
		try {
			writer.write(new StringBuilder()
				.append(type).append(fs).append(formatTime(curr_time)).append(fs).append("+").append(offset).append(ls)
				.append(message).append(ls)
				.append(ls)
				.toString()
			);
		} catch ( IOException e ) {
			logger.error("Error writing to log: ", e);
			close();
		}
	}

	@Override
	public void incoming(String message) {
		write(TYPE_IN, message);
	}

	@Override
	public void outgoing(String message) {
		write(TYPE_OUT, message);
	}

	@Override
	public synchronized void close() {
		if ( closed ) {
			return;
		}
		closed = true;
		try {
			writer.close();
		} catch ( IOException e ) {
			logger.error("Close log failed: ", e);
		}
		logger.info("Session log closed");
	}

}
