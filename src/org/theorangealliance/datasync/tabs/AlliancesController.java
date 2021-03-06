package org.theorangealliance.datasync.tabs;

import org.theorangealliance.datasync.DataSyncController;
import org.theorangealliance.datasync.json.MatchDetailRelicJSON;
import org.theorangealliance.datasync.logging.TOALogger;
import org.theorangealliance.datasync.models.Alliance;
import org.theorangealliance.datasync.models.MatchGeneral;
import org.theorangealliance.datasync.util.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.logging.Level;

public class AlliancesController {

    private DataSyncController controller;
    private Alliance[] alliances;

    public AlliancesController(DataSyncController instance){

        this.controller = instance;

    }

    public void importAlliancesScoring(HashMap<MatchGeneral, MatchDetailRelicJSON> scores){
        File allianceFile = new File(Config.SCORING_DIR + File.separator + "alliances.txt");
        if (allianceFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(allianceFile));
                String line;
                alliances = new Alliance[4];
                while ((line = reader.readLine()) != null) {
                    /* Alliance info */
                    String[] allianceInfo = line.split("\\|");
                    int division = Integer.parseInt(allianceInfo[0]);
                    int allianceNumber = Integer.parseInt(allianceInfo[1]);
                    int[] allianceNumbers = {Integer.parseInt(allianceInfo[3]), Integer.parseInt(allianceInfo[4]), Integer.parseInt(allianceInfo[5])};
                    alliances[allianceNumber-1] = new Alliance(division, allianceNumber, allianceNumbers);
                }
                reader.close();
                /* TODO - Make Upload Alliances so we can uncomment this
                controller.btnUploadAlliances.setDisable(false);*/
                updateAllianceLabels(scores);
                TOALogger.log(Level.INFO, "Alliance import successful.");
            } catch (Exception e) {
                e.printStackTrace();
                controller.sendError("Could not open file. " + e.getLocalizedMessage());
            }
        } else {
            controller.sendError("Could not locate alliances.txt from the Scoring System. Did you generate an elimination bracket?");
        }
    }

    public void importAlliancesTOA(HashMap<MatchGeneral, MatchDetailRelicJSON> scores){

        //TODO - This (May need the API updates from below to test)
        updateAllianceLabels(scores);

    }

    public void uploadAlliances(){
        // TODO - Waiting on API updates
    }

    public void purgeAlliances(){
        // TODO - Waiting on API updates
    }

    private void updateAllianceLabels(HashMap<MatchGeneral, MatchDetailRelicJSON> scores){

        int redS1 = 0, blueS1 = 0, redS2 = 0, blueS2 = 0;

        for(MatchGeneral match : scores.keySet()){
            String[] slots;
            if((slots = match.getMatchName().split(" ")).length == 4) {
                if (slots[1].equals("1") && match.getRedScore() != match.getBlueScore()) {
                    if (match.getRedScore() > match.getBlueScore()) {
                        redS1++;
                    } else {
                        blueS1++;
                    }
                } else if (slots[1].equals("2") && match.getRedScore() != match.getBlueScore()) {
                    if (match.getRedScore() > match.getBlueScore()) {
                        redS2++;
                    } else {
                        blueS2++;
                    }
                }
            }

        }

        String[] allianceStrings = new String[4];
        for(int i = 0; i < 4; i++){
            allianceStrings[i] = ("" + (i + 1)) + ": " + alliances[i].getAllianceNumbers()[0] + ", "  + alliances[i].getAllianceNumbers()[1] + ((alliances[i].getAllianceNumbers()[2] > 0) ? ", "  + alliances[i].getAllianceNumbers()[2]:"");
        }

        controller.labelRedFirstSemis.setText(allianceStrings[0]);
        controller.labelBlueFirstSemis.setText(allianceStrings[3]);
        controller.labelRedSecondSemis.setText(allianceStrings[1]);
        controller.labelBlueSecondSemis.setText(allianceStrings[2]);

        controller.labelRedFinals.setText(redS1 == 2 ? allianceStrings[0]: blueS1 == 2 ? allianceStrings[3] : "");
        controller.labelBlueFinals.setText(redS2 == 2 ? allianceStrings[1]: blueS2 == 2 ? allianceStrings[2] : "");

    }

}
