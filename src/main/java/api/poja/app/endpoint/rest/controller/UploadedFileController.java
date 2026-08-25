package api.poja.app.endpoint.rest.controller;

import api.poja.app.dto.UploadedFileResponse;
import api.poja.app.service.UploadedFileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class UploadedFileController {

	private final UploadedFileService uploadedFileService;

	@PostMapping(value = "/files", consumes = "multipart/form-data")
	public ResponseEntity<UploadedFileResponse> uploadFile(
			@RequestParam("email") String email, @RequestParam("file") MultipartFile file) {
		var uploadedFile = uploadedFileService.upload(email, file);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(UploadedFileResponse.from(uploadedFile));
	}
}
