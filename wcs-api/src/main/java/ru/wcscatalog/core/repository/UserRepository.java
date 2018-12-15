package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class UserRepository {

    private final EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<User> criteriaQuery;
    private Root<User> root;


    public UserRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        buildCriteriaQuery();
    }


    public Boolean existsByUsername(String username) {
        return false;
    }

    public User findByUsername(String username) {
        criteriaQuery.where(criteriaBuilder.equal(root.get("username"), username));
        Query query = entityManager.createQuery(criteriaQuery);
        return (User) query.getSingleResult();
    }

    public User findUserById(Long id) {
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
        Query query = entityManager.createQuery(criteriaQuery);
        return (User) query.getSingleResult();
    }

    @Transactional
    public User save(User user) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        return user;
    }

    private void buildCriteriaQuery() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(User.class);
        root = criteriaQuery.from(User.class);
    }
}
