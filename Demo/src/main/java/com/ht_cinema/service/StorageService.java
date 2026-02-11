package com.ht_cinema.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String save(MultipartFile file);
}
