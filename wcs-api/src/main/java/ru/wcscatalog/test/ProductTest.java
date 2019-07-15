package ru.wcscatalog.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.model.PriceType;
import ru.wcscatalog.core.model.Product;
import ru.wcscatalog.webapi.config.HibernateConfig;
import ru.wcscatalog.webapi.config.RootConfig;
import ru.wcscatalog.webapi.config.WebAppInitializer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;


import static org.hamcrest.MatcherAssert.assertThat;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RootConfig.class, HibernateConfig.class})
@WebAppConfiguration
public class ProductTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    public void product_Created() {
        Product product = new Product();
        product.setTitle("Тестовый продукт");
        product.setAlias("Test_product");
        product.setDescription("Description");
        product.setPrice(2f);
        product.setDiscountPrice(1f);
        product.setPriceType(PriceType.NORMAL);

//        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.persist(product);
        assertThat("", product.getId() != null);
    }
}
