package com.gerp.usermgmt.util;


import com.gerp.usermgmt.config.StorageProperties;
import com.gerp.usermgmt.pojo.transfer.document.DocumentMasterResponsePojo;
import com.gerp.usermgmt.pojo.transfer.document.DocumentSavePojo;
import com.gerp.usermgmt.services.transfer.impl.DocumentServiceData;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class DocumentUtil {

	private final DocumentServiceData documentServiceData;
	private final Path rootLocation;
	@Autowired
	private StorageProperties properties;

	public DocumentUtil(DocumentServiceData documentServiceData, StorageProperties properties) {
		this.documentServiceData = documentServiceData;
		this.rootLocation = Paths.get(properties.getLocation());
	}

	public DocumentMasterResponsePojo saveDocuments(DocumentSavePojo data, List<MultipartFile> files){
		MultiValueMap<String, Object> body
				= new LinkedMultiValueMap<>();
		if(this.checkEmpty(files))
			files.forEach(x->{
//				int index = files.indexOf(x);
				Resource resource = this.getUserFileResource(this.rootLocation.toString(), x, data.getStatus());
//				body.add("fileList[" + index + "]", resource);
				body.add("document", resource);
			});
		body.add("created_office_code",data.getOfficeCode()!=null?data.getOfficeCode().toString():null);
		body.add("created_by",data.getPisCode()!=null?data.getPisCode().toString():null);
		body.add("tags",data.getTags()!=null&&!data.getTags().isEmpty()?data.getTags().stream()
															.collect(Collectors.joining(",")).toString():null);
		body.add("document_type", data.getType());
//		int i = 0;

		if(data.getDocumentsToDelete()!=null)
			for (Long t : data.getDocumentsToDelete()) {
				body.add("documentsToDelete", t.toString());
//				body.add("documentsToDelete" + "[" + i + "]", t.toString());
//				i++;
			}

		DocumentMasterResponsePojo res = documentServiceData.create(body);
		return res;
	}

	public DocumentMasterResponsePojo saveDocument(DocumentSavePojo data, MultipartFile file){
		MultiValueMap<String, Object> body
				= new LinkedMultiValueMap<>();
		if(this.checkEmpty(file)){
			Resource resource = this.getUserFileResource(this.rootLocation.toString(), file, data.getStatus());
			body.add("document", resource);
		}
		body.add("created_office_code",data.getOfficeCode()!=null?data.getOfficeCode().toString():null);
		body.add("created_by",data.getPisCode()!=null?data.getPisCode().toString():null);
		body.add("tags",data.getTags()!=null&&!data.getTags().isEmpty()?data.getTags().stream()
				.collect(Collectors.joining(",")).toString():null);
		body.add("document_type", data.getType());
		DocumentMasterResponsePojo res = documentServiceData.create(body);
		return res;
	}

	public DocumentMasterResponsePojo updateDocument(DocumentSavePojo data, MultipartFile file){
		MultiValueMap<String, Object> body
				= new LinkedMultiValueMap<>();
		if(this.checkEmpty(file)){
			Resource resource = this.getUserFileResource(this.rootLocation.toString(), file, data.getStatus());
			body.add("file_location", resource);
		}
		body.add("document_id",data.getId().toString());
		body.add("document_type", data.getType());
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
		if(attachFilePart==null)
			return false;
		AtomicBoolean z = new AtomicBoolean(true);
		attachFilePart.forEach(x->{
			if(x.isEmpty())
				z.set(false);
		});
		return z.get();
	}

	public boolean checkEmpty(MultipartFile attachFilePart) {
		if(attachFilePart==null)
			return false;
		AtomicBoolean z = new AtomicBoolean(true);
		if(attachFilePart.isEmpty())
			z.set(false);
		return z.get();
	}
}
