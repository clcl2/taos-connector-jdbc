package com.taosdata.jdbc.ws;

import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.jdbc.annotation.CatalogRunner;
import com.taosdata.jdbc.annotation.Description;
import com.taosdata.jdbc.annotation.TestTarget;
import com.taosdata.jdbc.utils.SpecifyAddress;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * You need to start taosadapter before testing this method
 */
@Ignore
@RunWith(CatalogRunner.class)
@TestTarget(alias = "test connection with server", author = "huolibo", version = "2.0.37")
public class WSConnectionTest {
    //    private static final String host = "192.168.1.98";
    private static String host = "127.0.0.1";
    private static String port = "6041";
    private Connection connection;

    @Test
    @Description("normal test with websocket server")
    public void normalConection() throws SQLException {
        String url = "jdbc:TAOS-RS://" + host + ":" + port + "/log?user=root&password=taosdata";
        Properties properties = new Properties();
        properties.setProperty(TSDBDriver.PROPERTY_KEY_BATCH_LOAD, "true");
        connection = DriverManager.getConnection(url, properties);
    }

    @Test
    @Description("url has no db")
    public void withoutDBConection() throws SQLException {
        String url = "jdbc:TAOS-RS://" + host + ":" + port + "/?user=root&password=taosdata";
        Properties properties = new Properties();
        properties.setProperty(TSDBDriver.PROPERTY_KEY_BATCH_LOAD, "true");
        connection = DriverManager.getConnection(url, properties);
    }

    @Test
    @Description("user and password in property")
    public void propertyUserPassConection() throws SQLException {
        String url = "jdbc:TAOS-RS://" + host + ":" + port + "/";
        Properties properties = new Properties();
        properties.setProperty(TSDBDriver.PROPERTY_KEY_USER, "root");
        properties.setProperty(TSDBDriver.PROPERTY_KEY_PASSWORD, "taosdata");
        properties.setProperty(TSDBDriver.PROPERTY_KEY_BATCH_LOAD, "true");
        connection = DriverManager.getConnection(url, properties);
    }

    @Test(expected = SQLException.class)
    @Description("wrong password or user")
    public void wrongUserOrPasswordConection() throws SQLException {
        String url = "jdbc:TAOS-RS://" + host + ":" + port + "/log?user=abc&password=taosdata";
        Properties properties = new Properties();
        properties.setProperty(TSDBDriver.PROPERTY_KEY_BATCH_LOAD, "true");
        connection = DriverManager.getConnection(url, properties);
    }

    @Test
    @Description("sleep keep connection")
    public void keepConnection() throws SQLException, InterruptedException {
        String url = "jdbc:TAOS-RS://" + host + ":" + port + "/?user=root&password=taosdata";
        Properties properties = new Properties();
        properties.setProperty(TSDBDriver.PROPERTY_KEY_BATCH_LOAD, "true");
        connection = DriverManager.getConnection(url, properties);
        TimeUnit.SECONDS.sleep(20);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("show databases");
        TimeUnit.SECONDS.sleep(20);
        resultSet.next();
        resultSet.close();
        statement.close();
        connection.close();
    }

    @BeforeClass
    public static void beforeClass() {
        String specifyHost = SpecifyAddress.getInstance().getHost();
        if (specifyHost != null) {
            host = specifyHost;
        }
        String specifyPort = SpecifyAddress.getInstance().getRestPort();
        if (specifyHost != null) {
            port = specifyPort;
        }
    }
}
