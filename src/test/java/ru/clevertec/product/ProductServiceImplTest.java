package ru.clevertec.product;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.mapper.impl.ProductMapperImpl;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.service.impl.ProductServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Spy
    ProductMapper productMapperSpy = new ProductMapperImpl();
    @Mock
    ProductRepository productRepositoryMock;

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
                () -> assertThat(actualInfoProductDto.name()).isEqualTo("Варенье")
        );

    }

}
