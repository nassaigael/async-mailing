package api.poja.app.dto;

import api.poja.app.repository.model.UploadedFileEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UploadedFileResponseTest {

	@Test
	void shouldConvertEntityToResponse() {
		var id = UUID.randomUUID();
		var now = Instant.now();
		var entity = UploadedFileEntity.builder()
				.id(id)
				.nomFichier("document.pdf")
				.email("user@example.com")
				.createdAt(now)
				.build();

		var response = UploadedFileResponse.from(entity);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(id);
		assertThat(response.getNomFichier()).isEqualTo("document.pdf");
		assertThat(response.getEmail()).isEqualTo("user@example.com");
		assertThat(response.getCreatedAt()).isEqualTo(now);
	}

	@Test
	void shouldBuildResponseWithAllFields() {
		var id = UUID.randomUUID();
		var now = Instant.now();

		var response = UploadedFileResponse.builder()
				.id(id)
				.nomFichier("image.png")
				.email("test@example.com")
				.createdAt(now)
				.build();

		assertThat(response.getId()).isEqualTo(id);
		assertThat(response.getNomFichier()).isEqualTo("image.png");
		assertThat(response.getEmail()).isEqualTo("test@example.com");
		assertThat(response.getCreatedAt()).isEqualTo(now);
	}

	@Test
	void shouldHandleNullValues() {
		var entity = UploadedFileEntity.builder()
				.id(null)
				.nomFichier(null)
				.email(null)
				.createdAt(null)
				.build();

		var response = UploadedFileResponse.from(entity);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isNull();
		assertThat(response.getNomFichier()).isNull();
		assertThat(response.getEmail()).isNull();
		assertThat(response.getCreatedAt()).isNull();
	}
}