package api.poja.app.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import api.poja.app.endpoint.rest.controller.UploadedFileQueryController;
import api.poja.app.repository.model.UploadedFileEntity;
import api.poja.app.service.UploadedFileService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UploadedFileQueryController.class)
class UploadedFileQueryControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UploadedFileService uploadedFileService;

  @Test
  void shouldReturnListOfUploadedFiles() throws Exception {
    var id1 = UUID.randomUUID();
    var id2 = UUID.randomUUID();
    var now = Instant.now();

    var entity1 =
        UploadedFileEntity.builder()
            .id(id1)
            .nomFichier("document1.pdf")
            .email("user1@example.com")
            .createdAt(now)
            .build();

    var entity2 =
        UploadedFileEntity.builder()
            .id(id2)
            .nomFichier("image2.png")
            .email("user2@example.com")
            .createdAt(now.plusSeconds(60))
            .build();

    when(uploadedFileService.findAll()).thenReturn(List.of(entity1, entity2));

    mockMvc
        .perform(get("/files"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(id1.toString()))
        .andExpect(jsonPath("$[0].nomFichier").value("document1.pdf"))
        .andExpect(jsonPath("$[0].email").value("user1@example.com"))
        .andExpect(jsonPath("$[1].id").value(id2.toString()))
        .andExpect(jsonPath("$[1].nomFichier").value("image2.png"))
        .andExpect(jsonPath("$[1].email").value("user2@example.com"));
  }

  @Test
  void shouldReturnEmptyListWhenNoFiles() throws Exception {
    when(uploadedFileService.findAll()).thenReturn(List.of());

    mockMvc
        .perform(get("/files"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
