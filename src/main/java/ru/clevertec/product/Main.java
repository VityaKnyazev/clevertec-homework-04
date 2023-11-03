package ru.clevertec.product;

import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.exception.ProductNotFoundException;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.mapper.ProductMapperImpl;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.repository.database.connnection.AppConnection;
import ru.clevertec.product.repository.database.data.DatabaseManager;
import ru.clevertec.product.repository.database.data.impl.LiquibaseDatabaseManagerImpl;
import ru.clevertec.product.repository.database.service.DatabaseService;
import ru.clevertec.product.repository.database.service.impl.H2DatabaseService;
import ru.clevertec.product.repository.impl.InMemoryProductRepository;
import ru.clevertec.product.service.ProductService;
import ru.clevertec.product.service.impl.ProductServiceImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class Main {
    private static DatabaseService h2DatabaseService;


    public static void main(String[] args) {
        deployDatabase();

        AppConnection connection = new AppConnection();

        ProductService productService = buildProductService(connection);

        Transaction transaction = null;

        try(Session session = connection.getSessionFactory().getCurrentSession();) {


            transaction = session.getTransaction();
            transaction.begin();

            UUID uuid = productService.create(ProductDto.builder()
                    .name("Свекла")
                    .description("Овощи всесезонные")
                    .price(new BigDecimal(5.21f))
                    .build());

            System.out.printf("Product with uuid=%s created%n", uuid);

            InfoProductDto infoProductDto = productService.get(uuid);

            System.out.printf("Product with uuid=%s got from db %s%n", uuid, infoProductDto);

            productService.update(uuid, ProductDto.builder()
                    .name("Сверло ДП")
                    .description("Инструмент для ремонта")
                    .price(new BigDecimal(8.56))
                    .build());

            System.out.printf("Product with uuid=%s updated in db%n", uuid);

            productService.delete(uuid);

            System.out.printf("Product with uuid=%s deleted from db%n", uuid);

            session.getTransaction().commit();
        } catch (PersistenceException | IllegalArgumentException | ProductNotFoundException e) {

            if (transaction != null && transaction.getRollbackOnly()) {
                transaction.rollback();
            }

            System.out.println(e.getMessage());
        }

        AppConnection.shutdown();
        h2DatabaseService.stop();
    }

    public static void deployDatabase() {
        h2DatabaseService = new H2DatabaseService();
        h2DatabaseService.start();

        DatabaseManager liquibaseDatabaseManager = new LiquibaseDatabaseManagerImpl();
        liquibaseDatabaseManager.loadData();
    }

    public static ProductService buildProductService(AppConnection connection) {
        ProductRepository productRepository = new InMemoryProductRepository(connection.getSessionFactory());
        ProductMapper productMapperImpl = new ProductMapperImpl();

        ProductService productServiceImpl = new ProductServiceImpl(productMapperImpl, productRepository);

        return productServiceImpl;
    }
}
