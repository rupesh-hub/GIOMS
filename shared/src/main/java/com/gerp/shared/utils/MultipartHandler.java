package com.gerp.shared.utils;

import com.gerp.shared.configuration.CustomMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Component
public class MultipartHandler {
    private final CustomMessageSource customMessageSource;
    private String path;

    private MultipartHandler(CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
    }

    public String storeMerchantImages(MultipartFile multipartUserImage) throws Exception {

        if (multipartUserImage != null) {
            String imagePath = new StringBuilder().append(System.getProperty("user.dir")).append("/images/profile/").toString();
            File file = new File(imagePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            try {
                /*check userImage and set to, if no image is found default(defaultUserImage.jpg) image is saved*/
                multipartUserImage.transferTo(new File(imagePath + multipartUserImage.getOriginalFilename()));
                imagePath = new StringBuilder().append("resources/").append(multipartUserImage.getOriginalFilename()).toString();
                return imagePath;
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(e.getMessage());
            }

        }
        throw new Exception(customMessageSource.get("merchant.image.notfound"));
    }

    public String saveFileAfterNullCheck(MultipartFile multipartUserImage, String imageFor) {
        if (multipartUserImage != null) {
            return this.saveFile(multipartUserImage, imageFor);
        }
        return null;
    }

    public String saveFile(MultipartFile multipartUserImage, String imageFor) {
        String uuid = UUID.randomUUID().toString();
        String imagePath = new StringBuilder().append(System.getProperty("user.dir")).append(path).toString();
        File file = new File(imagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            multipartUserImage.transferTo(new File(imagePath + File.separator + uuid + multipartUserImage.getOriginalFilename()));
            imagePath = new StringBuilder()
                    .append("resources/")
                    .append(uuid)
                    .append(multipartUserImage.getOriginalFilename()).toString();
            return imagePath;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setPath(String path) {
        this.path = path;
    }
}
