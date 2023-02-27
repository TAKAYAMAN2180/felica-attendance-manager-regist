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

public class Main extends JFrame implements ActionListener {

    public JTextField idmField;
    public JTextField nameField;
    public JLabel labelForInfo;
    private String temIdm = "";
    //出席管理データの次に入力する列の番号を保持
    private int count = 0;

    Main() {
        setTitle("名前登録システム for 入退出管理システム");
        Toolkit toolkit = getToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Insets screenInsets = toolkit.getScreenInsets(getGraphicsConfiguration());

        int width = screenSize.width - (screenInsets.left + screenInsets.right);
        int height = screenSize.height - (screenInsets.top + screenInsets.bottom);

        setBounds(0, 0, width / 2, height / 2);

        Image im = null;
        URL url = this.getClass().getResource("icon.png");
        try {
            im = this.createImage((ImageProducer) url.getContent());
            setIconImage(im);
        } catch (Exception ex) {
            System.out.println("Resource Error!");
            im = null;
        }

        JPanel panel = new JPanel();
        GridLayout gridLayout = new GridLayout();
        gridLayout.setHgap(10);
        gridLayout.setVgap(20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel(" "));

        JLabel label = new JLabel("ICカードをカードリーダーにタッチしてください。");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        panel.add(new JLabel(" "));

        JPanel panelOfFlowLayout = new JPanel();
        panelOfFlowLayout.setLayout(new FlowLayout());

        JLabel label2 = new JLabel("IDM:");
        label2.setFont(new Font("msgothic.ttc", Font.PLAIN, 30));
        panelOfFlowLayout.add(label2);

        this.idmField = new JTextField("");
        this.idmField.setFont(new Font("msgothic.ttc", Font.PLAIN, 30));
        this.idmField.setPreferredSize(new Dimension(400, 40));
        panelOfFlowLayout.add(this.idmField);

        panel.add(panelOfFlowLayout);

        JPanel panelOfFlowLayout2 = new JPanel();
        panelOfFlowLayout.setLayout(new FlowLayout());

        JLabel label3 = new JLabel("登録する名前:");
        label3.setFont(new Font("msgothic.ttc", Font.PLAIN, 30));
        panelOfFlowLayout2.add(label3);

        this.nameField = new JTextField("");
        this.nameField.setFont(new Font("msgothic.ttc", Font.PLAIN, 30));
        this.nameField.setPreferredSize(new Dimension(400, 40));
        panelOfFlowLayout2.add(this.nameField);
        panel.add(panelOfFlowLayout2);

        panel.add(new JLabel(""));

        JButton button = new JButton("　　　　　決定　　　　　");
        button.addActionListener(this);
        button.setActionCommand("decide");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(button);

        JPanel panelForLabelForMsg = new JPanel();
        panelForLabelForMsg.setLayout(new FlowLayout());

        this.labelForInfo = new JLabel(" ");
        this.labelForInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.labelForInfo.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelForInfo.setFont(new Font("UDDigiKyokashoN-B.ttc", Font.BOLD, 20));
        panelForLabelForMsg.add(this.labelForInfo);

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

    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }

    public void process(String idm, String name) {
        name=name.replace(" ", "%20");
        this.labelForInfo.setText("");

        String musicFilePath = null;
        this.temIdm = idm;
        musicFilePath = "celebrationMusic.mp3";

        //ここでアクセスして出席か退出を取得
        String result;
        try {
            result = HttpUtil.sendHttpRequest("GET", "https://felica-attendance-manager.azurewebsites.net/api/name/regist?idm=" + idm + "&name=" + name);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showInternalMessageDialog(this.getContentPane(), "リクエストを送信中に予期せぬエラーが発生しました。");
            return;
        }
        switch (result) {
            case "Already exist":
                this.labelForInfo.setText("すでに名前が登録されています。");
                break;
            case "Regist Name":
                this.labelForInfo.setText("新たに名前を登録しました");
                break;
            default:
                System.out.println(result);
                JOptionPane.showInternalMessageDialog(this.getContentPane(), "リクエストを送信中に予期せぬエラーが発生しました。");
                return;
        }

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
            case "decide":
                process(this.idmField.getText(), this.nameField.getText());
                break;
        }
    }
}