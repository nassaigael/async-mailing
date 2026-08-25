package api.poja.app.service;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class ImageGrayscaleConverter {

  @SneakyThrows
  public File convertToGrayscale(File sourceFile, String extension) {
    var originalImage = ImageIO.read(sourceFile);
    if (originalImage == null) {
      throw new IllegalStateException(
          "Impossible de lire le fichier image : " + sourceFile.getName());
    }

    var grayscaleImage =
        new BufferedImage(
            originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    var graphics = grayscaleImage.getGraphics();
    graphics.drawImage(originalImage, 0, 0, null);
    graphics.dispose();

    var format = normalizeFormat(extension);
    var outputFile = File.createTempFile("bw-", extension.isBlank() ? ".png" : extension);
    ImageIO.write(grayscaleImage, format, outputFile);
    return outputFile;
  }

  private String normalizeFormat(String extension) {
    if (extension == null || extension.isBlank()) {
      return "png";
    }
    var format = extension.startsWith(".") ? extension.substring(1) : extension;
    return format.equalsIgnoreCase("jpg") ? "jpeg" : format.toLowerCase();
  }
}
