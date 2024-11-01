package org.jsmart.zerocode.openapi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * OpenAPI suports parameters at several places
 *   https://swagger.io/docs/specification/v3_0/describing-parameters/
 * Currently: only querystring and path parameters are supported
 * 
 * There are multiple formats for manage non primitive values:
 * https://swagger.io/docs/specification/v3_0/serialization/
 * Currently: the supported parameters are
 * - path: primitive
 * - querystring: primitive
 * 
 * Currently: no error handling if parameter type is not supported
 */
public class ParameterSerializer {

	public String serializePathParams(String path, List<Parameter> oaParams) {
		for (Parameter oaParam : filterParams(oaParams, "path")) {
			DataGeneratorFactory factory = new DataGeneratorFactory();
			JsonNode value = factory.getItem(oaParam.getName(), oaParam.getSchema()).generateUrlEncodedJsonValue();
			path = path.replace("{" + oaParam.getName() + "}", value.asText());
		}
		return path;
	}

	public JsonNode getQueryParams(List<Parameter> oaParams) {
		ObjectNode params = new ObjectMapper().createObjectNode();
		for (Parameter oaParam : filterParams(oaParams, "query")) {
			DataGeneratorFactory factory = new DataGeneratorFactory();
			JsonNode value = factory.getItem(oaParam.getName(), oaParam.getSchema()).generateJsonValue();
			params.set(oaParam.getName(), value);
		}
		return params;
	}

	private List<Parameter> filterParams(List<Parameter> oaParams, String in) {
		if (oaParams == null)
			return new ArrayList<>();
		return oaParams.stream().filter(oaParam -> in.equals(oaParam.getIn())).collect(Collectors.toList());
	}

}
