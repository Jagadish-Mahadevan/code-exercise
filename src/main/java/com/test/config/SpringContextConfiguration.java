package com.test.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.parse.DataParser;
import com.test.parse.JsonParserImpl;
import com.test.process.FileLogPostProcessor;
import com.test.process.FileLogProcessor;
import com.test.process.impl.FileLogPostProcessorImpl;
import com.test.process.impl.FileLogProcessorImpl;
import com.test.process.impl.SlowEventFinder;

@Configuration
public class SpringContextConfiguration {

	@Bean(name = "fileProcessor")
	public FileLogProcessor processor(DataParser parser, FileLogPostProcessor postProcesso) {
		return new FileLogProcessorImpl(parser, postProcesso);
	}

	@Bean
	public SlowEventFinder finder() {
		return new SlowEventFinder(null, null);
	}

	@Bean
	public FileLogPostProcessor postProcessor(JdbcTemplate jdbcTemplate) {
		return new FileLogPostProcessorImpl(jdbcTemplate);
	}

	@Bean
	public DataParser jsonParser(ObjectMapper mapper) {
		return new JsonParserImpl(mapper);
	}

	@Bean
	public ObjectMapper ObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public DataSource dataSource() {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUsername("sa");
		dataSource.setUrl("jdbc:hsqldb:file:D/Java/testdb");
		Resource initSchema = new ClassPathResource("jdbc/schema.sql");
		DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema);
		DatabasePopulatorUtils.execute(databasePopulator, dataSource);
		return dataSource;

	}
}