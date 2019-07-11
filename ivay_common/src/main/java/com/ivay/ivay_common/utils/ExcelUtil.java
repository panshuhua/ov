package com.ivay.ivay_common.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * excel工具类
 * 
 * @author xx
 *
 */
public class ExcelUtil {

    public static void excelLocal(String path, String fileName, String[] headers, List<Object[]> datas) {
        Workbook workbook = getWorkbook(headers, datas);
        if (workbook != null) {
            ByteArrayOutputStream byteArrayOutputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                workbook.write(byteArrayOutputStream);

                String suffix = ".xls";
                File file = new File(path + File.separator + fileName + suffix);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (byteArrayOutputStream != null) {
                        byteArrayOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导出excel
     * 
     * @param fileName
     * @param headers
     * @param datas
     * @param response
     */
    public static void excelExport(String fileName, String[] headers, List<Object[]> datas,
        HttpServletResponse response) {
        Workbook workbook = getWorkbook(headers, datas);
        if (workbook != null) {
            ByteArrayOutputStream byteArrayOutputStream = null;
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                workbook.write(byteArrayOutputStream);

                String suffix = ".xls";
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String((fileName + suffix).getBytes(), "iso-8859-1"));

                OutputStream outputStream = response.getOutputStream();
                outputStream.write(byteArrayOutputStream.toByteArray());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (byteArrayOutputStream != null) {
                        byteArrayOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 
     * @param headers
     *            列头
     * @param datas
     *            数据
     * @return
     */
    public static Workbook getWorkbook(String[] headers, List<Object[]> datas) {
        Workbook workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet();
        Row row = null;
        Cell cell = null;
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER_SELECTION);

        Font font = workbook.createFont();

        int line = 0, maxColumn = 0;
        if (headers != null && headers.length > 0) {// 设置列头
            row = sheet.createRow(line++);
            row.setHeightInPoints(23);
            font.setBold(true);
            font.setFontHeightInPoints((short)13);
            style.setFont(font);

            maxColumn = headers.length;
            for (int i = 0; i < maxColumn; i++) {
                cell = row.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(style);
            }
        }

        if (datas != null && datas.size() > 0) {// 渲染数据
            for (int index = 0, size = datas.size(); index < size; index++) {
                Object[] data = datas.get(index);
                if (data != null && data.length > 0) {
                    row = sheet.createRow(line++);
                    row.setHeightInPoints(20);

                    int length = data.length;
                    if (length > maxColumn) {
                        maxColumn = length;
                    }

                    for (int i = 0; i < length; i++) {
                        cell = row.createCell(i);
                        cell.setCellValue(data[i] == null ? null : data[i].toString());
                    }
                }
            }
        }

        for (int i = 0; i < maxColumn; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    /**
     * 读取Excel中的数据
     * 
     * @return
     * @throws IOException
     */
    public static List<XSSFRow> readXls(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
        List<XSSFRow> list = new ArrayList<XSSFRow>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = hssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    // HSSFCell no = hssfRow.getCell(0);
                    // HSSFCell name = hssfRow.getCell(1);
                    // HSSFCell age = hssfRow.getCell(2);
                    // HSSFCell score = hssfRow.getCell(3);
                    // list.add(HSSFCell);
                    list.add(xssfRow);
                }
            }
        }

        return list;

    }

    public static String getValue(XSSFCell xssfCell) {
        if (xssfCell != null) {
            if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
                // 返回布尔类型的值
                return String.valueOf(xssfCell.getBooleanCellValue());
            } else if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                // 返回数值类型的值
                // 科学计数法转换为字符串
                BigDecimal bd = new BigDecimal(xssfCell.getNumericCellValue());
                return bd.toPlainString();
            } else {
                // 返回字符串类型的值
                return String.valueOf(xssfCell.getStringCellValue());
            }
        } else {
            return "";
        }

    }


}
