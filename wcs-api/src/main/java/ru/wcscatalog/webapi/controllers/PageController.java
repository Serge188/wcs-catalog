package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.wcscatalog.core.dto.FactoryEntry;
import ru.wcscatalog.core.dto.PageEntry;
import ru.wcscatalog.core.dto.PageInput;
import ru.wcscatalog.core.model.Page;
import ru.wcscatalog.core.repository.PageRepository;

import java.util.List;

@Controller
@RequestMapping("/pages")
public class PageController {

    private final PageRepository pageRepository;

    public PageController(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @GetMapping()
    public ResponseEntity<List<PageEntry>> getAllFactories() {
        List<PageEntry> pages = pageRepository.getPages();
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/byAlias/{alias}")
    public ResponseEntity<PageEntry> getPageByAlias(@PathVariable("alias") String alias) {
        PageEntry page = pageRepository.getPageByAlias(alias);
        return ResponseEntity.ok(page);
    }

    @PostMapping()
    public ResponseEntity<?> createPage(@RequestBody PageInput input) {
        pageRepository.createPage(input);
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    public ResponseEntity<?> updatePage(@RequestBody PageInput input) {
        pageRepository.updatePage(input);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removePage(@PathVariable("id") Long id) {
        pageRepository.removePage(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mainMenuPages")
    public ResponseEntity<List<PageEntry>> getMainMenuPages() {
        List<PageEntry> pages = pageRepository.getMainMenuPages();
        return ResponseEntity.ok(pages);
    }
}
