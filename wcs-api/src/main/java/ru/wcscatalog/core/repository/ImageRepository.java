package ru.wcscatalog.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.model.*;
import ru.wcscatalog.core.utils.ImageResizer;
import ru.wcscatalog.core.utils.Transliterator;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Repository
public class ImageRepository {

    private static final String SERVER_FOLDER = "E:/wcs-catalog/wcs-webclient/src/";

//    private static final String SERVER_FOLDER = "D:/wcs-catalog/wcs-webclient/src/";


    private final EntityManagerFactory entityManagerFactory;
    private final Environment environment;
    private String serverFolder;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Image> criteriaQuery;
    private Root<Image> root;

    public ImageRepository(EntityManagerFactory entityManagerFactory, Environment environment) {
        this.entityManagerFactory = entityManagerFactory;
        this.environment = environment;
        this.serverFolder = environment.getRequiredProperty("storage");
        buildCriteriaQuery();
    }

    public Image createImageForObject(Object o, String data) throws Exception {
        String[] separatedData = data.split(",");
        if (separatedData.length > 1) {
            String fileExtension;
            if (separatedData[0].toLowerCase().contains("png")) {
                fileExtension = "png";
            } else {
                fileExtension = "jpg";
            }
            byte[] buf = Base64.getDecoder().decode(separatedData[1]);
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            BufferedImage imageFile = ImageIO.read(bais);

            if (o instanceof Category) {
                BufferedImage categoryAvatar = ImageResizer.resize(imageFile, 220, 160);
                String appFolder = "assets/img/category/";
                String fileName = ((Category) o).getAlias() + "." + fileExtension ;
                String path = serverFolder + appFolder + fileName;
                File outputfile = new File(path);
                ImageIO.write(categoryAvatar, fileExtension, outputfile);
                Image image = new Image();
                image.setCategoryImageLink(appFolder + fileName);
                entityManager.getTransaction().begin();
                entityManager.persist(image);
                entityManager.getTransaction().commit();
                return image;
            } else if (o instanceof OptionValue) {
                BufferedImage categoryAvatar = ImageResizer.resize(imageFile, 180, 180);
                String appFolder = "assets/img/options/";
                String optionAlias = Transliterator.transliteration(((OptionValue) o).getOption().getTitle());
                String valueAlias = ((OptionValue) o).getAlias();
                String fileName = optionAlias + "_" + valueAlias + "." + fileExtension ;
                String path = serverFolder + appFolder + fileName;
                File outputfile = new File(path);
                ImageIO.write(categoryAvatar, fileExtension, outputfile);
                Image image = new Image();
                image.setOptionImageLink(appFolder + fileName);
                entityManager.getTransaction().begin();
                entityManager.persist(image);
                entityManager.getTransaction().commit();
                return image;
            } else if (o instanceof Product || o instanceof SaleOffer) {
                String fileName;
                if (o instanceof Product) {
                    fileName = ((Product) o).getAlias() + String.valueOf(new Date().getTime()) + "." + fileExtension;
                } else {
                    String valueAlias = Transliterator.transliteration(((SaleOffer) o).getOptionValue().getValue());
                    fileName = ((SaleOffer) o).getProduct().getAlias() + "_" + valueAlias + "." + fileExtension;
                }

                BufferedImage imgBase = ImageResizer.resize(imageFile, 1200, 800);
                BufferedImage imgCard = ImageResizer.resize(imageFile, 198, 198);
                BufferedImage imgGallery = ImageResizer.resize(imageFile, 100, 100);
                BufferedImage imgPreview = ImageResizer.resize(imageFile, 90, 90);

                String imgOriginalFolder = "assets/img/catalog/original/";
                String imgBaseFolder = "assets/img/catalog/base_image/";
                String imgCardFolder = "assets/img/catalog/card/";
                String imgGalleryFolder = "assets/img/catalog/gallery/";
                String imgPreviewFolder = "assets/img/catalog/preview/";

                String originalPath = serverFolder + imgOriginalFolder + fileName;
                String basePath = serverFolder + imgBaseFolder + fileName;
                String cardPath = serverFolder + imgCardFolder + fileName;
                String galleryPath = serverFolder + imgGalleryFolder + fileName;
                String previewPath = serverFolder + imgPreviewFolder + fileName;

                File originalImgFile = new File(originalPath);
                File baseImgFile = new File(basePath);
                File cardImgFile = new File(cardPath);
                File galleryImgFile = new File(galleryPath);
                File previewImgFile = new File(previewPath);

                ImageIO.write(imageFile, fileExtension, originalImgFile);
                ImageIO.write(imgBase, fileExtension, baseImgFile);
                ImageIO.write(imgCard, fileExtension, cardImgFile);
                ImageIO.write(imgGallery, fileExtension, galleryImgFile);
                ImageIO.write(imgPreview, fileExtension, previewImgFile);

                Image image = new Image();

                image.setOriginalImageLink(imgOriginalFolder + fileName);
                image.setBaseImageLink(imgBaseFolder + fileName);
                image.setCardImageLink(imgCardFolder + fileName);
                image.setGalleryImageLink(imgGalleryFolder + fileName);
                image.setPreviewImageLink(imgPreviewFolder + fileName);

                entityManager.getTransaction().begin();
                entityManager.persist(image);
                entityManager.getTransaction().commit();
                return image;
            }
        } else {
            throw new Exception("Could not create an image");
        }
        return null;
    }

    public void removeImageForObject(Object o) {
        if (o instanceof Category && ((Category) o).getImage() != null) {
            Image img = ((Category) o).getImage();
            String path = serverFolder + img.getCategoryImageLink();
            try {
                Files.delete(Paths.get(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
            entityManager.remove(entityManager.contains(img) ? img : entityManager.merge(img));
        } else if (o instanceof Product) {
            List<Image> images = ((Product) o).getImages();
            images.forEach(img -> removeProductImage(img));

        } else if (o instanceof SaleOffer) {
            Image img = ((SaleOffer) o).getMainImage();
            removeProductImage(img);
        } else if (o instanceof OptionValue && ((OptionValue) o).getImage() != null) {
            Image img = ((OptionValue) o).getImage();
            String path = serverFolder + img.getOptionImageLink();
            try {
                Files.delete(Paths.get(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
            entityManager.remove(entityManager.contains(img) ? img : entityManager.merge(img));
        }
    }

    private void buildCriteriaQuery() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Image.class);
        root = criteriaQuery.from(Image.class);
    }

    private void removeProductImage(Image img) {
        String originalPath = serverFolder + img.getOriginalImageLink();
        String basePath = serverFolder + img.getBaseImageLink();
        String cardPath = serverFolder + img.getCardImageLink();
        String galleryPath = serverFolder + img.getGalleryImageLink();
        String previewPath = serverFolder + img.getPreviewImageLink();
        List<String> paths = Arrays.asList(originalPath, basePath, cardPath, galleryPath, previewPath);
        paths.forEach(p -> {
            if (p != null) {
                try {
                    Files.delete(Paths.get(p));
                } catch (Exception e) {

                }
            }
        });
        entityManager.remove(entityManager.contains(img) ? img : entityManager.merge(img));
    }
}
