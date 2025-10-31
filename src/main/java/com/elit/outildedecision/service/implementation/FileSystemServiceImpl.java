package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.service.FileSystemService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

/**
 * Implementation of file system operations used by the application.
 */
@Service
public class FileSystemServiceImpl implements FileSystemService {
    /**
     * Deletes a project folder and its content. Wraps IOExceptions into a
     * RuntimeException to avoid changing the service signature.
     *
     * @param folderPath folder path to delete
     */
    @Override
    public void deleteProjectFolder(final String folderPath) {
        try {
            FileUtils.deleteDirectory(new File(folderPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
