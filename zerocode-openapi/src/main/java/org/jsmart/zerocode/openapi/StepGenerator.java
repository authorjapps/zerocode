package org.jsmart.zerocode.openapi;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsmart.zerocode.core.domain.Step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import joptsimple.internal.Strings;

public class StepGenerator {

    private static final org.slf4j.Logger LOGGER = getLogger(StepGenerator.class);

	/**
	 * Generates a Zerocode Step object for a given path and http method
	 * from an OpenAPI operation object
	 */
	public Step parseOperation(String path, String method, Operation oaOperation) {
		LOGGER.info("Generating step: {} {}", method, path);

		// Path params must be serialized in the path
		ParameterSerializer serializer = new ParameterSerializer();
		String url = serializer.serializePathParams(path, oaOperation.getParameters());
		
		// Non primitive query params must be serialized in the path
		url = serializer.serializeQueryParams(url, oaOperation.getParameters());

		LOGGER.info("  Generated url: {}", url);

		ObjectNode request = new ObjectMapper().createObjectNode();

		// Primitive query params are added to the request
		JsonNode queryParams = serializer.getQueryParams(oaOperation.getParameters());
		if (!queryParams.isEmpty())
			request.set("queryParams", queryParams);

		JsonNode body = generateRequestBody(oaOperation.getRequestBody());
		// empty object does not generates body, but does for primitive types (e.g. string)
		if (!(body.isEmpty() && body.isObject()))
			request.set("body", body);
		
		LOGGER.info("  Generated request: {}", request);

		// Create assert for the 2xx response codes only
		ObjectNode assertions = new ObjectMapper().createObjectNode();
		List<String> responseCodes = generateResponses(oaOperation.getResponses());
		LOGGER.info("  Generated response: {}", responseCodes);
		if (!responseCodes.isEmpty())
			assertions.put("status", "$ONE.OF:[" + Strings.join(responseCodes, ", ") + "]");

		// Zerocode Step with the minimum of attributes
		Step step = new Step(null, null, path + " - " + oaOperation.getSummary(), 
				method, null, url, request, null, null, 
				assertions, null, null, false);
		return step;
	}

	private JsonNode generateRequestBody(RequestBody oaBody) {
		if (oaBody == null)
			return new ObjectMapper().createObjectNode();
		for (Entry<String, MediaType> oaItem : oaBody.getContent().entrySet()) {
			if ("application/json".equals(oaItem.getKey())) { // ignore other media types
				Schema<?> oaSchema = oaItem.getValue().getSchema();
				return generateJsonBody(oaSchema);
			}
		}
		return new ObjectMapper().createObjectNode(); // no mediatype found
	}

	private JsonNode generateJsonBody(Schema<?> oaSchema) {
		DataGeneratorFactory factory = new DataGeneratorFactory();
		if (oaSchema.getType() == null) // request body does not set the type if it is an object
			oaSchema.setType("object");
		return factory.getItem("body", oaSchema).generateJsonValue(); //actual generation of data
	}

	private List<String> generateResponses(Map<String, ApiResponse> oaResponses) {
		List<String> responseCodes = new ArrayList<>();
		if (oaResponses == null)
			return responseCodes;
		for (Entry<String, ApiResponse> oaResponse : oaResponses.entrySet()) {
			if (oaResponse.getKey().startsWith("2"))
				responseCodes.add(oaResponse.getKey());
		}
		return responseCodes;
	}

}
