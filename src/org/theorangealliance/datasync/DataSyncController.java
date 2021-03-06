package org.theorangealliance.datasync;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import org.theorangealliance.datasync.json.EventJSON;
import org.theorangealliance.datasync.logging.TOALogger;
import org.theorangealliance.datasync.models.MatchGeneral;
import org.theorangealliance.datasync.models.Team;
import org.theorangealliance.datasync.models.TeamRanking;
import org.theorangealliance.datasync.tabs.*;
import org.theorangealliance.datasync.util.Config;
import org.theorangealliance.datasync.util.TOAEndpoint;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Kyle Flynn on 11/28/2017.
 */
public class DataSyncController implements Initializable {

    /* Save file extension */
    private static final String SAVE_FILE_EXTENSION = ".txt";
    /* Default output file */
    private String saveFileName = "Settings" + SAVE_FILE_EXTENSION;

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

    /* This is for our teams tab. */
    @FXML public Tab tabTeams;
    @FXML public TableView<Team> tableTeams;
    @FXML public TableColumn colTeamsTeam;
    @FXML public TableColumn colTeamDiv;
    @FXML public TableColumn colTeamsRegion;
    @FXML public TableColumn colTeamsLeague;
    @FXML public TableColumn colTeamsShort;
    @FXML public TableColumn colTeamsLong;
    @FXML public TableColumn colTeamsLocation;
    @FXML public Button btnTeamsPost;
    @FXML public Button btnTeamsDelete;

    /* This is for our matches tab. */
    @FXML public Tab tabMatches;
    @FXML public Button btnMatchImport;
    @FXML public Button btnMatchScheduleUpload;
    @FXML public Button btnMatchUpload;
    @FXML public Button btnMatchBrowserView;
    @FXML public Button btnMatchOpen;
    @FXML public TableView<MatchGeneral> tableMatches;
    @FXML public TableColumn<MatchGeneral, String> colMatchName;
    @FXML public TableColumn<MatchGeneral, Boolean> colMatchDone;
    @FXML public TableColumn<MatchGeneral, Boolean> colMatchPosted;
    @FXML public Label labelScheduleUploaded;
    @FXML public Label labelMatchLevel;
    @FXML public Label labelMatchField;
    @FXML public Label labelMatchPlay;
    @FXML public Label labelMatchName;
    @FXML public Label labelMatchKey;
    @FXML public Label labelRedAuto;
    @FXML public Label labelRedTele;
    @FXML public Label labelRedEnd;
    @FXML public Label labelRedPenalty;
    @FXML public Label labelRedScore;
    @FXML public Label labelBlueAuto;
    @FXML public Label labelBlueTele;
    @FXML public Label labelBlueEnd;
    @FXML public Label labelBluePenalty;
    @FXML public Label labelBlueScore;
    @FXML public Label labelRedTeams;
    @FXML public Label labelBlueTeams;

    /* This is for our rankings tab. */
    @FXML public Tab tabRankings;
    @FXML public TableView<TeamRanking> tableRankings;
    @FXML public TableColumn<TeamRanking, Integer> colRank;
    @FXML public TableColumn<TeamRanking, Integer> colRankTeam;
    @FXML public TableColumn<TeamRanking, Integer> colRankWins;
    @FXML public TableColumn<TeamRanking, Integer> colRankLosses;
    @FXML public TableColumn<TeamRanking, Integer> colRankTies;
    @FXML public TableColumn<TeamRanking, Integer> colRankQP;
    @FXML public TableColumn<TeamRanking, Integer> colRankRP;
    @FXML public TableColumn<TeamRanking, Integer> colRankScore;
    @FXML public TableColumn<TeamRanking, Integer> colRankPlayed;
    @FXML public Button btnRankUpload;

    /* This is for our alliances tab */
    @FXML public Tab tabAllianceSelection;
    @FXML public Label labelRedFinals;
    @FXML public Label labelBlueFinals;
    @FXML public Label labelRedFirstSemis;
    @FXML public Label labelBlueFirstSemis;
    @FXML public Label labelRedSecondSemis;
    @FXML public Label labelBlueSecondSemis;
    @FXML public Button btnAllianceImportScoring;
    @FXML public Button btnAllianceImportTOA;
    @FXML public Button btnUploadAlliances;
    @FXML public Button btnPurgeAlliances;

    /* This is for our awards tab */
    @FXML public Tab tabAwards;
    @FXML public Label labelWinnerNumber;
    @FXML public Label labelSecondPlaceNumber;
    @FXML public Label labelThirdPlaceNumber;
    @FXML public Label labelWinnerName;
    @FXML public Label labelSecondPlaceName;
    @FXML public Label labelThirdPlaceName;
    @FXML public Label labelAwardDescription;
    @FXML public Label labelAwardComments;
    @FXML public ToggleGroup awardsGroup;
    @FXML public RadioButton radioInspire;
    @FXML public RadioButton radioThink;
    @FXML public RadioButton radioConnect;
    @FXML public RadioButton radioInnovate;
    @FXML public RadioButton radioDesign;
    @FXML public RadioButton radioMotivate;
    @FXML public RadioButton radioControl;
    @FXML public RadioButton radioJudges;
    @FXML public RadioButton radioCompass;
    @FXML public RadioButton radioPromote;
    @FXML public RadioButton radioDeansFinal;
    @FXML public RadioButton radioDeansSemi;

    /* This is for our advancement tab */
    @FXML public Tab tabAdvancement;

    /* This is for our sync tab. */
    @FXML public Tab tabSync;
    @FXML public Button btnSyncStart;
    @FXML public Button btnSyncStop;
    @FXML public CheckBox btnSyncMatches;

    /* Instances of our tab controllers. */
    private TeamsController teamsController;
    private MatchesController matchesController;
    private RankingsController rankingsController;
    private SyncController syncController;
    private AlliancesController alliancesController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.teamsController = new TeamsController(this);
        this.matchesController = new MatchesController(this);
        this.rankingsController = new RankingsController(this);
        this.syncController = new SyncController(this);
        this.alliancesController = new AlliancesController(this);

        labelSetupTest.setTextFill(Color.RED);
        labelSetupDir.setTextFill(Color.RED);


        if(!Config.BETA_TESTING){

            txtSetupDir.setEditable(false);
            btnSetupSelect.setDisable(true);
            btnSetupTestDir.setDisable(true);

        }else{

            enableAllWindows();

        }

        //Add all the

        readSettings();

    }

    @FXML
    public void openDocs() {
        try {
            Desktop.getDesktop().browse(new URI("https://drive.google.com/open?id=1fSdBXXRTmZvWZXSr1ieZB4vJNWNUefXYdbUPU6TyhsM"));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    @FXML
    public void testConnection() {
        if (txtSetupKey.getText().length() > 0 && txtSetupID.getText().length() > 0) {
            saveSettings();
            // This will grab the base URL.
            TOAEndpoint testConnection = new TOAEndpoint("GET", "event/" + txtSetupID.getText());
            testConnection.setCredentials(txtSetupKey.getText(), txtSetupID.getText());
            testConnection.execute((response, success) -> {
                if (success) {
                    // Setup our local config
                    Config.EVENT_API_KEY = txtSetupKey.getText();
                    Config.EVENT_ID = txtSetupID.getText();

                    EventJSON[] events = testConnection.getGson().fromJson(response, EventJSON[].class);
                    if (events.length > 0 && events[0] != null) {
                        if (events[0].getDivisionName() != null || events[0].getDivisionKey() > 0) {
                            Config.DUAL_DIVISION_EVENT = true;
                            sendInfo("Connection to TOA was successful. DETECTED DUAL DIVISION EVENT. Proceed to Scoring System setup.");
                        } else {
                            sendInfo("Connection to TOA was successful. Proceed to Scoring System setup.");
                        }
                    } else {
                        sendInfo("Connection to TOA was successful. Proceed to Scoring System setup.");
                    }

                    labelSetupTest.setText("Connection Successful");
                    labelSetupTest.setTextFill(Color.GREEN);

                    txtSetupDir.setEditable(true);
                    btnSetupSelect.setDisable(false);
                    btnSetupTestDir.setDisable(false);

                    matchesController.checkMatchSchedule();
                    matchesController.checkMatchDetails();
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
            saveSettings();
            String root = txtSetupDir.getText();
            File divisionsFile = new File(root + File.separator + "divisions.txt");
            if (divisionsFile.exists()) {
                Config.SCORING_DIR = root;

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(divisionsFile));
                    reader.readLine();
                    String line = reader.readLine();
                    Config.EVENT_NAME = line;
                    while ((line = reader.readLine()) != null) {
                        if (line.split("\\|").length > 3) {
                            Config.DIVISION_NAME = line.split("\\|")[1];
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                labelSetupDir.setTextFill(Color.GREEN);
                labelSetupDir.setText("Valid Directory.");

                sendInfo("Found divisions.txt");
                tabTeams.setDisable(false);
                tabMatches.setDisable(false);
                tabRankings.setDisable(false);
                tabSync.setDisable(false);
                tabAllianceSelection.setDisable(false);
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

    /* Teams Tab Functions */

    @FXML
    public void getTeamsByURL() {
        this.teamsController.getTeamsByURL();
    }

    @FXML
    public void getTeamsByFile() {
        this.teamsController.getTeamsByFile();
    }

    @FXML
    public void postEventTeams() {
        this.teamsController.postEventTeams();
    }

    @FXML
    public void deleteEventTeams() {
        this.teamsController.deleteEventTeams();
    }

    /* Matches Tab Functions */

    @FXML
    public void getMatchesByFile() {
        this.matchesController.getMatchesByFile();
    }

    @FXML
    public void postMatchSchedule() {
        this.matchesController.postMatchSchedule();
    }

    @FXML
    public void postSelectedMatch() {
        this.matchesController.postSelectedMatch();
    }

    @FXML
    public void viewFromTOA(){
        this.matchesController.viewFromTOA();
    }

    @FXML
    public void openMatchDetails() {
        this.matchesController.openMatchDetails();
    }

    @FXML
    public void deleteMatches(){
        this.matchesController.deleteMatches();
    }

    /* Sync Tab Functions */

    @FXML
    public void startAutoSync() {
        DataSync.getMainStage().setOnCloseRequest(closeEvent -> this.syncController.kill());
        this.syncController.execute((count) -> Platform.runLater(() -> {
            Date date = new Date();
            TOALogger.log(Level.INFO, "Executing update #" + count + " at " + DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
            TOALogger.log(Level.INFO, "There are " + Thread.activeCount() + " threads.");
            this.matchesController.syncMatches();
            this.matchesController.checkMatchSchedule();
            this.matchesController.checkMatchDetails();
            this.rankingsController.getRankingsByFile();
            this.rankingsController.postRankings();
            if (this.btnSyncMatches.selectedProperty().get()) {
                 this.matchesController.postCompletedMatches();
            }
        }));
    }

    @FXML
    public void stopAutoSync() {
        this.syncController.kill();
    }

    /* Rankings Tab Functions */

    @FXML
    public void getRankingsByFile() {
        this.rankingsController.getRankingsByFile();
    }

    @FXML
    public void postRankings() {
        this.rankingsController.postRankings();
    }

    @FXML
    public void deleteRankings() {
        this.rankingsController.deleteRankings();
    }

    /* Alliances Tab Functions */

    @FXML
    public void importAlliancesScoring(){
        this.alliancesController.importAlliancesScoring(this.matchesController.getMatchDetails());
    }

    @FXML
    public void importAlliancesTOA(){
        this.alliancesController.importAlliancesTOA(this.matchesController.getMatchDetails());
    }

    @FXML
    public void uploadAlliances(){
        this.alliancesController.uploadAlliances();
    }

    @FXML
    public void purgeAlliances(){
        this.alliancesController.purgeAlliances();
    }

    public HashMap<Integer, int[]> getTeamWL() {
        return this.matchesController.getTeamWL();
    }

    /* Outputs the current settings to the file specified by saveFileName (Default Settings.SAVE_FILE_EXTENSION) */
    private void saveSettings(){

        try (PrintWriter out = new PrintWriter(saveFileName)){

            out.println("Key:" + txtSetupKey.getText());
            out.println("ID:" + txtSetupID.getText());
            out.println("Directory:" + txtSetupDir.getText());

            out.close();

        }catch (FileNotFoundException e){

            TOALogger.log(Level.WARNING, "Error saving settings");

        }

    }

    /* Searches for files in the active directory with the proper extension, and then prompts the user for which one */
    private void readSettings(){

        //Check for saved settings
        try {
            ArrayList<String> files = new ArrayList<>();
            //Looks for files
            for(File file : new File(System.getProperty("user.dir")).listFiles()) {
                String name = file.getName();
                if (file.isFile() //Checks that the file is not a directory
                        && name.substring(name.length() - SAVE_FILE_EXTENSION.length(), name.length()).equals(SAVE_FILE_EXTENSION)) { //Ends with proper file type
                    files.add(name);
                }
            }

            //If it found any files, will prompt the user for which one
            if(files.size() > 0) {

                String file = promptForSaveFile(files);

                if(file != null){

                    saveFileName = file;

                    Scanner scan = new Scanner(new File(saveFileName));
                    while (scan.hasNextLine()) {
                        String[] line = scan.nextLine().split(":", 2);

                        if (line.length > 1) {
                            if (line[0].equalsIgnoreCase("Key")) {

                                txtSetupKey.setText(line[1]);

                            } else if (line[0].equalsIgnoreCase("ID")) {

                                txtSetupID.setText(line[1]);

                            } else if (line[0].equalsIgnoreCase("Directory")) {

                                txtSetupDir.setText(line[1]);
                                txtSetupDir.setEditable(false);

                            }
                        }
                    }
                }
            }

        } catch (FileNotFoundException e){
            /* Error opening file, continue with defaults */
        }
    }

    /* Prompts the user to select a file from the list, or run without a file */
    private String promptForSaveFile(ArrayList<String> files){

        //New pane without text
        JOptionPane fileSelector = new JOptionPane("");

        //Buttons
        fileSelector.setOptions(new String[] {"Select File", "Run without a file"});

        //Dropdown menu
        fileSelector.setSelectionValues(files.toArray());

        //Makes and shows the window
        JDialog window = fileSelector.createDialog(null, "Select a Settings File");
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window.setVisible(true);

        //returns the selected value if "Select File" was pressed, null otherwise
        try {
            return fileSelector.getValue().equals("Select File") ? (String) fileSelector.getInputValue() : null;
        }catch (NullPointerException e){//When the user closes the window
            System.exit(0);
        }

        //We don't go here
        return null;

    }

    /* Enables all windows, only used when BETA testing */
    private void enableAllWindows(){

        tabAdvancement.setDisable(false);
        tabAllianceSelection.setDisable(false);
        tabAwards.setDisable(false);
        tabMatches.setDisable(false);
        tabRankings.setDisable(false);
        tabSetup.setDisable(false);
        tabSync.setDisable(false);
        tabTeams.setDisable(false);

    }
}
