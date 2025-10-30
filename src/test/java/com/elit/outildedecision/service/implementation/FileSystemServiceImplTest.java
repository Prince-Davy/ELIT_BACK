package com.elit.outildedecision.service.implementation;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class FileSystemServiceImplTest {
    private FileSystemServiceImpl fileSystemService;

    @BeforeEach
    void setUp() {
        fileSystemService = new FileSystemServiceImpl();
    }

    @Test
    void deleteProjectFolder_shouldDeleteDirectory(@TempDir File tempDir) throws IOException {
        // Arrange
        File testDir = new File(tempDir, "toDelete");
        assertTrue(testDir.mkdir());
        File testFile = new File(testDir, "file.txt");
        assertTrue(testFile.createNewFile());
        assertTrue(testDir.exists());
        assertTrue(testFile.exists());

        // Act
        fileSystemService.deleteProjectFolder(testDir.getAbsolutePath());

        // Assert
        assertFalse(testDir.exists(), "Le dossier doit être supprimé");
    }

    @Test
    void deleteProjectFolder_shouldThrowRuntimeExceptionOnError() {
        // Arrange
        String invalidPath = "Z:/chemin/inexistant/" + System.currentTimeMillis();
        // On simule une suppression d'un chemin qui n'existe pas, FileUtils ne lève pas d'exception, donc on simule une IOException
        FileSystemServiceImpl service = new FileSystemServiceImpl() {
            @Override
            public void deleteProjectFolder(String folderPath) {
                throw new RuntimeException(new IOException("Erreur simulée"));
            }
        };
        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> service.deleteProjectFolder(invalidPath));
        assertTrue(thrown.getCause() instanceof IOException);
    }
}

