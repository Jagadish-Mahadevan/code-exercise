package com.test.parse;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.model.Event;

public class JsonParserImpl implements DataParser {

	private ObjectMapper mapper;
	
	public JsonParserImpl(ObjectMapper mapper) {
		super();
		this.mapper = mapper;
	}

	@Override
	public Event parse(String input) {
		Event event = null;
		try {
			event = mapper.readValue(input, Event.class);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		return event;
	}

}
