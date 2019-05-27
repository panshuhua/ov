package com.ivay.ivay_app.service;

import com.ivay.ivay_app.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

	FileInfo save(MultipartFile file) throws IOException;

	void delete(String id);

}
