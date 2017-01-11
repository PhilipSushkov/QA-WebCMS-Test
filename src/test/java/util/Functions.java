package util;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.*;


/**
 * Created by philipsushkov on 2016-12-08.
 */

public class Functions {
    private static Properties propUI;
    private static String currentDir;

    public static Properties ConnectToPropUI(String sPathSharedUIMap) throws IOException {
        propUI = new Properties();
        currentDir = System.getProperty("user.dir") + "/src/test/java/specs/";
        propUI.load(new FileInputStream(currentDir + sPathSharedUIMap));
        return propUI;
    }

    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    //#############################################################################
    //2. Create New Excel Sheet for any page
    public void CreateNewExcelSheet(String sSheetName, String[][] sValues, Properties propConf) {
        HSSFWorkbook workbook = null;
        try {
            try {
                FileInputStream inpFile = new FileInputStream(new File(propConf.getProperty("DataExcelFile")));
                workbook = new HSSFWorkbook(inpFile);
            } catch (FileNotFoundException e) {
                workbook = new HSSFWorkbook();
            }

            HSSFSheet sheet = null;

            try {
                sheet = workbook.createSheet(sSheetName);
            } catch (IllegalArgumentException e) {
                sheet = workbook.getSheet(sSheetName);
                for (int index = sheet.getLastRowNum(); index >= sheet.getFirstRowNum(); index--) {
                    sheet.removeRow(sheet.getRow(index));
                }
            }

            Map<Integer, Object[]> data = new HashMap<Integer, Object[]>();

            int count = 0;
            while (count < sValues.length) {
                data.put(Integer.valueOf(count+1), sValues[count]);
                count++;
            }

            Set<Integer> keyset = data.keySet();
            int rownum = 0;
            for (Integer key : keyset) {
                Row row = sheet.createRow(rownum++);
                Object [] objArr = data.get(key);
                int cellnum = 0;
                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    if(obj instanceof Date)
                        cell.setCellValue((Date)obj);
                    else if(obj instanceof Boolean)
                        cell.setCellValue((Boolean)obj);
                    else if(obj instanceof String)
                        cell.setCellValue((String)obj);
                    else if(obj instanceof Double)
                        cell.setCellValue((Double)obj);
                }
            }

            FileOutputStream outFile =
                    new FileOutputStream(new File(propConf.getProperty("DataExcelFile")));
            workbook.write(outFile);
            outFile.close();
            //Reporter.log("Excel was written successfully.<br>");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //#############################################################################
    //3. Read Excel File for completing any form
    public String[][] ReadExcelSheet(String sSheetName, int columnsTotal, String sExcept, Properties propConf) throws InterruptedException {
        try {

            FileInputStream file = new FileInputStream(new File(propConf.getProperty("DataExcelFile")));

            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheet(sSheetName);

            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            //String str[][] = new String[sheet.getLastRowNum()+1][columnsTotal];
            ArrayList<String[]> zoom = new ArrayList();

            //System.out.print(sheet.getLastRowNum() + "\n");

            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String temp[] = new String[row.getLastCellNum()];

                //System.out.print(row.getCell(0).getStringCellValue() + "\n");
                if (!String.valueOf(row.getCell(0)).equals(sExcept)) {
                    //zoom.add(new String[]{row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue()});
                    for (int i=0; i<row.getLastCellNum(); i++) {
                        if (row.getCell(i).getCellType() == 0)
                            temp[i] = Integer.toString(((int)row.getCell(i).getNumericCellValue()));
                        if (row.getCell(i).getCellType() == 1)
                            temp[i] = row.getCell(i).getStringCellValue();
                        //System.out.print(temp[i] + "\n");
                    }
                    zoom.add(temp);
                }
            }
            file.close();

            String str[][] = zoom.toArray(new String[zoom.size()][columnsTotal]);

            return str;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

}
