package com.zerocode.oas.stepgen.io;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;


/** The Constant log. */
@Slf4j
@Component
public class IOUtils {



	/**
	 * Delete file.
	 *
	 * @param target the target
	 * @return true, if successful
	 */
	public boolean deleteFile(String target) {
		File file = new File(target);
		if (file.exists()) {
			file.delete();
			return true;
		}
		return false;
	}

	/**
	 * File exists.
	 *
	 * @param target the target
	 * @return true, if successful
	 */
	public boolean fileExists(String target) {
		File file = new File(target);
		if (file.exists()) {
			log.error("File {} already exists  ", target);
			return true;
		}
		return false;
	}

	/**
	 * Ouput steps to file.
	 *
	 * @param scenario  the scenario
	 * @param target    the target
	 * @param overwrite the overwrite
	 */
	public void ouputStepsToFile(Object scenario, String target, boolean overwrite) {
		ObjectMapper objMapper = new ObjectMapper();
		deleteIfExists(target, overwrite);
		try {
			File file = new File(target);
			file.createNewFile();
			objMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objMapper.writerWithDefaultPrettyPrinter().writeValue(file, scenario);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error creating target", e);
		}
	}

	/**
	 * Delete if exists.
	 *
	 * @param target the target
	 * @param overwrite the overwrite
	 * @param file the file
	 * @return the file
	 */
	private void deleteIfExists(String target, boolean overwrite) {
		if (fileExists(target)) {
			if (overwrite) {
				log.info("Target exists. Overwrite flag is set, deleting target");
				File file = new File(target);
				file.delete();
				log.info("Target {} deleted", target);
			} else {
				throw new IllegalArgumentException(
						"XXXXXXXXX Target exists and Overwrite flag set to False XXXXXXXXXXX ");
			}
		}
	}

	/**
	 * Read open API spec from location.
	 *
	 * @param source the source
	 * @return the open API
	 */
	public OpenAPI readOpenAPISpecFromLocation(String source) {
		return (new OpenAPIV3Parser()).read(source, null, null);
	}

}
