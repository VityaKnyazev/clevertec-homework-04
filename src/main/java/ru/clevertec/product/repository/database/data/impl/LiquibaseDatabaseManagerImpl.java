package ru.clevertec.product.repository.database.data.impl;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.clevertec.product.repository.database.data.DatabaseManager;
import ru.clevertec.product.repository.database.data.exception.DatabaseManagerException;
import ru.clevertec.product.util.YAMLParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquibaseDatabaseManagerImpl implements DatabaseManager {
    private static final String PROPERTY = "application.yml";
    private static final String LOAD_DATA_ERROR = "Error when updating database with new data";

    private final String jdbcURL;
    private final String userName;
    private final String password;

    private final String changelogFile;

    public LiquibaseDatabaseManagerImpl() {
        YAMLParser yamlParser = new YAMLParser(PROPERTY);

        this.jdbcURL = yamlParser.getProperty("db", "jdbcUrl");
        this.userName = yamlParser.getProperty("db", "username");
        this.password = yamlParser.getProperty("db", "password");

        this.changelogFile = yamlParser.getProperty("liquibase", "changelogFile");
    }

    @Override
    public void loadData() {

        try {
            Connection connection = DriverManager.getConnection(jdbcURL,
                                                                userName,
                                                                password);
             Database database = DatabaseFactory.getInstance()
                     .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            try(Liquibase liquibase = new Liquibase(changelogFile,
                    new ClassLoaderResourceAccessor(), database)) {

                liquibase.update();

            }

        } catch (SQLException | LiquibaseException e) {
            throw new DatabaseManagerException(LOAD_DATA_ERROR, e);
        }
    }
}
