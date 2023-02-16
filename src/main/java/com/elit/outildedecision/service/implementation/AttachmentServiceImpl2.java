package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.AttachmentModel;
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
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttachmentServiceImpl2 implements AttachmentService {

    @Value("${file.upload-dir}")
    String STORING_DIRECTORY;
    private final ProjectRepository projectRepository;
    private final AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl2(ProjectRepository projectRepository, AttachmentRepository attachmentRepository) {
        this.projectRepository = projectRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public ResponseEntity<Object> uploadAttachment(List<MultipartFile> files, Long projectId) throws ProjectNotFoundException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas !"));
        String folderPath = StringUtils.cleanPath(STORING_DIRECTORY + project.getFolderPath());
        for (MultipartFile file : files) {
            try {
                String filePath = StringUtils.cleanPath(folderPath + "\\" + file.getOriginalFilename());
                Files.copy(file.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
                Attachment attachment = Attachment.builder()
                        .filePath(filePath)
                        .fileName(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .project(project)
                        .build();
                attachmentRepository.save(attachment);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement du fichier : " + file.getOriginalFilename());
            }
        }
        return ResponseEntity.ok().body("Fichiers enregistrés avec succés");
    }

    @Override
    public ResponseEntity deleteAttachment(Long attachmentId) throws AttachmentNotFoundException, ProjectNotFoundException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Ce fichier n'existe pas!"));
        File fileToDelete = new File(attachment.getFilePath());
        if (FileSystemUtils.deleteRecursively(fileToDelete)) {
            attachmentRepository.deleteById(attachmentId);
            return ResponseEntity.ok().body("Fichier supprimé avec succès.");
        } else {
            return ResponseEntity
                    .badRequest()
                    .body("Erreur lors de la suppression du fichier.");
        }
    }

    @Override
    public void openAttachment(Long attachmentId, HttpServletResponse response) throws AttachmentNotFoundException, IOException {

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Ce fichier n'existe pas !"));

        File file = new File(attachment.getFilePath());
        if (file.exists()) {
            //get the mimetype
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);

            /**
             * In a regular HTTP response, the Content-Disposition response header is a
             * header indicating if the content is expected to be displayed inline in the
             * browser, that is, as a Web page or as part of a Web page, or as an
             * attachment, that is downloaded and saved locally.
             *
             */

            /**
             * Here we have mentioned it to show inline
             */
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            //Here we have mentioned it to show as attachment
            //response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());

        } else {
            System.out.println("no exitia el file");
        }
    }

    @Override
    public void downloadAttachment(Long attachmentId, HttpServletResponse response)  throws AttachmentNotFoundException, IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Ce fichier n'existe pas !"));

        File file = new File(attachment.getFilePath());

        System.out.println("file " + file.getPath());
        System.out.println("file.getName() " + file.getName());
        if (file.exists()) {

            //get the mimetype
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);

            /**
             * In a regular HTTP response, the Content-Disposition response header is a
             * header indicating if the content is expected to be displayed inline in the
             * browser, that is, as a Web page or as part of a Web page, or as an
             * attachment, that is downloaded and saved locally.
             *
             */

            //Here we have mentioned it to show as attachment
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());

        } else {
            System.out.println("no exitia el file");
        }
    }

    @Override
    public ResponseEntity<String> addAttachmentToProject(Long projectId, AttachmentModel files) throws ProjectNotFoundException {
        Project projectFromDB = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas !"));

        String folderPath  = StringUtils.cleanPath(STORING_DIRECTORY + projectFromDB.getFolderPath());
        for (MultipartFile file : files.getAttachments()) {
            try {
                String filePath = StringUtils.cleanPath(folderPath + "\\" + file.getOriginalFilename());
                Files.copy(file.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
                Attachment attachment = Attachment.builder()
                        .filePath(filePath)
                        .fileName(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .project(projectFromDB)
                        .build();
                attachmentRepository.save(attachment);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement du fichier : " + file.getOriginalFilename());
            }
        }
        return ResponseEntity.ok()
                .body("Votre Fichier est bien enregistré");
    }

    @Override
    public ResponseEntity<String> renameAttachment(Long attachmentId, HttpEntity<String> attachmentNameJson) throws AttachmentNotFoundException, IOException {
        Attachment attachmentFromDB = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Ce fichier n'existe pas !"));

        Map<String, String> newName = new ObjectMapper().readValue(attachmentNameJson.getBody(), HashMap.class);

        attachmentFromDB.setFileName(newName.get("newAttachmentName"));
        Path oldFile = Paths.get(attachmentFromDB.getFilePath());
        Path newPath = Files.move(oldFile, oldFile.resolveSibling(newName.get("newAttachmentName")));
        attachmentFromDB.setFilePath(StringUtils.cleanPath(newPath.toString()));
        attachmentRepository.save(attachmentFromDB);
        return ResponseEntity.ok()
                .body("Votre fichier a été mis à jour");
    }

}
