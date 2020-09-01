package org.jsmart.zerocode.proto.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.jsmart.zerocode.proto.PersonsProto.Person;

import com.google.protobuf.InvalidProtocolBufferException;

public class PersonDeserializer implements Deserializer<Person> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public Person deserialize(String topic, byte[] data) {
		try {
			return Person.parseFrom(data);
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
