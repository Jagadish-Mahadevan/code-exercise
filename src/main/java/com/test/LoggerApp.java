package com.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.model.Event;

public class LoggerApp {

	public static void main(String[] args) {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("D:/Java/events15.txt"))) {
			
			 for(int size=0;size<10000000;size++) {
				 Event start = new Event();
				 start.setHost("12345");
				 String id = UUID.randomUUID().toString();
				 start.setId(id);
				 start.setState("STARTED");
				 start.setTimestamp(1491377495212L);
				 start.setType("APPLICATION_LOG");
			 
				 Event end = new Event();
				 end.setHost("12345");
				 end.setId(id);
				 end.setState("COMPLETED");
				 if(size % 1000 == 0) {
					 end.setTimestamp(1491377495217L);
				 } else {
					 end.setTimestamp(1491377495216L);
				 }
				 end.setType("APPLICATION_LOG");
				 
				 writer.write(new ObjectMapper().writeValueAsString(start));
				 writer.newLine();
				 writer.write(new ObjectMapper().writeValueAsString(end));
				 writer.newLine();
			 }

        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
