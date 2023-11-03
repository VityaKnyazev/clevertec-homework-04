package ru.clevertec.product.repository.database.service.impl;

import org.h2.tools.Server;
import ru.clevertec.product.repository.database.service.DatabaseService;
import ru.clevertec.product.repository.database.service.exception.DatabaseServiceException;

import java.sql.SQLException;

public class H2DatabaseService implements DatabaseService {

    private static final String H2_DATABASE_CREATING_ERROR = "Error creating h2 database";
    private static final String H2_DATABASE_STARTING_ERROR = "Error starting h2 database";
    private final Server server;

    public H2DatabaseService() {

        try {
            server = Server.createTcpServer("-tcpPort", "9092", "-tcpPassword", "admin", "-ifNotExists");
        } catch (SQLException e) {
            throw new DatabaseServiceException(H2_DATABASE_CREATING_ERROR, e);
        }

    }

    @Override
    public void start() {

        try {
            server.start();
        } catch (SQLException e) {
            throw new DatabaseServiceException(H2_DATABASE_STARTING_ERROR, e);
        }
    }

    @Override
    public void stop() {

        if (server != null) {
            server.stop();
        }
    }
}
