package ru.wcscatalog.core.repository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.dto.OfferOptionEntry;
import ru.wcscatalog.core.dto.OfferOptionInput;
import ru.wcscatalog.core.dto.OptionValueInput;
import ru.wcscatalog.core.model.Image;
import ru.wcscatalog.core.model.OfferOption;
import ru.wcscatalog.core.model.OptionValue;
import ru.wcscatalog.core.utils.Transliterator;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class OptionsRepository {
    private final ImageRepository imageRepository;
    private Dao dao;

    public OptionsRepository(ImageRepository imageRepository, Dao dao) {
        this.imageRepository = imageRepository;
        this.dao = dao;
    }

    public List<OfferOptionEntry> getAllOptions() {
        List<OfferOption> options = dao.getAll(OfferOption.class);
        List<OfferOptionEntry> optionEntries = options.stream().map(OfferOptionEntry::fromOfferOption).collect(Collectors.toList());
//        fillOptionValues(optionEntries);
        return optionEntries;
    }

    public OfferOption getOptionById(Long id) {
        return dao.byId(id, OfferOption.class);
    }

    public OfferOption getOptionByNameAndCategory(String name, Long categoryId) {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<OfferOption> criteriaQuery = criteriaBuilder.createQuery(OfferOption.class);
        Root<OfferOption> root = criteriaQuery.from(OfferOption.class);
        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(root.get("name"), name),
                criteriaBuilder.equal(root.get("category"), categoryId)));
        return dao.createQuery(criteriaQuery).stream().findFirst().orElse(null);
    }

    public OptionValue getOptionValueById(Long id) {
        return dao.byId(id, OptionValue.class);
    }

    private OptionValue getOptionValueByAlias(String alias) {
        return dao.singleByProperty("alias", alias, OptionValue.class);
    }

    @PreAuthorize("isAuthenticated()")
    public OfferOptionEntry createOrUpdateOption(OfferOptionInput input) throws Exception {
        OfferOption option = null;
        if (input.getId() != null) {
            option = getOptionById(input.getId());
        }
        if (option == null) {
            option = new OfferOption();
        }
        option.setTitle(input.getTitle());
        option.setName(input.getName());
        List<OptionValue> oldValues = getValuesForOptions(Collections.singletonList(option.getId()));
        for (OptionValue v: oldValues) {
            Optional<OptionValueInput> correspondingNewValue = input
                    .getValues()
                    .stream()
                    .filter(x -> x.getId() != null)
                    .filter(x -> x.getId().equals(v.getId()))
                    .findFirst();
            if (!correspondingNewValue.isPresent()) {
                removeValue(v.getId());
            }
        }
        option.setShowInFilter(input.getShowInFilter());
        dao.add(option);
        for (OptionValueInput v: input.getValues()) {
            createOrUpdateValue(v, option);
        }
        return OfferOptionEntry.fromOfferOption(option);
    }

    @PreAuthorize("isAuthenticated()")
    public void removeOption(Long optionId) {
        OfferOption option = getOptionById(optionId);
        if (option != null) {
            dao.remove(option);
        }
    }

    @PreAuthorize("isAuthenticated()")
    public OptionValue createOrUpdateValue(OptionValueInput input, OfferOption option) throws Exception{
        OptionValue optionValue = null;
        if (input.getId() != null) {
            optionValue = getOptionValueById(input.getId());
        }
        if (optionValue == null) {
            optionValue = new OptionValue();
        }
        optionValue.setValue(input.getValue());
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
        dao.add(optionValue);
        return optionValue;
    }

    @PreAuthorize("isAuthenticated()")
    public void removeValue(Long valueId) throws Exception{
        OptionValue optionValue = getOptionValueById(valueId);
        if (optionValue != null ){
            try {
                imageRepository.removeImageForObject(optionValue);
                dao.remove(optionValue);
            } catch (Exception e) {
                throw new Exception("error.occurred.while.removing.option.value");
            }
        }

    }

    protected List<OptionValue> getValuesForOptions(Collection<Long> optionIds) {
        if (optionIds.isEmpty()) {
            return new ArrayList<>();
        }
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<OptionValue> criteriaQuery = criteriaBuilder.createQuery(OptionValue.class);
        Root<OptionValue> root = criteriaQuery.from(OptionValue.class);
        Join optionJoin = root.join("option");
        criteriaQuery.where(optionJoin.get("id").in(optionIds));
        return dao.createQuery(criteriaQuery);
    }
}
