package ru.wcscatalog.core.utils;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageResizer {

    public static BufferedImage resize(BufferedImage image, int width, int height) throws Exception {
        float targetRatio = ((float)height) / width;
        float sourceRatio = ((float)image.getHeight()) / image.getWidth();
        BufferedImage cutImage = image;
        if (targetRatio != sourceRatio) {
            if (targetRatio > sourceRatio) {
                float newWidth = image.getHeight() / targetRatio;
                cutImage = image.getSubimage((int)Math.abs(newWidth - image.getWidth())/2, 0, (int) newWidth, image.getHeight());
            } else {
                float newHeight = image.getWidth() * targetRatio;
                cutImage = image.getSubimage(0, (int)Math.abs(newHeight - image.getHeight())/2, image.getWidth(), (int) newHeight);
            }
        }
        BufferedImage resizedImage = Thumbnails.of(cutImage).size(width, height).outputQuality(1.0).asBufferedImage();
        return resizedImage;
    }
}
