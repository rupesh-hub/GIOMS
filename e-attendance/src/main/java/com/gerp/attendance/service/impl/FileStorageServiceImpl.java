package com.gerp.attendance.service.impl;

import com.gerp.attendance.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Transactional
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;


    @Autowired
    public FileStorageServiceImpl(@Value("${storage.location:temp}") String fileLocation){
        this.fileStorageLocation =
                Paths.get(fileLocation)
                .toAbsolutePath()
                .normalize();

        try {

            Files.createDirectories(this.fileStorageLocation);

        } catch (Exception ex) {

            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);

        }

    }

    @Override
    public Resource loadFile(String fileName) throws Exception {

        try {
            Path file = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new FileNotFoundException("Could not find file");
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not download file");
        }
    }
}
