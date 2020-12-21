package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import module.*;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class ElGamalController {

    private final FileChooser fileChooser = new FileChooser();
    public ElGamal elGamal = new ElGamal();

    @FXML
    public void exit() {
        Platform.exit();
    }

    @FXML//KEY
    public TextField keyGTextField;
    public TextField keyHTextField;
    public TextField keyATextField;
    public TextField modNTextField;
    public Button generateKeyButton;

    public Button readKeyButton;
    public Button writeKeyButton;

    @FXML//PLAINTEXT
    public TextField plaintextFileRead;
    public Button plaintextOpenButton;

    public TextArea plaintextTextBox;

    public TextField plaintextFileWrite;
    public Button cyphertextWriteButton;

    @FXML//CYPHERTEXT
    public TextField cyphertextFileRead;
    public Button cyphertextOpenButton;

    public TextArea cyphertextTextBox;

    public TextField cyphertextFileWrite;
    public Button plaintextWriteButton;

    @FXML//RADIO
    public RadioButton fileRadio;
    public RadioButton textboxRadio;

    @FXML//ENCRYPTE, DECRYPTE
    public Button encryptButton;
    public Button decryptButton;


    public void encrypt() {
        if (textboxRadio.isSelected()) {
            if (plaintextTextBox.getText().isEmpty()) {
                DialogBox.dialogAboutError("Plaintext can't be empty!");
                return;
            }
            String plainText = plaintextTextBox.getText();
            elGamal.setPlainTextByte(plainText.getBytes(StandardCharsets.ISO_8859_1));
        }
        if(fileRadio.isSelected()) {
            if (plaintextFileRead.getText().isEmpty()) {
                DialogBox.dialogAboutError("Choose o file!");
                return;
            }
        }
        BigInteger[] cypher = elGamal.encrypt();

        elGamal.setCypherText(cypher);
        StringBuilder sb = new StringBuilder();
        for (BigInteger bigInteger : cypher) {
            sb.append(bigInteger);
            sb.append("\n");
        }
        cyphertextTextBox.setText(sb.toString());
    }

    public void decrypt() {
        if (textboxRadio.isSelected()) {
            if (cyphertextTextBox.getText().isEmpty()) {
                DialogBox.dialogAboutError("Plaintext can't be empty!");
                return;
            }
            String cypherText = cyphertextTextBox.getText();

            String[] rows = cypherText.split("\n");
            BigInteger[] cipher = new BigInteger[rows.length];
            for(int i = 0; i < rows.length; i++) {
                cipher[i] = new BigInteger(rows[i]);
            }

            elGamal.setCypherText(cipher);
        }
        if(fileRadio.isSelected()) {
            if (cyphertextFileRead.getText().isEmpty()) {
                DialogBox.dialogAboutError("Choose o file!");
                return;
            }
        }
        BigInteger[] plainText = elGamal.decryptToBigInt();
        elGamal.setPlainText(plainText);

        StringBuilder sb = new StringBuilder();
        for (BigInteger bigInteger : plainText) {
            sb.append(bigIntToString(bigInteger));
            sb.append("\n");
        }
        plaintextTextBox.setText(sb.toString());
    }

    public void generateKey() {
        elGamal.generateKey();
        keyATextField.setText(elGamal.getX().toString());
        keyGTextField.setText(elGamal.getG().toString());
        keyHTextField.setText(elGamal.getY().toString());
        modNTextField.setText(elGamal.getP().toString());
    }

    public static BigInteger stringToBigInt(String str)
    {
        byte[] tab = new byte[str.length()];
        for (int i = 0; i < tab.length; i++)
            tab[i] = (byte)str.charAt(i);
        return new BigInteger(1,tab);
    }

    public static String bigIntToString(BigInteger n)
    {
        byte[] tab = n.toByteArray();
        StringBuilder sb = new StringBuilder();
        for (byte b : tab) sb.append((char) b);
        return sb.toString();
    }



    //READ FROM FILE & WRITE TO FILE

    private static File configureOpenFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Choose a file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        return fileChooser.showOpenDialog(null);
    }


    private static File configureWriteFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Write a file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        return fileChooser.showSaveDialog(null);
    }


    public void readKeyFile() {
        fileChooser.setTitle("Read a private Key");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        String keyX = null;
        String mod = null;
        try {
            Scanner sc = new Scanner(file);
            keyX = sc.nextLine();
            mod = sc.nextLine();
            sc.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}
        if (keyX == null || mod == null) {
            DialogBox.dialogAboutError("Private key cant be null");
            return;
        }
        elGamal.setX(new BigInteger(keyX));
        elGamal.setP(new BigInteger(mod));
        keyATextField.setText(keyX);
        modNTextField.setText(mod);

        fileChooser.setTitle("Write a public Key");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        String keyG = null;
        String keyY = null;
        try {
            Scanner sc = new Scanner(file);
            keyG = sc.nextLine();
            keyY = sc.nextLine();
            sc.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}
        if (keyG == null || keyY == null) {
            DialogBox.dialogAboutError("Public key cant be null");
            return;
        }
        elGamal.setG(new BigInteger(keyG));
        elGamal.setY(new BigInteger(keyY));
        keyGTextField.setText(keyG);
        keyHTextField.setText(keyY);

        elGamal.setPm1(elGamal.getP().subtract(BigInteger.ONE));
    }

    public void writeKeyFile() {
        fileChooser.setTitle("Write a private Key");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(file.toString()));
            String keyA = elGamal.getX().toString();
            String mod = elGamal.getP().toString();
            out.write(keyA);
            out.append('\n');
            out.append(mod);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileChooser.setTitle("Write a public key");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(file.toString()));
            String keyG = elGamal.getG().toString();
            String keyH = elGamal.getY().toString();
            String mod = elGamal.getP().toString();
            out.write(keyG);
            out.append('\n');
            out.append(keyH);
            out.append('\n');
            out.append(mod);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openPlainText() {
        File file = configureOpenFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        byte[] bytes = null;
        try {
            FileInputStream in = new FileInputStream(file);
            int size = in.available();
            bytes = new byte[size];
            in.read(bytes);
            in.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(bytes == null) {
            DialogBox.dialogAboutError("File is empty!");
            return;
        }

        elGamal.setPlainTextByte(bytes);
        plaintextFileRead.setText(file.toString());
        plaintextTextBox.setText(new String(bytes, StandardCharsets.ISO_8859_1));
    }


    public void openCyphertText()
    {
        File file = null;
        StringBuilder sb = null;
        try {
            file = configureOpenFileChooser(fileChooser);
            BufferedReader in = new BufferedReader(new FileReader(file.toString()));
            sb = new StringBuilder();
            while(in.ready()) {
                sb.append(in.readLine());
                sb.append("\n");
            }
            in.close();
        } catch (IOException e) {e.printStackTrace();}

        assert sb != null;
        String[] rows = sb.toString().split("\n");
        BigInteger[] cipher = new BigInteger[rows.length];
        for(int i = 0; i < rows.length; i++) {
            cipher[i] = new BigInteger(rows[i]);
        }

        elGamal.setCypherText(cipher);
        cyphertextFileRead.setText(file.toString());
        cyphertextTextBox.setText(sb.toString());

    }

    public void writePlainText() {
        BigInteger[] dane = elGamal.getPlainText();
        File file = configureWriteFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);

            for (BigInteger bigInteger : dane) {
                byte[] tab = bigInteger.toByteArray();
                byte[] temp;
                if(tab[0] == 0) {
                    temp = ElGamal.subtable(tab, 1, tab.length);
                    out.write(temp);
                }
                else{
                    out.write(tab);
                }

            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        plaintextFileWrite.setText(file.toString());
    }


    public void writeCypherText()
    {
        BigInteger[] dane = elGamal.getCypherText();
        File file = configureWriteFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file.toString(), StandardCharsets.ISO_8859_1));
            for(int i = 0; i < dane.length; i++){
                out.write(dane[i].toString());
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {e.printStackTrace();}
        cyphertextFileWrite.setText(file.toString());
    }

}
