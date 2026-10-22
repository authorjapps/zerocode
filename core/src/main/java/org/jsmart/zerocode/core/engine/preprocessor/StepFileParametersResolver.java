package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.jsmart.zerocode.core.domain.Step;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeValueTokens.JSON_PAYLOAD_FILE;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeValueTokens.STEP_FILE_PARAM;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeValueTokens.YAML_PAYLOAD_FILE;
import static org.jsmart.zerocode.core.utils.SmartUtils.getJsonFilePhToken;

/**
 * Replaces ${PARAM.name} (or {{PARAM.name}}) tokens inside an inlined "stepFile"/"stepFiles" with the
 * values from the "parameters" of the referencing step.
 *
 * Runs before the usual token resolution, so a parameter value may itself carry tokens such as
 * ${RANDOM.NUMBER} or ${$.earlier_step.response.body.id}, which are then resolved as normal.
 * The parameters apply only to the steps of that one reference and do not leak into the scenario.
 */
public class StepFileParametersResolver {
    private static final String QUOTED_PARAM = Pattern.quote(STEP_FILE_PARAM);
    private static final Pattern PARAM_TOKEN_PATTERN = Pattern.compile(
            "\\$\\{" + QUOTED_PARAM + "([^}]+)\\}|\\{\\{" + QUOTED_PARAM + "([^}]+)\\}\\}");
    private static final String STEP_FILE = "stepFile";
    private static final String STEP_FILES = "stepFiles";
    private static final String INLINE_SOURCE = "<inline>";

    private final ObjectMapper objectMapper;

    public StepFileParametersResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Fills in the ${PARAM.x} values of the step file content.
     *
     * @param rawStep     the step before the file was read, used to name the file in error messages
     * @param inlinedStep the step after the file was read
     * @return the step with its parameters filled in, or inlinedStep as-is if it has no step file
     */
    public Step resolve(Step rawStep, Step inlinedStep) {
        final JsonNode parameters = inlinedStep.getParameters();
        final boolean hasParameters = parameters != null && !parameters.isNull();

        if (!hasStepFileContent(inlinedStep)) {
            failIfParametersPresent(hasParameters, inlinedStep);
            return inlinedStep;
        }
        if (hasParameters && !parameters.isObject()) {
            throw new RuntimeException(describeSource(rawStep, inlinedStep.getId())
                    + " has \"parameters\" that is not a JSON object: " + parameters);
        }

        final ParameterContext context = new ParameterContext(
                hasParameters ? parameters : JsonNodeFactory.instance.objectNode(),
                describeSource(rawStep, inlinedStep.getId()));

        final ObjectNode stepNode = objectMapper.valueToTree(inlinedStep);
        final String stepFileField = inlinedStep.getStepFile() != null ? STEP_FILE : STEP_FILES;
        stepNode.set(stepFileField, substitute(stepNode.get(stepFileField), context));

        return toStep(stepNode, inlinedStep.getId());
    }

    private boolean hasStepFileContent(Step step) {
        final List<JsonNode> stepFiles = step.getStepFiles();
        return step.getStepFile() != null || (stepFiles != null && !stepFiles.isEmpty());
    }

    private void failIfParametersPresent(boolean hasParameters, Step step) {
        if (!hasParameters) {
            return;
        }
        throw new RuntimeException("Step (name: " + step.getName() + ", id: " + step.getId()
                + ") has \"parameters\", but \"parameters\" is only supported together with \"stepFile\" or \"stepFiles\"");
    }

    private JsonNode substitute(JsonNode node, ParameterContext context) {
        if (node.isTextual()) {
            return TextNode.valueOf(replaceParamTokens(node.asText(), context));
        }
        if (node.isArray()) {
            final ArrayNode resolvedArray = JsonNodeFactory.instance.arrayNode();
            node.forEach(element -> resolvedArray.add(substitute(element, context)));
            return resolvedArray;
        }
        if (node.isObject()) {
            final ObjectNode resolvedObject = JsonNodeFactory.instance.objectNode();
            node.fields().forEachRemaining(field -> resolvedObject.set(field.getKey(), substitute(field.getValue(), context)));
            return resolvedObject;
        }
        return node;
    }

    private String replaceParamTokens(String text, ParameterContext context) {
        final Matcher matcher = PARAM_TOKEN_PATTERN.matcher(text);
        final StringBuffer resolved = new StringBuffer();
        while (matcher.find()) {
            final String paramName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            final String paramValue = context.valueOf(paramName, matcher.group(0));
            matcher.appendReplacement(resolved, Matcher.quoteReplacement(paramValue));
        }
        matcher.appendTail(resolved);
        return resolved.toString();
    }

    private Step toStep(ObjectNode stepNode, String stepId) {
        try {
            return objectMapper.treeToValue(stepNode, Step.class);
        } catch (JsonProcessingException exx) {
            throw new RuntimeException("Failed to apply \"parameters\" to step file content of step id: " + stepId, exx);
        }
    }

    private String describeSource(Step rawStep, String stepId) {
        final String sourceFiles = rawStep.getStepFile() != null
                ? STEP_FILE + " '" + fileNameOf(rawStep.getStepFile()) + "'"
                : STEP_FILES + " '" + rawStep.getStepFiles().stream().map(this::fileNameOf).collect(joining(", ")) + "'";
        return sourceFiles + " (id: " + stepId + ")";
    }

    private String fileNameOf(JsonNode fileReference) {
        if (!fileReference.isTextual()) {
            return INLINE_SOURCE;
        }
        final String token = getJsonFilePhToken(fileReference.asText());
        if (token == null) {
            return fileReference.asText();
        }
        return removeStart(removeStart(token, JSON_PAYLOAD_FILE), YAML_PAYLOAD_FILE);
    }

    private static class ParameterContext {
        private final JsonNode parameters;
        private final String sourceDescription;

        ParameterContext(JsonNode parameters, String sourceDescription) {
            this.parameters = parameters;
            this.sourceDescription = sourceDescription;
        }

        String valueOf(String paramName, String paramToken) {
            final JsonNode value = parameters.get(paramName);
            if (value == null) {
                throw new RuntimeException(sourceDescription + " uses " + paramToken
                        + ", but '" + paramName + "' is not in \"parameters\"");
            }
            return value.isValueNode() ? value.asText() : value.toString();
        }
    }
}
