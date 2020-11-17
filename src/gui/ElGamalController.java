package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class ElGamalController {

    @FXML
    private MainController mainController;

    @FXML
    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML//KEY
    public TextField keyTextField;
    public Button generateKeyButton;

    public TextField keyFileRead;
    public Button readKeyButton;

    public TextField keyFileWrite;
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


}
