package ru.clevertec.product.repository.database.service;

import ru.clevertec.product.repository.database.service.exception.DatabaseServiceException;

import java.sql.SQLException;

/**
 *
 * Represents database service
 *
 */
public interface DatabaseService {

    /**
     *
     * Start database service
     * @throws DatabaseServiceException when failed to start database service
     */
    void start() throws DatabaseServiceException;

    /**
     *
     * Stop database service
     *
     */
    void stop();

}
