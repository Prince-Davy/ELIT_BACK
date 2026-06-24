package com.elit.outildedecision.shared.attachment.service;

/**
 * Service to manage file system operations related to project folders.
 */
public interface FileSystemService {

    /**
     * Delete a project folder and its contents.
     *
     * @param folderPath absolute or relative path of the folder to delete
     */
    void deleteProjectFolder(String folderPath);
}
