import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class StartFrame extends JFrame implements ActionListener {
    private final JLabel labelToShowFilePath;
    private final JLabel labelForMsg;
    private final JTextField jTextField;
    private String filePath = null;

    StartFrame() {
        //一番最初にROOMIDの一覧を取得する
        try {
            URL url = new URL("https://felica-attendance-manager.azurewebsites.net/api/rooms/show");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            StringBuilder result = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader((new BufferedReader(new InputStreamReader(connection.getInputStream()))))) {
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            Integer[] intArray = mapper.readValue(result.toString(), Integer[].class);
            System.out.println(intArray);
        } catch (Exception e) {
            JOptionPane.showInternalMessageDialog(this.getContentPane(), "データベースとの接続中にエラーが発生しました。" + e.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
        }


        Image im = null;
        URL url = this.getClass().getResource("icon.png");
        try {
            im = this.createImage((ImageProducer) url.getContent());
            setIconImage(im);
        } catch (Exception ex) {
            ex.printStackTrace();
            im = null;
        }

        setTitle("Felica出席管理プログラム");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(200, 200, 800, 370);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Felica出席管理プログラム");
        label.setFont(new Font("msgothic.ttc", Font.PLAIN, 30));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        panel.add(new JLabel(" "));

        JLabel labelForChoice = new JLabel("ROOMIDを入力してください");
        labelForChoice.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelForChoice);

        this.jTextField = new JTextField("");
        this.jTextField.setFont(new Font("msgothic.ttc", Font.PLAIN, 30));
        this.jTextField.setPreferredSize(new Dimension(100, 40));
        this.jTextField.setHorizontalAlignment((int) Component.CENTER_ALIGNMENT);
        this.jTextField.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(this.jTextField);

        panel.add(new JLabel(" "));

        JLabel labelForFileMsg = new JLabel("体温管理用の指定したフォーマットのExcelファイルの場所を選択してください。");
        labelForFileMsg.setFont(new Font("msgothic.ttc", Font.PLAIN, 20));
        labelForFileMsg.setHorizontalAlignment(SwingConstants.CENTER);
        labelForFileMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelForFileMsg);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        this.labelToShowFilePath = new JLabel("ファイルの場所：（選択されていません。右の『設定』ボタンを押してファイルを選択してください。）");
        panel1.add(this.labelToShowFilePath);

        JButton btnToChooseFile = new JButton("設定");
        btnToChooseFile.addActionListener(this);
        btnToChooseFile.setActionCommand("Choose File");
        panel1.add(btnToChooseFile);

        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(panel1);

        panel.add(new JLabel(" "));

        this.labelForMsg = new JLabel(" ");
        this.labelForMsg.setFont(new Font("msgothic.ttc", Font.BOLD, 20));
        this.labelForMsg.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelForMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.labelForMsg.setForeground(Color.RED);
        panel.add(this.labelForMsg);

        panel.add(new JLabel(" "));

        JButton button = new JButton("　　　　　決定　　　　　");
        button.addActionListener(this);
        button.setActionCommand("Decide");
        panel.add(button);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(new JLabel(" "));

        getContentPane().add(panel);
    }

    public static void main(String[] args) {
        StartFrame start = new StartFrame();
        start.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            /*case "Decide":
                if (this.jList.getSelectedIndex() == -1) {
                    this.labelForMsg.setText("『入室』もしくは『退出』を選択してください。");
                    JOptionPane.showInternalMessageDialog(this.getContentPane(), "入室もしくは退出を選択してください。",
                            "エラー", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                if (this.filePath == null) {
                    this.labelForMsg.setText("体温管理用ファイルの場所が設定されていません。『設定』で指定してください。");
                    JOptionPane.showInternalMessageDialog(this.getContentPane(), "体温管理用ファイルの場所が設定されていません。『設定』で指定してください。",
                            "エラー", JOptionPane.ERROR_MESSAGE);
                    break;
                }

                boolean hasEntrance;
                if (this.jList.getSelectedIndex() == 0) {
                    hasEntrance = true;
                } else {
                    hasEntrance = false;
                }

                Main main = new Main(hasEntrance, this.filePath);
                main.setVisible(true);
                dispose();
                break;

            case "Choose File":
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setPreferredSize(new Dimension(1000, 700));
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "エクセルファイル(*.xls;*.xlsx;*.xlsm)", "xls", "xlsx", "xlsm");
                fileChooser.setFileFilter(filter);
                int selected = fileChooser.showOpenDialog(this);
                if (selected == JFileChooser.APPROVE_OPTION) {
                    this.labelToShowFilePath.setText("ファイルの場所：" + fileChooser.getSelectedFile().getPath());
                    this.filePath = fileChooser.getSelectedFile().getPath();
                } else if (selected == JFileChooser.ERROR_OPTION) {
                    JOptionPane.showInternalMessageDialog(this.getContentPane(), "ファイルの読込中に予期せぬエラーが発生しました。もう一度選択してください。",
                            "エラー", JOptionPane.ERROR_MESSAGE);
                    this.labelForMsg.setText("ファイルの読込中に予期せぬエラーが発生しました。もう一度選択してください。");
                } else if (selected == JFileChooser.CANCEL_OPTION) {
                }
                break;*/
        }
    }
}

