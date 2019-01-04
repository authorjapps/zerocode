package org.jsmart.zerocode.core.kafka.helper;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.validateConsumeProperties;

public class KafkaFileRecordHelper {
    public static void handleRecordsDump(ConsumerLocalConfigs consumeLocalTestProps, List<ConsumerRecord> fetchedRecords) {
        String fileDumpType = consumeLocalTestProps != null ? consumeLocalTestProps.getFileDumpType() : null;

        if (fileDumpType != null) {

            validateConsumeProperties(consumeLocalTestProps);

            switch (fileDumpType) {
                case "RAW":
                    dumpRecords(consumeLocalTestProps.getFileDumpTo(), fetchedRecords);
                    break;

                case "BIN":
                    //TODO - Handle image data etc.
                    break;

                case "JSON":
                    // TODO - dump JSON data to a file
                    break;

                default:
                    throw new RuntimeException("Unsupported fileDumpType - '" + fileDumpType + "'");
            }
        }
    }

    public static void dumpRecords(String fileName, List<ConsumerRecord> fetchedRecords) {

        File file = createCascadeIfNotExisting(fileName);

        try {
            FileWriter writer = new FileWriter(file.getAbsoluteFile());

            for (ConsumerRecord aRecord : fetchedRecords) {
                String key = aRecord.key() != null ? aRecord.key().toString() : "";
                String value = aRecord.value() != null ? aRecord.value().toString() : "";
                writer.write(key + value + osIndependentNewLine());
            }
            writer.close();
        } catch (IOException exx) {
            throw new RuntimeException("Could not write to file '" + fileName + "' exception >> " + exx);
        }
    }

    private static File createCascadeIfNotExisting(String fileName) {
        try {
            Path path = Paths.get(fileName);
            Files.createDirectories(path.getParent());

            File file = new File(fileName);

            return file;
        } catch (IOException exx) {
            throw new RuntimeException("Create file '" + fileName + "' Exception" + exx);
        }
    }

    private static String osIndependentNewLine() {
        return System.getProperty("line.separator");
    }
}
