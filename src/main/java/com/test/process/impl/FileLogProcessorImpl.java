package com.test.process.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.test.model.Event;
import com.test.parse.DataParser;
import com.test.process.FileLogProcessor;
import com.test.process.LogProcessor;

public class FileLogProcessorImpl implements FileLogProcessor {

	private DataParser jsonParser;

	LogProcessor<Map<String, Event>> postProcessor;
	
	private static final Logger logger = LoggerFactory.getLogger(FileLogProcessorImpl.class);
	
	public FileLogProcessorImpl(DataParser jsonParser, LogProcessor<Map<String, Event>> postProcessor) {
		this.jsonParser = jsonParser;
		this.postProcessor = postProcessor;
	}
	
	@Override
	public void process(String filepath) {
		
		logger.debug("pre processing started");
		Map<String, Event> events = new HashMap<>();
		try (Stream<String> inputStream = Files.lines(Paths.get(filepath))) {
			inputStream
				.map(line -> jsonParser.parse(line))
				.forEach(event -> {
					event.setAssociatedEvent(events.put(event.getId(),event));
				});
		} catch (IOException e) {
			throw new RuntimeException();
		}

		logger.debug("pre processing completed");
		postProcessor.process(events);
	}

}
