package ru.clevertec.product.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.exception.ProductNotFoundException;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.mapper.ProductMapperImpl;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.service.impl.ProductServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Spy
    private ProductMapper productMapperSpy = new ProductMapperImpl();
    @Mock
    private ProductRepository productRepositoryMock;

    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    public void checkGetShouldReturnInfoProductDTO() {

        Product expectedProduct = Product.builder()
                .uuid(new UUID(25L, 56L))
                .name("Варенье")
                .description("Консервы сладкие")
                .price(new BigDecimal(8.25f))
                .created(LocalDateTime.now())
                .build();

        Mockito.when(productRepositoryMock.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(expectedProduct));

        UUID inputUuid = new UUID(25L, 56L);

        InfoProductDto actualInfoProductDto = productServiceImpl.get(inputUuid);

        assertAll(
                () -> assertThat(actualInfoProductDto).isNotNull(),
                () -> assertThat(actualInfoProductDto.name()).isEqualTo("Варенье"),
                () -> assertThat(actualInfoProductDto.description()).isEqualTo("Консервы сладкие")
        );

    }

    @Test
    public void checkGetShouldThrowProductNotFoundException() {

        Mockito.when(productRepositoryMock.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.empty());

        UUID inputUuid = new UUID(25L, 56L);

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> productServiceImpl.get(inputUuid));
    }

    @Test
    public void checkGetAllShouldReturnInfoProductDTOs() {

        List<Product> expectedProducts = new ArrayList<>() {{
            add(Product.builder()
                    .uuid(new UUID(12L, 50L))
                    .name("Варенье")
                    .description("Консервы сладкие")
                    .price(new BigDecimal(8.25f))
                    .created(LocalDateTime.now())
                    .build());
            add(Product.builder()
                    .uuid(new UUID(25L, 56L))
                    .name("Печенье")
                    .description("Консервы соленые")
                    .price(new BigDecimal(5.12f))
                    .created(LocalDateTime.now())
                    .build());
        }};

        Mockito.when(productRepositoryMock.findAll())
                .thenReturn(expectedProducts);

        List<InfoProductDto> actualInfoProductDTOs = productServiceImpl.getAll();

        assertAll(
                () -> assertThat(actualInfoProductDTOs).isNotEmpty(),
                () -> assertThat(actualInfoProductDTOs).hasSize(2)
        );
    }

    @Test
    public void checkGetAllShouldReturnEmptyListInfoProductDTOs() {

        Mockito.when(productRepositoryMock.findAll())
                .thenReturn(new ArrayList<>());

        List<InfoProductDto> actualInfoProductDTOs = productServiceImpl.getAll();

        assertThat(actualInfoProductDTOs).isEmpty();
    }

    @Test
    public void checkCreateShouldReturnUUIDOfCreatedProduct() {

        Mockito.when(productRepositoryMock.save(Mockito.any(Product.class)))
                .thenAnswer(invocation -> {
                    Product savedProduct = (Product) invocation.getArguments()[0];
                    savedProduct.setUuid(new UUID(32L, 25L));
                    return savedProduct;
                });

        ProductDto inputProductDTO = ProductDto.builder()
                .name("Печенье")
                .description("Сладости мучные")
                .price(new BigDecimal(5.22f))
                .build();

        UUID actualUUID = productServiceImpl.create(inputProductDTO);

        assertAll(
                () -> assertThat(actualUUID).isNotNull(),
                () -> assertThat(actualUUID).isEqualTo(new UUID(32L, 25L))
        );


    }

    @ParameterizedTest
    @MethodSource("getNegativeInputForCreate")
    public void checkCreateShouldReturnNullUUIDOnInvalidProductDTO(ProductDto invalidProductDto) {

        UUID actualUUID = productServiceImpl.create(invalidProductDto);

        assertThat(actualUUID).isNull();
    }

    @Test
    public void checkUpdateShouldUpdateExistingProduct() {

        UUID inputUuid = new UUID(245L, 324L);
        ProductDto inputProductDto = ProductDto.builder()
                .name("Печенье")
                .description("Сладости мучные")
                .price(new BigDecimal(5.22f))
                .build();

        productServiceImpl.update(inputUuid, inputProductDto);

        Mockito.verify(productRepositoryMock).save(productArgumentCaptor.capture());

        assertAll(
                () -> assertThat(productArgumentCaptor.getValue()).isNotNull(),
                () -> assertThat(productArgumentCaptor.getValue()).isInstanceOf(Product.class),
                () -> assertThat(productArgumentCaptor.getValue().getUuid()).isEqualTo(inputUuid)
        );
    }

    @ParameterizedTest
    @MethodSource("getNegativeInputForUpdate")
    public void checkUpdateShouldNotUpdateExistingProduct(UUID inputUUID, ProductDto inputProductDTO) {

        productServiceImpl.update(inputUUID, inputProductDTO);

        Mockito.verify(productRepositoryMock, Mockito.never()).save(Mockito.any(Product.class));

    }

    @Test
    public void checkDeleteShouldDeleteExistingProduct() {

        ArgumentCaptor<UUID> uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);


        UUID inputUUUID = UUID.randomUUID();

        productServiceImpl.delete(inputUUUID);


        Mockito.verify(productRepositoryMock).delete(uuidArgumentCaptor.capture());

        assertAll(
                () -> assertThat(uuidArgumentCaptor.getValue()).isNotNull(),
                () -> assertThat(uuidArgumentCaptor.getValue()).isEqualTo(inputUUUID)
        );
    }

    private static Stream<ProductDto> getNegativeInputForCreate() {
        return Stream.of(
                null,
                ProductDto.builder()
                        .name(null)
                        .description("Мучные конфеты")
                        .price(new BigDecimal(5.28f))
                        .build(),
                ProductDto.builder()
                        .name("Варенье")
                        .description("Мучное")
                        .price(new BigDecimal(5.28f))
                        .build(),
                ProductDto.builder()
                        .name("Варенье")
                        .description("Мучные конфеты")
                        .price(new BigDecimal("0.00"))
                        .build()
        );
    }

    private static Stream<Arguments> getNegativeInputForUpdate() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, ProductDto.builder()
                        .name("Варенье")
                        .description("Мучные конфеты")
                        .price(new BigDecimal(5.25f))
                        .build()),
                Arguments.of(UUID.randomUUID(), null),

                Arguments.of(UUID.randomUUID(), ProductDto.builder()
                        .name("Суп")
                        .description("Мучные конфеты")
                        .price(new BigDecimal(5.25f))
                        .build()),
                Arguments.of(UUID.randomUUID(), ProductDto.builder()
                        .name("Суповой набор")
                        .description("Мучные конфеты")
                        .price(new BigDecimal(5.25f))
                        .build()),
                Arguments.of(UUID.randomUUID(), ProductDto.builder()
                        .name("Варенье")
                        .description("Конфетное")
                        .price(new BigDecimal(5.25f))
                        .build()),
                Arguments.of(UUID.randomUUID(), ProductDto.builder()
                        .name("Варенье")
                        .description("Конфетная страна государство Гренудийское")
                        .price(new BigDecimal(5.25f))
                        .build()),
                Arguments.of(UUID.randomUUID(), ProductDto.builder()
                        .name("Варенье")
                        .description("Мучные конфеты")
                        .build()),
                Arguments.of(UUID.randomUUID(), ProductDto.builder()
                        .name("Варенье")
                        .description("Мучные конфеты")
                        .price(new BigDecimal(0.00f))
                        .build()),
                Arguments.of(UUID.randomUUID(), ProductDto.builder()
                        .name("Варенье")
                        .description("Мучные конфеты")
                        .price(new BigDecimal(-0.000000000001f))
                        .build())
        );
    }

}
