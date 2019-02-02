package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dto.OfferOptionEntry;
import ru.wcscatalog.core.dto.OfferOptionInput;
import ru.wcscatalog.core.dto.OptionValueInput;
import ru.wcscatalog.core.model.Image;
import ru.wcscatalog.core.model.OfferOption;
import ru.wcscatalog.core.model.OptionValue;
import ru.wcscatalog.core.utils.Transliterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OptionsRepository {
    private final ImageRepository imageRepository;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    public OptionsRepository(ImageRepository imageRepository, EntityManagerFactory entityManagerFactory) {
        this.imageRepository = imageRepository;
        this.entityManagerFactory = entityManagerFactory;
        initialiseCriteriaBuilder();
    }

    public List<OfferOptionEntry> getAllOptions() {
        CriteriaQuery<OfferOption> criteriaQuery = criteriaBuilder.createQuery(OfferOption.class);
        List<OfferOption> options = entityManager.createQuery(criteriaQuery).getResultList();
        return options.stream().map(OfferOptionEntry::fromOfferOption).collect(Collectors.toList());
    }

    private OfferOption getOptionById(Long id) {
        CriteriaQuery<OfferOption> criteriaQuery = criteriaBuilder.createQuery(OfferOption.class);
        Root<OfferOption> root = criteriaQuery.from(OfferOption.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
        Optional<OfferOption> option = entityManager.createQuery(criteriaQuery).getResultList().stream().findFirst();
        return option.orElse(null);
    }

    private OptionValue getOptionValueById(Long id) {
        CriteriaQuery<OptionValue> criteriaQuery = criteriaBuilder.createQuery(OptionValue.class);
        Root<OptionValue> root = criteriaQuery.from(OptionValue.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
        Optional<OptionValue> option = entityManager.createQuery(criteriaQuery).getResultList().stream().findFirst();
        return option.orElse(null);
    }

    private OptionValue getOptionValueByAlias(String alias) {
        CriteriaQuery<OptionValue> criteriaQuery = criteriaBuilder.createQuery(OptionValue.class);
        Root<OptionValue> root = criteriaQuery.from(OptionValue.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("alias"), alias));
        Optional<OptionValue> option = entityManager.createQuery(criteriaQuery).getResultList().stream().findFirst();
        return option.orElse(null);
    }

    public OfferOption createOrUpdateOption(OfferOptionInput input) {
        OfferOption option = null;
        if (input.getId() == null) {
            option = getOptionById(input.getId());
        }
        if (option == null) {
            option = new OfferOption();
        }
        option.setTitle(input.getTitle());
        entityManager.getTransaction().begin();
        entityManager.persist(option);
        entityManager.getTransaction().commit();
        return option;
    }

    public void removeOption(Long optionId) {
        CriteriaQuery<OptionValue> criteriaQuery = criteriaBuilder.createQuery(OptionValue.class);
        Root<OptionValue> root = criteriaQuery.from(OptionValue.class);
        Join optionJoin = root.join("option");
        criteriaQuery.where(criteriaBuilder.equal(optionJoin.get("id"), optionId));
        List<OptionValue> valuesList = entityManager.createQuery(criteriaQuery).getResultList();
        valuesList.forEach(v -> {
            entityManager.remove(v);
        });
        OfferOption option = getOptionById(optionId);
        if (option != null) {
            entityManager.remove(option);
        }
    }

    public void createOrUpdateValue(OptionValueInput input) throws Exception{
        OptionValue optionValue = null;
        if (input.getId() != null) {
            optionValue = getOptionValueById(input.getId());
        }
        if (optionValue == null) {
            optionValue = new OptionValue();
        }
        optionValue.setValue(input.getValue());
        OfferOption option = getOptionById(input.getOptionId());
        if (option == null) {
            throw new Exception("error.option.for.value.not.found");
        }
        optionValue.setOption(option);
        String alias = Transliterator.transliteration(input.getValue());
        boolean aliasIsBusy = true;
        int count = 0;
        while (aliasIsBusy) {
            OptionValue valueWithSameAlias = getOptionValueByAlias(alias);
            if (valueWithSameAlias != null) {
                alias = alias + String.valueOf(count++);
            } else {
                aliasIsBusy = false;
            }
        }
        optionValue.setAlias(alias);
        if (input.getImageInput() != null) {
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createImageForObject(optionValue, data);
            optionValue.setImage(image);
        }
    }

    public void removeValue(Long valueId) throws Exception{
        OptionValue optionValue = getOptionValueById(valueId);
        if (optionValue != null ){
            try {
                imageRepository.removeImageForObject(optionValue);
                entityManager.getTransaction().begin();
                entityManager.remove(optionValue);
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                throw new Exception("error.occurred.while.removing.option.value");
            }
        }

    }

    private void initialiseCriteriaBuilder() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }
}
