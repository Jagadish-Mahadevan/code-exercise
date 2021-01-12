package com.test.process.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.test.model.Event;

public class SlowEventFinder implements Callable<List<Event>> {

	//TODO: make configurable item
	private static final int MAX_MILLIS_ALLOWED = 4;
	
	private Map<String, Event> events;
	List<String> workingKeys;
	
	private static final Logger logger = LoggerFactory.getLogger(SlowEventFinder.class);
	
	public SlowEventFinder(Map<String, Event> events, List<String> workingKeys) {
		this.events = events;
		this.workingKeys = workingKeys;
	}

	@Override
	public List<Event> call() throws Exception {
		logger.debug("thread method called... {} {} ", events.size(), workingKeys.size());
		List<Event> persistList = new ArrayList<>();
		workingKeys.stream().forEach(eventId -> {
			if( (events.get(eventId).getTimestamp() > events.get(eventId).getAssociatedEvent().getTimestamp() &&
					events.get(eventId).getTimestamp() - events.get(eventId).getAssociatedEvent().getTimestamp() > MAX_MILLIS_ALLOWED ) ||
					(events.get(eventId).getTimestamp() < events.get(eventId).getAssociatedEvent().getTimestamp() && 
							events.get(eventId).getAssociatedEvent().getTimestamp() - events.get(eventId).getTimestamp() > MAX_MILLIS_ALLOWED)) {
				events.get(eventId).setAlert(true);
			}
			persistList.add(events.get(eventId));
		});

		logger.debug("thread method finished...");
		return persistList;
	}

}
