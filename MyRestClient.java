package com.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyRestClient {
    static String URL = "https://api.cryptowat.ch";

    public static void main(String[] args) throws IOException {
        // JERSEY REST CLIENT
        Client client = Client.create();
        WebResource resource = client.resource(URL);
        resource.header("SecureToken", "XXXXX");
        resource.queryParam("wing", "east");
        String response = resource.get(String.class);
        System.out.println("JSON Response:" + response);

        // JACKSON API to convert JSON to JAVA Object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, false);
        MyResponse myResponse = (MyResponse) mapper.readValue(response, MyResponse.class);

        System.out.println("MyResponse Object : " + myResponse);

        // DB logic to store MyResponse in database
        if (null != myResponse && null != myResponse.getResult() && null != myResponse.getAllowance()) {
            Connection conn = null;
            try {
                conn =
                        DriverManager.getConnection("jdbc:mysql://localhost/test?" +
                                "user=minty&password=greatsqldb");
                PreparedStatement statement = conn.prepareStatement("INSERT INTO RESULT_TABLE VALUES (?, ?, ?, ?)");
                statement.setString(1, myResponse.getResult().getDocumentation());
                statement.setString(2, myResponse.getResult().getRevision());
                statement.setString(3, myResponse.getResult().getUptime());
                statement.setString(4, myResponse.getAllowance().getCost());
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (null != conn) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        }


    }


}

