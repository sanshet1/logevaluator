package com.cs.logevaluator;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cs.logevaluator.service.LogEvaluatorService;

@SpringBootApplication
public class LogEvaluatorApplication implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogEvaluatorApplication.class);
	
	@Autowired
	private LogEvaluatorService logEvaluatorService;
	
	public static void main(String[] args) {
		
		SpringApplication.run(LogEvaluatorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
				
		 StopWatch watch = new StopWatch();
		 watch.start();
		 logEvaluatorService.evaluate(args); 
		 watch.stop();
		
		 LOGGER.info("Total time taken to complete proccessing is :"+watch.getTime()+" ms" );
	}

}
