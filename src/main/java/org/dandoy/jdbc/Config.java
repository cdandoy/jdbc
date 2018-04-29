package org.dandoy.jdbc;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Config {
    private static final Properties PROPERTIES = getProperties();
    private final String _url;
    private final String _username;
    private final String _password;

    private Config(String url, String username, String password) {
        _url = url;
        _username = username;
        _password = password;
    }

    public static Connection getConnection(String base) throws SQLException {
        final Config config = getProperties(base);
        try {
            return DriverManager.getConnection(config._url, config._username, config._password);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to " + base, e);
        }
    }

    private static Config getProperties(String base) {
        final String url = PROPERTIES.getProperty(base + ".url");
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalStateException("Invalid url for " + base);
        }
        return new Config(
                url,
                PROPERTIES.getProperty(base + ".username"),
                PROPERTIES.getProperty(base + ".password")
        );
    }

    private static Properties getProperties() {
        final Properties properties = new Properties();
        final File file = getFile();
        if (file != null) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                final InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, StandardCharsets.UTF_8);
                properties.load(inputStreamReader);
            } catch (IOException e) {
                throw new IllegalStateException("Operation failed", e);
            }
        }
        return properties;
    }

    private static File getFile() {
        final File file = new File(System.getProperty("user.home"), ".jdbc/jdbc.properties");
        if (file.isFile()) {
            return file;
        }

        return getFileFromCwd();
    }

    private static File getFileFromCwd() {
        for (File dir = new File(".").getAbsoluteFile(); dir != null; dir = dir.getParentFile()) {
            final File file = new File(dir, "files/jdbc.properties");
            if (file.isFile()) {
                return file;
            }
        }
        return null;
    }
}
