package com.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.test.config.SpringContextConfiguration;
import com.test.process.FileLogProcessor;

/**
 * Similar to an ETL operation.
 * Here the extraction is done in the FileLogProcessor  
 * Transformation is done in FileLogPostProcessor/SlowEventFinder
 * Loading is done in the DAO (TODO)
 * 
 * this standalone assumes that the system is vertically scaled up to deal with memory while handling larger files.
 * also the file is read in one go to minimise the I/O operation  
 */

public class App 
{
	private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
    	ApplicationContext context = new AnnotationConfigApplicationContext(SpringContextConfiguration.class);
    	FileLogProcessor processor = context.getBean("fileProcessor", FileLogProcessor.class);
    	logger.debug("Spring context loaded..");
    	processor.process(args[0]);
    	logger.debug("Process Complete");
    }
}
