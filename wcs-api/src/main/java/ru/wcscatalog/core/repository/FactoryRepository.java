package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dto.FactoryEntry;
import ru.wcscatalog.core.dto.FactoryInput;
import ru.wcscatalog.core.model.Factory;
import ru.wcscatalog.core.utils.AliasChecker;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FactoryRepository {
    private AliasChecker aliasChecker;
    private Dao dao;

    public FactoryRepository(AliasChecker aliasChecker, Dao dao) {
        this.aliasChecker = aliasChecker;
        this.dao = dao;
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

    public void createFactory(FactoryInput input) {
        Factory factory = new Factory();
        factory.setTitle(input.getTitle());
        factory.setAlias(aliasChecker.findUniqueAliasForEntity(factory.getClass(), input.getTitle()));
        factory.setDescription(input.getDescription());
        dao.add(factory);
    }

    public void updateFactory(FactoryInput input) {
        Factory factory = getFactoryById(input.getId());
        if (factory == null) {
            return;
        }
        factory.setTitle(input.getTitle());
    }

    public void removeFactory(Long id) {
        Factory factory = getFactoryById(id);
        if (factory != null) {
            dao.remove(factory);
        }

    }
}
