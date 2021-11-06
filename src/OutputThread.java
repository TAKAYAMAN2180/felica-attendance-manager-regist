import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OutputThread implements Runnable {
    private File file;
    private int rowCount = 0;
    private Clerk clerk;
    private int count;

    OutputThread(File file, Clerk clerk,int count) {
        this.file = file;
        this.clerk = clerk;
        this.count = count;
    }

    @Override
    public void run() {
        Workbook wb = null;
        try (FileInputStream fis = new FileInputStream(this.file)) {
            wb = WorkbookFactory.create(fis);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(HorizontalAlignment.CENTER);

        Sheet sheet = wb.getSheetAt(0);


        byte columnNumForTime;
        if (this.clerk.getHasEntrance()) {
            columnNumForTime = 1;
        } else {
            columnNumForTime = 2;
        }


            Row row = sheet.getRow(this.count + 1);

            Cell cellForStudentNum = row.createCell(0);
            cellForStudentNum.setCellValue(this.clerk.getStudentNum());

            Cell cellForTime = row.createCell(columnNumForTime);
            cellForTime.setCellValue(new SimpleDateFormat("kk:mm:ss").format(this.clerk.getTime()));

            Cell cellForName = row.createCell(3);
            cellForName.setCellValue(this.clerk.getName());


        try (FileOutputStream fos = new FileOutputStream(file)) {
            wb.write(fos);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
