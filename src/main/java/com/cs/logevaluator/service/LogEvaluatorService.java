package com.cs.logevaluator.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.logevaluator.entity.EventAlert;
import com.cs.logevaluator.model.Event;
import com.cs.logevaluator.repo.EventAlertRepository;
import com.cs.logevaluator.utils.LogEvaluatorsConstants;
import com.cs.logevaluator.validator.InputArgumentsValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LogEvaluatorService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogEvaluatorService.class);

	@Autowired
	private InputArgumentsValidator InputArgumentsValidator;

	@Autowired
	private EventAlertRepository eventAlertRepository;

	public void evaluate(String... args) {

		InputArgumentsValidator.validateArguments(args);

		evaluateEvent(args[0]);

	}

	private void evaluateEvent(String filePath) {

		Map<String, Event> tempEventMap = new HashMap<>();
		Map<String, EventAlert> alertMap = new HashMap<>();

		LOGGER.info("File processing started...");

		try (LineIterator lineIterator = FileUtils.lineIterator(new File(filePath))) {
		
			while (lineIterator.hasNext()) {
				Event event;
				try {
					event = new ObjectMapper().readValue(lineIterator.nextLine(), Event.class);
					LOGGER.trace("{}", event);

					if (tempEventMap.containsKey(event.getId())) {
						Event eventFromTempMap = tempEventMap.get(event.getId());
						long executionTime = calculateExecutionTime(event, eventFromTempMap);
						EventAlert alert = new EventAlert(event, Math.toIntExact(executionTime));

						if (executionTime > LogEvaluatorsConstants.EVENTALERTTHRESHOLD) {
							alert.setAlert(true);
							LOGGER.trace("Execution time for the event {} is {}ms", event.getId(), executionTime);
						}
						alertMap.put(event.getId(), alert);

						tempEventMap.remove(event.getId());
					} else {
						tempEventMap.put(event.getId(), event);
					}
				} catch (JsonProcessingException ex) {
					LOGGER.error("Exception occured while parsing the event "+ ex.getMessage());
				}
				if (LogEvaluatorsConstants.SAVEEVENTALERTTHRESHOLD == alertMap.size()) {
					LOGGER.debug("Saving {} event alerts", alertMap.size());
					eventAlertRepository.saveAll(alertMap.values());
					alertMap = new HashMap<>();
				}
			} 
			if (alertMap.size() > 0) {
				LOGGER.debug("Saving {} event alerts", alertMap.size());
				eventAlertRepository.saveAll(alertMap.values());
			}
		} catch (IOException ex) {
			LOGGER.error("Exception occured while accessing the file: "+ex.getMessage());
		}
		
		LOGGER.info("File processing completed...");
	}

	private long calculateExecutionTime(Event event1, Event event2) {

		Event startEvent = Stream.of(event1, event2)
				.filter(event -> LogEvaluatorsConstants.STARTED.equals(event.getState())).findFirst().orElse(null);
		Event endEvent = Stream.of(event1, event2)
				.filter(event -> LogEvaluatorsConstants.FINISHED.equals(event.getState())).findFirst().orElse(null);

		return Objects.requireNonNull(endEvent).getTimestamp() - Objects.requireNonNull(startEvent).getTimestamp();
	}
}
