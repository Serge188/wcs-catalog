package ru.wcscatalog.core.repository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.dto.PageEntry;
import ru.wcscatalog.core.dto.PageInput;
import ru.wcscatalog.core.model.Image;
import ru.wcscatalog.core.model.Page;
import ru.wcscatalog.core.utils.AliasChecker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional
public class PageRepository {
    private final AliasChecker aliasChecker;
    private final ImageRepository imageRepository;
    private Dao dao;

    public PageRepository(AliasChecker aliasChecker,
                          ImageRepository imageRepository, Dao dao) {
        this.aliasChecker = aliasChecker;
        this.imageRepository = imageRepository;
        this.dao = dao;
    }

    public List<PageEntry> getPages() {
        List<Page> pages = dao.getAll(Page.class);
        return pages.stream().map(PageEntry::fromPage).collect(Collectors.toList());
    }

    public Page getPageById(Long id) {
        return dao.byId(id, Page.class);
    }

    public PageEntry getPageByAlias(String alias) {
        Page page = dao.singleByProperty("alias", alias, Page.class);
        return PageEntry.fromPage(page);
    }

    @PreAuthorize("isAuthenticated()")
    public void createPage(PageInput input) {
        if (input == null) {
            return;
        }
        Page page = new Page();
        updatePageFromInput(page, input);
        dao.add(page);
    }

    @PreAuthorize("isAuthenticated()")
    public void updatePage(PageInput input) {
        if (input == null || input.getId() == null) {
            return;
        }
        Page page = getPageById(input.getId());
        updatePageFromInput(page, input);
    }

    @PreAuthorize("isAuthenticated()")
    public void updatePageFromInput(Page page, PageInput input) {
        page.setTitle(input.getTitle());
        if ((!input.getTitle().equals(page.getTitle())) || page.getAlias() == null) {
            page.setAlias(aliasChecker.findUniqueAliasForEntity(Page.class, input.getTitle()));
        }
        page.setSlider(input.getSlider());
        page.setSliderHeader(input.getSliderHeader());
        page.setSliderPromo(input.getSliderPromo());
        page.setSliderAnnotation(input.getSliderAnnotation());
//        page.setContent(input.getContent());

        if (input.getContent() != null) {
            page.setContent(input
                    .getContent()
                    .replaceAll("\\n", "<br/>")
                    .replaceAll("\\t", "&nbsp;&nbsp;&nbsp;"));
        }
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

    @PreAuthorize("isAuthenticated()")
    public void removePage(Long pageId) {
        Page page = getPageById(pageId);
        if (page != null) {
            dao.remove(page);
        }
    }

    public List<PageEntry> getMainMenuPages() {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<Page> criteriaQuery = criteriaBuilder.createQuery(Page.class);
        Root<Page> root = criteriaQuery.from(Page.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("showInMainMenu"), true));
        List<Page> pages = dao.createQuery(criteriaQuery);
        return pages.stream().map(PageEntry::fromPage).collect(Collectors.toList());
    }
}
