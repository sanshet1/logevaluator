package com.cs.logevaluator.validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InputArgumentsValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(InputArgumentsValidator.class);

	public void validateArguments(String[] args) {
		LOGGER.debug("Validating input arguments...");
		if (args.length == 0) {
			throw new IllegalArgumentException("Please provide valid absolute path for log file to evaluate.");
		} else {

			try {
				isValidFilePath(args[0]);
			} catch (FileNotFoundException e) {
				LOGGER.error("Unable to find the file " + args[0]);
			}
		}
	}

	private void isValidFilePath(String path) throws FileNotFoundException {
		File file = new File(path);
		try {
			file.getCanonicalPath();
		} catch (IOException e) {
			throw new FileNotFoundException("Unable to find the file " + path);
		}
	}

}
