package org.jsmart.zerocode.core.di.main;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsmart.zerocode.core.di.module.CsvParserModule;
import org.jsmart.zerocode.core.di.module.GsonModule;
import org.jsmart.zerocode.core.di.module.HttpClientModule;
import org.jsmart.zerocode.core.di.module.ObjectMapperModule;
import org.jsmart.zerocode.core.di.module.PropertiesInjectorModule;
import org.jsmart.zerocode.core.engine.executor.ApiServiceExecutor;
import org.jsmart.zerocode.core.engine.executor.ApiServiceExecutorImpl;
import org.jsmart.zerocode.core.engine.executor.httpapi.HttpApiExecutor;
import org.jsmart.zerocode.core.engine.executor.httpapi.HttpApiExecutorImpl;
import org.jsmart.zerocode.core.engine.executor.javaapi.JavaMethodExecutor;
import org.jsmart.zerocode.core.engine.executor.javaapi.JavaMethodExecutorImpl;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessorImpl;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeExternalFileProcessor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeExternalFileProcessorImpl;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeParameterizedProcessor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeParameterizedProcessorImpl;
import org.jsmart.zerocode.core.engine.sorter.ZeroCodeSorter;
import org.jsmart.zerocode.core.engine.sorter.ZeroCodeSorterImpl;
import org.jsmart.zerocode.core.engine.validators.ZeroCodeValidator;
import org.jsmart.zerocode.core.engine.validators.ZeroCodeValidatorImpl;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.jsmart.zerocode.core.report.ZeroCodeReportGeneratorImpl;
import org.jsmart.zerocode.core.runner.ZeroCodeMultiStepsScenarioRunner;
import org.jsmart.zerocode.core.runner.ZeroCodeMultiStepsScenarioRunnerImpl;

import static org.jsmart.zerocode.core.utils.EnvUtils.getEnvValueString;
import static org.jsmart.zerocode.core.utils.PropertiesProviderUtils.checkAndLoadOldProperties;
import static org.jsmart.zerocode.core.utils.PropertiesProviderUtils.loadAbsoluteProperties;
import static org.jsmart.zerocode.core.utils.SmartUtils.isValidAbsolutePath;

public class ApplicationMainModule extends AbstractModule {
    private static final Logger LOGGER = Logger.getLogger(ApplicationMainModule.class.getName());

    private static final Pattern ENV_PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    private final String serverEnv;

    public ApplicationMainModule(String serverEnv) {
        this.serverEnv = serverEnv;
    }

    @Override
    public void configure() {
        /*
         * Install other guice modules
         */
        install(new ObjectMapperModule());
        install(new HttpClientModule());
        install(new GsonModule());
        install(new PropertiesInjectorModule(serverEnv));
        install(new CsvParserModule());
        //install(new KafkaModule());

        /*
         * Bind Direct classes, classes to interfaces etc
         */
        bind(ZeroCodeMultiStepsScenarioRunner.class).to(ZeroCodeMultiStepsScenarioRunnerImpl.class);
        bind(ApiServiceExecutor.class).to(ApiServiceExecutorImpl.class);
        bind(HttpApiExecutor.class).to(HttpApiExecutorImpl.class);
        bind(JavaMethodExecutor.class).to(JavaMethodExecutorImpl.class);
        bind(ZeroCodeAssertionsProcessor.class).to(ZeroCodeAssertionsProcessorImpl.class);
        bind(ZeroCodeValidator.class).to(ZeroCodeValidatorImpl.class);
        bind(ZeroCodeReportGenerator.class).to(ZeroCodeReportGeneratorImpl.class);
        bind(ZeroCodeExternalFileProcessor.class).to(ZeroCodeExternalFileProcessorImpl.class);
        bind(ZeroCodeParameterizedProcessor.class).to(ZeroCodeParameterizedProcessorImpl.class);
        bind(ZeroCodeSorter.class).to(ZeroCodeSorterImpl.class);
        bind(org.jsmart.zerocode.core.s3.S3Client.class).to(org.jsmart.zerocode.core.s3.BasicS3Client.class);

        // ------------------------------------------------
        // Bind properties for localhost, CI, DIT, SIT etc
        // ------------------------------------------------
        Names.bindProperties(binder(), getProperties(serverEnv));
    }

    public Properties getProperties(String host) {
        final Properties properties = new Properties();

        if(isValidAbsolutePath(host)){
            return resolveEnvPlaceholders(loadAbsoluteProperties(host, properties));
        }

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(host));

            // ----------------------------------------------------
            // Below code is for backward compatibility,
            // remove this after publishing for withdrawing support
            // ----------------------------------------------------
            checkAndLoadOldProperties(properties);

        } catch (Exception e) {
            LOGGER.warning("###Oops!Exception### while reading target env file: " + host + ". Have you mentioned env details?");
            throw new RuntimeException("could not read the target-env properties file --" + host + "-- from the classpath.");
        }

        return resolveEnvPlaceholders(properties);
    }

    /**
     * Resolves any {@code ${name}} placeholders found in the loaded property values against
     * system properties and OS environment variables (in that order, via
     * {@link org.jsmart.zerocode.core.utils.EnvUtils#getEnvValueString(String)}).
     * <p>
     * This lets a target-env properties file reference values supplied on the command line, e.g.
     * <pre>
     *     db.username=${db.username}
     * </pre>
     * run with {@code -Ddb.username=alice} so the injected value becomes {@code alice}.
     * <p>
     * A placeholder whose name resolves to no system property or env var is left untouched, so
     * files that legitimately contain {@code ${...}} for downstream tooling are not corrupted.
     * Values without any placeholder are returned unchanged.
     */
    public Properties resolveEnvPlaceholders(Properties properties) {
        if (properties == null) {
            return null;
        }

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                properties.setProperty((String) entry.getKey(), resolvePlaceholders((String) value));
            }
        }

        return properties;
    }

    private static String resolvePlaceholders(String value) {
        Matcher matcher = ENV_PLACEHOLDER_PATTERN.matcher(value);
        StringBuffer resolved = new StringBuffer();

        while (matcher.find()) {
            String placeholderName = matcher.group(1);
            String replacement = getEnvValueString(placeholderName);

            // Leave the placeholder literally in place when it cannot be resolved (backward compatible).
            String token = replacement != null ? replacement : matcher.group(0);
            matcher.appendReplacement(resolved, Matcher.quoteReplacement(token));
        }
        matcher.appendTail(resolved);

        return resolved.toString();
    }

}
