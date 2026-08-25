package api.poja.app.service.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import api.poja.app.endpoint.event.model.FileUploadConfirmationRequested;
import api.poja.app.file.bucket.BucketComponent;
import api.poja.app.mail.Email;
import api.poja.app.mail.Mailer;
import api.poja.app.repository.UploadedFileRepository;
import api.poja.app.repository.model.UploadedFileEntity;
import api.poja.app.service.ImageGrayscaleConverter;
import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

<<<<<<< HEAD
import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

=======
>>>>>>> d5331f8 (fix(all): format UploadedFileResponseTest)
@ExtendWith(MockitoExtension.class)
class FileUploadConfirmationRequestedServiceTest {

  @Mock private UploadedFileRepository uploadedFileRepository;

  @Mock private BucketComponent bucketComponent;

  @Mock private ImageGrayscaleConverter imageGrayscaleConverter;

  @Mock private Mailer mailer;

  @InjectMocks private FileUploadConfirmationRequestedService service;

  @TempDir File tempDir;

  @Test
  void shouldProcessEventSuccessfully() throws Exception {
    var fileId = UUID.randomUUID();
    var event = FileUploadConfirmationRequested.builder().fileId(fileId).build();

    var entity =
        UploadedFileEntity.builder()
            .id(fileId)
            .nomFichier("photo.jpg")
            .email("user@example.com")
            .createdAt(Instant.now())
            .build();

    var originalFile = new File(tempDir, "original.jpg");
    originalFile.createNewFile();
    var convertedFile = new File(tempDir, "converted.jpg");
    convertedFile.createNewFile();

    var presignedUrl =
        new URL(
            "https://bucket.s3.region.amazonaws.com/uploads/"
                + fileId
                + "/bw.jpg?X-Amz-Signature=xxx");

    when(uploadedFileRepository.findById(fileId)).thenReturn(Optional.of(entity));
    when(bucketComponent.download("uploads/" + fileId + "/original.jpg")).thenReturn(originalFile);
    when(imageGrayscaleConverter.convertToGrayscale(originalFile, ".jpg"))
        .thenReturn(convertedFile);
    when(bucketComponent.presign("uploads/" + fileId + "/bw.jpg", java.time.Duration.ofDays(7)))
        .thenReturn(presignedUrl);

    service.accept(event);

    verify(bucketComponent).download("uploads/" + fileId + "/original.jpg");
    verify(imageGrayscaleConverter).convertToGrayscale(originalFile, ".jpg");
    verify(bucketComponent).upload(convertedFile, "uploads/" + fileId + "/bw.jpg");
    verify(bucketComponent).presign("uploads/" + fileId + "/bw.jpg", java.time.Duration.ofDays(7));

    var emailCaptor = ArgumentCaptor.forClass(Email.class);
    verify(mailer).accept(emailCaptor.capture());

    var email = emailCaptor.getValue();
    assertThat(email.subject()).isEqualTo("Votre fichier a été converti en noir et blanc");
    assertThat(email.htmlBody()).contains("photo.jpg");
    assertThat(email.htmlBody())
        .contains("https://bucket.s3.region.amazonaws.com/uploads/" + fileId + "/bw.jpg");
    assertThat(email.htmlBody()).contains("valable pendant 7 jours");
  }

<<<<<<< HEAD
	@Test
	void shouldHandlePngFileCorrectly() throws Exception {
		var fileId = UUID.randomUUID();
		var event = FileUploadConfirmationRequested.builder()
				.fileId(fileId)
				.build();

		var entity = UploadedFileEntity.builder()
				.id(fileId)
				.nomFichier("image.png")
				.email("user@example.com")
				.createdAt(Instant.now())
				.build();

		var originalFile = new File(tempDir, "original.png");
		originalFile.createNewFile();
		var convertedFile = new File(tempDir, "converted.png");
		convertedFile.createNewFile();

		when(uploadedFileRepository.findById(fileId)).thenReturn(Optional.of(entity));
		when(bucketComponent.download(anyString())).thenReturn(originalFile);
		when(imageGrayscaleConverter.convertToGrayscale(originalFile, ".png")).thenReturn(convertedFile);
		when(bucketComponent.presign(anyString(), any())).thenReturn(new URL("https://test.com"));

		service.accept(event);

		verify(imageGrayscaleConverter).convertToGrayscale(originalFile, ".png");
		verify(bucketComponent).upload(convertedFile, "uploads/" + fileId + "/bw.png");
	}

	@Test
	void shouldHandleFileWithoutExtension() throws Exception {
		var fileId = UUID.randomUUID();
		var event = FileUploadConfirmationRequested.builder()
				.fileId(fileId)
				.build();

		var entity = UploadedFileEntity.builder()
				.id(fileId)
				.nomFichier("document")
				.email("user@example.com")
				.createdAt(Instant.now())
				.build();

		var originalFile = new File(tempDir, "original");
		originalFile.createNewFile();
		var convertedFile = new File(tempDir, "converted");
		convertedFile.createNewFile();

		when(uploadedFileRepository.findById(fileId)).thenReturn(Optional.of(entity));
		when(bucketComponent.download(anyString())).thenReturn(originalFile);
		when(imageGrayscaleConverter.convertToGrayscale(originalFile, "")).thenReturn(convertedFile);
		when(bucketComponent.presign(anyString(), any())).thenReturn(new URL("https://test.com"));

		service.accept(event);

		verify(bucketComponent).download("uploads/" + fileId + "/original");
		verify(bucketComponent).upload(convertedFile, "uploads/" + fileId + "/bw");
	}

	@Test
	void shouldThrowExceptionWhenFileNotFound() {
		var fileId = UUID.randomUUID();
		var event = FileUploadConfirmationRequested.builder()
				.fileId(fileId)
				.build();
=======
  @Test
  void shouldThrowExceptionWhenFileNotFound() {
    var fileId = UUID.randomUUID();
    var event = FileUploadConfirmationRequested.builder().fileId(fileId).build();
>>>>>>> d5331f8 (fix(all): format UploadedFileResponseTest)

    when(uploadedFileRepository.findById(fileId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.accept(event))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Fichier introuvable pour l'id " + fileId);

    verify(bucketComponent, never()).download(any());
    verify(imageGrayscaleConverter, never()).convertToGrayscale(any(), any());
    verify(mailer, never()).accept(any());
  }

  @Test
  void shouldHandleBucketDownloadFailure() {
    var fileId = UUID.randomUUID();
    var event = FileUploadConfirmationRequested.builder().fileId(fileId).build();

    var entity =
        UploadedFileEntity.builder()
            .id(fileId)
            .nomFichier("photo.jpg")
            .email("user@example.com")
            .createdAt(Instant.now())
            .build();

    when(uploadedFileRepository.findById(fileId)).thenReturn(Optional.of(entity));
    when(bucketComponent.download(anyString())).thenThrow(new RuntimeException("Bucket error"));

    assertThatThrownBy(() -> service.accept(event))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Bucket error");

    verify(imageGrayscaleConverter, never()).convertToGrayscale(any(), any());
    verify(mailer, never()).accept(any());
  }

  @Test
  void shouldHandleConversionFailure() throws Exception {
    var fileId = UUID.randomUUID();
    var event = FileUploadConfirmationRequested.builder().fileId(fileId).build();

    var entity =
        UploadedFileEntity.builder()
            .id(fileId)
            .nomFichier("photo.jpg")
            .email("user@example.com")
            .createdAt(Instant.now())
            .build();

    var originalFile = new File(tempDir, "original.jpg");
    originalFile.createNewFile();

    when(uploadedFileRepository.findById(fileId)).thenReturn(Optional.of(entity));
    when(bucketComponent.download(anyString())).thenReturn(originalFile);
    when(imageGrayscaleConverter.convertToGrayscale(any(), any()))
        .thenThrow(new IllegalStateException("Conversion failed"));

    assertThatThrownBy(() -> service.accept(event))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Conversion failed");

<<<<<<< HEAD
		verify(bucketComponent, never()).upload(any(), any());
		verify(mailer, never()).accept(any());
	}

	@Test
	void shouldHandleEmailFailure() throws Exception {
		var fileId = UUID.randomUUID();
		var event = FileUploadConfirmationRequested.builder()
				.fileId(fileId)
				.build();

		var entity = UploadedFileEntity.builder()
				.id(fileId)
				.nomFichier("photo.jpg")
				.email("user@example.com")
				.createdAt(Instant.now())
				.build();

		var originalFile = new File(tempDir, "original.jpg");
		originalFile.createNewFile();
		var convertedFile = new File(tempDir, "converted.jpg");
		convertedFile.createNewFile();

		when(uploadedFileRepository.findById(fileId)).thenReturn(Optional.of(entity));
		when(bucketComponent.download(anyString())).thenReturn(originalFile);
		when(imageGrayscaleConverter.convertToGrayscale(any(), any())).thenReturn(convertedFile);
		when(bucketComponent.presign(any(), any())).thenReturn(new URL("https://test.com"));
		doThrow(new RuntimeException("Email failed")).when(mailer).accept(any());

		assertThatThrownBy(() -> service.accept(event))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("Email failed");

		verify(bucketComponent).upload(convertedFile, any());
	}

	@Test
	void shouldIncludeCorrectLinksInEmail() throws Exception {
		var fileId = UUID.randomUUID();
		var event = FileUploadConfirmationRequested.builder()
				.fileId(fileId)
				.build();

		var entity = UploadedFileEntity.builder()
				.id(fileId)
				.nomFichier("report.pdf")
				.email("user@example.com")
				.createdAt(Instant.now())
				.build();

		var originalFile = new File(tempDir, "original.pdf");
		originalFile.createNewFile();
		var convertedFile = new File(tempDir, "converted.pdf");
		convertedFile.createNewFile();

		var presignedUrl = new URL("https://bucket.s3.region.amazonaws.com/uploads/" + fileId + "/bw.pdf?X-Amz-Expires=604800&X-Amz-Signature=abc123");
		when(uploadedFileRepository.findById(fileId)).thenReturn(Optional.of(entity));
		when(bucketComponent.download(anyString())).thenReturn(originalFile);
		when(imageGrayscaleConverter.convertToGrayscale(any(), any())).thenReturn(convertedFile);
		when(bucketComponent.presign(any(), any())).thenReturn(presignedUrl);

		service.accept(event);

		var emailCaptor = ArgumentCaptor.forClass(Email.class);
		verify(mailer).accept(emailCaptor.capture());

		var email = emailCaptor.getValue();
		assertThat(email.htmlBody()).contains("report.pdf");
		assertThat(email.htmlBody()).contains(presignedUrl.toString());
		assertThat(email.htmlBody()).contains("7 jours");
	}

	@Test
	void shouldSendEmailToCorrectRecipient() throws Exception {
		var fileId = UUID.randomUUID();
		var event = FileUploadConfirmationRequested.builder()
				.fileId(fileId)
				.build();

		var recipientEmail = "recipient@example.com";
		var entity = UploadedFileEntity.builder()
				.id(fileId)
				.nomFichier("photo.jpg")
				.email(recipientEmail)
				.createdAt(Instant.now())
				.build();

		var originalFile = new File(tempDir, "original.jpg");
		originalFile.createNewFile();
		var convertedFile = new File(tempDir, "converted.jpg");
		convertedFile.createNewFile();

		when(uploadedFileRepository.findById(fileId)).thenReturn(Optional.of(entity));
		when(bucketComponent.download(anyString())).thenReturn(originalFile);
		when(imageGrayscaleConverter.convertToGrayscale(any(), any())).thenReturn(convertedFile);
		when(bucketComponent.presign(any(), any())).thenReturn(new URL("https://test.com"));

		service.accept(event);

		var emailCaptor = ArgumentCaptor.forClass(Email.class);
		verify(mailer).accept(emailCaptor.capture());

		var email = emailCaptor.getValue();
		assertThat(email.to()).isNotNull();
		assertThat(email.to().getAddress()).isEqualTo(recipientEmail);
	}
}
=======
    verify(bucketComponent, never()).upload(any(), any());
    verify(mailer, never()).accept(any());
  }
}
>>>>>>> d5331f8 (fix(all): format UploadedFileResponseTest)
