package com.company.service.impl;

import com.company.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    private final String uploadPath;

    public FileServiceImpl(@Value("${application.file.upload.path}") String uploadPath) {
        this.uploadPath = uploadPath;
    }

    @Override
    public String saveFile(MultipartFile file, Long userId) {
        log.info("Saving file, userId: {}",userId);
        final String subUploadPath = "users" + separator + userId;

        return uploadFile(file, subUploadPath);
    }

    private String uploadFile(MultipartFile file, String subUploadPath) {
        final String fullPath = uploadPath + separator + subUploadPath;

        File targetFolder = new File(fullPath);

        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create folder, fileFullPath: {}", fullPath);
                return null;
            }
        }

        final String fileExtension = getFileExtension(file.getOriginalFilename());
        final String finalUploadPath = fullPath + separator + currentTimeMillis() + "." + fileExtension;

        Path path = Paths.get(finalUploadPath);

        try {
            log.info("File is saving, finalUploadPath: {}", finalUploadPath);
            Files.write(path, file.getBytes());

            return finalUploadPath;
        } catch (IOException e) {
            log.error("File wos not saved, errorMessage: {}", e.getMessage());
            return null;
        }
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            log.warn("Filename is null or empty.");
            return null;
        }

        int lastDotIndex = originalFilename.lastIndexOf('.');

        if (lastDotIndex == -1) {
            log.warn("Filename does not contains '.', filename: {}", originalFilename);
            return null;
        }

        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }

}
