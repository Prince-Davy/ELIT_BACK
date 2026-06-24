package com.elit.outildedecision.shared.attachment.service.implementation;

import com.elit.outildedecision.shared.attachment.entity.Attachment;
import com.elit.outildedecision.shared.attachment.entity.AttachmentContent;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.attachment.repository.AttachmentContentRepository;
import com.elit.outildedecision.shared.attachment.repository.AttachmentRepository;
import com.elit.outildedecision.shared.attachment.service.AttachmentService;
import com.elit.outildedecision.shared.attachment.exception.AttachmentNotFoundException;
import com.elit.outildedecision.shared.attachment.exception.InvalidAttachmentException;
import com.elit.outildedecision.shared.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Manages the attachments of a project: upload, rename, download, preview and deletion.
 * Attachment bytes are persisted in the database ({@code attachment_content} table) rather than on
 * the filesystem, so they survive container restarts and are shared across replicas.
 */
@Slf4j
@Service
public class AttachmentServiceImpl implements AttachmentService {
  /**
   * The Project repository.
   */
  private final ProjectRepository projectRepository;
  /**
   * The Attachment repository.
   */
  private final AttachmentRepository attachmentRepository;
  /**
   * The repository for the binary content of attachments.
   */
  private final AttachmentContentRepository attachmentContentRepository;
  /**
   * The User repository.
   */
  private final UserRepository userRepository;
  /**
   * Base folder used to build the logical attachment path (kept for compatibility, no disk I/O).
   */
  @Value("${file.upload-dir}")
  private String storingDirectory;
  /**
   * The constant FILE_NOT_FOUND.
   */
  private static final String FILE_NOT_FOUND = "Fichier introuvable !";
  /**
   * Default MIME type used when the attachment content type cannot be determined.
   */
  private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
  /**
   * JSON key carrying the new file name in the rename request body.
   */
  private static final String NEW_ATTACHMENT_NAME_KEY = "newAttachmentName";
  /**
   * Reusable JSON mapper (thread-safe once configured).
   */
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  /**
   * Maximum accepted size of a single attachment in bytes (10 MiB): aligned with the multipart
   * {@code max-file-size}. The file is held in memory before being persisted as a BLOB, so this
   * bound (together with {@code max-request-size}) caps the per-request memory footprint.
   */
  private static final long MAX_ATTACHMENT_SIZE_BYTES = 10L * 1024 * 1024;
  /**
   * File extensions forbidden on upload for security reasons (executables and scripts).
   */
  private static final Set<String> FORBIDDEN_EXTENSIONS = Set.of(
      "exe", "msi", "bat", "cmd", "com", "scr", "pif", "sh", "bash",
      "ps1", "psm1", "vbs", "vbe", "js", "jse", "jar", "dll", "app", "deb", "rpm", "dmg");

  /**
   * Instantiates a new Attachment service.
   *
   * @param projectRepository           the project repository
   * @param attachmentRepository        the attachment repository
   * @param attachmentContentRepository the attachment binary content repository
   * @param userRepository              the user repository
   */
  public AttachmentServiceImpl(
      final ProjectRepository projectRepository,
      final AttachmentRepository attachmentRepository,
      final AttachmentContentRepository attachmentContentRepository,
      final UserRepository userRepository) {
    this.projectRepository = projectRepository;
    this.attachmentRepository = attachmentRepository;
    this.attachmentContentRepository = attachmentContentRepository;
    this.userRepository = userRepository;
  }

  /**
   * Uploads the given files and links them to the project.
   *
   * @param files     the files to upload
   * @param projectId the project the files are attached to
   * @param userEmail the email of the uploading user
   * @return the success confirmation message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws FileCouldNotBeSaved      if a file could not be read
   */
  @Override
  @Transactional
  public String uploadAttachment(final List<MultipartFile> files, final Long projectId, final String userEmail)
      throws ProjectNotFoundException, FileCouldNotBeSaved {
    Project project = projectRepository
        .findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas !"));
    String folderPath = StringUtils.cleanPath(storingDirectory + project.getFolderPath());

    for (MultipartFile file : files) {
      validateAttachment(file);
      try {
        byte[] bytes = file.getBytes();
        String filePath = StringUtils.cleanPath(folderPath.concat("/").concat(file.getOriginalFilename()));
        User user = userRepository.findByUserEmail(userEmail);
        Attachment attachment = Attachment.builder()
            .filePath(filePath)
            .fileName(file.getOriginalFilename())
            .contentType(file.getContentType())
            .project(project)
            .createdAt(project.getCreatedAt())
            .updatedAt(project.getUpdatedAt())
            .creator(user)
            .build();
        Attachment savedAttachment = attachmentRepository.save(attachment);
        attachmentContentRepository.save(new AttachmentContent(savedAttachment.getAttachmentId(), bytes));
      } catch (IOException e) {
        log.error("Could not save uploaded file {} for project {}", file.getOriginalFilename(), projectId, e);
        throw new FileCouldNotBeSaved("Erreur lors de l'enregistrement du fichier : " + file.getOriginalFilename());
      }
    }
    log.info("Uploaded {} attachment(s) for project {}", files.size(), projectId);
    return "Fichiers enregistrés avec succés";
  }

  /**
   * Validates an uploaded attachment before its bytes are read into memory: the file must be
   * present, not empty, within {@link #MAX_ATTACHMENT_SIZE_BYTES} and must not carry a forbidden
   * (executable/script) extension.
   *
   * @param file the uploaded file to validate
   * @throws InvalidAttachmentException if the file is missing, too large or has a forbidden type
   */
  private void validateAttachment(final MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new InvalidAttachmentException("Un fichier vide ou sans nom a été fourni.");
    }
    if (file.getSize() > MAX_ATTACHMENT_SIZE_BYTES) {
      throw new InvalidAttachmentException(
          "Le fichier « " + file.getOriginalFilename() + " » dépasse la taille maximale autorisée (10 Mo).");
    }
    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
    if (extension != null && FORBIDDEN_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
      throw new InvalidAttachmentException(
          "Le fichier « " + file.getOriginalFilename() + " » a un format non autorisé pour des raisons de sécurité.");
    }
  }

  /**
   * Deletes a single attachment (record and binary content).
   *
   * @param attachmentId the attachment to delete
   * @return the success confirmation message
   * @throws AttachmentNotFoundException if the attachment does not exist
   */
  @Override
  @Transactional
  public String deleteAttachment(final Long attachmentId)
      throws AttachmentNotFoundException {
    Attachment attachment = attachmentRepository
        .findById(attachmentId)
        .orElseThrow(() -> new AttachmentNotFoundException("Ce fichier n'existe pas!"));
    deleteContentIfPresent(attachment.getAttachmentId());
    attachmentRepository.deleteById(attachmentId);
    log.info("Deleted attachment {}", attachmentId);
    return "Fichier supprimé avec succès.";
  }

  /**
   * Deletes every attachment of the given project (by id).
   *
   * @param projectId the project whose attachments are deleted
   * @throws ProjectNotFoundException    if the project does not exist
   * @throws AttachmentNotFoundException if the project has no attachment
   */
  @Transactional
  @Override
  public void deleteAllAttachmentsOfProject(final Long projectId)
      throws ProjectNotFoundException, AttachmentNotFoundException {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas !"));

    List<Attachment> attachments = attachmentRepository.findAllByProject(project);

    if (attachments.isEmpty()) {
      throw new AttachmentNotFoundException("Aucun fichier trouvé pour ce projet !");
    }

    deleteAttachmentRecords(attachments);
  }

  /**
   * Streams an attachment inline (browser preview).
   *
   * @param attachmentId the attachment to open
   * @param response     the servlet response to write the bytes into
   * @throws AttachmentNotFoundException if the attachment does not exist
   * @throws IOException                 if the response cannot be written
   */
  @Override
  @Transactional(readOnly = true)
  public void openAttachment(final Long attachmentId, final HttpServletResponse response)
      throws AttachmentNotFoundException, IOException {
    streamAttachment(attachmentId, response, "inline");
  }

  /**
   * Streams an attachment as a download.
   *
   * @param attachmentId the attachment to download
   * @param response     the servlet response to write the bytes into
   * @throws AttachmentNotFoundException if the attachment does not exist
   * @throws IOException                 if the response cannot be written
   */
  @Override
  @Transactional(readOnly = true)
  public void downloadAttachment(final Long attachmentId, final HttpServletResponse response)
      throws AttachmentNotFoundException, IOException {
    streamAttachment(attachmentId, response, "attachment");
  }

  /**
   * Deletes every attachment of the given project entity.
   *
   * @param project the project whose attachments are deleted
   * @throws AttachmentNotFoundException if the project has no attachment
   */
  @Override
  @Transactional
  public void deleteAttachments(final Project project) throws AttachmentNotFoundException {
    List<Attachment> attachments = attachmentRepository.findAllByProject(project);

    if (attachments.isEmpty()) {
      throw new AttachmentNotFoundException("Aucun fichier trouvé pour ce projet !");
    }

    deleteAttachmentRecords(attachments);
  }

  /**
   * Renames an attachment (database record only; the bytes are unchanged).
   *
   * @param attachmentId       the attachment to rename
   * @param attachmentNameJson the request body holding the new name
   * @return the success confirmation message
   * @throws AttachmentNotFoundException if the attachment does not exist
   * @throws IOException                 if the request body cannot be parsed
   */
  @Override
  @Transactional
  public String renameAttachment(final Long attachmentId, final HttpEntity<String> attachmentNameJson)
      throws AttachmentNotFoundException, IOException {
    Attachment attachmentFromDb = attachmentRepository
        .findById(attachmentId)
        .orElseThrow(() -> new AttachmentNotFoundException(FILE_NOT_FOUND));

    Map<String, String> newName =
        OBJECT_MAPPER.readValue(attachmentNameJson.getBody(), new TypeReference<Map<String, String>>() { });

    String newFileName = newName.get(NEW_ATTACHMENT_NAME_KEY);
    attachmentFromDb.setFileName(newFileName);
    // resolveSibling is a pure path computation (no filesystem access): keep filePath consistent.
    String newPath = Paths.get(attachmentFromDb.getFilePath()).resolveSibling(newFileName).toString();
    attachmentFromDb.setFilePath(StringUtils.cleanPath(newPath));
    attachmentRepository.save(attachmentFromDb);
    log.info("Renamed attachment {} to {}", attachmentId, newFileName);
    return "Votre fichier a été mis à jour";
  }

  /**
   * Streams the bytes of an attachment (loaded from the database) into the servlet response with the
   * given disposition.
   *
   * @param attachmentId the attachment to stream
   * @param response     the servlet response
   * @param disposition  the {@code Content-Disposition} type ({@code inline} or {@code attachment})
   * @throws AttachmentNotFoundException if the attachment does not exist
   * @throws IOException                 if the response cannot be written
   */
  private void streamAttachment(final Long attachmentId, final HttpServletResponse response, final String disposition)
      throws AttachmentNotFoundException, IOException {
    Attachment attachment = attachmentRepository
        .findById(attachmentId)
        .orElseThrow(() -> new AttachmentNotFoundException(FILE_NOT_FOUND));

    AttachmentContent attachmentContent = attachmentContentRepository.findById(attachmentId).orElse(null);
    if (attachmentContent == null) {
      log.error("Attachment content is missing in database for attachmentId={}", attachmentId);
      return;
    }

    String mimeType = attachment.getContentType();
    if (!StringUtils.hasText(mimeType)) {
      mimeType = URLConnection.guessContentTypeFromName(attachment.getFileName());
    }
    if (mimeType == null) {
      mimeType = DEFAULT_MIME_TYPE;
    }

    byte[] content = attachmentContent.getContent();
    response.setContentType(mimeType);
    response.setHeader("Content-Disposition",
        String.format("%s; filename=\"%s\"", disposition, attachment.getFileName()));
    response.setContentLength(content.length);
    response.getOutputStream().write(content);
  }

  /**
   * Deletes the database records and binary content of the given attachments, logging each outcome.
   *
   * @param attachments the attachments to delete
   */
  private void deleteAttachmentRecords(final List<Attachment> attachments) {
    for (Attachment attachment : attachments) {
      try {
        deleteContentIfPresent(attachment.getAttachmentId());
        attachmentRepository.deleteById(attachment.getAttachmentId());
        log.info("Deleted attachment {}", attachment.getAttachmentId());
      } catch (RuntimeException e) {
        log.error("Error while deleting attachment {}", attachment.getAttachmentId(), e);
      }
    }
  }

  /**
   * Deletes the binary content of an attachment if it exists.
   *
   * @param attachmentId the attachment id whose content must be removed
   */
  private void deleteContentIfPresent(final Long attachmentId) {
    if (attachmentContentRepository.existsById(attachmentId)) {
      attachmentContentRepository.deleteById(attachmentId);
    }
  }
}
