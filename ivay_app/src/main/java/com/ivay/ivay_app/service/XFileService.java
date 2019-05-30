package com.ivay.ivay_app.service;

import com.ivay.ivay_repository.model.XFileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface XFileService {

	XFileInfo save(MultipartFile file, String flag, String gid) throws IOException;

	void delete(String id);

}
