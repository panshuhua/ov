package com.ivay.ivay_app.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.web.multipart.MultipartFile;

public interface XAccountCheckService {

    void importAccountDatas(String type, MultipartFile file) throws IOException, ParseException;

    void checkBkCollectionDatas(String time);

    void checkBkTransferDatas(String time);

}
