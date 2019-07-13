package com.ivay.ivay_common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;

/**
 * 文件工具类
 *
 * @author xx
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String saveFile(MultipartFile file, String pathname) {
        try {
            File targetFile = new File(pathname);
            if (targetFile.exists()) {
                return pathname;
            }

            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            file.transferTo(targetFile);

            return pathname;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public static boolean deleteFile(String pathname) {
        File file = new File(pathname);
        if (file.exists()) {
            boolean flag = file.delete();

            if (flag) {
                File[] files = file.getParentFile().listFiles();
                if (files == null || files.length == 0) {
                    file.getParentFile().delete();
                }
            }
            return flag;
        }
        return false;
    }

    public static String fileMd5(InputStream inputStream) {
        try {
            return DigestUtils.md5Hex(inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public static String getPath() {
        return "/" + LocalDate.now().toString().replace("-", "/") + "/";
    }

    /**
     * 将文本写入文件
     *
     * @param value
     * @param path
     */
    public static void saveTextFile(String value, String path) {
        FileWriter writer = null;
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            writer = new FileWriter(file);
            writer.write(value);
            writer.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public static String getText(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        try {
            return getText(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public static String getText(InputStream inputStream) {
        InputStreamReader isr = null;
        BufferedReader bufferedReader = null;
        try {
            isr = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                builder.append(string).append("\n");
            }

            return builder.toString();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return null;
    }
}
