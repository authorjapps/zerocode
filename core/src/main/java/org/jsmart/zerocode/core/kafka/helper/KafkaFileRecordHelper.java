package org.jsmart.zerocode.core.kafka.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.jsmart.zerocode.core.kafka.KafkaConstants.AVRO;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.JSON;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.RAW;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.PROTO;

public class KafkaFileRecordHelper {

    private static final Gson gson = new GsonSerDeProvider().get();
    private static final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    public static void handleRecordsDump(ConsumerLocalConfigs consumeLocalTestProps,
                                         List<ConsumerRecord> rawRecords,
                                         List<ConsumerJsonRecord> jsonRecords) {
        String recordType = consumeLocalTestProps != null ? consumeLocalTestProps.getRecordType() : null;

        if (recordType != null) {

            switch (recordType) {
                case RAW:
                    dumpRawRecordsIfEnabled(consumeLocalTestProps.getFileDumpTo(), rawRecords);
                    break;
                case PROTO:   
                case AVRO:
                case JSON:
                    dumpJsonRecordsIfEnabled(consumeLocalTestProps.getFileDumpTo(), jsonRecords);
                    break;

                case "BIN":
                    //TODO - Handle image data etc.
                    break;

                case "HEX":
                    //TODO - Handle hex-fied stream/msg.
                    break;


                default:
                    throw new RuntimeException("Unsupported recordType - '" + recordType + "'");
            }
        }
    }

    public static void dumpRawRecordsIfEnabled(String fileName, List<ConsumerRecord> fetchedRecords) {

        if (fileName != null) {
            File file = createCascadeIfNotExisting(fileName);
            try {
                FileWriter writer = new FileWriter(file.getAbsoluteFile());
                for (ConsumerRecord thisRecord : fetchedRecords) {
                    writer.write(gson.toJson(thisRecord) + osIndependentNewLine());
                }

                writer.close();

            } catch (IOException exx) {
                throw new RuntimeException("Could not write to file '" + fileName + "' exception >> " + exx);
            }
        }
    }

    public static void dumpJsonRecordsIfEnabled(String fileName, List<ConsumerJsonRecord> fetchedRecords) {

        if (fileName != null) {
            File file = createCascadeIfNotExisting(fileName);
            try {
                FileWriter writer = new FileWriter(file.getAbsoluteFile());
                for (ConsumerJsonRecord thisRecord : fetchedRecords) {
                    writer.write(objectMapper.writeValueAsString(thisRecord) + osIndependentNewLine());
                }

                writer.close();

            } catch (IOException exx) {
                throw new RuntimeException("Could not write to file '" + fileName + "', exception was >> " + exx);
            }
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
