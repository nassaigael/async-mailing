package api.poja.app.dto;

import api.poja.app.repository.model.UploadedFileEntity;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UploadedFileResponse {
  private UUID id;
  private String nomFichier;
  private String email;
  private Instant createdAt;

  public static UploadedFileResponse from(UploadedFileEntity entity) {
    return UploadedFileResponse.builder()
        .id(entity.getId())
        .nomFichier(entity.getNomFichier())
        .email(entity.getEmail())
        .createdAt(entity.getCreatedAt())
        .build();
  }
}
