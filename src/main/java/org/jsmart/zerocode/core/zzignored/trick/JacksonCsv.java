package org.jsmart.zerocode.core.zzignored.trick;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JacksonCsv {

    public static void main(String[] args) {

        List<User> users = new ArrayList<>();
        User userHeader = new User("First", "Last", "Age");
        User user1 = new User("First name1", "Last Name1", null);
        User user2 = new User("First name2", "Last Name2", "22");
        users.add(userHeader);
        users.add(user1);
        users.add(user2);

        CsvSchema schema = CsvSchema.builder()
                .addColumn("firstName")
                .addColumn("lastName")
                .addColumn("age", CsvSchema.ColumnType.NUMBER)
                //.addColumn("comments")
                //.setUseHeader(true)
                //.setStrictHeaders(false)
                .build();

//        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
//        ObjectMapper mapper = new CsvMapper();

        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        ObjectWriter writer = mapper.writer(schema.withLineSeparator("\n"));
        try {
            writer.writeValue(new File("target/ModifiedUsers.csv"), users);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

//@JsonPropertyOrder({ "firstName", "lastName", "age"})
class User {
    private String firstName;
    private String lastName;
    private String age;

    public User(String firstName, String lastName, String age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAge() {
        return age;
    }
}