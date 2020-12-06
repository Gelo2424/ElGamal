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
import java.util.Scanner;

public class ElGamalController {



    @FXML
    private MainController mainController;

    private final FileChooser fileChooser = new FileChooser();
    public ElGamal elGamal = new ElGamal();

    @FXML
    public void exit() {
        Platform.exit();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
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
            elGamal.setPlainText(plainText);
        }
        if(fileRadio.isSelected()) {
            if (plaintextFileRead.getText().isEmpty()) {
                DialogBox.dialogAboutError("Choose o file!");
                return;
            }
        }

        String cypher = elGamal.encryptFromStringToString();
        elGamal.setCypherText(cypher);
        cyphertextTextBox.setText(cypher);
    }

    public void decrypt() {
        if (textboxRadio.isSelected()) {
            if (cyphertextTextBox.getText().isEmpty()) {
                DialogBox.dialogAboutError("Plaintext can't be empty!");
                return;
            }
            String cypherText = cyphertextTextBox.getText();
            elGamal.setCypherText(cypherText);
        }
        if(fileRadio.isSelected()) {
            if (cyphertextFileRead.getText().isEmpty()) {
                DialogBox.dialogAboutError("Choose o file!");
                return;
            }
        }
        String plainText = elGamal.decryptFromStringToString(elGamal.getCypherText());
        System.out.println(plainText);
        elGamal.setPlainText(plainText);
        plaintextTextBox.setText(plainText);
    }

    public void generateKey() {
        elGamal.generateKey();
        keyATextField.setText(elGamal.getA().toString());
        keyGTextField.setText(elGamal.getG().toString());
        keyHTextField.setText(elGamal.getH().toString());
        modNTextField.setText(elGamal.getN().toString());
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
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
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tab.length; i++)
            sb.append((char)tab[i]);
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
        String keyA = null;
        String mod = null;
        try {
            Scanner sc = new Scanner(file);
            keyA = sc.nextLine();
            mod = sc.nextLine();
            sc.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}
        if (keyA == null || mod == null) {
            DialogBox.dialogAboutError("Private key cant be null");
            return;
        }
        elGamal.setA(new BigInteger(keyA));
        elGamal.setN(new BigInteger(mod));
        keyATextField.setText(keyA);
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
        String keyH = null;
        try {
            Scanner sc = new Scanner(file);
            keyG = sc.nextLine();
            keyH = sc.nextLine();
            sc.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}
        if (keyG == null || keyH == null) {
            DialogBox.dialogAboutError("Public key cant be null");
            return;
        }
        elGamal.setG(new BigInteger(keyG));
        elGamal.setH(new BigInteger(keyH));
        keyGTextField.setText(keyG);
        keyHTextField.setText(keyH);
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
            String keyA = elGamal.getA().toString();
            String mod = elGamal.getN().toString();
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
            String keyH = elGamal.getH().toString();
            String mod = elGamal.getN().toString();
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
        StringBuilder sb = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file.toString(), StandardCharsets.ISO_8859_1));
            sb = new StringBuilder();
            while(in.ready()) {
                sb.append(in.readLine());
                sb.append("\n");
            }
            in.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(sb == null) {
            DialogBox.dialogAboutError("File is empty!");
            return;
        }

        String plainText = sb.toString();
        elGamal.setPlainText(plainText);
        plaintextFileRead.setText(file.toString());
        plaintextTextBox.setText(plainText);
    }

    public void openCyphertText() {
        File file = configureOpenFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        StringBuilder sb = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file.toString()));
            sb = new StringBuilder();
            while(in.ready()) {
                sb.append(in.readLine());
                sb.append("\n");
            }
            in.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(sb == null) {
            DialogBox.dialogAboutError("File is empty!");
            return;
        }

        String cypherText = sb.toString();

        elGamal.setCypherText(cypherText);
        cyphertextFileRead.setText(file.toString());
        cyphertextTextBox.setText(cypherText);
    }

    public void writePlainText() {
        File file = configureWriteFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(file.toString(), StandardCharsets.ISO_8859_1));
            out.write(elGamal.getPlainText());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        plaintextFileWrite.setText(file.toString());
    }

    public void writeCypherText() {
        File file = configureWriteFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(file.toString()));
            out.write(elGamal.getCypherText());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cyphertextFileWrite.setText(file.toString());
    }
}
