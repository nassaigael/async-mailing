package api.poja.app.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "uploaded_file")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileEntity {

  @Id private UUID id;

  @Column(name = "nom_fichier", nullable = false)
  private String nomFichier;

  @Column(nullable = false)
  private String email;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
