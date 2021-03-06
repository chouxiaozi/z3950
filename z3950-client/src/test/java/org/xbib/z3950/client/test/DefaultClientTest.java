package org.xbib.z3950.client.test;

import org.junit.Test;
import org.xbib.z3950.client.DefaultClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class DefaultClientTest {

    private static final Logger logger = Logger.getLogger(DefaultClientTest.class.getName());

    @Test
    public void testCQL() {
        for (String serviceName : Arrays.asList("LIBRIS", "SWB")) {
            String query = "bib.identifierISSN = 00280836";
            int from = 1;
            int size = 10;
            try (DefaultClient client = newZClient(serviceName)) {
                logger.log(Level.INFO, "executing CQL " + serviceName);
                int count = client.searchCQL(query, from, size,
                        (status, total, returned, elapsedMillis) ->
                                logger.log(Level.INFO, serviceName + " total results = " + total),
                        record -> logger.log(Level.INFO, "record = " + record));
                logger.log(Level.INFO, "returned records = " + count);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    @Test
    public void testPQF() {
        for (String serviceName : Arrays.asList("LIBRIS", "SWB")) {
            String query = "@attr 1=8 \"00280836\"";
            int from = 1;
            int size = 10;
            try (DefaultClient client = newZClient(serviceName)) {
                logger.log(Level.INFO, "executing PQF " + serviceName);
                int count = client.searchPQF(query, from, size,
                        (status, total, returned, elapsedMillis) ->
                                logger.log(Level.INFO, serviceName + " status = " + status + " total results = " + total),
                        record -> logger.log(Level.INFO, "record = " + record.toString(Charset.forName(client.getEncoding()))));
                logger.log(Level.INFO, "returned records = " + count);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private DefaultClient newZClient(String name) throws IOException {
        return newZClient(getProperties(name));
    }

    private Properties getProperties(String name) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream(name + ".properties")) {
            properties.load(inputStream);
        }
        return properties;
    }

    private static DefaultClient newZClient(Properties properties) {
        DefaultClient.Builder builder = DefaultClient.builder();
        if (properties.containsKey("host")) {
            builder.setHost(properties.getProperty("host"));
        }
        if (properties.containsKey("port")) {
            builder.setPort(Integer.parseInt(properties.getProperty("port")));
        }
        if (properties.containsKey("user")) {
            builder.setUser(properties.getProperty("user"));
        }
        if (properties.containsKey("pass")) {
            builder.setPass(properties.getProperty("pass"));
        }
        if (properties.containsKey("database")) {
            builder.setDatabases(Collections.singletonList(properties.getProperty("database")));
        }
        if (properties.containsKey("elementsetname")) {
            builder.setElementSetName(properties.getProperty("elementsetname"));
        }
        if (properties.containsKey("preferredrecordsyntax")) {
            builder.setPreferredRecordSyntax(properties.getProperty("preferredrecordsyntax"));
        }
        if (properties.containsKey("resultsetname")) {
            builder.setResultSetName(properties.getProperty("resultsetname"));
        }
        if (properties.containsKey("encoding")) {
            builder.setEncoding(properties.getProperty("encoding"));
        }
        if (properties.containsKey("format")) {
            builder.setFormat(properties.getProperty("format"));
        }
        if (properties.containsKey("type")) {
            builder.setType(properties.getProperty("type"));
        }
        return builder.build();
    }

}
