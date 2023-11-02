package ru.clevertec.product.repository.database.connnection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.util.YAMLParser;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AppConnection {

    private static final String PROPERTIES = "application.yml";

    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    private final YAMLParser yamlParser;

    public AppConnection() {
        this.yamlParser = new YAMLParser(PROPERTIES);
    }

    private DataSource hikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(yamlParser.getProperty("db", "driverClassName"));
        hikariConfig.setJdbcUrl(yamlParser.getProperty("db", "jdbcUrl"));
        hikariConfig.setUsername(yamlParser.getProperty("db", "username"));
        hikariConfig.setPassword(yamlParser.getProperty("db", "password"));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(yamlParser.getProperty("db", "maxPoolSize")));
        hikariConfig.setConnectionTimeout(Long.parseLong(yamlParser.getProperty("db", "connectionTimeout")));

        return new HikariDataSource(hikariConfig);
    }


    public SessionFactory getSessionFactory() {

        if (sessionFactory == null) {
            try {

                Map<String, Object> properties = new HashMap<>();
                properties.put("hibernate.connection.datasource", hikariDataSource());
                properties.put("hbm2ddl.auto", yamlParser.getProperty("hibernate", "schema"));
                properties.put("hibernate.current_session_context_class",
                               yamlParser.getProperty("hibernate", "sessionContext"));
                properties.put("hibernate.dialect",
                                yamlParser.getProperty("hibernate", "dialect"));
                properties.put("hibernate.connection.isolation",
                            yamlParser.getProperty("hibernate", "transactionIsolationValue"));
                properties.put("hibernate.show.sql",
                               yamlParser.getProperty("hibernate", "showSql"));

                registry = new StandardServiceRegistryBuilder().applySettings(properties)
                                                               .build();

                MetadataSources sources = new MetadataSources(registry);
                sources.addAnnotatedClass(Product.class);

                Metadata metadata = sources.getMetadataBuilder().build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                log.error(e.getMessage(), e);

                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }

        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
