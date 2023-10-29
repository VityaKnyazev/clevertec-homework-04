package ru.clevertec.product.repository.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InMemoryProductRepository implements ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryProductRepository.class);


    private static final String FIND_ALL_QUERY = "SELECT uuid, name, description, price, created FROM product";

    private static final String SEARCHING_ERROR = "Error when searching product(s) {}";
    private static final String NULL_POINTER_PRODUCT_ERROR = "Given product is null";

    private SessionFactory sessionFactory;

    @Override
    public Optional<Product> findById(UUID uuid) {
        Optional<Product> product = Optional.empty();

        try (Session session = sessionFactory.getCurrentSession()) {

            product = Optional.ofNullable(session.find(Product.class, uuid));

        } catch (HibernateException | IllegalArgumentException e) {
            log.error(SEARCHING_ERROR, e.getMessage(), e);
        }

        return product;

    }

    @Override
    public List<Product> findAll() {

        List<Product> products = new ArrayList<>();

        try (Session session = sessionFactory.getCurrentSession()) {

            products = session.createNativeQuery(FIND_ALL_QUERY, Product.class)
                               .setReadOnly(true)
                               .list();

        } catch (HibernateException e) {
            log.error(SEARCHING_ERROR, e.getMessage(), e);
        }

        return products;

    }

    @Override
    public Product save(Product product) {

        if (product == null) {
            throw new IllegalArgumentException(NULL_POINTER_PRODUCT_ERROR);
        }

        try (Session session = sessionFactory.getCurrentSession()) {

            boolean isInTransaction = session.getTransaction().isActive();

            if (!isInTransaction) {
                session.getTransaction().begin();
            }

            if (product.getUuid() == null) {
                session.persist(product);
            } else {
                findById(product.getUuid()).ifPresent(savedProduct -> {

                    savedProduct.setName(product.getName());
                    savedProduct.setDescription(product.getDescription());
                    savedProduct.setPrice(product.getPrice());

                    session.merge(savedProduct);
                });
            }


            if (!isInTransaction) {
                session.getTransaction().commit();
            }

        }

        return product;

    }

    @Override
    public void delete(UUID uuid) {

        findById(uuid).ifPresent(product -> {

            try (Session session = sessionFactory.getCurrentSession()) {

                boolean isInTransaction = session.getTransaction().isActive();

                if (!isInTransaction) {
                    session.getTransaction().begin();
                }

                session.remove(product);

                if (!isInTransaction) {
                    session.getTransaction().commit();
                }

            }

        });

    }
}
