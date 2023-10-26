package ru.clevertec.product.service;

import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.exception.ProductNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    /**
     * Ищет продукт по идентификатору
     *
     * @param uuid идентификатор продукта
     * @return найденный продукт
     * @throws ProductNotFoundException если не найден
     */
    InfoProductDto get(UUID uuid);

    /**
     * Возвращает все существующий продукты
     *
     * @return лист с информацией о продуктах
     */
    List<InfoProductDto> getAll();

    /**
     * Создаёт новый продукт из DTO
     *
     * @param productDto DTO с информацией о создании
     * @return идентификатор созданного продукта
     */
    UUID create(ProductDto productDto);

    /**
     * Обновляет уже существующий продукт из информации полученной в DTO
     *
     * @param uuid       идентификатор продукта для обновления
     * @param productDto DTO с информацией об обновлении
     */
    void update(UUID uuid, ProductDto productDto);

    /**
     * Удаляет существующий продукт
     *
     * @param uuid идентификатор продукта для удаления
     */
    void delete(UUID uuid);
}
