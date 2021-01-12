package com.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.test.config.SpringContextConfiguration;
import com.test.process.FileLogProcessor;

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
