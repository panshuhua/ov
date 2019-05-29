package com.ivay.ivay_app.service.impl;

import com.ivay.ivay_app.advice.BusinessException;
import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.dao.XFileInfoDao;
import com.ivay.ivay_app.dao.XUserExtInfoDao;
import com.ivay.ivay_app.model.XFileInfo;
import com.ivay.ivay_app.model.XUserExtInfo;
import com.ivay.ivay_app.service.XFileService;
import com.ivay.ivay_common.utils.FileUtil;
import com.ivay.ivay_common.utils.SysVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;


@Service
public class XFileServiceImpl implements XFileService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private XFileInfoDao xFileInfoDao;

    @Autowired
    private XUserExtInfoDao xUserExtInfoDao;

    @Autowired
    private I18nService i18nService;

    @Value("${files.path}")
    private String filesPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XFileInfo save(MultipartFile file, String flag, String gid) throws IOException {
        if (file == null) {
            throw new BusinessException(i18nService.getMessage("response.error.file.lack.code"),
                    i18nService.getMessage("response.error.file.lack.msg"));
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw new BusinessException(i18nService.getMessage("response.error.file.name.code"),
                    i18nService.getMessage("response.error.file.name.msg"));
        }

        XUserExtInfo xUserExtInfo = xUserExtInfoDao.getByGid(gid);
        if (xUserExtInfo == null) {
            throw new BusinessException(i18nService.getMessage("response.error.user.needexinfo.code"),
                    i18nService.getMessage("response.error.user.needexinfo.msg"));
        }

        String md5 = FileUtil.fileMd5(file.getInputStream());
        XFileInfo xFileInfo = xFileInfoDao.getById(md5);

        if (xFileInfo != null) {
            // 已上传过，则更新文件信息
            xFileInfoDao.update(xFileInfo);
        } else {
            // 以md5作为文件名
            filename = filename.substring(filename.lastIndexOf(".")); // 后缀名
            String pathname = FileUtil.getPath() + md5 + filename;
            String fullPath = filesPath + pathname;

            // 保存文件
            FileUtil.saveFile(file, fullPath);

            // 保存文件信息
            xFileInfo = new XFileInfo();
            xFileInfo.setId(md5);
            xFileInfo.setSize(file.getSize());
            xFileInfo.setPath(fullPath);
            xFileInfo.setUrl(pathname);
            String contentType = file.getContentType();
            xFileInfo.setContentType(contentType);
            xFileInfo.setType(contentType.startsWith("image/") ? 1 : 0);
            xFileInfoDao.save(xFileInfo);
            logger.debug("上传文件{}", fullPath);
        }

        // 保存授信扩展信息
        switch (flag) {
            case SysVariable.PHOTO_TYPE_FRONT:
                xUserExtInfo.setPhoto1Url(md5);
                break;
            case SysVariable.PHOTO_TYPE_BACK:
                xUserExtInfo.setPhoto2Url(md5);
                break;
            case SysVariable.PHOTO_TYPE_HAND:
                xUserExtInfo.setPhoto3Url(md5);
                break;
            default:
                logger.info("传入照片类型出错");
        }
        xUserExtInfo.setUpdateTime(new Date());
        xUserExtInfoDao.update(xUserExtInfo);
        return xFileInfo;
    }

    @Override
    public void delete(String id) {
        XFileInfo xFileInfo = xFileInfoDao.getById(id);
        if (xFileInfo != null) {
            String fullPath = xFileInfo.getPath();
            FileUtil.deleteFile(fullPath);

            xFileInfoDao.delete(id);
            logger.debug("删除文件：{}", xFileInfo.getPath());
        }
    }

}
