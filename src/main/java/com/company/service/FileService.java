package com.company.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveFile(MultipartFile file,Long userId);
}
