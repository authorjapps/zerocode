package org.jsmart.zerocode.core.di.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GsonSerDeProvider implements Provider<Gson> {

    @Override
    public Gson get() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(KafkaHeadersAdapter.FACTORY)
                .create();
    }

    static class KafkaHeadersAdapter extends TypeAdapter<Headers> {

        static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                if (type.getRawType() == Headers.class) {
                    return (TypeAdapter<T>) new KafkaHeadersAdapter(gson);
                }
                return null;
            }
        };

        private final Gson gson;

        public KafkaHeadersAdapter(Gson gson) {
            this.gson = gson;
        }

        @Override
        public void write(JsonWriter writer, Headers value) throws IOException {
            if (value == null || !value.iterator().hasNext()) {
                writer.nullValue();
            } else {
                Map<String, String> headers = new HashMap<>();
                value.forEach(header -> headers.put(header.key(), new String(header.value())));
                gson.getAdapter(Map.class).write(writer, headers);
            }
        }

        @Override
        public Headers read(JsonReader reader) throws IOException {
            Headers headers = null;
            JsonToken peek = reader.peek();
            if (JsonToken.NULL.equals(peek)) {
                reader.nextNull();
            } else {
                Map<String, String> map = gson.getAdapter(Map.class).read(reader);

                headers = new RecordHeaders();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    headers.add(key, value == null ? null : value.getBytes());
                }
            }

            return headers;
        }
    }

}
