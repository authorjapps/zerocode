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
		
		// Header params are also added to the request, but not added immediatly
		// because after reading the requests, some additional media type headers may be added
		JsonNode headerParams = serializer.getHeaderParams(oaOperation.getParameters());
		
		JsonNode body = generateRequestBody(oaOperation.getRequestBody(), headerParams);
		if (!headerParams.isEmpty()) // 
			request.set("headers", headerParams);
		// empty object does not generates body, but does for primitive types (e.g. string)
		if (!(body.isEmpty() && body.isObject()))
			request.set("body", body);
		
		LOGGER.info("  Generated request: {}", request);

		// Create assert for the 2xx response codes only
		ObjectNode assertions = new ObjectMapper().createObjectNode();
		List<String> responseCodes = generateResponses(oaOperation.getResponses());
		LOGGER.info("  Generated response: {}", responseCodes);
		// Although not frequent, a response could contain more than one 2xx,
		// but $ONE.OF can't be use to assert the response. Takes the first (if any)
		if (!responseCodes.isEmpty())
			assertions.put("status", Integer.valueOf(responseCodes.get(0)));

		// Zerocode Step with the minimum of attributes
		Step step = new Step(null, null, path + " - " + oaOperation.getSummary(), 
				method, null, url, request, null, null, 
				assertions, null, null, false);
		return step;
	}

	private JsonNode generateRequestBody(RequestBody oaBody, JsonNode currentHeaders) {
		if (oaBody == null)
			return new ObjectMapper().createObjectNode();
		// By default, application/json is used (not required to set in headers), and it is the 
		// request body returned (this will exclude other that appear before, eg. application/xml)
		// But if none is found, returns the first one that finds and adds the media type to the headers
		Entry<String, MediaType> fallbackMediaType = null;
		for (Entry<String, MediaType> oaItem : oaBody.getContent().entrySet()) {
			if (fallbackMediaType == null)
				fallbackMediaType = oaItem; //keep the first.
			if ("application/json".equals(oaItem.getKey())) { // ignore other media types
				Schema<?> oaSchema = oaItem.getValue().getSchema();
				return generateJsonBody(oaSchema);
			}
		}
		// at this point, no json body was found
		if (fallbackMediaType == null) { // no mediatype found
			return new ObjectMapper().createObjectNode();
		} else { // there is a fallaback, add header and return the body
			((ObjectNode) currentHeaders).put("Content-Type", fallbackMediaType.getKey().toString());
			Schema<?> oaSchema = fallbackMediaType.getValue().getSchema();
			return generateJsonBody(oaSchema);
		}
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
