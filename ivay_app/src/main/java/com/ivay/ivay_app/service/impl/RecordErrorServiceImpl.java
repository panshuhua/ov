package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.dto.ErrorMsg;
import com.ivay.ivay_app.service.RecordErrorService;
import com.ivay.ivay_common.utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class RecordErrorServiceImpl implements RecordErrorService {

    @Value("${errorInfo.path}")
    private String errorInfoPath;

    @Override
    public void saveErrorInfo(ErrorMsg errorMsg) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String d = sdf.format(date);
        String path = errorInfoPath + d + "-" + errorMsg.getErrorName() + "-" + date.getTime() + ".txt";
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        d = sdf.format(date);
        String text = "出错时间：" + d + "\n错误名称：" + errorMsg.getErrorName() + "\n错误内容：\n" + errorMsg.getErrorInfo();
        FileUtil.saveTextFile(text, path);
    }

}
