package it.janczewski.batchProcessing.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public static void procesImages(String path) {
        File dir = new File(path);
        if (dir.isDirectory() && dir.listFiles() != null) {
            Stream.of(dir.listFiles()).map(file -> {
                if (!file.getName().contains("thumbnail")) {
                    BufferedImage imageFile = null;
                    try {
                        imageFile = ImageIO.read(file);
                    } catch (IOException e) {
                        logger.error("Image read error", e);
                        return file.getAbsolutePath() + " read error";
                    }
                    int height = imageFile.getHeight();
                    int width = imageFile.getWidth();
                    int type = imageFile.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : imageFile.getType();
                    int downgreeFactor = calculateResizeFactor(height, width);
                    BufferedImage resized = createThumbnail(imageFile, type, downgreeFactor);
                    String thumbnailName = file.getName().contains(".") ? file.getName().replace(".", "_thumbnail.") : file.getName() + "_thumbnail";
                    try {
                        ImageIO.write(resized, "jpg", new File(path + "/" + thumbnailName));
                    } catch (IOException e) {
                        logger.error("Image write error", e);
                        return file.getAbsolutePath() + " write error";
                    }
                    return thumbnailName + " generated";
                } else {
                    return file.getName() + " skipped";
                }
            }).forEach(logger::info);
        }
    }

    private static int calculateResizeFactor(int height, int width) {
        int factor = 0;
        while (height > 400 | width > 400) {
            factor += 1;
            height /= 2;
            width /= 2;
        }
        return factor;
    }

    private static BufferedImage createThumbnail(BufferedImage originalImage, int type, int factor) {
        int div = factor != 0 ? 2 * factor : 1;
        int newWidth = (originalImage.getWidth() / (div));
        int newHeight = (originalImage.getHeight() / (div));
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return resizedImage;
    }
}
