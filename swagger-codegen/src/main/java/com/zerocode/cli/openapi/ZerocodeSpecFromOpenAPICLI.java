package com.zerocode.cli.openapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.zerocode.openapi.template.processor.OpenAPIStepsGenerator;

@Component
public class ZerocodeSpecFromOpenAPICLI implements CommandMarker{

	@Autowired
	OpenAPIStepsGenerator generator;
	
	  @CliCommand(value = { "zc-gen-steps", "gc" }, help = "Generates zerocode steps from a swagger fille")
	    public String webSave(@CliOption(key = { "source", "file" }, help = "Location of input openapi jso/yaml contract.",mandatory = true) String  source, @CliOption(key = { "target", "file" }, mandatory = true, help = "The name of the file.") String target) {
	       generator.transform(source, target);
	        return "Done.";
	}
}
