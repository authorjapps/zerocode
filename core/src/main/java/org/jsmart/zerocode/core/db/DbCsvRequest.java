package org.jsmart.zerocode.core.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DbCsvRequest {
    private final String tableName;
    private final List<String> csvSource;
    private final Boolean withHeaders;
    private final String nullString;

    public DbCsvRequest(
            @JsonProperty(value="tableName", required=true) String tableName,
            @JsonProperty("csvSource") JsonNode csvSourceJsonNode,
            @JsonProperty("withHeaders") Boolean withHeaders,
            @JsonProperty("nullString") String nullString) {
        this.tableName = tableName;
        this.withHeaders = Optional.ofNullable(withHeaders).orElse(false);
        this.nullString = Optional.ofNullable(nullString).orElse("");
        this.csvSource = Optional.ofNullable(csvSourceJsonNode).map(this::getCsvSourceFrom).orElse(Collections.emptyList());
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getCsvSource() {
        return csvSource;
    }

    public boolean getWithHeaders() {
        return withHeaders;
    }

    public String getNullString() {
        return nullString;
    }

    // Code below is duplicated from org.jsmart.zerocode.core.domain.Parametrized.java and not included in tests.
    // TODO Consider some refactoring later and review error message when file not found
    
    private List<String> getCsvSourceFrom(JsonNode csvSourceJsonNode) {
        try {
            if (csvSourceJsonNode.isArray()) {
                return readCsvSourceFromJson(csvSourceJsonNode);

            } else {
                return readCsvSourceFromExternalCsvFile(csvSourceJsonNode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing csvSource", e);
        }
    }

    private List<String> readCsvSourceFromJson(JsonNode csvSourceJsonNode) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
        });
        return reader.readValue(csvSourceJsonNode);
    }

    private List<String> readCsvSourceFromExternalCsvFile(JsonNode csvSourceJsonNode) throws IOException {
        String csvSourceFilePath = csvSourceJsonNode.textValue();
        if (StringUtils.isNotBlank(csvSourceFilePath)) {
            Path path = Paths.get("./src/test/resources/",csvSourceFilePath);
            List<String> csvSourceFileLines = Files.lines(path)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            //if (this.ignoreHeader) {
            //    return csvSourceFileLines.stream()
            //            .skip(1)
            //            .collect(Collectors.toList());
            //}
            return csvSourceFileLines;
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "Parameterized{" +
                "tableName=" + tableName +
                ", csvSource=" + csvSource +
                ", withHeaders=" + withHeaders +
                ", nullString=" + nullString +
                '}';
    }
}
