package api.poja.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImageGrayscaleConverterTest {

  private final ImageGrayscaleConverter converter = new ImageGrayscaleConverter();

  @TempDir File tempDir;

  @Test
  void shouldConvertPngToGrayscale() throws Exception {
    var sourceFile = createTestImage(tempDir, "source.png", "png", 100, 100);
    var extension = ".png";

    var result = converter.convertToGrayscale(sourceFile, extension);

    assertThat(result).exists();
    var image = ImageIO.read(result);
    assertThat(image).isNotNull();
    assertThat(image.getWidth()).isEqualTo(100);
    assertThat(image.getHeight()).isEqualTo(100);
    assertThat(image.getType()).isEqualTo(BufferedImage.TYPE_BYTE_GRAY);
  }

  @Test
  void shouldConvertJpgToGrayscale() throws Exception {
    var sourceFile = createTestImage(tempDir, "source.jpg", "jpg", 200, 150);
    var extension = ".jpg";

    var result = converter.convertToGrayscale(sourceFile, extension);

    assertThat(result).exists();
    var image = ImageIO.read(result);
    assertThat(image).isNotNull();
    assertThat(image.getWidth()).isEqualTo(200);
    assertThat(image.getHeight()).isEqualTo(150);
  }

  @Test
  void shouldUsePngDefaultWhenExtensionIsBlank() throws Exception {
    var sourceFile = createTestImage(tempDir, "source", "png", 100, 100);
    var extension = "";

    var result = converter.convertToGrayscale(sourceFile, extension);

    assertThat(result).exists();
    assertThat(result.getName()).endsWith(".png");
  }

  @Test
  void shouldUsePngDefaultWhenExtensionIsNull() throws Exception {
    var sourceFile = createTestImage(tempDir, "source", "png", 100, 100);

    var result = converter.convertToGrayscale(sourceFile, null);

    assertThat(result).exists();
    assertThat(result.getName()).endsWith(".png");
  }

  @Test
  void shouldThrowExceptionWhenFileIsNotImage() throws Exception {
    var sourceFile = new File(tempDir, "not-an-image.txt");
    Files.writeString(sourceFile.toPath(), "This is not an image");

    assertThatThrownBy(() -> converter.convertToGrayscale(sourceFile, ".txt"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Impossible de lire le fichier image");
  }

  private File createTestImage(File dir, String filename, String format, int width, int height)
      throws Exception {
    var file = new File(dir, filename);
    var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    ImageIO.write(image, format, file);
    return file;
  }
}
