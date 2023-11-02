package ru.clevertec.product.repository.database.data;

import ru.clevertec.product.repository.database.data.exception.DatabaseManagerException;

/**
 *
 * Represents manager for managing database data (set start-up data and so on)
 *
 */
public interface DatabaseManager {

    /**
     *
     * Set start-up data to database
     *
     * @throws DatabaseManagerException when error on connecting to database resource or
     *         error when loading data
     */

    void loadData() throws DatabaseManagerException;
}
