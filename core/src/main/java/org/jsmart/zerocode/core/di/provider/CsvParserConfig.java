package org.jsmart.zerocode.core.di.provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * /
 *  Configuration class for parsing CSV files with Jackson's CSV module,
 *  using a custom deserializer to maintain compatibility with non-standard formats previously handled by uniVocity.
 *
 */
public class CsvParserConfig {

    public static class CustomStringDeserializer extends StdDeserializer<String> {
        // Immutable map of replacement rules: pattern → replacement
        private static final Map<String, String> REPLACEMENTS = createReplacements();

        /**
         * Creates an immutable map of replacement rules for escape patterns.
         * @return A map containing patterns and their replacements.
         */
        private static Map<String, String> createReplacements() {
            Map<String, String> map = new HashMap<>();
            map.put("\\'", "'"); // Backslash-escaped single quote
            map.put("''", "'");  // Double single quote (single-quoted CSV)
            map.put("\\\\", "\\"); // Double backslash to preserve literal backslash
            return Collections.unmodifiableMap(map);
        }

        public CustomStringDeserializer() {
            super(String.class);
        }

        /**
         * Deserializes a String value from the CSV parser, applying custom escape pattern replacements.
         * <p>
         * The method processes the input string to handle non-standard escape patterns required
         * for the expected output (e.g., <code>["a'c", "d\"f", "x\y"]</code>). It uses a stream-based
         * approach to apply replacements only when the pattern is present, ensuring efficiency.
         * <p>
         * Without this deserializer, Jackson's default CSV parser may:
         * <ul>
         *     <li>Strip literal backslashes (e.g., <code>x\y</code> becomes <code>xy</code>).</li>
         *     <li>Misinterpret single-quote escaping (e.g., <code>\'</code> or <code>''</code>).</li>
         * </ul>
         * <p>
         * This implementation ensures compatibility with the previous CSV parsing library's behavior
         * and handles inputs like <code>"a'c","d""f","x\y"</code> or <code>"a\'c","d\"f","x\y"</code>.
         *
         * @return The processed string with escape patterns replaced, or null if the input is null.
         * @throws IOException If an I/O error occurs during parsing.
         */
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            final String value = p.getText();
            if (Objects.isNull(value)) {
                return null;
            }
            return REPLACEMENTS.entrySet().stream()
                    .filter(entry -> value.contains(entry.getKey()))
                    .reduce(value, (current, entry) -> current.replace(entry.getKey(), entry.getValue()), (v1, v2) -> v1);
        }
    }

}
