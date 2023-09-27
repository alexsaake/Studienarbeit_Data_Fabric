package com.mse.datafabric.utils;

import com.mse.datafabric.dataProducts.DataProductRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private DataProductRepository dataProductRepository;

    public TableJsonConverter(){

    }
    public void connectDB() throws SQLException {
        if (conn != null && stmt != null)
            return;

        conn = DriverManager.getConnection("jdbc:postgresql://immofabric.de:5432/datafabric", "msedfdb", "msedfdb");
        stmt = conn.createStatement();

    }
    @ShellMethod( "getJson" )
    public String getAllTableDataAsJsonString(String shortkey) throws SQLException {
        if(!dataProductRepository.shortkeyIsTableName(shortkey))
            return "{}";

        connectDB();
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + shortkey);

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
