package org.jsmart.zerocode.core.zzignored.trick;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON2CSV {
    public static void main(String myHelpers[]){
        String jsonString = "{\n" +
                "  \"infile\": [\n" +
                "    {\n" +
                "      \"loop\": 1,\n" +
                "      \"name\": \"get_shower_room\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"loop\": 1,\n" +
                "      \"name\": \"get_another_shower_room\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"loop\": 1,\n" +
                "      \"name\": \"get_another_shower_room1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"loop\": 2,\n" +
                "      \"name\": \"get_another_shower_room2\"\n" +
                "    }\n" +
                "  ]\n" +
                "\n" +
                "}";

        JSONObject output;
        try {
            output = new JSONObject(jsonString);


            JSONArray docs = output.getJSONArray("infile");

            File file=new File("target/fromJSON.csv");
            String csv = CDL.toString(docs);
            FileUtils.writeStringToFile(file, csv);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
