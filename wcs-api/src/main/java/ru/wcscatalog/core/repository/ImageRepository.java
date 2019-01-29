package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.model.Category;
import ru.wcscatalog.core.model.Image;
import ru.wcscatalog.core.utils.ImageResizer;

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
import java.util.Base64;

@Repository
public class ImageRepository {

    private static final String SERVER_FOLDER = "E:/wcs-catalog/wcs-webclient/src/";

    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Image> criteriaQuery;
    private Root<Image> root;

    public ImageRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
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
                String path = SERVER_FOLDER + appFolder + fileName;
                File outputfile = new File(path);
                ImageIO.write(categoryAvatar, fileExtension, outputfile);
                Image image = new Image();
                image.setCategoryImageLink(appFolder + fileName);
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
            String path = SERVER_FOLDER + img.getCategoryImageLink();
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
}
