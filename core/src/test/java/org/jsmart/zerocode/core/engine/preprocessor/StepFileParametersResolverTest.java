package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.Step;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.read;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class StepFileParametersResolverTest {
    private static final String STEP_FILES_PATH = "unit_test_files/step_file_parameters/user_steps.json";
    private static final String STEP_FILE_PATH = "unit_test_files/step_file_parameters/login_step.json";
    private static final String NO_PARAM_STEP_FILES_PATH = "unit_test_files/step_file_parameters/no_param_steps.json";

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private final ZeroCodeExternalFileProcessorImpl externalFileProcessor = new ZeroCodeExternalFileProcessorImpl(objectMapper);

    @Test
    public void testResolveStepFilesWillReplaceParametersInUrlRequestAndAssertions() throws IOException {
        final Step rawStep = stepFilesStep(STEP_FILES_PATH,
                "{\"username\": \"alice\", \"role\": \"ADMIN\", \"retries\": 3, \"active\": true}");

        final String innerStepsJson = resolveInnerStepsJson(rawStep);

        assertThat(read(innerStepsJson, "$[0].request.body.username"), is("alice"));
        assertThat(read(innerStepsJson, "$[0].request.body.role"), is("ADMIN"));
        assertThat(read(innerStepsJson, "$[1].url"), is("/api/v1/users/alice"));
        assertThat(read(innerStepsJson, "$[1].assertions.body.username"), is("alice"));
    }

    // Making sure both type supported: ${..} and {{..}}
    @Test
    public void testResolveDoubleCurlyBraceTokenWillReplaceParameter() throws IOException {
        final Step rawStep = stepFilesStep(STEP_FILES_PATH,
                "{\"username\": \"alice\", \"role\": \"ADMIN\", \"retries\": 3, \"active\": true}");

        final String innerStepsJson = resolveInnerStepsJson(rawStep);

        assertThat(read(innerStepsJson, "$[1].assertions.body.role"), is("ADMIN"));
    }

    @Test
    public void testResolveNumberAndBooleanParametersWillGetTextForTypeCasts() throws IOException {
        final Step rawStep = stepFilesStep(STEP_FILES_PATH,
                "{\"username\": \"alice\", \"role\": \"ADMIN\", \"retries\": 3, \"active\": true}");

        final String innerStepsJson = resolveInnerStepsJson(rawStep);

        assertThat(read(innerStepsJson, "$[0].request.body.retries"), is("(int)3"));
        assertThat(read(innerStepsJson, "$[0].request.body.active"), is("(boolean)true"));
    }

    @Test
    public void testResolveSingleStepFileWillReplaceParameters() throws IOException {
        final Step rawStep = parseStep("{\"id\": \"login_as_admin\", "
                + "\"stepFile\": \"${JSON.FILE:" + STEP_FILE_PATH + "}\", "
                + "\"parameters\": {\"username\": \"alice\", \"password\": \"secret\"}}");

        final Step resolvedStep = resolve(rawStep);
        final String innerStepJson = objectMapper.writeValueAsString(resolvedStep.getStepFile());

        assertThat(read(innerStepJson, "$.request.body.username"), is("alice"));
        assertThat(read(innerStepJson, "$.request.body.password"), is("secret"));
    }

    @Test
    public void testResolveParameterValueWithOtherTokensWillKeepThemForLaterResolution() throws IOException {
        final Step rawStep = stepFilesStep(STEP_FILES_PATH,
                "{\"username\": \"user_${RANDOM.NUMBER}\", \"role\": \"${$.signup.response.body.role}\", "
                        + "\"retries\": 3, \"active\": true}");

        final String innerStepsJson = resolveInnerStepsJson(rawStep);

        assertThat(read(innerStepsJson, "$[0].request.body.username"), is("user_${RANDOM.NUMBER}"));
        assertThat(read(innerStepsJson, "$[0].request.body.role"), is("${$.signup.response.body.role}"));
    }

    @Test
    public void testResolveStepFilesWillKeepIdAndParametersOnReferencingStep() throws IOException {
        final Step rawStep = stepFilesStep(STEP_FILES_PATH,
                "{\"username\": \"alice\", \"role\": \"ADMIN\", \"retries\": 3, \"active\": true}");

        final Step resolvedStep = resolve(rawStep);

        assertThat(resolvedStep.getId(), is("create_admin"));
        assertThat(resolvedStep.getParameters().get("username").asText(), is("alice"));
    }

    @Test
    public void testResolveWithMissingParameterWillFailWithClearMessage() throws IOException {
        final Step rawStep = stepFilesStep(STEP_FILES_PATH,
                "{\"username\": \"alice\", \"retries\": 3, \"active\": true}");

        final RuntimeException exception = assertThrows(RuntimeException.class, () -> resolve(rawStep));

        assertThat(exception.getMessage(), is("stepFiles '" + STEP_FILES_PATH + "' (id: create_admin) "
                + "uses ${PARAM.role}, but 'role' is not in \"parameters\""));
    }

    @Test
    public void testResolveStepFileWithoutParametersWillFailForUsedParameter() throws IOException {
        final Step rawStep = parseStep("{\"id\": \"login_as_admin\", "
                + "\"stepFile\": \"${JSON.FILE:" + STEP_FILE_PATH + "}\"}");

        final RuntimeException exception = assertThrows(RuntimeException.class, () -> resolve(rawStep));

        assertThat(exception.getMessage(), is("stepFile '" + STEP_FILE_PATH + "' (id: login_as_admin) "
                + "uses ${PARAM.username}, but 'username' is not in \"parameters\""));
    }

    @Test
    public void testResolveInlineStepFileWithMissingParameterWillFailNamingInlineSource() throws IOException {
        final Step rawStep = parseStep("{\"id\": \"inline_step\", "
                + "\"stepFile\": {\"name\": \"echo\", \"url\": \"\", \"operation\": \"\", "
                + "\"request\": {\"user\": \"${PARAM.username}\"}, \"assertions\": {}}}");

        final RuntimeException exception = assertThrows(RuntimeException.class, () -> resolve(rawStep));

        assertThat(exception.getMessage(), is("stepFile '<inline>' (id: inline_step) "
                + "uses ${PARAM.username}, but 'username' is not in \"parameters\""));
    }

    @Test
    public void testResolveNonObjectParametersWillFailWithClearMessage() throws IOException {
        final Step rawStep = stepFilesStep(STEP_FILES_PATH, "[\"alice\"]");

        final RuntimeException exception = assertThrows(RuntimeException.class, () -> resolve(rawStep));

        assertThat(exception.getMessage(), is("stepFiles '" + STEP_FILES_PATH + "' (id: create_admin) "
                + "has \"parameters\" that is not a JSON object: [\"alice\"]"));
    }

    @Test
    public void testResolveParametersWithoutStepFileWillFailWithClearMessage() throws IOException {
        final Step rawStep = parseStep("{\"name\": \"plain\", \"url\": \"\", \"operation\": \"\", "
                + "\"request\": {}, \"assertions\": {}, \"parameters\": {\"username\": \"alice\"}}");

        final RuntimeException exception = assertThrows(RuntimeException.class, () -> resolve(rawStep));

        assertThat(exception.getMessage(), is("Step (name: plain, id: null) has \"parameters\", "
                + "but \"parameters\" is only supported together with \"stepFile\" or \"stepFiles\""));
    }

    @Test
    public void testResolveStepWithoutStepFileWillGetSameStep() throws IOException {
        final Step rawStep = parseStep("{\"name\": \"plain\", \"url\": \"\", \"operation\": \"\", "
                + "\"request\": {\"user\": \"${PARAM.username}\"}, \"assertions\": {}}");

        final Step resolvedStep = resolve(rawStep);

        assertThat(resolvedStep, sameInstance(rawStep));
    }

    @Test
    public void testResolveStepFilesWithoutParamTokensWillGetUnchangedSteps() throws IOException {
        final Step rawStep = parseStep("{\"id\": \"plain_files\", "
                + "\"stepFiles\": \"${JSON.FILE:" + NO_PARAM_STEP_FILES_PATH + "}\"}");
        final Step inlinedStep = externalFileProcessor.resolveExtJsonFile(rawStep);

        final Step resolvedStep = externalFileProcessor.resolveStepFileParameters(rawStep, inlinedStep);

        assertThat(objectMapper.writeValueAsString(resolvedStep), is(objectMapper.writeValueAsString(inlinedStep)));
    }

    @Test
    public void testCreateFromResolvedStepFilesWillGetInnerStepsWithParameters() throws IOException {
        final Step rawStep = stepFilesStep(STEP_FILES_PATH,
                "{\"username\": \"bob\", \"role\": \"VIEWER\", \"retries\": 1, \"active\": false}");

        final List<Step> innerSteps = externalFileProcessor.createFromStepFile(resolve(rawStep), rawStep.getId());

        assertThat(innerSteps.size(), is(2));
        assertThat(innerSteps.get(1).getUrl(), is("/api/v1/users/bob"));
    }

    private Step stepFilesStep(String stepFilesPath, String parametersJson) throws IOException {
        return parseStep("{\"id\": \"create_admin\", "
                + "\"stepFiles\": \"${JSON.FILE:" + stepFilesPath + "}\", "
                + "\"parameters\": " + parametersJson + "}");
    }

    private Step parseStep(String stepJson) throws IOException {
        return objectMapper.readValue(stepJson, Step.class);
    }

    private Step resolve(Step rawStep) {
        final Step inlinedStep = externalFileProcessor.resolveExtJsonFile(rawStep);
        return externalFileProcessor.resolveStepFileParameters(rawStep, inlinedStep);
    }

    private String resolveInnerStepsJson(Step rawStep) throws IOException {
        return objectMapper.writeValueAsString(resolve(rawStep).getStepFiles());
    }
}
