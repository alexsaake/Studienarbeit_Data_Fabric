package com.mse.datafabric.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ShellComponent
@Component
public class TableJsonConverter {
    Connection conn = null;
    Statement stmt=null;

    public TableJsonConverter(){

    }
    public void connectDB() throws SQLException {
        if (conn != null && stmt != null)
            return;

        conn = DriverManager.getConnection("jdbc:h2:file:~/db/datafabric;AUTO_SERVER=true", "sa", "");
        stmt = conn.createStatement();

    }
    @ShellMethod( "getJson" )
    public String getAllTableDataAsJsonString(String tableName) throws SQLException {
        List<String> whitelist = Arrays.asList("IMMO_DATA");

        if(!whitelist.contains(tableName))
            return "{}";

        connectDB();
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + tableName);

        ResultSetMetaData md = resultSet.getMetaData();
        int numCols = md.getColumnCount();
        List<String> colNames = IntStream.range(0, numCols)
                .mapToObj(i -> {
                    try {
                        return md.getColumnName(i + 1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return "?";
                    }
                })
                .toList();

        JSONArray result = new JSONArray();
        while (resultSet.next()) {
            JSONObject row = new JSONObject();
            colNames.forEach(cn -> {
                try {
                    Object value = resultSet.getObject(cn);
                    if(value == null)
                        value = "";
                    row.put(cn, value);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
            });
            result.put(row);
        }
        return result.toString();
    }

}
