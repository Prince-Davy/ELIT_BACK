package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.service.FileSystemService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

@Service
public class FileSystemServiceImpl implements FileSystemService {
    @Override
    public void deleteProjectFolder(String folderPath) {
        try {
            FileUtils.deleteDirectory(new File(folderPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

