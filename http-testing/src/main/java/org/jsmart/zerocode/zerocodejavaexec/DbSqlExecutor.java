package org.jsmart.zerocode.zerocodejavaexec;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jsmart.zerocode.zerocodejavaexec.pojo.DbResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbSqlExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbSqlExecutor.class);

    @Inject(optional = true)
    @Named("db.conn.user.name")
    private String dbUserName;

    @Inject(optional = true)
    @Named("db.conn.password")
    private String dbPassword;


    public static final String RESULTS_KEY = "results";

    public Map<String, List<DbResult>> fetchDbCustomers(String sqlStatement){

        Map<String, List<DbResult>> results = executeSelectSql(sqlStatement);

        return results;
    }

    private Map<String, List<DbResult>> executeSelectSql(String sqlStatement) {
        LOGGER.info("\n\nDB Connection user:{}, password:{}\n\n", dbUserName, dbPassword);

        /**
         * ----------------------------------------------------------------------------------
         * // Your code goes here. //
         * e.g.
         * - You can use JDBC-connection/spring JDBC template and fetch the results using
         * the above 'userName and password'
         * ----------------------------------------------------------------------------------
         */

        /**
         * Once you finished the DB execution and you will get the list of results from DB in
         * the 'results' list. Values hard coded below for the example understanding only.
         * In reality you get these results from the DB.
         */
        List<DbResult> results = new ArrayList<>();
        results.add(new DbResult(1, "Elon Musk"));
        results.add(new DbResult(2, "Jeff Bezos"));

        Map<String, List<DbResult>> resultsMap = new HashMap<>();
        resultsMap.put(RESULTS_KEY, results);

        return resultsMap;

    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
