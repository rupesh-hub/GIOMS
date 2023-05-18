package com.gerp.attendance.util;

import com.gerp.attendance.Pojo.document.DocumentMasterResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentSavePojo;
import com.gerp.attendance.Proxy.DocumentServiceData;
import com.gerp.attendance.config.StorageProperties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DocumentUtil {

    private final DocumentServiceData documentServiceData;
    private final Path rootLocation;

    public DocumentUtil(DocumentServiceData documentServiceData, StorageProperties properties) {
        this.documentServiceData = documentServiceData;
        this.rootLocation = Paths.get(properties.getLocation());
    }

    public DocumentMasterResponsePojo saveDocuments(DocumentSavePojo data, List<MultipartFile> files) {
        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        if (this.checkEmpty(files))
            files.forEach(x -> {
//				int index = files.indexOf(x);
                Resource resource = this.getUserFileResource(this.rootLocation.toString(), x, data.getStatus());
//				body.add("fileList[" + index + "]", resource);
                body.add("document", resource);
            });
        body.add("created_office_code", data.getOfficeCode() != null ? data.getOfficeCode().toString() : null);
        body.add("created_by", data.getPisCode() != null ? data.getPisCode().toString() : null);
        body.add("tags", data.getModuleKey() != null ? data.getModuleKey().toString() : null);
        body.add("sub_module", data.getSubModuleKey() != null ? data.getSubModuleKey().toString() : null);
//		body.add("tags",data.getTags()!=null&&!data.getTags().isEmpty()?data.getTags().stream()
//															.collect(Collectors.joining(",")).toString():null);
        body.add("document_type", data.getType());
//		int i = 0;

        if (data.getDocumentsToDelete() != null)
            for (Long t : data.getDocumentsToDelete()) {
                body.add("documentsToDelete", t.toString());
//				body.add("documentsToDelete" + "[" + i + "]", t.toString());
//				i++;
            }

        DocumentMasterResponsePojo res = documentServiceData.create(body);
        return res;
    }

    public DocumentMasterResponsePojo saveDocument(DocumentSavePojo data, MultipartFile file) {
        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (this.checkEmpty(file))
            body.add("document", getUserFileResource(this.rootLocation.toString(), file, data.getStatus()));

        body.add("created_office_code", data.getOfficeCode() != null ? data.getOfficeCode() : null);
        body.add("created_by", data.getPisCode() != null ? data.getPisCode() : null);
        body.add("tags", data.getModuleKey() != null ? data.getModuleKey() : null);
        body.add("sub_module", data.getSubModuleKey() != null ? data.getSubModuleKey() : null);
        body.add("document_type", data.getType());
        return documentServiceData.create(body);
    }

    public DocumentMasterResponsePojo updateDocument(DocumentSavePojo data, MultipartFile file) {
        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        if (this.checkEmpty(file)) {
            Resource resource = this.getUserFileResource(this.rootLocation.toString(), file, data.getStatus());
            body.add("file_location", resource);
        }
        body.add("document_id", data.getId().toString());
        body.add("document_type_id", data.getType());
        DocumentMasterResponsePojo res = documentServiceData.update(body);
        res.setDocuments(Arrays.asList(new DocumentResponsePojo(res.getDocumentId(), res.getFileName(), res.getSizeKB())));
        return res;
    }

    public DocumentMasterResponsePojo updateDocuments(DocumentSavePojo data, List<MultipartFile> files) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (checkEmpty(files))
            files.forEach(x -> {
                Resource resource = this.getUserFileResource(this.rootLocation.toString(), x, data.getStatus());
                body.add("document_files", resource);
            });
        body.add("document_master_id", data.getDocumentMasterId().toString());
        body.add("documents_to_remove", StringUtils.join(data.getDocumentsToDelete(), ","));
        DocumentMasterResponsePojo res = documentServiceData.update(body);
        return res;
    }

    private Resource getUserFileResource(String filepath, MultipartFile file, String version) {
        //to upload in-memory bytes use ByteArrayResource instead
        File createFile = new File(filepath);
        if (!createFile.exists()) {
            createFile.mkdirs();
        }
        File file1 = new File(filepath, file.getOriginalFilename());
        try {
            FileUtils.writeByteArrayToFile(file1, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileSystemResource(file1);
    }

    public boolean checkEmpty(List<MultipartFile> attachFilePart) {
        if (attachFilePart == null)
            return false;
        AtomicBoolean z = new AtomicBoolean(true);
        attachFilePart.forEach(x -> {
            if (x.isEmpty())
                z.set(false);
        });
        return z.get();
    }

    public boolean checkEmpty(MultipartFile attachFilePart) {
        if (attachFilePart == null)
            return false;
        AtomicBoolean z = new AtomicBoolean(true);
        if (attachFilePart.isEmpty())
            z.set(false);
        return z.get();
    }

    public void returnExcelFile(Workbook workbook, String fileName, HttpServletResponse response) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
            response.setContentLength((int) out.size());
            response.setContentType(MediaType.parseMediaType("application/xlsx").toString());
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(out.toByteArray()));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//	private  void generatePDFFromHTML(String filename,HttpServletResponse response){
//		try{
//		Document document = new Document();
//		PdfWriter writer = PdfWriter.getInstance(document,
//				new FileOutputStream("src/output/html.pdf"));
//		document.open();
//		XMLWorkerHelper.getInstance().parseXHtml(writer, document,
//				new FileInputStream(filename));
//
//		document.close();
//
//		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
//		response.setContentLength((int) out.size());
//		response.setContentType(MediaType.parseMediaType("application/pdf").toString());
//		InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(writer.toByteArray()));
//		FileCopyUtils.copy(inputStream, response.getOutputStream());
//	}catch (Exception e){
//		e.printStackTrace();
//	}
//
//	}

    public void returnExcelFile2(Workbook workbook, String fileName, HttpServletResponse response) {
        try {
            File file = new File(this.rootLocation.toString(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
            response.setContentLength((int) file.length());
            response.setContentType(MediaType.parseMediaType("application/xlsx").toString());
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
