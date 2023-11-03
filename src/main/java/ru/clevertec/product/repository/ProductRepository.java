package ru.clevertec.product.repository;

import jakarta.persistence.PersistenceException;
import ru.clevertec.product.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    /**
     * Ищет в памяти продукт по идентификатору
     *
     * @param uuid идентификатор продукта
     * @return Optional<Product> если найден, иначе Optional.empty()
     */
    Optional<Product> findById(UUID uuid);

    /**
     * Ищет все продукты в памяти
     *
     * @return список найденных продуктов
     */
    List<Product> findAll();

    /**
     * Сохраняет или обновляет продукт в памяти
     *
     * @param product сохраняемый продукт
     * @return сохранённый продукт
     * @throws IllegalArgumentException если переданный продукт null
     * @throws PersistenceException если переданный продукт не удалось сохранить в базу
     *                              или обновить в базе
     */
    Product save(Product product) throws IllegalArgumentException, PersistenceException;

    /**
     * Удаляет продукт из памяти по идентификатору
     *
     * @param uuid идентификатор продукта
     *
     * @throws PersistenceException если найденный продукт по переданному uuid
     *         не удалось удалить из базы
     */
    void delete(UUID uuid);
}
