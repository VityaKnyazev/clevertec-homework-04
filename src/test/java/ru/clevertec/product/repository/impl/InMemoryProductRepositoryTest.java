package ru.clevertec.product.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.product.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class InMemoryProductRepositoryTest {
    @Mock
    private SessionFactory sessionFactoryMock;

    @Mock
    private Session sessionMock;

    @Mock
    private NativeQuery<Product> nativeQueryMock;

    @Mock
    private Transaction transactionMock;

    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    @InjectMocks
    private InMemoryProductRepository inMemoryProductRepository;

    @Test
    public void checkFindByIdShouldReturnOptionalProduct() {

        Product expectedProduct = Product.builder()
                .uuid(new UUID(128L, 256L))
                .name("Печенье")
                .description("Сладость")
                .price(new BigDecimal(6.28f))
                .created(LocalDateTime.now())
                .build();

        Mockito.when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
        Mockito.when(sessionMock.find(Mockito.any(Class.class), Mockito.any(UUID.class)))
                .thenReturn(expectedProduct);

        UUID inputUUID = new UUID(128L, 256L);

        Optional<Product> actualProductWrap = inMemoryProductRepository.findById(inputUUID);

        assertAll(
                () -> assertThat(actualProductWrap).isPresent(),
                () -> assertThat(actualProductWrap.get()).isEqualTo(expectedProduct)
        );


    }

    @Test
    public void checkFindByIdShouldReturnOptionalEmpty() {

        Mockito.when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
        Mockito.when(sessionMock.find(Mockito.any(Class.class), Mockito.isNull()))
                .thenThrow(IllegalArgumentException.class);

        UUID inputUUID = null;

        assertThat(inMemoryProductRepository.findById(inputUUID)).isEmpty();
    }

    @Test
    public void checkFindAllShouldReturnProducts() {

        List<Product> expectedProducts = new ArrayList<>() {{
            add(Product.builder()
                    .uuid(new UUID(128L, 256L))
                    .name("Печенье")
                    .description("Сладость")
                    .price(new BigDecimal(6.28f))
                    .created(LocalDateTime.now())
                    .build());
            add(Product.builder()
                    .uuid(new UUID(100L, 201L))
                    .name("Варенье")
                    .description("Сладость")
                    .price(new BigDecimal(3.44f))
                    .created(LocalDateTime.now())
                    .build());
        }};

        Mockito.when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
        Mockito.when(sessionMock.createNativeQuery(Mockito.anyString(), Mockito.any(Class.class)))
                .thenReturn(nativeQueryMock);
        Mockito.when(nativeQueryMock.setReadOnly(Mockito.anyBoolean())).thenReturn(nativeQueryMock);
        Mockito.when(nativeQueryMock.list()).thenReturn(expectedProducts);

        List<Product> actualProducts = inMemoryProductRepository.findAll();

        assertAll(
                () -> assertThat(actualProducts).isNotEmpty(),
                () -> assertThat(actualProducts).hasSize(2)
        );

    }

    @Test
    public void checkFindAllShouldReturnEmptyList() {

        Mockito.when(sessionFactoryMock.getCurrentSession()).thenThrow(HibernateException.class);

        List<Product> actualProducts = inMemoryProductRepository.findAll();

        assertThat(actualProducts).isEmpty();
    }

    @Test
    public void checkSaveShouldReturnSavedProduct() {

        Product expectedSavingProduct = Product.builder()
                                .uuid(new UUID(100L, 201L))
                                .name("Соленье")
                                .description("Сладость")
                                .price(new BigDecimal(3.44f))
                                .created(LocalDateTime.now())
                                .build();

        Mockito.when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
        Mockito.when(sessionMock.getTransaction()).thenReturn(transactionMock);
        Mockito.when(transactionMock.isActive()).thenReturn(false);
        Mockito.doNothing().when(transactionMock).begin();
        Mockito.doNothing().when(transactionMock).commit();

        inMemoryProductRepository.save(expectedSavingProduct);

        Mockito.verify(sessionMock).persist(productArgumentCaptor.capture());

        assertThat(productArgumentCaptor.getValue())
                                        .isEqualTo(expectedSavingProduct);
    }

    @Test
    public void checkDeleteShouldRemoveProductByUUID() {

        Product expectedDeletingProduct = Product.builder()
                .uuid(new UUID(128L, 256L))
                .name("Печенье")
                .description("Сладость")
                .price(new BigDecimal(6.28f))
                .created(LocalDateTime.now())
                .build();

        Mockito.when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock)
                                                            .thenReturn(sessionMock);
        Mockito.when(sessionMock.find(Mockito.any(Class.class), Mockito.any(UUID.class)))
                .thenReturn(expectedDeletingProduct);

        Mockito.when(sessionMock.getTransaction()).thenReturn(transactionMock);
        Mockito.when(transactionMock.isActive()).thenReturn(false);
        Mockito.doNothing().when(transactionMock).begin();
        Mockito.doNothing().when(transactionMock).commit();

        UUID deletingActualUUID = new UUID(128L, 256L);
        inMemoryProductRepository.delete(deletingActualUUID);

        Mockito.verify(sessionMock).remove(productArgumentCaptor.capture());

        assertThat(productArgumentCaptor.getValue()).isEqualTo(expectedDeletingProduct);
    }

}
