package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dto.PageEntry;
import ru.wcscatalog.core.dto.PageInput;
import ru.wcscatalog.core.model.Image;
import ru.wcscatalog.core.model.Page;
import ru.wcscatalog.core.utils.AliasChecker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PageRepository {
    private final EntityManagerFactory entityManagerFactory;
    private final AliasChecker aliasChecker;
    private final ImageRepository imageRepository;
    private EntityManager entityManager;

    public PageRepository(EntityManagerFactory entityManagerFactory,
                          AliasChecker aliasChecker,
                          ImageRepository imageRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.aliasChecker = aliasChecker;
        this.imageRepository = imageRepository;
        initialiseCriteriaBuilder();
    }

    public List<PageEntry> getPages() {
        CriteriaQuery<Page> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Page.class);
        Root<Page> root = criteriaQuery.from(Page.class);
        List<Page> pages = entityManager.createQuery(criteriaQuery).getResultList();
        List<PageEntry> entries = pages.stream().map(PageEntry::fromPage).collect(Collectors.toList());
        return entries;
    }

    public Page getPageById(Long id) {
        CriteriaQuery<Page> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Page.class);
        Root<Page> root = criteriaQuery.from(Page.class);
        criteriaQuery.where(entityManager.getCriteriaBuilder().equal(root.get("id"), id));
        Optional<Page> page = entityManager.createQuery(criteriaQuery).getResultList().stream().findFirst();
        return page.orElse(null);
    }

    public PageEntry getPageByAlias(String alias) {
        CriteriaQuery<Page> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Page.class);
        Root<Page> root = criteriaQuery.from(Page.class);
        criteriaQuery.where(entityManager.getCriteriaBuilder().equal(root.get("alias"), alias));
        Page page = entityManager.createQuery(criteriaQuery).getSingleResult();
        PageEntry entry = PageEntry.fromPage(page);
        fillChildPages(entry);
        return entry;
    }

    public void createPage(PageInput input) {
        if (input == null) {
            return;
        }
        Page page = new Page();
        updatePageFromInput(page, input);
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        entityManager.persist(page);
        entityManager.getTransaction().commit();
    }

    public void updatePage(PageInput input) {
        if (input == null || input.getId() == null) {
            return;
        }
        Page page = getPageById(input.getId());
        updatePageFromInput(page, input);
        entityManager.getTransaction().begin();
        entityManager.persist(page);
        entityManager.getTransaction().commit();
    }

    public void updatePageFromInput(Page page, PageInput input) {
        page.setTitle(input.getTitle());
        page.setAlias(aliasChecker.findUniqueAliasForEntity(Page.class, input.getTitle()));
        page.setSlider(input.getIsSlider());
        page.setSliderHeader(input.getSliderHeader());
        page.setSliderPromo(input.getSliderPromo());
        page.setSliderAnnotation(input.getSliderAnnotation());
        page.setContent(input.getContent());
        page.setShowInMainMenu(input.isShowInMainMenu());
        page.setShowInSideMenu(input.isShowInSideMenu());
        if (input.getParentPageId() != null) {
            page.setParentPage(getPageById(input.getParentPageId()));
        }
        if (input.getImageInput() != null) {
            String data = ((String) input.getImageInput());
            try {
                Image image = imageRepository.createImageForObject(page, data);
                page.setImage(image);
            } catch (Exception e) {}
        }
        if (input.getSliderImageInput() != null) {
            String data = ((String) input.getSliderImageInput());
            try {
                Image image = imageRepository.createSliderImage(page, data);
                page.setSliderImage(image);
            } catch (Exception e) {}
        }
    }

    public void removePage(Long pageId) {
        Page page = getPageById(pageId);
//        entityManager.remove(entityManager.contains(page) ? page : entityManager.merge(page));
        entityManager.getTransaction().begin();
        entityManager.remove(page);
        entityManager.getTransaction().commit();
    }

    public List<PageEntry> getMainMenuPages() {
        CriteriaQuery<Page> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Page.class);
        Root<Page> root = criteriaQuery.from(Page.class);
        criteriaQuery.where(entityManager.getCriteriaBuilder().equal(root.get("showInMainMenu"), true));
        List<Page> pages = entityManager.createQuery(criteriaQuery).getResultList();
        List<PageEntry> entries = pages.stream().map(PageEntry::fromPage).collect(Collectors.toList());
        return entries;
    }

    private void initialiseCriteriaBuilder() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
    }

    private void fillChildPages(PageEntry page) {
        CriteriaQuery<Page> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Page.class);
        Root<Page> root = criteriaQuery.from(Page.class);
        Join parent = root.join("parentPage");
        criteriaQuery.where(entityManager.getCriteriaBuilder().equal(parent.get("id"), page.getId()));
        List<Page> childPages = entityManager.createQuery(criteriaQuery).getResultList();
        page.setChildPages(childPages.stream().map(PageEntry::fromPage).collect(Collectors.toList()));
    }
}
