package api.poja.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import api.poja.app.repository.model.UploadedFileEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class UploadedFileRepositoryTest {

  @Autowired private UploadedFileRepository repository;

  @Autowired private TestEntityManager entityManager;

  @Test
  void shouldSaveEntity() {
    var id = UUID.randomUUID();
    var now = Instant.now();
    var entity =
        UploadedFileEntity.builder()
            .id(id)
            .nomFichier("test.pdf")
            .email("test@example.com")
            .createdAt(now)
            .build();

    var saved = repository.save(entity);

    assertThat(saved).isNotNull();
    assertThat(saved.getId()).isEqualTo(id);
    assertThat(saved.getNomFichier()).isEqualTo("test.pdf");
    assertThat(saved.getEmail()).isEqualTo("test@example.com");
    assertThat(saved.getCreatedAt()).isEqualTo(now);
  }

  @Test
  void shouldFindById() {
    var id = UUID.randomUUID();
    var now = Instant.now();
    var entity =
        UploadedFileEntity.builder()
            .id(id)
            .nomFichier("document.pdf")
            .email("user@example.com")
            .createdAt(now)
            .build();
    entityManager.persist(entity);
    entityManager.flush();

    var found = repository.findById(id);

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(id);
    assertThat(found.get().getNomFichier()).isEqualTo("document.pdf");
    assertThat(found.get().getEmail()).isEqualTo("user@example.com");
    assertThat(found.get().getCreatedAt()).isEqualTo(now);
  }

  @Test
  void shouldReturnEmptyWhenNotFound() {
    var id = UUID.randomUUID();

    var found = repository.findById(id);

    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindAll() {
    var id1 = UUID.randomUUID();
    var id2 = UUID.randomUUID();
    var now = Instant.now();

    var entity1 =
        UploadedFileEntity.builder()
            .id(id1)
            .nomFichier("file1.pdf")
            .email("user1@example.com")
            .createdAt(now)
            .build();

    var entity2 =
        UploadedFileEntity.builder()
            .id(id2)
            .nomFichier("file2.jpg")
            .email("user2@example.com")
            .createdAt(now.plusSeconds(60))
            .build();

    entityManager.persist(entity1);
    entityManager.persist(entity2);
    entityManager.flush();

    var all = repository.findAll();

    assertThat(all).hasSize(2);
    assertThat(all).extracting(UploadedFileEntity::getId).containsExactlyInAnyOrder(id1, id2);
  }

  @Test
  void shouldDeleteById() {
    var id = UUID.randomUUID();
    var now = Instant.now();
    var entity =
        UploadedFileEntity.builder()
            .id(id)
            .nomFichier("to-delete.pdf")
            .email("delete@example.com")
            .createdAt(now)
            .build();
    entityManager.persist(entity);
    entityManager.flush();

    repository.deleteById(id);
    entityManager.flush();

    var found = repository.findById(id);
    assertThat(found).isEmpty();
  }
}
