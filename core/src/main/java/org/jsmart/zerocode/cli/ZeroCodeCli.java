package org.jsmart.zerocode.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name="zerocode",
        description="Zerocode command line options:"
)
public class ZeroCodeCli implements Runnable {

    @Option(names = {"-f", "--file"}, paramLabel = "test_scenario_file", description = "A test scenario file to execute.")
    String  file;

    @Option(names = {"-e", "--env"}, paramLabel = "host_env_details", description = "Host details for the test scenarios.")
    String  env;

    @Override
    public void run() {
        System.out.println("Scenario file:" + file);
        System.out.println("Env file:" + env);
    }

    public static void main(String[] args) {

        int exitCode = new CommandLine(new ZeroCodeCli())
                .execute(args);

        System.exit(exitCode);
    }
}