package api.poja.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import api.poja.app.endpoint.event.EventProducer;
import api.poja.app.endpoint.event.model.FileUploadConfirmationRequested;
import api.poja.app.file.bucket.BucketComponent;
import api.poja.app.repository.UploadedFileRepository;
import api.poja.app.repository.model.UploadedFileEntity;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class UploadedFileServiceTest {

  @Mock private UploadedFileRepository uploadedFileRepository;

  @Mock private BucketComponent bucketComponent;

  @Mock private EventProducer<FileUploadConfirmationRequested> eventProducer;

  @InjectMocks private UploadedFileService service;

  @Test
  void shouldUploadFileSuccessfully() throws Exception {
    var email = "user@example.com";
    var multipartFile =
        new MockMultipartFile("file", "document.pdf", "application/pdf", "test content".getBytes());

    when(uploadedFileRepository.save(any(UploadedFileEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = service.upload(email, multipartFile);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getNomFichier()).isEqualTo("document.pdf");
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getCreatedAt()).isNotNull();

    verify(uploadedFileRepository).save(any(UploadedFileEntity.class));
    verify(bucketComponent).upload(any(File.class), any(String.class));
    verify(eventProducer).accept(any(List.class));
  }

  @Test
  void shouldFindAllFiles() {
    var id1 = UUID.randomUUID();
    var id2 = UUID.randomUUID();
    var entity1 =
        UploadedFileEntity.builder()
            .id(id1)
            .nomFichier("file1.pdf")
            .email("user1@example.com")
            .createdAt(Instant.now())
            .build();
    var entity2 =
        UploadedFileEntity.builder()
            .id(id2)
            .nomFichier("file2.jpg")
            .email("user2@example.com")
            .createdAt(Instant.now())
            .build();
    var expectedList = List.of(entity1, entity2);

    when(uploadedFileRepository.findAll()).thenReturn(expectedList);

    var result = service.findAll();

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedList);
    verify(uploadedFileRepository).findAll();
  }

  @Test
  void shouldGetFileByIdWhenExists() {
    var id = UUID.randomUUID();
    var entity =
        UploadedFileEntity.builder()
            .id(id)
            .nomFichier("file.pdf")
            .email("user@example.com")
            .createdAt(Instant.now())
            .build();

    when(uploadedFileRepository.findById(id)).thenReturn(Optional.of(entity));

    var result = service.getById(id);

    assertThat(result).isEqualTo(entity);
    verify(uploadedFileRepository).findById(id);
  }

  @Test
  void shouldThrowExceptionWhenFileNotFoundById() {
    var id = UUID.randomUUID();
    when(uploadedFileRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getById(id))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Fichier introuvable pour l'id " + id);
    verify(uploadedFileRepository).findById(id);
  }

  @Test
  void shouldGenerateCorrectOriginalKey() {
    var id = UUID.randomUUID();
    var extension = ".pdf";

    var result = UploadedFileService.originalKey(id, extension);

    assertThat(result).isEqualTo("uploads/" + id + "/original.pdf");
  }

  @Test
  void shouldGenerateCorrectBlackAndWhiteKey() {
    var id = UUID.randomUUID();
    var extension = ".png";

    var result = UploadedFileService.blackAndWhiteKey(id, extension);

    assertThat(result).isEqualTo("uploads/" + id + "/bw.png");
  }
}
