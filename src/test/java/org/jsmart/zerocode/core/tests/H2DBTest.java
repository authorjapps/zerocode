package org.jsmart.zerocode.core.tests;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.sql.*;

public class H2DBTest {

    @Before
    public void setUp() throws Exception{

        Connection connection = createAndGetConnection();
        Statement statement = connection.createStatement();

        String createTableSql=  "CREATE TABLE IF NOT EXISTS CUSTOMER (id number, name varchar(20))";
        statement.executeUpdate(createTableSql);

        String insertSql= "INSERT into CUSTOMER values (1, 'Jeff Bejo')";
        statement.executeUpdate(insertSql);

        statement.close();
        connection.close();
    }

    @Test
    public void testH2DB() throws Exception{

        Connection connection = createAndGetConnection();
        Statement statement = connection.createStatement();
        String selectSql=  "Select * from CUSTOMER";
        ResultSet resultSet= statement.executeQuery(selectSql);
        assertThat(resultSet, is(notNullValue()));

        int count = -1;
        if (resultSet.next())
        {
            count = resultSet.getInt(1);
        }

        assertThat(count,is(1));

    }


    private  Connection createAndGetConnection() throws ClassNotFoundException, SQLException {
        Connection conn;
        Class.forName("org.h2.Driver");
      return  DriverManager.getConnection("jdbc:h2:~/test");
    }

}
