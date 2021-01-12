package com.test.process.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.test.model.Event;
import com.test.process.FileLogPostProcessor;

public class FileLogPostProcessorImpl implements FileLogPostProcessor  {

	//TODO: make a configurable item
	private static final int POOL_SIZE = 10;

	private JdbcTemplate jdbcTemplate;
	
	public FileLogPostProcessorImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static final Logger logger = LoggerFactory.getLogger(FileLogPostProcessorImpl.class);
	
	public void process(Map<String, Event> events) {
		
		logger.debug("Post processing started... {} ", events.size());
		ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);

		List<String> eventKeys = new ArrayList<>();
		Set<String> eventKeySet = events.keySet();
		Iterator<String> keysIterator = eventKeySet.iterator();
		int counter=1;
		int listPerThread = eventKeySet.size()/POOL_SIZE;
		List<Future<List<Event>>> futures = new ArrayList<>(POOL_SIZE);
		
		//TODO: Logic to be corrected for event count not being a number ending with 0
		while(keysIterator.hasNext()) {
			eventKeys.add(keysIterator.next());
			if(listPerThread == counter) {
				logger.debug("creating thread...");
				SlowEventFinder task = new SlowEventFinder(events, eventKeys);
				futures.add(executorService.submit(task));
				eventKeys = new ArrayList<>();
				counter = 0;
			}
			counter++;
		}

	    for (Future<List<Event>> future : futures) {
	        try {
	        	List<Event> processed = future.get();
	        	logger.debug("getting result from a thread...");
	        	//TODO: create a DAO and move the DB operations to that. JdbcTemplate will be injected on to that DAO
	        	// so that all DB opertions can be abstracted away in to the DAO
	        	processed.stream().forEach(e -> {
	        		this.jdbcTemplate.update( "insert into event (id, state, type, host, timestamp, alert) values(?,?,?,?,?,?)",
	        				e.getId(), e.getState(), e.getType(), e.getHost(), e.getTimestamp(), e.getAlert());
	        		
	        	});
	        	
	        	//TODO: batch insert of all records from each thread and to be done in one transaction
	        	
	        	/*
				this.jdbcTemplate.batchUpdate(
			            "insert into event (id, state, type, host, timestamp, alert) values(?,?,?,?,?,?)",
			            new BatchPreparedStatementSetter() {

			                public void setValues(PreparedStatement ps, int i) throws SQLException {
			                    ps.setString(1, processed.get(i).getId());
			                    ps.setString(2, processed.get(i).getState());
			                    ps.setString(3, processed.get(i).getType());
			                    ps.setString(4, processed.get(i).getHost());
			                    ps.setDouble(5, processed.get(i).getTimestamp());
			                    ps.setBoolean(6, processed.get(i).getAlert());
			                }

			                public int getBatchSize() {
			                    return processed.size();
			                }

			            });
			     */
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
	    }
	    
		if(futures.stream().allMatch(f -> f.isDone())) {
			logger.debug("shutting down executorservice");
			executorService.shutdown();
		}
		
		//int count = jdbcTemplate.queryForInt("select count(*) from event");
		//logger.debug("DB record count : " + count);
	}
}
