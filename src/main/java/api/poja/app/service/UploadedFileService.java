package api.poja.app.service;

import api.poja.app.endpoint.event.EventProducer;
import api.poja.app.endpoint.event.model.FileUploadConfirmationRequested;
import api.poja.app.file.bucket.BucketComponent;
import api.poja.app.repository.UploadedFileRepository;
import api.poja.app.repository.model.UploadedFileEntity;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class UploadedFileService {

  private final UploadedFileRepository uploadedFileRepository;
  private final BucketComponent bucketComponent;
  private final EventProducer<FileUploadConfirmationRequested> eventProducer;

  @SneakyThrows
  public UploadedFileEntity upload(String email, MultipartFile file) {
    var id = UUID.randomUUID();
    var nomFichier = file.getOriginalFilename();
    var extension = extractExtension(nomFichier);

    var entity =
        UploadedFileEntity.builder()
            .id(id)
            .nomFichier(nomFichier)
            .email(email)
            .createdAt(Instant.now())
            .build();
    uploadedFileRepository.save(entity);

    var tempFile = File.createTempFile("upload-" + id, extension);
    file.transferTo(tempFile);
    bucketComponent.upload(tempFile, originalKey(id, extension));

    var event = FileUploadConfirmationRequested.builder().fileId(id).build();
    eventProducer.accept(List.of(event));

    return entity;
  }

  public List<UploadedFileEntity> findAll() {
    return uploadedFileRepository.findAll();
  }

  public UploadedFileEntity getById(UUID id) {
    return uploadedFileRepository
        .findById(id)
        .orElseThrow(() -> new IllegalStateException("Fichier introuvable pour l'id " + id));
  }

  public static String originalKey(UUID id, String extension) {
    return "uploads/" + id + "/original" + extension;
  }

  public static String blackAndWhiteKey(UUID id, String extension) {
    return "uploads/" + id + "/bw" + extension;
  }

  private String extractExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "";
    }
    return filename.substring(filename.lastIndexOf('.'));
  }
}
