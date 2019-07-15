package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.wcscatalog.core.dto.OfferOptionEntry;
import ru.wcscatalog.core.dto.OfferOptionInput;
import ru.wcscatalog.core.dto.OptionValueInput;
import ru.wcscatalog.core.model.OfferOption;
import ru.wcscatalog.core.repository.OptionsRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/options")
public class OptionsController {

    private final OptionsRepository optionsRepository;

    public OptionsController(OptionsRepository optionsRepository) {
        this.optionsRepository = optionsRepository;
    }

    @GetMapping()
    public ResponseEntity<List<OfferOptionEntry>> getOpitons() {
        List<OfferOptionEntry> options = optionsRepository.getAllOptions();
        return ResponseEntity.ok(options);
    }

    @PostMapping("/option")
    public ResponseEntity<?> createNewOption(@RequestBody OfferOptionInput input) {
        try {
            OfferOptionEntry option = optionsRepository.createOrUpdateOption(input);
            return ResponseEntity.ok(option);
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of("error.creating.option"));
        }
    }

    @DeleteMapping("/option/{id}")
    public ResponseEntity<?> deleteOption(@PathVariable("id") Long optionId) {
        optionsRepository.removeOption(optionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/option")
    public ResponseEntity<?> updateOption(@RequestBody OfferOptionInput input) {
        try {
            OfferOptionEntry option = optionsRepository.createOrUpdateOption(input);
            return ResponseEntity.ok(option);
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of("error.updating.option"));
        }
    }

//    @PostMapping("/optionValue")
//    public ResponseEntity<?> addNewOptionValue(@RequestBody OptionValueInput input) {
//        try {
//            optionsRepository.createOrUpdateValue(input);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.of(Optional.of("error.option.for.value.not.found"));
//        }
//    }
//
//    @PutMapping("/optionValue")
//    public ResponseEntity<?> updateOptionValue(@RequestBody OptionValueInput input) {
//        try {
//            optionsRepository.createOrUpdateValue(input);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.of(Optional.of("error.option.for.value.not.found"));
//        }
//    }

    @DeleteMapping("/optionValue/{id}")
    public ResponseEntity<?> deleteOptionValue(@PathVariable("id") Long optionValueId) {
        optionsRepository.removeOption(optionValueId);
        return ResponseEntity.ok().build();
    }

}
