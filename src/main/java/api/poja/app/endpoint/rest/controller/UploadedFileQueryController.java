package api.poja.app.endpoint.rest.controller;

import api.poja.app.dto.UploadedFileResponse;
import api.poja.app.service.UploadedFileService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UploadedFileQueryController {

	private final UploadedFileService uploadedFileService;

	@GetMapping("/files")
	public List<UploadedFileResponse> getAllFiles() {
		return uploadedFileService.findAll().stream().map(UploadedFileResponse::from).toList();
	}
}
