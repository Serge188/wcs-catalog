package ru.wcscatalog.core.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.model.*;
import ru.wcscatalog.core.repository.Dao;
import ru.wcscatalog.core.repository.ImageRepository;
import ru.wcscatalog.core.repository.OptionsRepository;
import ru.wcscatalog.core.repository.ProductRepository;
import ru.wcscatalog.core.utils.AliasChecker;
import ru.wcscatalog.core.utils.Transliterator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final Dao dao;
    private final AliasChecker aliasChecker;
    private final OptionsRepository optionsRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    public ProductService(Dao dao, AliasChecker aliasChecker,
                          OptionsRepository optionsRepository,
                          ProductRepository productRepository,
                          ImageRepository imageRepository){
        this.dao = dao;
        this.aliasChecker = aliasChecker;
        this.optionsRepository = optionsRepository;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    @Transactional
    public void createProductsFromFile(String fileData) throws IOException {
        if (fileData.split(",").length != 2) {
            return;
        }
        String data = fileData.split(",")[1];
        byte[] buffer = Base64.getDecoder().decode(data);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
//        FileInputStream inputStream = new FileInputStream(new File(data));
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet descriptionSheet = workbook.getSheetAt(0);
        XSSFSheet imagesSheet = workbook.getSheetAt(1);
        XSSFSheet featuresSheet = workbook.getSheetAt(2);
        XSSFSheet pricesSheet = workbook.getSheetAt(3);
        XSSFSheet productAndCatSheet = workbook.getSheetAt(4);

        Row row = productAndCatSheet.getRow(0);
        long factoryId = (long) row.getCell(1).getNumericCellValue();
        row = productAndCatSheet.getRow(1);
        long categoryId = (long) row.getCell(1).getNumericCellValue();

        Factory factory = dao.byId(factoryId, Factory.class);
        Category category = dao.byId(categoryId, Category.class);

        if (factory == null || category == null) {
            return;
        }

        List<Product> products = new ArrayList<>();
        Iterator<Row> rowIterator = descriptionSheet.rowIterator();
        while(rowIterator.hasNext()) {
            Product product;
            row = rowIterator.next();
            String productTitle = row.getCell(0).getStringCellValue();
            product = productRepository.getProductByTitleAndCategory(productTitle, categoryId);
            if (product == null) {
                product = new Product();
            }
            product.setTitle(productTitle);
            product.setAlias(aliasChecker.findUniqueAliasForEntity(Product.class, product.getTitle()));
            product.setDescription(row.getCell(1).getStringCellValue());
            product.setFactory(factory);
            product.setCategory(category);
            products.add(product);
            dao.add(product);
        }
//        rowIterator = pricesSheet.rowIterator();
//        while(rowIterator.hasNext()) {
//            row = rowIterator.next();
//            String productTitle = row.getCell(0).getStringCellValue();
//            OfferOption offerOption = optionsRepository.getOptionByNameAndCategory("Размеры", categoryId);
//            String value = row.getCell(1).getStringCellValue();
//            OptionValue optionValue = offerOption.getValues().stream().filter(x -> x.getValue().equals(value)).findFirst().orElse(null);
//            if (optionValue != null) {
//                SaleOffer saleOffer = new SaleOffer();
//                saleOffer.setOfferOption(offerOption);
//                saleOffer.setOptionValue(optionValue);
//                saleOffer.setDiscountPrice((float)row.getCell(2).getNumericCellValue());
//                saleOffer.setPrice((float)row.getCell(3).getNumericCellValue());
//
//                Optional<Product> product = products.stream().filter(p -> p.getTitle().equals(productTitle)).findFirst();
//                if(product.isPresent()) {
//                    saleOffer.setProduct(product.get());
//                    dao.add(saleOffer);
//                }
//            }
//        }
        rowIterator = featuresSheet.rowIterator();
        while(rowIterator.hasNext()) {
            row = rowIterator.next();
            String productTitle = row.getCell(0).getStringCellValue();
            String optionName = row.getCell(1).getStringCellValue();
            OfferOption offerOption = optionsRepository.getOptionByNameAndCategory(optionName + "(" + category.getTitle() + ")", categoryId);
            if (offerOption== null) {
                offerOption = new OfferOption();
                offerOption.setName(optionName + "(" + category.getTitle() + ")");
                offerOption.setTitle(optionName);
                offerOption.setCategory(category);
            }
            dao.add(offerOption);
            String value = row.getCell(2).getStringCellValue();
            OptionValue optionValue;
            if (!offerOption.getValues().stream().map(OptionValue::getValue).collect(Collectors.toList()).contains(value)) {
                optionValue = new OptionValue();
                optionValue.setOption(offerOption);
                optionValue.setValue(value);
                optionValue.setAlias(Transliterator.transliteration(value));
                dao.add(optionValue);
                offerOption.getValues().add(optionValue);
            } else {
                optionValue = offerOption.getValues().stream().filter(x -> x.getValue().equals(value)).findAny().get();
            }
            Optional<Product> product = products.stream().filter(p -> p.getTitle().equals(productTitle)).findFirst();

            if (product.isPresent()) {
                ProductOptionsRelation optionsRelation =  new ProductOptionsRelation();
                optionsRelation.setProduct(product.get());
                optionsRelation.setOption(offerOption);
                optionsRelation.setValue(optionValue);
                dao.add(optionsRelation);
            }
        }

//        rowIterator = imagesSheet.rowIterator();
//        while (rowIterator.hasNext()) {
//            row = rowIterator.next();
//            String productTitle = row.getCell(0).getStringCellValue();
//            Optional<Product> product = products.stream().filter(p -> p.getTitle().equals(productTitle)).findFirst();
//            if (product.isPresent()) {
//                String link = row.getCell(1).getStringCellValue();
//                try (BufferedInputStream in = new BufferedInputStream(new URL(link).openStream())) {
//                    byte[] b = Base64.getEncoder().encode(in.readAllBytes());
//                    String imageData = new String(b, StandardCharsets.ISO_8859_1);
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(".jpg,");
//                    sb.append(imageData);
//                    Image image = imageRepository.createImageForObject(product.get(), sb.toString());
//                    image.setProduct(product.get());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        products.forEach(p -> {
//            p.getImages().forEach(i -> i.setMainImage(false));
//        });
//        products.forEach(p -> {
//            Optional<Image> firstIdImage = p.getImages().stream().min(Comparator.comparingLong(Image::getId));
//            firstIdImage.ifPresent(i -> i.setMainImage(true));
//        });
    }
}
