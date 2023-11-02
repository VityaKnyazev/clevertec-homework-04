package ru.clevertec.product;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.repository.database.connnection.AppConnection;
import ru.clevertec.product.repository.database.data.DatabaseManager;
import ru.clevertec.product.repository.database.data.impl.LiquibaseDatabaseManagerImpl;
import ru.clevertec.product.repository.database.service.DatabaseService;
import ru.clevertec.product.repository.database.service.impl.H2DatabaseService;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {
        DatabaseService h2DatabaseService = new H2DatabaseService();
        h2DatabaseService.start();

        DatabaseManager liquibaseDatabaseManager = new LiquibaseDatabaseManagerImpl();
        liquibaseDatabaseManager.loadData();

        AppConnection appConnection = new AppConnection();
        try (Session session = appConnection.getSessionFactory().getCurrentSession()) {
            session.getTransaction().begin();
            List<Product> products = session.createNativeQuery("SELECT * FROM product", Product.class).list();
            products.stream().forEach(System.out::println);
            session.getTransaction().commit();
        }

        AppConnection.shutdown();
        h2DatabaseService.stop();
    }
}
