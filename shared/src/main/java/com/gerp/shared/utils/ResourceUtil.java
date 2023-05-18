package com.gerp.shared.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ResourceUtil {
    /**
     * store multipart file in temp folder and return it as resource
     *
     * @param filepath
     * @param file
     * @return
     * @throws IOException
     */
    public static Resource getUserFileResource(String filepath, MultipartFile file) throws IOException {
        //to upload in-memory bytes use ByteArrayResource instead
        File createFile = new File(filepath);
        if (!createFile.exists()) {
            createFile.mkdirs();
        }
        File file1 = new File(filepath, file.getOriginalFilename());
        FileUtils.writeByteArrayToFile(file1, file.getBytes());
        return new FileSystemResource(file1);
    }

    /**
     * delete temp files after save
     *
     * @param resourceList
     * @throws IOException
     */
    public static void getDeleteFileResource(List<Resource> resourceList) throws IOException {
        for (Resource resource : resourceList) {
            File file = resource.getFile();
            file.delete();
        }
    }
}
