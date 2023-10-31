package ru.clevertec.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
@Mapper
public interface ProductMapper {

    /**
     * Маппит DTO в продукт без UUID
     *
     * @param productDto - DTO для маппинга
     * @return новый продукт
     */
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    Product toProduct(ProductDto productDto);

    /**
     * Маппит текущий продукт в DTO без даты
     *
     * @param product - существующий продукт
     * @return DTO с идентификатором
     */
    InfoProductDto toInfoProductDto(Product product);

    /**
     * Сливает существующий продукт с информацией из DTO
     * не меняет дату создания и идентификатор
     *
     * @param product    существующий продукт
     * @param productDto информация для обновления
     * @return обновлённый продукт
     */
    default Product merge(Product product, ProductDto productDto) {

        if (product != null) {
            product.setName(productDto.name());
            product.setDescription(productDto.description());
            product.setPrice(productDto.price());
        }

        return product;
    }

}
