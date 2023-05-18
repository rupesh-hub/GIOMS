package com.gerp.dartachalani.utils;

import com.gerp.dartachalani.constant.FilePath;
import com.gerp.dartachalani.dto.RestResponseDto;
import org.apache.maven.shared.utils.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUploadUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtils.class);

    private static String url;

    public static ResponseEntity<?> uploadFile(MultipartFile multipartFile, String type) {
        if (multipartFile.isEmpty()) {
            return new RestResponseDto().failureModel("Select File");
        }

        try {
            InputStream stream = multipartFile.getInputStream();
            final byte[] bytes = multipartFile.getBytes();

            Path path = Paths.get(FilePath.getOSPath() + url);
            if (!Files.exists(path)) {
                new File(FilePath.getOSPath() + url).mkdirs();
            }

            final String imagePath = url + System.currentTimeMillis() + "." + FileUtils
                    .getExtension(multipartFile.getOriginalFilename()).toLowerCase();

            path = Paths.get(FilePath.getOSPath() + imagePath);

            Files.write(path, bytes);

            return new RestResponseDto().successModel(imagePath);
        } catch (IOException e) {
            logger.error("Error wile writing file {}", e);
            return new RestResponseDto().failureModel("Fail");
        }

    }

    /**
     * File is uploaded  and renamed that of documenttype
     */
    public static ResponseEntity<?> uploadFile(MultipartFile multipartFile, String url,
                                               String documentName) {

        try {
            final byte[] bytes = multipartFile.getBytes();

            Path path = Paths.get(FilePath.getOSPath() + url);
            if (!Files.exists(path)) {
                new File(FilePath.getOSPath() + url).mkdirs();
            }
            // check if file under same name exits, if exists delete it
            File dir = path.toFile();
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File f : files) {
                    // remove file if exists
                    if (f.getName().toLowerCase().contains(documentName.toLowerCase())) {
                        try {
                            f.delete();
                        } catch (Exception e) {
                            logger.error("Failed to delete file {} {}", f, e);
                        }
                    }
                }

            }
            String fileExtension = FileUtils.getExtension(multipartFile.getOriginalFilename())
                    .toLowerCase();
            url = url + documentName.toLowerCase() + "." + fileExtension;

            path = Paths.get(FilePath.getOSPath() + url);
            Files.write(path, bytes);
            return new RestResponseDto().successModel(url);
        } catch (IOException e) {
            logger.error("Error while saving file {}", e);
            return new RestResponseDto().failureModel("Fail");
        }
    }

    public static void createZip(String sourceDirPath, String zipFilePath) throws IOException {
        deleteFile(zipFilePath);
        File directory = new File(sourceDirPath);
        if (!directory.exists()) {
            return;
        }
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            logger.info("error creating zip of source {} with error {}", sourceDirPath,
                                    e.getMessage());
                        }
                    });
        }
    }

    public static void deleteFile(String location) {
        File dir = new File(location);
        try {
            dir.delete();
            logger.info("deleting file of path {}", location);

        } catch (Exception e) {
            logger.error("error deleting  of path {}", location, e);
        }

    }


}

