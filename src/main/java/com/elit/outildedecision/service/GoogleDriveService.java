package com.elit.outildedecision.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface GoogleDriveService {

  List<File> getGoogleFilesByName(String fileNameLike) throws IOException;

  File createGoogleFolder(String folderIdParent, String folderName) throws IOException;

  File createGoogleFile(
      String googleFolderIdParent,
      String contentType, //
      String customFileName,
      InputStream inputStream)
      throws IOException;

  Permission createPublicPermission(String googleFileId) throws IOException;

  void deleteFile(String fileId) throws IOException;
}
