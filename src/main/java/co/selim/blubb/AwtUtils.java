package co.selim.blubb;

import co.selim.hslimage.HSLImage;

import java.awt.image.BufferedImage;

class AwtUtils {
    static HSLImage fromBufferedImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        HSLImage img = new HSLImage(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                img.setPixel(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return img;
    }
}
