package api.poja.app.service.event;

import api.poja.app.endpoint.event.model.FileUploadConfirmationRequested;
import api.poja.app.file.bucket.BucketComponent;
import api.poja.app.mail.Email;
import api.poja.app.mail.Mailer;
import api.poja.app.repository.UploadedFileRepository;
import api.poja.app.service.ImageGrayscaleConverter;
import api.poja.app.service.UploadedFileService;
import jakarta.mail.internet.InternetAddress;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileUploadConfirmationRequestedService
		implements Consumer<FileUploadConfirmationRequested> {

	private static final Duration PRESIGNED_URL_VALIDITY = Duration.ofDays(7);

	private final UploadedFileRepository uploadedFileRepository;
	private final BucketComponent bucketComponent;
	private final ImageGrayscaleConverter imageGrayscaleConverter;
	private final Mailer mailer;

	@SneakyThrows
	@Override
	public void accept(FileUploadConfirmationRequested event) {
		var fileId = event.getFileId();
		var entity =
				uploadedFileRepository
						.findById(fileId)
						.orElseThrow(
								() -> new IllegalStateException("Fichier introuvable pour l'id " + fileId));

		var extension = extractExtension(entity.getNomFichier());
		var originalKey = UploadedFileService.originalKey(fileId, extension);
		var blackAndWhiteKey = UploadedFileService.blackAndWhiteKey(fileId, extension);

		var originalFile = bucketComponent.download(originalKey);
		var blackAndWhiteFile = imageGrayscaleConverter.convertToGrayscale(originalFile, extension);
		bucketComponent.upload(blackAndWhiteFile, blackAndWhiteKey);

		var presignedUri = bucketComponent.presign(blackAndWhiteKey, PRESIGNED_URL_VALIDITY);

		sendNotificationEmail(entity.getEmail(), entity.getNomFichier(), presignedUri.toString());
	}

	@SneakyThrows
	private void sendNotificationEmail(String recipientEmail, String nomFichier, String downloadUrl) {
		var recipient = new InternetAddress(recipientEmail);
		var subject = "Votre fichier a été converti en noir et blanc";
		var htmlBody = buildEmailBody(nomFichier, downloadUrl);

		mailer.accept(new Email(recipient, List.of(), List.of(), subject, htmlBody, List.of()));
	}

	private String buildEmailBody(String nomFichier, String downloadUrl) {
		return "<html><body>"
				+ "<p>Bonjour,</p>"
				+ "<p>Votre fichier <strong>"
				+ nomFichier
				+ "</strong> a bien été converti en noir et blanc.</p>"
				+ "<p><a href=\""
				+ downloadUrl
				+ "\">Télécharger la version noir et blanc</a></p>"
				+ "<p>Ce lien est valable pendant 7 jours.</p>"
				+ "</body></html>";
	}

	private String extractExtension(String filename) {
		if (filename == null || !filename.contains(".")) {
			return "";
		}
		return filename.substring(filename.lastIndexOf('.'));
	}
}
