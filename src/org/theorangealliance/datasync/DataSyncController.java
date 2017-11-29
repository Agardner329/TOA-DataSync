package org.theorangealliance.datasync;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import org.theorangealliance.datasync.models.Team;
import org.theorangealliance.datasync.tabs.TeamsController;
import org.theorangealliance.datasync.util.Config;
import org.theorangealliance.datasync.util.TOAEndpoint;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kyle Flynn on 11/28/2017.
 */
public class DataSyncController implements Initializable {

    /* This is the left-side of the setup tab. */
    @FXML public Tab tabSetup;
    @FXML public TextField txtSetupKey;
    @FXML public TextField txtSetupID;
    @FXML public Button btnSetupTest;
    @FXML public Label labelSetupTest;
    @FXML public Label txtConsole;

    /* This is the right-side of the setup tab. */
    @FXML public TextField txtSetupDir;
    @FXML public Button btnSetupSelect;
    @FXML public Button btnSetupTestDir;
    @FXML public Label labelSetupDir;

    /* This is for our teams tab */
    @FXML public Tab tabTeams;
    @FXML public TableView<Team> tableTeams;
    @FXML public TableColumn colTeamsTeam;
    @FXML public TableColumn colTeamsRegion;
    @FXML public TableColumn colTeamsLeague;
    @FXML public TableColumn colTeamsShort;
    @FXML public TableColumn colTeamsLong;
    @FXML public TableColumn colTeamsLocation;

    /* Instances of our tab controllers */
    private TeamsController teamsController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.teamsController = new TeamsController(this);

        labelSetupTest.setTextFill(Color.RED);
        labelSetupDir.setTextFill(Color.RED);

        txtSetupDir.setEditable(false);
        btnSetupSelect.setDisable(true);
        btnSetupTestDir.setDisable(true);

        txtSetupKey.setText("TESTING_123_R3L1C");
        txtSetupID.setText("1718-FIM-TST");
        txtSetupDir.setText("C:\\Users\\Kyle Flynn\\Desktop\\FTC Stuff\\Scoring System");
    }

    @FXML
    public void testConnection() {
        if (txtSetupKey.getText().length() > 0 && txtSetupKey.getText().length() > 0) {
            // This will grab the base URL.
            TOAEndpoint testConnection = new TOAEndpoint("POST", "upload");
            testConnection.setCredentials(txtSetupKey.getText(), txtSetupID.getText());
            testConnection.execute((response, success) -> {
                if (success) {
                    // Setup our local config
                    Config.EVENT_API_KEY = txtSetupKey.getText();
                    Config.EVENT_ID = txtSetupID.getText();

                    sendInfo("Connection to TOA was successful. Proceed to Scoring System setup.");

                    labelSetupTest.setText("Connection Successful");
                    labelSetupTest.setTextFill(Color.GREEN);

                    txtSetupDir.setEditable(true);
                    btnSetupSelect.setDisable(false);
                    btnSetupTestDir.setDisable(false);
                } else {
                    sendError("Connection to TOA unsuccessful. " + response);

                    labelSetupTest.setText("Connection Unsuccessful.");
                    labelSetupTest.setTextFill(Color.RED);
                }
            });
        } else {
            sendError("You must enter the two fields described.");
        }
    }

    @FXML
    public void openScoringDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Scoring System Directory");
        File file = directoryChooser.showDialog(DataSync.getMainStage());

        if (file != null && file.isDirectory()) {
            txtSetupDir.setText(file.getAbsolutePath());
        } else {
            sendError("Error in selecting scoring system directory.");
        }
    }

    @FXML
    public void testDirectory() {
        if (txtSetupDir.getText().length() > 0) {
            String root = txtSetupDir.getText();
            File divisionsFile = new File(root + "\\divisions.txt");
            if (divisionsFile.exists()) {
                Config.SCORING_DIR = root;

                labelSetupDir.setTextFill(Color.GREEN);
                labelSetupDir.setText("Valid Directory.");

                sendInfo("Found divisions.txt");

                tabTeams.setDisable(false);
            } else {
                sendError("Scoring System not setup. Are you using the right directory?");
            }
        } else {
            sendError("You must select a directory for the scoring system root.");
        }
    }

    public void sendInfo(String message) {
        txtConsole.setTextFill(Color.GREEN);
        txtConsole.setText(message);
    }

    public void sendWarning(String message) {
        txtConsole.setTextFill(Color.YELLOW);
        txtConsole.setText(message);
    }

    public void sendError(String message) {
        txtConsole.setTextFill(Color.RED);
        txtConsole.setText(message);
    }

    @FXML
    public void getTeamsByURL() {
        this.teamsController.getTeamsByURL();
    }

}