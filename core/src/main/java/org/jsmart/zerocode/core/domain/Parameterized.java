package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Parameterized {
    private final List<Object> valueSource;
    private final List<String> csvSource;

    public Parameterized(
            @JsonProperty("valueSource") List<Object> valueSource,
            @JsonProperty("csvSource") JsonNode csvSourceJsonNode) {
        this.valueSource = valueSource;
        this.csvSource = getCsvSourceFrom(csvSourceJsonNode);
    }

    public List<Object> getValueSource() {
        return valueSource;
    }

    public List<String> getCsvSource() {
        return csvSource;
    }

    private List<String> getCsvSourceFrom(JsonNode csvSourceJsonNode) {
        try {
            if (csvSourceJsonNode.isArray()) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
                });
                return reader.readValue(csvSourceJsonNode);

            } else {
                String csvSourceFilePath = csvSourceJsonNode.textValue();
                if (StringUtils.isNotBlank(csvSourceFilePath)) {
                    Path path = Paths.get(csvSourceFilePath);
                    return Files.lines(path).collect(Collectors.toList());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing csvSource", e);
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "Parameterized{" +
                "valueSource=" + valueSource +
                ", csvSource=" + csvSource +
                '}';
    }
}
