package com.gerp.shared.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class FileStorageService {


    private final Path rootLocation;


    @Autowired
    public FileStorageService() {
        this.rootLocation = Paths.get("C:/Test");
    }

    /**
     * this is automatically called during run time
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    /**
     * @param file
     * @return
     */
//    public String store(MultipartFile file) {
//        Path baseFilePath;
//        String[] split = file.getOriginalFilename().split("\\.");
//        String originalFilename = split[0] + "_" + UUID.randomUUID() + "." + split[1];
//        String filePath = this.rootLocation + File.separator;
//        File fileDir = new File(filePath);
//        if (!fileDir.exists()) {
//            fileDir.mkdirs();
//        }
//
//        baseFilePath = Paths.get(fileDir.getPath());
//        System.out.println(baseFilePath);
//        if (file.isEmpty()) {
//            throw new RuntimeException("Failed to store empty file " + originalFilename);
//        }
//        if (originalFilename.contains("..")) {
//            // This is a security check
//            throw new RuntimeException(
//                    "Cannot store file with relative path outside current directory "
//                            + originalFilename);
//        }
//        try (InputStream inputStream = file.getInputStream()) {
//            Files.copy(inputStream, baseFilePath.resolve(originalFilename));
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("File Already Exist");
//        }
//        return filePath + originalFilename;
//    }
    public String store(File file) {
        Path baseFilePath;
        String[] split = file.getName().split("\\.");
        String originalFilename = split[0] + "_" + UUID.randomUUID() + "." + split[1];
        String filePath = this.rootLocation + File.separator;
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        baseFilePath = Paths.get(fileDir.getPath());
        System.out.println(baseFilePath);
        if (!file.exists()) {
            throw new RuntimeException("Failed to store empty file " + originalFilename);
        }
        if (originalFilename.contains("..")) {
            throw new RuntimeException(
                    "Cannot store file with relative path outside current directory "
                            + originalFilename);
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            Files.copy(inputStream, baseFilePath.resolve(originalFilename));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File Already Exist");
        }
        return filePath + originalFilename;
    }

    /**
     * @return
     */
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }

    }

    public Stream<Path> loadAll(Path rootPath) {
        try {
            return Files.walk(rootPath, 1)
                    .filter(path -> !path.equals(rootPath))
                    .map(rootPath::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }

    }

    /**
     * @param filename
     * @return
     */
    private Path load(String filename) {
        return rootLocation.resolve(filename);
    }


    /**
     * @param filename
     * @return
     */
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException(
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }


    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void delete(File file) {
        FileSystemUtils.deleteRecursively(file);
    }

    public void delete(Path file) throws IOException {
        FileSystemUtils.deleteRecursively(file);
    }
}
