package ru.wcscatalog.core.repository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.dto.FactoryEntry;
import ru.wcscatalog.core.dto.FactoryInput;
import ru.wcscatalog.core.model.Factory;
import ru.wcscatalog.core.model.Image;
import ru.wcscatalog.core.utils.AliasChecker;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class FactoryRepository {
    private AliasChecker aliasChecker;
    private Dao dao;
    private ImageRepository imageRepository;

    public FactoryRepository(AliasChecker aliasChecker, Dao dao, ImageRepository imageRepository) {
        this.aliasChecker = aliasChecker;
        this.dao = dao;
        this.imageRepository = imageRepository;
    }

    public List<FactoryEntry> getAllFactories() {
        List<Factory> factories = dao.getAll(Factory.class);
        List<FactoryEntry> entries = new ArrayList<>();
        for (Factory p: factories) {
            entries.add(FactoryEntry.fromFactory(p));
        }
        return entries;
    }

    public List<FactoryEntry> getPopularBrands() {
        List<Factory> factories = dao.listByProperty("popular", true, Factory.class);
        List<FactoryEntry> entries = new ArrayList<>();
        for (Factory p: factories) {
            entries.add(FactoryEntry.fromFactory(p));
        }
        return entries;
    }

    public Factory getFactoryById(Long id) {
        return dao.byId(id, Factory.class);
    }

    @PreAuthorize("isAuthenticated()")
    public void createFactory(FactoryInput input) throws Exception {
        Factory factory = new Factory();
        factory.setTitle(input.getTitle());
        factory.setAlias(aliasChecker.findUniqueAliasForEntity(factory.getClass(), input.getTitle()));
        factory.setDescription(input.getDescription());
        dao.add(factory);
        if (input.getImageInput() != null) {
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createFactoryImage(data, factory.getAlias());
            factory.setImage(image);
            dao.add(image);
        }
    }

    @PreAuthorize("isAuthenticated()")
    public void updateFactory(FactoryInput input) throws Exception {
        Factory factory = getFactoryById(input.getId());
        if (factory == null) {
            return;
        }
        factory.setTitle(input.getTitle());
        factory.setDescription(input.getDescription());

        if (input.getImageInput() != null) {
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createFactoryImage(data, factory.getAlias());
            factory.setImage(image);
        }
    }

    @PreAuthorize("isAuthenticated()")
    public void removeFactory(Long id) {
        Factory factory = getFactoryById(id);
        if (factory != null) {
            imageRepository.removeFactoryImage(factory.getImage().getId());
            dao.remove(factory);
        }

    }
}
