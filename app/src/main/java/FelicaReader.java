import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import javazoom.jl.decoder.JavaLayerException;

import java.io.*;


public class FelicaReader implements Runnable {
    final public static short WILDCARD = (short) 0xFE00;        // ワイルドカード
    final public static short SUICA = 0x03;
    final private Main main;
    Pointer pasoriHandle;
    Pointer felicaHandle;
    String studentNum;

    public FelicaReader(Main main) throws NoFoundReaderException {
        this.main = main;
        this.pasoriHandle = FelicaLib.INSTANCE.pasori_open(null);
        if (this.pasoriHandle == null) {
            throw new NoFoundReaderException("felicalib.dllを開けません");
        }
        if (FelicaLib.INSTANCE.pasori_init(this.pasoriHandle) != 0) {
            throw new NoFoundReaderException("PaSoRiに接続できません");
        }
    }

    public String getStudentNum(short systemCode) throws FelicaException {
        FelicaLib.INSTANCE.felica_free(felicaHandle);
        felicaHandle = FelicaLib.INSTANCE.felica_polling(pasoriHandle, systemCode, (byte) 0, (byte) 0);
        if (felicaHandle == Pointer.NULL) {
            throw new FelicaException("カード読み取り失敗");
        }
        byte[] buf = new byte[16];
        FelicaLib.INSTANCE.felica_read_without_encryption02(felicaHandle, 0x1A8B, 0, (byte) 0x00, buf);
        String studentNum = "";
        for (int i = 2; i < buf.length - 3; i++) {
            String temStudentNum = String.format("%02x", buf[i]);
            studentNum = studentNum + temStudentNum.charAt(1);
        }
        return studentNum;
    }

    public String getName(short systemCode) throws FelicaException {
        FelicaLib.INSTANCE.felica_free(felicaHandle);
        felicaHandle = FelicaLib.INSTANCE.felica_polling(pasoriHandle, systemCode, (byte) 0, (byte) 0);
        if (felicaHandle == Pointer.NULL) {
            throw new FelicaException("カード読み取り失敗");
        }
        byte[] buf = new byte[16];
        FelicaLib.INSTANCE.felica_read_without_encryption02(felicaHandle, 0x1A8B, 0, (byte) 0x01, buf);
        String studentName = "";
        for (int i = 0; i < buf.length; i++) {
            String temStudentName = String.format("%02X", buf[i]);
            String a = String.valueOf(buf[i]);
            studentName = studentName + changeBytesDataForName(temStudentName);
        }
        return studentName;
    }

    public void close() {
        if (felicaHandle != Pointer.NULL) {
            FelicaLib.INSTANCE.felica_free(felicaHandle);
        }
        if (pasoriHandle != Pointer.NULL) {
            FelicaLib.INSTANCE.pasori_close(pasoriHandle);
        }
    }

    @Override
    public void run() {
        while (true) {
            // Felicaカードの読み取りループ
            try {
                this.studentNum = getStudentNum(FelicaReader.WILDCARD);

                if (!this.studentNum.equals("00000000000")) {
                    if (!this.main.studentNumField.getText().equals(this.studentNum)) {
                        String studentName = getName(FelicaReader.WILDCARD);
                        this.main.studentNumField.setText(this.studentNum);
                        this.main.process(this.studentNum, studentName);
                    }
                } else {
                    try (InputStream is = new FileInputStream(new File("「ブッブー」.mp3"))) {
                        this.main.play(is);
                    } catch (FileNotFoundException fileNotFoundException) {
                        System.out.println("fileNotFoundException");
                    } catch (JavaLayerException javaLayerException) {
                        System.out.println("unsupportedAudioException");
                    } catch (IOException ioexception) {
                        ioexception.printStackTrace();
                    }
                }
                //JOptionPane.showMessageDialog(this.main.getContentPane(), "学生証ではないか、正しく読み取りができていません。", "エラー", JOptionPane.ERROR_MESSAGE);


            } catch (FelicaException e) {
                this.studentNum = "";
            }
            try {
                Thread.sleep(500); // 0.5秒おきに読み取り
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String changeBytesDataForName(String bytesData) {
        String result = null;
        switch (bytesData) {
            case "41":
                result = "A";
                break;
            case "42":
                result = "B";
                break;
            case "43":
                result = "C";
                break;
            case "44":
                result = "D";
                break;
            case "45":
                result = "E";
                break;
            case "46":
                result = "F";
                break;
            case "47":
                result = "G";
                break;
            case "48":
                result = "H";
                break;
            case "49":
                result = "I";
                break;
            case "4A":
                result = "J";
                break;
            case "4B":
                result = "K";
                break;
            case "4C":
                result = "L";
                break;
            case "4D":
                result = "M";
                break;
            case "4E":
                result = "N";
                break;
            case "4F":
                result = "M";
                break;
            case "50":
                result = "P";
                break;
            case "51":
                result = "Q";
                break;
            case "52":
                result = "R";
                break;
            case "53":
                result = "S";
                break;
            case "54":
                result = "T";
                break;
            case "55":
                result = "U";
                break;
            case "56":
                result = "V";
                break;
            case "57":
                result = "W";
                break;
            case "58":
                result = "X";
                break;
            case "59":
                result = "Y";
                break;
            case "5A":
                result = "Z";
                break;
            case "61":
                result = "a";
                break;
            case "62":
                result = "b";
                break;
            case "63":
                result = "c";
                break;
            case "64":
                result = "d";
                break;
            case "65":
                result = "e";
                break;
            case "66":
                result = "f";
                break;
            case "67":
                result = "g";
                break;
            case "68":
                result = "h";
                break;
            case "69":
                result = "i";
                break;
            case "6A":
                result = "j";
                break;
            case "6B":
                result = "k";
                break;
            case "6C":
                result = "l";
                break;
            case "6D":
                result = "m";
                break;
            case "6E":
                result = "n";
                break;
            case "6F":
                result = "o";
                break;
            case "70":
                result = "p";
                break;
            case "71":
                result = "q";
                break;
            case "72":
                result = "r";
                break;
            case "73":
                result = "s";
                break;
            case "74":
                result = "t";
                break;
            case "75":
                result ="u";
                break;
            case "76":
                result = "v";
                break;
            case "77":
                result = "w";
                break;
            case "78":
                result = "x";
                break;
            case "79":
                result = "y";
                break;
            case "7A":
                result = "z";
                break;
            case "00":
                result = "";
                break;
            case "20":
                result = "　";
                break;
            case "A7":
                result = "ぁ";
                break;
            case "A8":
                result = "ぃ";
                break;
            case "A9":
                result = "ぅ";
                break;
            case "AA":
                result = "ぇ";
                break;
            case "AB":
                result = "ぉ";
                break;
            case "AC":
                result = "ゃ";
                break;
            case "AD":
                result = "ゅ";
                break;
            case "AE":
                result = "ょ";
                break;
            case "AF":
                result = "っ";
                break;
            case "B1":
                result = "あ";
                break;
            case "B2":
                result = "い";
                break;
            case "B3":
                result = "う";
                break;
            case "B4":
                result = "え";
                break;
            case "B5":
                result = "お";
                break;
            case "B6":
                result = "か";
                break;
            case "B7":
                result = "き";
                break;
            case "B8":
                result = "く";
                break;
            case "B9":
                result = "け";
                break;
            case "BA":
                result = "こ";
                break;
            case "BB":
                result = "さ";
                break;
            case "BC":
                result = "し";
                break;
            case "BD":
                result = "す";
                break;
            case "BE":
                result = "せ";
                break;
            case "BF":
                result = "そ";
                break;
            case "C0":
                result = "た";
                break;
            case "C1":
                result = "ち";
                break;
            case "C2":
                result = "つ";
                break;
            case "C3":
                result = "て";
                break;
            case "C4":
                result = "と";
                break;
            case "C5":
                result = "な";
                break;
            case "C6":
                result = "に";
                break;
            case "C7":
                result = "ぬ";
                break;
            case "C8":
                result = "ね";
                break;
            case "C9":
                result = "の";
                break;
            case "CA":
                result = "は";
                break;
            case "CB":
                result = "ひ";
                break;
            case "CC":
                result = "ふ";
                break;
            case "CD":
                result = "へ";
                break;
            case "CE":
                result = "ほ";
                break;
            case "CF":
                result = "ま";
                break;
            case "D0":
                result = "み";
                break;
            case "D1":
                result = "む";
                break;
            case "D2":
                result = "め";
                break;
            case "D3":
                result = "も";
                break;
            case "D4":
                result = "や";
                break;
            case "D5":
                result = "ゆ";
                break;
            case "D6":
                result = "よ";
                break;
            case "D7":
                result = "ら";
                break;
            case "D8":
                result = "り";
                break;
            case "D9":
                result = "る";
                break;
            case "DA":
                result = "れ";
                break;
            case "DB":
                result = "ろ";
                break;
            case "DC":
                result = "わ";
                break;
            case "A6":
                result = "を";
                break;
            case "DD":
                result = "ん";
                break;
            case "DE":
                result = "゛";
                break;
            case "DF":
                result = "゜";
                break;
            case "2D":
                result = "-";
                break;
            case "A5":
                result = "・";
                break;
            case "3D":
                result = "=";
                break;
            default:
                result = "☒";
                break;
        }
        return result;
    }

    public interface FelicaLib extends Library {
        FelicaLib INSTANCE = (FelicaLib) Native.loadLibrary("felicalib",
                FelicaLib.class);

        Pointer pasori_open(String dummy);

        int pasori_init(Pointer pasoriHandle);

        void pasori_close(Pointer pasoriHandle);

        Pointer felica_polling(Pointer pasoriHandle, short systemCode, byte rfu, byte time_slot);

        void felica_free(Pointer felicaHandle);

        void felica_getidm(Pointer felicaHandle, byte[] data);

        void felica_getpmm(Pointer felicaHandle, byte[] data);

        int felica_read_without_encryption02(Pointer felicaHandle, int serviceCode, int mode, byte addr, byte[] data);
    }
}





