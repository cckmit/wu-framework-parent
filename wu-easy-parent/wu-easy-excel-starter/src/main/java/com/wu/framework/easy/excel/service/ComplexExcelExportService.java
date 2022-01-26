package com.wu.framework.easy.excel.service;

import com.wu.framework.easy.excel.stereotype.EasyExcel;
import com.wu.framework.easy.excel.stereotype.EasyExcelBean;
import com.wu.framework.easy.excel.util.ISheetShowContextMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * description 复杂Excel导出工具
 *
 * @author Jia wei Wu
 * @date 2020/10/6 下午8:28
 */
public class ComplexExcelExportService implements ExcelExcelService {


    /**
     * description
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/10/6 上午11:12
     */
    @SneakyThrows
    public static byte[] exportExcel(EasyExcel easyExcel, Collection collection) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 多工作簿
        if (easyExcel.multipleSheet()) {
            List<List> splitList = ExcelExcelService.splitList(new ArrayList(collection), easyExcel.limit());
            ISheetShowContextMethod iSheetShowContextMethod = easyExcel.sheetShowContext().getISheetShowContextMethod().newInstance();
            List<String> sheetContextList = iSheetShowContextMethod.sheetContext(collection.size(), easyExcel.limit());
            for (int i = 0; i < splitList.size(); i++) {
                complexSingleSheet(workbook, sheetContextList.get(i), easyExcel, splitList.get(i));
            }
        } else {
            complexSingleSheet(workbook, easyExcel.fileName(), easyExcel, collection);
        }
        workbook.write(out);
        return out.toByteArray();
    }

    /**
     * 复杂单个工作簿导出
     *
     * @param workbook
     * @param sheetName
     * @param easyExcel
     * @param collection
     */
    public static void complexSingleSheet(HSSFWorkbook workbook, String sheetName, EasyExcel easyExcel, Collection collection) {
        try {
            //首先检查数据看是否是正确的
            Iterator iterator = collection.iterator();
            if (!iterator.hasNext()) {
                throw new Exception("数据错误");
            }
            //取得实际泛型类
            Object ts = iterator.next();
            /* 生成一个表格 */
            HSSFSheet sheet = workbook.createSheet(sheetName);
            // 设置表格默认列宽度为15个字节
            sheet.setDefaultColumnWidth(20);
            // 生成一个样式
            HSSFCellStyle style = workbook.createCellStyle();
            // 设置标题样式
//            style = ExcelStyle.setHeadStyle(workbook, style);
            List<Field> fieldList;
            if (easyExcel.useAnnotation()) {
                fieldList = Arrays.stream(ts.getClass().getDeclaredFields()).
                        filter(field -> null != field.getAnnotation(easyExcel.fieldColumnAnnotation()) || null != field.getAnnotation(EasyExcelBean.class)).
                        peek(field -> field.setAccessible(true)).
                        collect(Collectors.toList());
            } else {
                fieldList = Arrays.stream(ts.getClass().getDeclaredFields()).peek(field -> field.setAccessible(true)).collect(Collectors.toList());
            }
            // 产生表格标题行
            HSSFRow row = sheet.createRow(0);
//            Field clazzField=new Field(ts.getClass(),ts.getClass().getName(),ts.getClass(),0,-1,"",null);
//            createCellHeader(row, style, 0, 0,clazzField , easyExcel);
            /**
             * 额外的头
             */
            int additionalHeader = 0;
            for (int i = 0; i < fieldList.size(); i++) {
                createCellHeader(row, style, i, additionalHeader, fieldList.get(i), easyExcel);
            }
            int rowIndex = 1;// 从第二行开始
            // 额外的单元格
            int additionalCell = 0;
            Integer rowNum = createCell(sheet, sheet.createRow(1), 1, 0, additionalCell, collection, easyExcel);
            System.out.println("总行数:" + rowNum);
//            createCell(new CreateCell(sheet, sheet.createRow(1), 1, 0, additionalCell, collection, easyExcel));
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * description  创建一个单元格表头
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/10/7 上午10:19
     */
    public static void createCellHeader(HSSFRow hssfRow, HSSFCellStyle style, int i, int additionalHeader, Field field, EasyExcel easyExcel) {
        if (Collection.class.isAssignableFrom(field.getType())) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                // 集合中的范性
                Class paramClass = (Class) parameterizedType.getActualTypeArguments()[0];
                List<Field> paramClassFieldList = Arrays.stream(paramClass.getDeclaredFields()).
                        filter(paramClassField -> null != paramClassField.getAnnotation(easyExcel.fieldColumnAnnotation()) ||
                                null != paramClassField.getAnnotation(EasyExcelBean.class)).
                        peek(paramClassField -> paramClassField.setAccessible(true)).
                        collect(Collectors.toList());
                for (Field paramClassField : paramClassFieldList) {
                    createCellHeader(hssfRow, style, i, additionalHeader, paramClassField, easyExcel);
                    additionalHeader++;
                }
            }
        } else {
            // 正常数据
            HSSFCell hssfCell = hssfRow.createCell(i + additionalHeader);
            hssfCell.setCellStyle(style);
            String headerName;
            if (easyExcel.useAnnotation()) {
                Annotation filedAnnotation = field.getAnnotation(easyExcel.fieldColumnAnnotation());
                if (null == filedAnnotation) {
                    return;
                } else {
                    Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(filedAnnotation);
                    headerName = String.valueOf(annotationAttributes.getOrDefault(easyExcel.fieldColumnAnnotationAttribute(), field.getName()));
                }
            } else {
                headerName = field.getName();
            }
            HSSFRichTextString text = new HSSFRichTextString(headerName);
            hssfCell.setCellValue(text);
        }
    }

    /**
     * 将文本添加到单元格
     *
     * @param sheet
     * @param cellStartIndex
     * @param additionalCell
     * @param fieldVal
     * @param easyExcel
     */
    @SneakyThrows
    public static Integer createCell(HSSFSheet sheet, HSSFRow hssfRow, Integer rowStartIndex, int cellStartIndex, int additionalCell, Object fieldVal, EasyExcel easyExcel) {
        if (Collection.class.isAssignableFrom(fieldVal.getClass()) && !ObjectUtils.isEmpty(fieldVal)) {
            Iterator iterator = ((Collection) fieldVal).iterator();
            // List 第一个数据写在上一层
            Integer tempRowStartIndex;
            Object first = iterator.next();
            tempRowStartIndex = createCell(sheet, hssfRow, rowStartIndex, cellStartIndex, additionalCell, first, easyExcel);
            while (iterator.hasNext()) {
                // 加一行
                rowStartIndex++;
                tempRowStartIndex++;
                hssfRow = sheet.createRow(tempRowStartIndex);
                // 临时的额外列数
                int tempAdditionalCell = additionalCell;
                int tempCellStartIndex = cellStartIndex;
                Object paramClassFieldVal = iterator.next();
                tempRowStartIndex = createCell(sheet, hssfRow, tempRowStartIndex, tempCellStartIndex, tempAdditionalCell, paramClassFieldVal, easyExcel);
            }
            return tempRowStartIndex;
        } else if (!isBaseType(fieldVal)) {
            // 非基本数据类型
            Class paramClass = fieldVal.getClass();
            List<Field> paramClassFieldList = Arrays.stream(paramClass.getDeclaredFields()).
                    filter(paramClassField -> null != paramClassField.getAnnotation(easyExcel.fieldColumnAnnotation()) || null != paramClassField.getAnnotation(EasyExcelBean.class)).
                    peek(paramClassField -> paramClassField.setAccessible(true)).
                    collect(Collectors.toList());
            // 临时的额外列数
            int tempAdditionalCell = additionalCell;
            int tempCellStartIndex = cellStartIndex;
            Integer tempRowStartIndex = 0;
            for (Field paramClassField : paramClassFieldList) {
                tempRowStartIndex = createCell(sheet, hssfRow, rowStartIndex, tempCellStartIndex, tempAdditionalCell, paramClassField.get(fieldVal), easyExcel);
//                    tempAdditionalCell++;
                tempCellStartIndex++;
            }
            return tempRowStartIndex;
        } else {
            // 正常数据
            HSSFCell hssfCell = hssfRow.createCell(cellStartIndex + additionalCell);
            String textValue = String.valueOf(fieldVal);
            hssfCell.setCellValue(textValue);
            return rowStartIndex;
        }
    }

    /**
     * description 多个sheet 导出
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/10/6 上午11:12
     */
    public static byte[] exportExcelMultipleSheet(EasyExcel easyExcel, List<String> titlist, List<List> dataSet) {
        //首先检查数据看是否是正确的
        if (titlist.size() == 0 || !dataSet.iterator().hasNext()) {
            throw new RuntimeException("传入的数据不对！");
        }
        byte[] bytes = new byte[0];
        for (int o = 0; o < dataSet.size(); o++) {
            exportExcel(easyExcel, dataSet.get(0));
        }
        return bytes;
    }

    /**
     * 判断object是否为基本类型
     *
     * @param object
     * @return
     */
    public static boolean isBaseType(Object object) {
        Class className = object.getClass();
        String packageName = className.getPackage().getName();
        if (className.equals(java.lang.Integer.class) ||
                className.equals(java.lang.Byte.class) ||
                className.equals(java.lang.Long.class) ||
                className.equals(java.lang.Double.class) ||
                className.equals(java.lang.Float.class) ||
                className.equals(java.lang.Character.class) ||
                className.equals(java.lang.Short.class) ||
                className.equals(java.lang.Boolean.class) ||
                packageName.startsWith("java")) {
            return true;
        }
        return false;
    }

    public static int xx(int a) {
        if (a > 10) {
            a--;
            return xx(a);
        } else if (a < 10) {
            int b = 0;
            while (a < 10) {
                a--;
                b = xx(a);
            }
            return b;
        } else {

            System.out.println(a);
            return a;
        }

    }

//    public static void main(String[] args) {
//        int a = 99;
//        a = xx(a);
//        System.out.println("外面的结果" + a);
//    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCell {
        private HSSFSheet sheet;
        private HSSFRow hssfRow;
        private Integer rowStartIndex;
        private int cellStartIndex;
        private int additionalCell;
        private Object fieldVal;
        private EasyExcel easyExcel;
    }
}



