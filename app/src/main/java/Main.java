import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ImageProducer;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends JFrame implements ActionListener, WindowListener {

    private final boolean IS_ENTRANCE;
    private final File file;
    private final String FILEPATH_OF_BODY_TEMPERATURE_SHEET;
    public JTextField studentNumField;
    public JLabel labelForInfo;
    public JLabel labelForMsg;
    public JLabel labelForTemperaMsg;
    private String temStudentNum = "";
    //出席管理データの次に入力する列の番号を保持
    private int count = 0;

    Main(boolean hasEntrance, String filePathOfBodyTemperatureSheet) {
        this.FILEPATH_OF_BODY_TEMPERATURE_SHEET = filePathOfBodyTemperatureSheet;
        this.IS_ENTRANCE = hasEntrance;

        Date startTime = new Date();

        String pass = System.getProperty("user.home") + "\\Desktop";
        String fileName = new SimpleDateFormat("MM月dd日 kk時mm分ss秒").format(startTime) + "～" + "入退出時間管理表.xlsx";
        this.file = new File(pass, fileName);

        Workbook wb = null;

        try (InputStream fis = this.getClass().getResourceAsStream("template.xlsx")) {
            wb = WorkbookFactory.create(fis);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(HorizontalAlignment.CENTER);

        Sheet sheet = wb.getSheetAt(0);
        Row rowForInfo = sheet.getRow(1);
        Cell cellForInfo = rowForInfo.createCell(6);

        if (this.IS_ENTRANCE) {
            cellForInfo.setCellValue("入室");
        } else {
            cellForInfo.setCellValue("退出");
        }

        Row rowForTime = sheet.getRow(2);
        Cell cellForStartTime = rowForTime.createCell(6);
        cellForStartTime.setCellValue(new SimpleDateFormat("MM/dd kk:mm:ss").format(startTime));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            wb.write(fos);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        addWindowListener(this);

        setTitle("入退出時間管理表");
        Toolkit toolkit = getToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Insets screenInsets = toolkit.getScreenInsets(getGraphicsConfiguration());

        int width = screenSize.width - (screenInsets.left + screenInsets.right);
        int height = screenSize.height - (screenInsets.top + screenInsets.bottom);

        setBounds(0, 0, width, height);

        Image im = null;
        URL url = this.getClass().getResource("icon.png");
        try {
            im = this.createImage((ImageProducer) url.getContent());
            setIconImage(im);
        } catch (Exception ex) {
            System.out.println("Resource Error!");
            im = null;
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        GridLayout gridLayout = new GridLayout();
        gridLayout.setHgap(10);
        gridLayout.setVgap(20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel(" "));

        JLabel label = new JLabel("学生証をカードリーダーにタッチするか、手動で入力してください。");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        panel.add(new JLabel(" "));

        JPanel panelOfFlowLayout = new JPanel();
        panelOfFlowLayout.setLayout(new FlowLayout());

        JLabel label2 = new JLabel("学籍番号:");
        label2.setFont(new Font("msgothic.ttc", Font.PLAIN, 30));
        panelOfFlowLayout.add(label2);

        this.studentNumField = new JTextField("");
        this.studentNumField.addActionListener(this);
        this.studentNumField.setActionCommand("Student number input");
        this.studentNumField.setFont(new Font("msgothic.ttc", Font.PLAIN, 30));
        this.studentNumField.setPreferredSize(new Dimension(200, 40));
        panelOfFlowLayout.add(this.studentNumField);

        panel.add(panelOfFlowLayout);

        panel.add(new JLabel(""));

        JPanel panelForLabelForMsg = new JPanel();
        panelForLabelForMsg.setLayout(new FlowLayout());

        this.labelForInfo = new JLabel(" ");
        this.labelForInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.labelForInfo.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelForInfo.setFont(new Font("UDDigiKyokashoN-B.ttc", Font.BOLD, 80));
        panelForLabelForMsg.add(this.labelForInfo);


        this.labelForMsg = new JLabel(" ");
        this.labelForMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.labelForMsg.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelForMsg.setFont(new Font("UDDigiKyokashoN-B.ttc", Font.BOLD, 80));
        this.labelForMsg.setForeground(Color.RED);
        panelForLabelForMsg.add(this.labelForMsg);

        this.labelForTemperaMsg = new JLabel(" ");
        this.labelForTemperaMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.labelForTemperaMsg.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelForTemperaMsg.setFont(new Font("UDDigiKyokashoN-B.ttc", Font.BOLD, 80));
        this.labelForTemperaMsg.setForeground(Color.RED);
        panelForLabelForMsg.add(this.labelForTemperaMsg);

        panel.add(panelForLabelForMsg);

        getContentPane().add(panel);

        FelicaReader felicaReader;

        try {
            felicaReader = new FelicaReader(this);
            Thread thread = new Thread(felicaReader);
            thread.start();

        } catch (NoFoundReaderException noFoundReaderException) {
            JOptionPane.showMessageDialog(this, noFoundReaderException, "注意", JOptionPane.ERROR_MESSAGE);
        }

    }

    private static boolean checkEmpty(Cell cell) {
        boolean isEmpty = false;
        try {
            if (cell.getDateCellValue() != null) {
                isEmpty = true;
            }
        } catch (IllegalStateException e) {
            try {
                if (cell.getStringCellValue() != null) {
                    isEmpty = true;
                }
            } catch (IllegalArgumentException ex) {
                if (String.valueOf((long) cell.getNumericCellValue()) != null) {
                    isEmpty = true;
                }
            }
        }
        return isEmpty;
    }

    public void process(String studentNum, String studentName) {
        this.labelForTemperaMsg.setText("");
        this.labelForMsg.setText("");

        String musicFilePath = null;
        this.temStudentNum = studentNum;
        if (this.IS_ENTRANCE) {
            this.labelForInfo.setText("受け付けました。");
            musicFilePath = "celebrationMusic.mp3";
        } else {
            this.labelForInfo.setText("受け付けました。お疲れ様でした。");
            musicFilePath = "farewellGreeting.mp3";
        }
        if (this.IS_ENTRANCE) {
            //体温記録表が提出されているかの確認
            Workbook workbookToRead = null;
            try {
                workbookToRead = WorkbookFactory.create(new File(this.FILEPATH_OF_BODY_TEMPERATURE_SHEET));
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(this, "指定されたパスのデータが体温記録のExcelデータではありません。");
                ioException.printStackTrace();
                System.exit(-1);
            }
            Sheet sheetToRead = null;
            try {
                sheetToRead = workbookToRead.getSheetAt(0);
            } catch (NullPointerException | IllegalArgumentException e) {
                JOptionPane.showInternalMessageDialog(this.getContentPane(), "選択したExcelファイルにはシートがありません。確認してください。プログラムを終了します。",
                        "エラー", JOptionPane.ERROR_MESSAGE);
            }
            boolean hasRegistered = false;
            int currentRowCountForCheck = 1;
            while (true) {
                Row rowToRead = sheetToRead.getRow(currentRowCountForCheck);
                Cell cellToRead = null;
                try {
                    cellToRead = rowToRead.getCell(1);
                } catch (NullPointerException nullPointerException) {
                    hasRegistered = false;
                    break;
                }
                String getStudentNumOfTemperatureData = null;
                try {
                    getStudentNumOfTemperatureData = String.valueOf((long) cellToRead.getNumericCellValue());
                } catch (IllegalStateException | NullPointerException illegalStateException) {
                    try {
                        getStudentNumOfTemperatureData = cellToRead.getStringCellValue();
                    } catch (IllegalStateException | NullPointerException illegalStateException1) {
                        hasRegistered = false;
                    }
                    break;
                }
                if (getStudentNumOfTemperatureData.equals(String.valueOf(studentNum))) {
                    hasRegistered = true;
                    //学生証番号の一致->msgの出力
                    Cell cellOfMsg = rowToRead.getCell(2);
                    Cell cell=rowToRead.getCell(1);


                    String msg = "";
                    try {
                        msg = cellOfMsg.getStringCellValue();
                        //音声を流し、ラベルを変更したうえで終了
                        musicFilePath = "errSound.mp3";
                        this.labelForMsg.setText(msg);
                        break;
                    } catch (NullPointerException ex) {
                        //No msg
                        break;
                    } catch (IllegalArgumentException illegalArgumentException) {
                        try {
                            msg = cellOfMsg.getStringCellValue();
                            //音声を流し、ラベルを変更したうえで終了
                            musicFilePath = "errSound.mp3";
                            this.labelForMsg.setText(msg);
                            break;
                        } catch (IllegalArgumentException illegalArgumentException1) {
                            JOptionPane.showMessageDialog(this, "メッセージの欄には文字列もしくは数字を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                currentRowCountForCheck++;
            }
            if (!hasRegistered) {
                musicFilePath = "errSound.mp3";
                this.labelForTemperaMsg.setText("体温記録表が提出されていません。");
            } else {
                this.labelForTemperaMsg.setText("");
            }
        }
        Clerk clerk = new Clerk(studentNum, studentName, this.IS_ENTRANCE);
        OutputThread outputThread = new OutputThread(this.file, clerk, this.count);
        this.count++;
        Thread threadForOutputThread = new Thread(outputThread);
        threadForOutputThread.start();

        try (InputStream is = this.getClass().getResourceAsStream(musicFilePath)) {
            play(is);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (JavaLayerException javaLayerException) {
            System.out.println("unsupportedAudioException");
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public void play(InputStream mp3file) throws JavaLayerException {

        AudioDevice device = FactoryRegistry.systemRegistry().createAudioDevice();
        // create an MP3 player
        AdvancedPlayer player = new AdvancedPlayer(mp3file, device);
        player.play();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Student number input":
                if (!this.temStudentNum.equals(this.studentNumField.getText())) {
                    process(this.studentNumField.getText(), null);
                }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        Workbook wb = null;
        try (FileInputStream fis = new FileInputStream(this.file)) {
            wb = WorkbookFactory.create(fis);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        CellStyle cs = null;
        try {
            cs = wb.createCellStyle();
        } catch (NullPointerException nullPointerException) {
            JOptionPane.showMessageDialog(this, "Template.xlsが不正に変更されています。", "エラー", JOptionPane.ERROR_MESSAGE);
        }
        cs.setAlignment(HorizontalAlignment.CENTER);

        Sheet sheet = wb.getSheetAt(0);
        Row rowForTime = sheet.getRow(2);
        Cell cellForEndTime = rowForTime.createCell(8);
        cellForEndTime.setCellValue(new SimpleDateFormat("MM/dd kk:mm:ss").format(new Date()));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            wb.write(fos);
        } catch (FileNotFoundException fileNotFoundException) {
            JOptionPane.showMessageDialog(this, fileNotFoundException.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}