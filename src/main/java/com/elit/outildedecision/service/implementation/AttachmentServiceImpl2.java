
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.repository.AttachmentRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.AttachmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this is used to implement the attachment service, it contains all the methods
 * that are used to manage the attachments of a project.
 *
 */
@Service
public class AttachmentServiceImpl2 implements AttachmentService {
  /** The Project repository.*/
  private final ProjectRepository projectRepository;
  /** The Attachment repository.*/
  private final AttachmentRepository attachmentRepository;
  /**
   * The Storing directory.
   * this is used to store the attachments of a project.
   */
  @Value("${file.upload-dir}")
  private String storingDirectory;

  /**
   * Class's Constructor : Instantiates a new Attachment service.
   * dependency injection via constructor of the project repository and the attachment repository
   * @param projectRepository the project repository
   * @param attachmentRepository the attachment repository
   */
  public AttachmentServiceImpl2(
      final ProjectRepository projectRepository, final AttachmentRepository attachmentRepository) {
    this.projectRepository = projectRepository;
    this.attachmentRepository = attachmentRepository;
  }

 /**
    * this method is used to upload attachments of a project.
    *
    * @param files the files to upload
    * @param projectId the project id to which the files will be attached
    * @return the response entity
    * @throws ProjectNotFoundException the project not found exception if the project does not exist
    */
  @Override
  public ResponseEntity<Object> uploadAttachment(final List<MultipartFile> files, final Long projectId)
      throws ProjectNotFoundException {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas !"));
    String folderPath = StringUtils.cleanPath(storingDirectory + project.getFolderPath());
    for (MultipartFile file : files) {
      try {
        String filePath = StringUtils.cleanPath(folderPath + "\\" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
        Attachment attachment =
            Attachment.builder()
                .filePath(filePath)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .project(project)
                .build();
        attachmentRepository.save(attachment);
      } catch (IOException e) {
        throw new RuntimeException(
            "Erreur lors de l'enregistrement du fichier : " + file.getOriginalFilename());
      }
    }
    return ResponseEntity.ok().body("Fichiers enregistrés avec succés");
  }
  /**
   * this method is used to delete an attachment of a project.
   *
   * @param attachmentId the attachment id to delete
   * @return the response entity
   * @throws AttachmentNotFoundException the attachment not found exception if the attachment does
   *     not exist
   */
  @Override
  public ResponseEntity deleteAttachment(final Long attachmentId)
      throws AttachmentNotFoundException {
    Attachment attachment =
        attachmentRepository
            .findById(attachmentId)
            .orElseThrow(() -> new AttachmentNotFoundException("Ce fichier n'existe pas!"));
    File fileToDelete = new File(attachment.getFilePath());
    if (FileSystemUtils.deleteRecursively(fileToDelete)) {
      attachmentRepository.deleteById(attachmentId);
      return ResponseEntity.ok().body("Fichier supprimé avec succès.");
    } else {
      return ResponseEntity.badRequest().body("Erreur lors de la suppression du fichier.");
    }
}
  /**
   * this method is used to Visualize an attachment of a project on the browser.
   *
   * @param attachmentId the attachment id to open
   * @param response the response
   * @throws AttachmentNotFoundException the attachment not found exception if the attachment does
   *     not exist
   * @throws IOException the io exception if there is an error while opening the file
   */
  @Override
  public void openAttachment(final Long attachmentId, final HttpServletResponse response)
      throws AttachmentNotFoundException, IOException {

    Attachment attachment =
        attachmentRepository
            .findById(attachmentId)
            .orElseThrow(() -> new AttachmentNotFoundException("Ce fichier n'existe pas !"));

    File file = new File(attachment.getFilePath());
    if (file.exists()) {
      // get the mimetype
      String mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if (mimeType == null) {
        // unknown mimetype so set the mimetype to application/octet-stream
        mimeType = "application/octet-stream";
      }

      response.setContentType(mimeType);
      // Here we have mentioned it to show inline
      response.setHeader(
          "Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

      response.setContentLength((int) file.length());

      InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

      FileCopyUtils.copy(inputStream, response.getOutputStream());

    } else {
      System.out.println("Ce fichier n'existe pas!");
    }
  }
  /**
   * this method is used to download an attachment of a project.
   *
   * @param attachmentId the attachment id to download
   * @param response the response
   * @throws AttachmentNotFoundException the attachment not found exception if the attachment does
   *     not exist
   * @throws IOException the io exception if there is an error while downloading the file
   */
  @Override
  public void downloadAttachment(final Long attachmentId, final HttpServletResponse response)
      throws AttachmentNotFoundException, IOException {
    Attachment attachment =
        attachmentRepository
            .findById(attachmentId)
            .orElseThrow(() -> new AttachmentNotFoundException("Ce fichier n'existe pas !"));

    File file = new File(attachment.getFilePath());

    System.out.println("file " + file.getPath());
    System.out.println("file.getName() " + file.getName());
    if (file.exists()) {

      // get the mimetype
      String mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if (mimeType == null) {
        // unknown mimetype so set the mimetype to application/octet-stream
        mimeType = "application/octet-stream";
      }

      response.setContentType(mimeType);

      // Here we have mentioned it to show as attachment
      response.setHeader(
          "Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

      response.setContentLength((int) file.length());

      InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

      FileCopyUtils.copy(inputStream, response.getOutputStream());

    } else {
      System.out.println("no exitia el file");
    }
  }
  /**
   * this method is used to rename an attachment of a project.
   *
   * @param attachmentId the attachment id to rename
   * @param attachmentNameJson the attachment name json containing the new name
   * @return the response entity
   * @throws AttachmentNotFoundException the attachment not found exception if the attachment does
   *     not exist
   * @throws IOException the io exception if there is an error while renaming the file
   */
  @Override
  public ResponseEntity<String> renameAttachment(
      final Long attachmentId, final HttpEntity<String> attachmentNameJson)
      throws AttachmentNotFoundException, IOException {
    Attachment attachmentFromDB =
        attachmentRepository
            .findById(attachmentId)
            .orElseThrow(() -> new AttachmentNotFoundException("Ce fichier n'existe pas !"));

    Map<String, String> newName =
        new ObjectMapper().readValue(attachmentNameJson.getBody(), HashMap.class);

    attachmentFromDB.setFileName(newName.get("newAttachmentName"));
    Path oldFile = Paths.get(attachmentFromDB.getFilePath());
    Path newPath = Files.move(oldFile, oldFile.resolveSibling(newName.get("newAttachmentName")));
    attachmentFromDB.setFilePath(StringUtils.cleanPath(newPath.toString()));
    attachmentRepository.save(attachmentFromDB);
    return ResponseEntity.ok().body("Votre fichier a été mis à jour");
  }
}

