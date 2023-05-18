package com.gerp.attendance.service;

import org.springframework.core.io.Resource;

public interface FileStorageService {
    Resource loadFile(String fileName) throws Exception ;
}
