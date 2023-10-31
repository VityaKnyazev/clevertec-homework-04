package ru.clevertec.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public class ProductMapperImplTest {

    private ProductMapper productMapperImpl;

    @BeforeEach
    public void setUp() {
        productMapperImpl = new ProductMapperImpl();
    }

    @Test
    public void checkToProductShouldReturnProduct() {

        ProductDto inputProductDto = ProductDto.builder()
                .name("Вафли")
                .description("Молочные сладости")
                .price(new BigDecimal(6.55f))
                .build();

        Product actualProduct = productMapperImpl.toProduct(inputProductDto);

        assertAll(
                () -> assertThat(actualProduct.getUuid()).isNull(),
                () -> assertThat(actualProduct.getName()).isNotEmpty().containsPattern("[A-Яa-я\\s\\t]{5,10}"),
                () -> {
                    String description = actualProduct.getDescription();
                    if (description != null) {
                        assertThat(actualProduct.getDescription()).hasSizeGreaterThan(10)
                                .hasSizeLessThan(30);
                    }
                },
                () -> assertThat(actualProduct.getPrice()).isNotNull().isPositive(),
                () -> assertThat(actualProduct.getCreated()).isBeforeOrEqualTo(LocalDateTime.now())
        );

    }

    @Test
    public void checkToProductShouldReturnNullProductWhenProductDTOIsNull() {

        ProductDto inputProductDto = null;

        Product actualProduct = productMapperImpl.toProduct(inputProductDto);

        assertThat(actualProduct).isNull();

    }

    @Test
    public void checkToInfoProductDtoShouldReturnInfoProductDto() {

        Product inputProduct = Product.builder()
                .uuid(new UUID(25L, 43L))
                .name("Конфеты")
                .description("Без ГМО")
                .price(new BigDecimal(21.55f))
                .created(LocalDateTime.now())
                .build();

        InfoProductDto actualInfoProductDTO = productMapperImpl.toInfoProductDto(inputProduct);

        assertAll(
                () -> assertThat(actualInfoProductDTO.uuid()).isNotNull(),
                () -> assertThat(actualInfoProductDTO.name()).isNotEmpty().isNotNull().containsPattern("[A-Яa-я\\s\\t]{5,10}"),
                () -> assertThat(actualInfoProductDTO.description()).isNotNull(),
                () -> assertThat(actualInfoProductDTO.price()).isNotNull().isPositive()
        );


    }

    @Test
    public void checkToInfoProductDtoShouldReturnNullWhenProductIsNull() {

        Product inputProduct = null;

        InfoProductDto actualInfoProductDTO = productMapperImpl.toInfoProductDto(inputProduct);

        assertThat(actualInfoProductDTO).isNull();
    }

    @Test
    public void checkMergeShouldReturnMergedProductWithProductDTO() {

        Product inputProduct = Product.builder()
                .uuid(new UUID(25L, 43L))
                .name("Вафли")
                .description("Молочные сладости")
                .price(new BigDecimal(4.23f))
                .created(LocalDateTime.now())
                .build();

        ProductDto inputProductDto = ProductDto.builder()
                .name("Вафли К")
                .description("Молочные горы")
                .price(new BigDecimal(6.55f))
                .build();

        Product actualProduct = productMapperImpl.merge(inputProduct, inputProductDto);

        assertAll(
                () -> assertThat(actualProduct.getUuid()).isNotNull(),
                () -> assertThat(actualProduct.getName()).isNotEmpty().containsPattern("[A-Яa-я\\s\\t]{5,10}"),
                () -> {
                    String description = actualProduct.getDescription();
                    if (description != null) {
                        assertThat(actualProduct.getDescription()).hasSizeGreaterThan(10)
                                .hasSizeLessThan(30);
                    }
                },
                () -> assertThat(actualProduct.getPrice()).isNotNull().isPositive(),
                () -> assertThat(actualProduct.getCreated()).isBeforeOrEqualTo(LocalDateTime.now())
        );

    }

    @ParameterizedTest
    @MethodSource("getNegativeInputsForCheckMerge")
    public void checkMergeShouldReturnNullWhenGivenNullProductOrProductDTO(Product inputProduct,
                                                                           ProductDto inputProductDTO) {

        Product actualProduct = productMapperImpl.merge(inputProduct, inputProductDTO);

        assertThat(actualProduct).isNull();
    }

    private static Stream<Arguments> getNegativeInputsForCheckMerge() {

        return Stream.of(
                Arguments.of(null, ProductDto.builder()
                        .name("Вафли К")
                        .description("Молочные горы")
                        .price(new BigDecimal(6.55f))
                        .build()),
                Arguments.of(Product.builder()
                        .uuid(new UUID(25L, 43L))
                        .name("Вафли")
                        .description("Молочные сладости")
                        .price(new BigDecimal(4.23f))
                        .created(LocalDateTime.now())
                        .build(), null),
                Arguments.of(null, null)
        );

    }
}
