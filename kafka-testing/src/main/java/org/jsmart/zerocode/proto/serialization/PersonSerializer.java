package org.jsmart.zerocode.proto.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.jsmart.zerocode.proto.PersonsProto.Person;

public class PersonSerializer implements Serializer<Person>{

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] serialize(String topic, Person data) {
		return data.toByteArray();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
