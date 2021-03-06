package ru.wcscatalog.core.repository;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
public class ImageRepository {

    private final EntityManagerFactory entityManagerFactory;
    private final Environment environment;
    private String serverFolder;
    private final Dao dao;

    public ImageRepository(EntityManagerFactory entityManagerFactory, Environment environment, Dao dao) {
        this.entityManagerFactory = entityManagerFactory;
        this.environment = environment;
        this.serverFolder = environment.getRequiredProperty("storage");
        this.dao = dao;
    }

    private String getFileExtension(String data) throws Exception {
        String[] separatedData = data.split(",");
        if (separatedData.length > 1) {
            String fileExtension;
            if (separatedData[0].toLowerCase().contains("png")) {
                fileExtension = "png";
            } else {
                fileExtension = "jpg";
            }
            return fileExtension;
        } else {
            throw new Exception("Could not create an image");
        }
    }

    private BufferedImage createBufferedImageFromData(String data) throws Exception {
        String[] separatedData = data.split(",");
        if (separatedData.length > 1) {
            byte[] buf = Base64.getDecoder().decode(separatedData[1]);
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            return ImageIO.read(bais);
        } else {
            throw new Exception("Could not create an image");
        }
    }

    public Image createImageForGalleryItem(String data, String name, boolean isMainImage) throws Exception {
        String fileExtension = getFileExtension(data);
        BufferedImage imageFile = createBufferedImageFromData(data);
        BufferedImage originalImage = imageFile;
        BufferedImage previewImage = ImageResizer.resize(imageFile, 248, 186);
        String imgOriginalFolder = "assets/img/photoGallery/";
        String imgPreviewFolder = "assets/img/photoGallery/preview/";

        String fileName = Transliterator.transliteration(name) + "_" + new Date().getTime();

        String originalPath = serverFolder + imgOriginalFolder + fileName;
        String previewPath = serverFolder + imgPreviewFolder + fileName;

        File originalFile = new File(originalPath);
        File previewFile = new File(previewPath);

        ImageIO.write(originalImage, fileExtension, originalFile);
        ImageIO.write(previewImage, fileExtension, previewFile);

        Image image = new Image();
        image.setOriginalImageLink(imgOriginalFolder + fileName);
        image.setPreviewImageLink(imgPreviewFolder + fileName);
        image.setMainImage(isMainImage);
        dao.add(image);
        return image;
    }

    public Image createFactoryImage(String data, String name) throws Exception {
        String fileExtension = getFileExtension(data);
        BufferedImage imageFile = createBufferedImageFromData(data);
        BufferedImage originalImage = imageFile;
        String imgOriginalFolder = "assets/img/factories/";

        String fileName = name + "_" + new Date().getTime();

        String originalPath = serverFolder + imgOriginalFolder + fileName;

        File originalFile = new File(originalPath);

        ImageIO.write(originalImage, fileExtension, originalFile);

        Image image = new Image();
        image.setOriginalImageLink(imgOriginalFolder + fileName);
        dao.add(image);
        return image;
    }

    public void removePhotoGalleryImage(Long imageId) {
        Image image = dao.byId(imageId, Image.class);
        String originalPath = serverFolder + image.getOriginalImageLink();
        String previewPath = serverFolder + image.getPreviewImageLink();
        try {
            Files.delete(Paths.get(originalPath));
            Files.delete(Paths.get(previewPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dao.remove(image);
    }

    public void removeFactoryImage(Long imageId) {
        if (imageId == null) return;
        Image image = dao.byId(imageId, Image.class);
        String originalPath = serverFolder + image.getOriginalImageLink();
        try {
            Files.delete(Paths.get(originalPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dao.remove(image);
    }

    public Image createImageForObject(Object o, String data) throws Exception {
        String fileExtension = getFileExtension(data);
        BufferedImage imageFile = createBufferedImageFromData(data);
//        String[] separatedData = data.split(",");
//        if (separatedData.length > 1) {
//            String fileExtension;
//            if (separatedData[0].toLowerCase().contains("png")) {
//                fileExtension = "png";
//            } else {
//                fileExtension = "jpg";
//            }
//            byte[] buf = Base64.getDecoder().decode(separatedData[1]);
//            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
//            BufferedImage imageFile = ImageIO.read(bais);
            if (o instanceof Category || o instanceof Page) {
                BufferedImage categoryAvatar = ImageResizer.resize(imageFile, 220, 160);
                String appFolder = "assets/img/category/";
                String fileName;
                if (o instanceof Category) {
                    fileName = ((Category) o).getAlias() + "." + fileExtension;
                } else {
                    fileName = ((Page) o).getAlias() + "." + fileExtension;
                }

                String path = serverFolder + appFolder + fileName;
                File outputfile = new File(path);
                ImageIO.write(categoryAvatar, fileExtension, outputfile);
                Image image = new Image();
                image.setCategoryImageLink(appFolder + fileName);
                dao.add(image);
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
                dao.add(image);
                return image;
            } else if (o instanceof Product || o instanceof SaleOffer) {
                String fileName;
                if (o instanceof Product) {
                    fileName = ((Product) o).getAlias() + String.valueOf(new Date().getTime()) + "." + fileExtension;
                } else {
                    String valueAlias = Transliterator.transliteration(((SaleOffer) o).getOptionValue().getValue());
                    fileName = ((SaleOffer) o).getProduct().getAlias() + "_" + valueAlias + "." + fileExtension;
                }

                BufferedImage imgBase = imageFile;
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
                dao.add(image);
                return image;
            }
//        } else {
//            throw new Exception("Could not create an image");
//        }
        return null;
    }

    public Image createSliderImage(Page page, String data) throws Exception {
        String[] separatedData = data.split(",");
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
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
            String appFolder = "assets/img/catalog/original/";
            String fileName = page.getAlias() + "." + fileExtension;

            String path = serverFolder + appFolder + fileName;
            File outputfile = new File(path);
            ImageIO.write(imageFile, fileExtension, outputfile);
            Image image = new Image();
            image.setOriginalImageLink(appFolder + fileName);
            dao.add(image);
            return image;
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
            dao.remove(img);
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
            dao.remove(img);
        }
    }

    public void removeProductImage(Image img) {
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
        dao.remove(img);
    }

    public void removeImageFromProduct(Long imageId) {
        Image image = dao.byId(imageId, Image.class);
        if (image != null) {
            removeProductImage(image);
        }
    }
}
