package com.golfpvcc.teamscore.Player;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.golfpvcc.teamscore.Database.PlayerRecord;
import com.golfpvcc.teamscore.Database.RealmScoreCardAccess;
import com.golfpvcc.teamscore.DisplayScoreCardDetail;
import com.golfpvcc.teamscore.R;
import com.golfpvcc.teamscore.Team.TeamPlayerScoreData;
import com.golfpvcc.teamscore.Team.TeamScoreTotals;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_9_GAME;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_GROSS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_NET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_POINT_QUOTA;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_STABLEFORD;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DOUBLE_TEAM_SCORE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.JUST_RAW_SCORE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NINETH_HOLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_ALBATROSS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_BIRDEIS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_BOGGY;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_DOUBLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_EAGLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_OTHER;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_PAR;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_TARGET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREENHOLES;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_BIRDIE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_DOUBLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_EAGLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_END;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_OTHER;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_PAR;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_PTS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_QUOTA;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_SCORE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_STABLEFORD;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM__BOGGEY;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.TEAM_GROSS_SCORE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.TEAM_NET_SCORE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.TEAM_SCORE;

/*
This class is a rewrite of the "class DisplayPlayerHoleData" The new class is broken down into two class now, player's class and team player's class.
There is one class for each player, the class holds the net, gross,  point quote and Team Player Score Data class. The class is used to populate the score card for
each player and keep track if the team used this player's score for the team game.
 */
public class DisplayPlayerScoreData extends TeamPlayerScoreData {
    private PlayerRecord m_PlayerDataBaseRecord;
    private Context m_Context;        // view port
    private int m_PlayerHandicap;                // player's handicap
    private byte m_CurrentHoleTeamMask;
    private float m_PoinQuotaTargetValue;
    private int[] m_PointQuotaArray, m_SummaryStokeScore;
    private HoleStokeDetails[] m_HoleDetails;
    private TextView m_tvPlayerName, m_tvTotal, m_tvCurrentHoleScore, m_tvPlayerNameDataEntry;

    /*
    Constructor
     */
    public DisplayPlayerScoreData(Context context, int[] PointQuota, TextView[] tvCells, PlayerRecord PlayerDatabaseRec) {
        int ScreenCellInx = 0, FrontNineInx, BackNineInx = NINETH_HOLE;
        String PlayerName;

        m_Context = context;
        m_CurrentHoleTeamMask = 0;
        m_PointQuotaArray = PointQuota;
        m_PlayerDataBaseRecord = PlayerDatabaseRec;        // access into the database for this player name, scores and handicap
        m_PlayerHandicap = m_PlayerDataBaseRecord.getM_Handicap();
        m_PoinQuotaTargetValue = m_PlayerHandicap - m_PointQuotaArray[PQ_TARGET];  // player's total score will start off with a negative number (Handicap - Target) i.e.  19 - 36 = -19.

        m_HoleDetails = new HoleStokeDetails[HOLES_18];         // Declare an array of classes to hold HoleStokeDetails
        m_tvPlayerName = tvCells[ScreenCellInx++];            // Player' name, [0]

        for (FrontNineInx = 0; FrontNineInx < SCREENHOLES; FrontNineInx++, BackNineInx++, ScreenCellInx++) { // the screen only needs 9 text views, the players 18 hole data will use the same text views for hole 1 & 10

            m_HoleDetails[FrontNineInx] = new HoleStokeDetails(tvCells[ScreenCellInx]);  // The TV for the player's score on the card are zero based
            m_HoleDetails[BackNineInx] = new HoleStokeDetails(tvCells[ScreenCellInx]);  // cell zero is the player's name, Text Views are for the screen score card - only need 9 of them 1-9 and 10-18
        }
        m_tvTotal = tvCells[ScreenCellInx];       // text view for the total score [10]

        PlayerName = m_PlayerDataBaseRecord.get_PlayerName();
        m_tvPlayerName.setText(PlayerName);
    }

    /*
    This function used by the Main Summary Activity to calculate play's scores.
     */
    public DisplayPlayerScoreData(Context context, int[] PointQuota, PlayerRecord PlayerDatabaseRec, int[] CoursePar, int[] courseHandicap) {

        int CourseHandicapForHole;
        m_Context = context;
        m_PointQuotaArray = PointQuota;
        m_PlayerDataBaseRecord = PlayerDatabaseRec;        // access into the database for this player name, scores and handicap
        m_HoleDetails = new HoleStokeDetails[HOLES_18];         // Declare an array of classes to hold HoleStokeDetails
        for (int Hole = 0; Hole < HOLES_18; Hole++) {
            m_HoleDetails[Hole] = new HoleStokeDetails();       // declare hole class
        }

        m_PlayerHandicap = m_PlayerDataBaseRecord.getM_Handicap();
        m_PoinQuotaTargetValue = m_PlayerHandicap - m_PointQuotaArray[PQ_TARGET];  // player's total score will start off with a negative number (Handicap - Target) i.e.  19 - 36 = -19.

        m_SummaryStokeScore = new int[SUM_END]; // hold the player's golf summary - "Score", "Quota", "Eagle", "Birdies", " Pars", "Bog", "Dbl", "Othr", "9 Pts"
        for (int i = 0; i < SUM_END; i++) {
            m_SummaryStokeScore[i] = 0;         // clear array
        }

        for (int Hole = 0; Hole < HOLES_18; Hole++) {
            CourseHandicapForHole = courseHandicap[Hole];
            setStrokeHoles(Hole, CourseHandicapForHole);      // the hole with a strokes
            setTeamTheParForEachHole(Hole, (byte) CoursePar[Hole]); // used by team scoring
        }

        TeamClearHoleMask();          // set  variables to zero
        LoadPlayerScoresIntoHoleClass();            // Load the player's scores from the database
        LoadPlayerQuotaScores();                    // calculate quota scores
    }

    /*
    This function will load the saved player's score into the HoleStokeDetails class
     */
    public void LoadPlayerScoresIntoHoleClass() {
        int Hole;
        byte PlayerScore, TeamMask;
        byte[] mByteScore = m_PlayerDataBaseRecord.getmByteScore();     // read the database record for the player's score, this record is zero based

        for (Hole = 0; Hole < HOLES_18; Hole++) {
            PlayerScore = (byte) (JUST_RAW_SCORE & mByteScore[Hole]);
            m_HoleDetails[Hole].setPlayerGrossScore(PlayerScore);
            TeamMask = (byte) (TEAM_SCORE & mByteScore[Hole]);
            m_HoleDetails[Hole].setTeamHoleMask(TeamMask);
        }
    }

    /*
    This function will load the player's quota score into Holes Class on start up.
     */
    public void LoadPlayerQuotaScores() {
        byte GrossScore, NetScore, ParForHole, PointQuotaScore, StablefordScore;

        for (int Hole = 0; Hole < HOLES_18; Hole++) {

            GrossScore = (byte) m_HoleDetails[Hole].getGrossScore();
            if (0 < GrossScore) {
                ParForHole = GetTeamTheParForThisHole(Hole);

                PointQuotaScore = (byte) (GrossScore - ParForHole);     // point quote
                PointQuotaScore = DeterminePointScoreQuotaScore(PointQuotaScore);
                m_HoleDetails[Hole].setQuotaHoleScore(PointQuotaScore);

                NetScore = (byte) m_HoleDetails[Hole].getNetScore();    // stable ford scoring, use the net score
                StablefordScore = (byte) (NetScore - ParForHole);
                StablefordScore = DeterminePointScoreQuotaScore(StablefordScore);
                m_HoleDetails[Hole].setStablefordHoleScore(StablefordScore);
            }
        }
    }

    /*
    This function will determine what type of high light box will be used on the score card.  The drawable files must have the background color for the text view in the file,
    therefore app need six different file to cover all of the case.
     */
    private void SetTeamBoxHighLightOnScoreCard(int currentHole) {
        byte TeamMask, ShotPerHole;
        int ID_BoxHighLight = 0;

        TeamMask = m_HoleDetails[currentHole].getTeamHoleMask();
        ShotPerHole = m_HoleDetails[currentHole].getNumberOfShotsForThisHole();

        switch (TeamMask) {
            case (TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE):
                ID_BoxHighLight = DisplayGrossDoubleBox(ShotPerHole);
                break;

            case (TEAM_NET_SCORE + DOUBLE_TEAM_SCORE):
                ID_BoxHighLight = DisplayNetDoubleBox(ShotPerHole);
                break;

            case TEAM_GROSS_SCORE:
                ID_BoxHighLight = DisplayGrossBox(ShotPerHole);
                break;

            case TEAM_NET_SCORE:
                ID_BoxHighLight = DisplayNetBox(ShotPerHole);
                break;
        }

        if (0 < ID_BoxHighLight)
            m_HoleDetails[currentHole].m_tvScoreCell.setBackground(ContextCompat.getDrawable(m_Context, ID_BoxHighLight));
    }

    /*
     *********************** Here are the function for the lower half of the screen ********************
     */
/*
This function saves the text view for the lower half of the screen data entry - This is the current score to save when the next/prev button are pressed.
 */
    public void setTvPlayerLowerCurScore(TextView tvCurrentHoleScore) {
        m_tvCurrentHoleScore = tvCurrentHoleScore;
    }

    /*
    This function will return the text view of the player current hole score on the lower half of the screen
     */
    public TextView getTvPlayerLowerCurScore() {
        return m_tvCurrentHoleScore;
    }

    /*
This function will save the Text View for the player's name on the lower half of the data entry part of the screen
 */
    public void setTvPlayerLowerName(TextView tv_tmp) {
        m_tvPlayerNameDataEntry = tv_tmp;
    }

    /*
    This function will return the text view of the player name on the lower half of the screen
     */
    public TextView getTvPlayerLowerName() {
        return m_tvPlayerNameDataEntry;
    }

    /*
This function will add the golfer name to the lower part of the score card.
 */
    public void AddNameToScoreCar() {
        String PlayerName;
        PlayerName = m_PlayerDataBaseRecord.get_PlayerName();
        m_tvPlayerNameDataEntry.setText(m_PlayerHandicap + " - " + PlayerName);
    }

    /*
    This function will add a stroke to the current player's score - Will stop after 9 strokes on a hole
    mTvPlayerCurScore.setText(Integer.toString(mIntPlayer_CurrentHoleScore));           // just display the current hole score on the screen
     */
    public void AddStrokeToCurrentScore() {
        int intCurrentScore;

        intCurrentScore = GetCurrentPlayerLowerScore();
        if (intCurrentScore < 9)
            intCurrentScore++;
        SetCurrentPlayerLowerScore(intCurrentScore);
    }

    /*
    This function will subtract a stroke to the current player's score - Will stop after 1 strokes on a hole
     */
    public void SubtractStrokeToCurrentScore() {
        int intCurrentScore;
        intCurrentScore = GetCurrentPlayerLowerScore();
        if (1 < intCurrentScore)
            intCurrentScore--;
        SetCurrentPlayerLowerScore(intCurrentScore);
    }

    /*
    This function will get the player score from the lower have of the screen
     */
    private int GetCurrentPlayerLowerScore() {
        String strCurrentScore;
        int intCurrentScore;

        strCurrentScore = m_tvCurrentHoleScore.getText().toString();
        intCurrentScore = Integer.parseInt(strCurrentScore);

        return intCurrentScore;
    }

    /*
    This function will save the player's current score into the database
     */
    public void SavePlayerHoleScoreToDatabase(RealmScoreCardAccess realmScoreCardAccess, int CurrentHole) {
        byte CurrentHoleScore, NetScore, ParForHole, PointQuotaScore;

        CurrentHoleScore = (byte) GetCurrentPlayerLowerScore();          // read the text view of the lower score entry and converts the string to an int
        m_HoleDetails[CurrentHole].setPlayerGrossScore(CurrentHoleScore);

        ParForHole = GetTeamTheParForThisHole(CurrentHole);
        // save the point quota score
        PointQuotaScore = (byte) (CurrentHoleScore - ParForHole);
        PointQuotaScore = DeterminePointScoreQuotaScore(PointQuotaScore);
        m_HoleDetails[CurrentHole].setQuotaHoleScore(PointQuotaScore);

        // save the Stableford score
        NetScore = (byte) m_HoleDetails[CurrentHole].getNetScore();
        PointQuotaScore = (byte) (NetScore - ParForHole);
        PointQuotaScore = DeterminePointScoreQuotaScore(PointQuotaScore);
        m_HoleDetails[CurrentHole].setStablefordHoleScore(PointQuotaScore);

        CurrentHoleScore += m_HoleDetails[CurrentHole].getTeamHoleMask();

        realmScoreCardAccess.SaveThePlayerHoleScore(m_PlayerDataBaseRecord, CurrentHole, CurrentHoleScore); // save the score
    }

    /*
    This function will handle the user selecting a team gross score using the lower half of the screen by clicking on the players name
     */
    public void SetTeamGrossScore(int CurrentHole) {
        int TeamRealScoreMask;
        String TeamScoreColor;

        setPlayerMaskToDefaultColor();      // clear the screen high light for hole and player's name
        TeamRealScoreMask = m_HoleDetails[CurrentHole].getTeamHoleMask();

        if (TEAM_GROSS_SCORE == TeamRealScoreMask) {
            m_HoleDetails[CurrentHole].setTeamHoleMask((byte) 0);   // clear the flag
        } else {
            m_HoleDetails[CurrentHole].setTeamHoleMask((byte) TEAM_GROSS_SCORE);
            TeamScoreColor = m_Context.getString(Integer.parseInt(String.valueOf(R.color.team_box_high_light_gross_used_once)));    // set the current hole back to the default color
            m_tvCurrentHoleScore.setBackgroundColor(Color.parseColor(TeamScoreColor));     // high light the player current score
        }
    }

    /*
    This function will handle the user selecting a team double gross score using the lower half of the screen by long clicking on the players name
     */
    public void SetTeamDoubleGrossScore(int CurrentHole) {
        int TeamRealScoreMask;
        String TeamScoreColor;

        setPlayerMaskToDefaultColor();      // clear the screen high light for hole and player's name
        TeamRealScoreMask = m_HoleDetails[CurrentHole].getTeamHoleMask();

        if ((TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE) == TeamRealScoreMask) {
            m_HoleDetails[CurrentHole].setTeamHoleMask((byte) 0);   // clear the flag
        } else {
            m_HoleDetails[CurrentHole].setTeamHoleMask((byte) (TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE));
            TeamScoreColor = m_Context.getString(Integer.parseInt(String.valueOf(R.color.team_box_high_light_gross_twice)));    // set the current hole back to the default color
            m_tvCurrentHoleScore.setBackgroundColor(Color.parseColor(TeamScoreColor));     // high light the player current score
        }
    }

    /*
    This function will handle the user selecting a team net score using the lower half of the screen by clicking on the players current score
     */
    public void SetTeamNetScore(int CurrentHole) {
        int TeamRealScoreMask;
        String TeamScoreColor;

        setPlayerMaskToDefaultColor();      // clear the screen high light for hole and player's name
        TeamRealScoreMask = m_HoleDetails[CurrentHole].getTeamHoleMask();

        if (TEAM_NET_SCORE == TeamRealScoreMask) {
            m_HoleDetails[CurrentHole].setTeamHoleMask((byte) 0);   // clear the flag
        } else {
            m_HoleDetails[CurrentHole].setTeamHoleMask((byte) TEAM_NET_SCORE);
            TeamScoreColor = m_Context.getString(Integer.parseInt(String.valueOf(R.color.team_box_high_light_net_used_once)));    // set the current hole back to the default color
            m_tvPlayerNameDataEntry.setBackgroundColor(Color.parseColor(TeamScoreColor));     // high light the player current score
        }
    }

    /*
    This function will handle the user selecting a team doublenet score using the lower half of the screen by long clicking on the players current score
     */
    public void SetTeamDoubleNetScore(int CurrentHole) {
        int TeamRealScoreMask;
        String TeamScoreColor;

        setPlayerMaskToDefaultColor();      // clear the screen high light for hole and player's name
        TeamRealScoreMask = m_HoleDetails[CurrentHole].getTeamHoleMask();

        if ((TEAM_NET_SCORE + DOUBLE_TEAM_SCORE) == TeamRealScoreMask) {
            m_HoleDetails[CurrentHole].setTeamHoleMask((byte) 0);   // clear the flag
        } else {
            m_HoleDetails[CurrentHole].setTeamHoleMask((byte) (TEAM_NET_SCORE + DOUBLE_TEAM_SCORE));
            TeamScoreColor = m_Context.getString(Integer.parseInt(String.valueOf(R.color.team_box_high_light_net_twice)));    // set the current hole back to the default color
            m_tvPlayerNameDataEntry.setBackgroundColor(Color.parseColor(TeamScoreColor));     // high light the player current score
        }
    }

    /*
  This function will take the score from the score card and load the lower entry data for the player - if the player score is zero then use the par for the hole
  Previous button was clicked, Set the player current score high light - ie is this score used by the team in a gross or net game.
   */
    public void MoveNextHoleScoreToLowerHalfOfScreen(int CurrentHole) {
        int TeamRealScoreMask, HoleScore;
        String TeamScoreColor, strHoleScore;

        HoleScore = m_HoleDetails[CurrentHole].getGrossScore();
        if (HoleScore == 0) {
            HoleScore = GetTeamTheParForThisHole(CurrentHole);      // set the player hole score to Par
        }
        strHoleScore = "" + HoleScore;
        m_tvCurrentHoleScore.setText(strHoleScore);

        TeamRealScoreMask = m_HoleDetails[CurrentHole].getTeamHoleMask();

        switch (TeamRealScoreMask) {
            case (TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE):
                TeamScoreColor = m_Context.getString(Integer.parseInt(String.valueOf(R.color.team_box_high_light_gross_twice)));    // set the current hole back to the default color
                break;

            case (TEAM_NET_SCORE + DOUBLE_TEAM_SCORE):
                TeamScoreColor = m_Context.getString(Integer.parseInt(String.valueOf(R.color.team_box_high_light_net_twice)));    // set the current hole back to the default color
                break;

            case TEAM_GROSS_SCORE:
                TeamScoreColor = m_Context.getString(Integer.parseInt(String.valueOf(R.color.team_box_high_light_gross_used_once)));    // set the current hole back to the default color
                break;

            case TEAM_NET_SCORE:
                TeamScoreColor = m_Context.getString(Integer.parseInt(String.valueOf(R.color.team_box_high_light_net_used_once)));    // set the current hole back to the default color
                break;
            default:
                TeamScoreColor = m_Context.getString(Integer.parseInt(String.valueOf(R.color.default_hole_color)));    // set the current hole back to the default color
                break;
        }
        if ((TeamRealScoreMask & TEAM_NET_SCORE) == TEAM_NET_SCORE) {
            m_tvPlayerNameDataEntry.setBackgroundColor(Color.parseColor(TeamScoreColor));     // high light the player name score
        } else {
            m_tvCurrentHoleScore.setBackgroundColor(Color.parseColor(TeamScoreColor));     // high light the player current score
        }
    }

    /*
    This function will clear the lower half of the screen player's name and current hole score back to the default color
     */
    public void setPlayerMaskToDefaultColor() {
        m_tvPlayerNameDataEntry.setBackgroundColor(Color.WHITE);     // set the gross score on the card
        m_tvCurrentHoleScore.setBackgroundColor(Color.WHITE);       // set the net score on the card
    }

    /*
    This function will Set the player score in the lower have of the screen
     */
    private void SetCurrentPlayerLowerScore(int intCurrentScore) {

        m_tvCurrentHoleScore.setText(Integer.toString(intCurrentScore));
    }

    /*
    This function will set the player hole class to the number of strokes the will get for this hole
     */
    public void setStrokeHoles(int Hole, int courseHandicapForHole) {
        byte ShotGivenForHole = 0;

        if (courseHandicapForHole <= m_PlayerHandicap) {
            ShotGivenForHole = 1;
            if (HOLES_18 < m_PlayerHandicap) {
                if (courseHandicapForHole <= (m_PlayerHandicap - HOLES_18))   // check for two stokes a hole
                    ShotGivenForHole = 2;
            }
        }
        m_HoleDetails[Hole].setStrokeForHole(ShotGivenForHole);    // set the current hole for the back ground color
    }

    /*
    This function will paint the score card with player's stroke holes for the front or back nine holes
     */
    public void SetBackgroundColorForStrokeHoles(DisplayScoreCardDetail.WhatNineIsDisplayed DisplayFrontOrBackScoreCard, int displayMode) {
        String color;
        long MyColor, DigitTextColor;
        int StartHole = 0, ScoreValue;
        byte ParForHole;

        if (DisplayFrontOrBackScoreCard == DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY) {
            StartHole = NINETH_HOLE;
        }

        for (int ScreenHole = 0; ScreenHole < SCREENHOLES; ScreenHole++) {
            MyColor = m_HoleDetails[StartHole].getStrokeBackgroundColor();
            color = m_Context.getString(Integer.parseInt(String.valueOf(MyColor)));    // set the current hole back to the color
            m_HoleDetails[StartHole].m_tvScoreCell.setBackgroundColor(Color.parseColor(color));

            ParForHole = GetTeamTheParForThisHole(StartHole);
            ScoreValue = m_HoleDetails[StartHole].DisplayScoreCardScore(displayMode, ParForHole);         // display the player's score if hole was played otherwize display space.
            DigitTextColor = getScoreCardNumberColor(ScoreValue);
            color = m_Context.getString(Integer.parseInt(String.valueOf(DigitTextColor)));    // set the current hole back to the color
            m_HoleDetails[StartHole].m_tvScoreCell.setTextColor(Color.parseColor(color));

            SaveTeamCurrentHoleScore(StartHole, displayMode);
            SetTeamBoxHighLightOnScoreCard(StartHole);           // set the score card high lights

            StartHole++;        // next hole on the score card ??
        }
    }

    /*
    This function will save the team score dependant on what the display is current to the user.
     */
    private void SaveTeamCurrentHoleScore(int currentHole, int displayMode) {
        byte TeamHoleMask, ScoreToSave = 0, GrossScore, ParForHole;

        GrossScore = (byte) m_HoleDetails[currentHole].getGrossScore();
        TeamHoleMask = m_HoleDetails[currentHole].getTeamHoleMask();
        ParForHole = GetTeamTheParForThisHole(currentHole);

        switch (displayMode) {
            case DISPLAY_MODE_GROSS:    // this displays as team over/under on the card
                ScoreToSave = (byte) (GrossScore - ParForHole);
                TeamSaveGrossNetTeamScore(currentHole, ScoreToSave, TeamHoleMask, m_HoleDetails[currentHole].getNumberOfShotsForThisHole());
                break;

            case DISPLAY_MODE_NET:      // this displays as team score on the card
                ScoreToSave = GrossScore;
                TeamSaveGrossNetTeamScore(currentHole, ScoreToSave, TeamHoleMask, m_HoleDetails[currentHole].getNumberOfShotsForThisHole());
                break;

            case DISPLAY_MODE_POINT_QUOTA:
                ScoreToSave = m_HoleDetails[currentHole].getQuotaHoleScore();
                TeamSavePointQuotaTeamScore(currentHole, ScoreToSave, TeamHoleMask);
                setTeamScoreForHole(currentHole, ScoreToSave);  // the mTeamNetHoleScore variable used to calculate total team score for all of the players
                break;

            case DISPLAY_MODE_STABLEFORD:
                ScoreToSave = m_HoleDetails[currentHole].getStablefordHoleScore();
                TeamSaveStablefordTeamScore(currentHole, ScoreToSave, TeamHoleMask);
                setTeamScoreForHole(currentHole, ScoreToSave);  // the mTeamNetHoleScore variable used to calculate total team score for all of the players
                break;
        }

    }

    /*
 This function will save the current hole played to the team scoring class.
  */
    public void MovePlayerTeamScoreToTeamScoreTotal(int currentHole, TeamScoreTotals teamScoreTotals, int displayMode) {
        byte PlayerTeamScore;

        SaveTeamCurrentHoleScore(currentHole, displayMode);

        PlayerTeamScore = getTeamScoreForHole(currentHole);
        teamScoreTotals.AddPlayerTeamHoleScore(currentHole, PlayerTeamScore);                     // save the player team score
        teamScoreTotals.AddPlayerTeamUsedScore(currentHole, getTeamScoreUsedByHole(currentHole));   // save the number of score used by the hole
    }


    /*
This function returns the score card color for the score vaule.
*/
    private long getScoreCardNumberColor(int ScoreValue) {
        long StrokeColor;

        switch (ScoreValue) {
            case -2:
                StrokeColor = R.color.two_under_color;
                break;
            case -1:
                StrokeColor = R.color.one_under_color;
                break;
            case 0:
                StrokeColor = R.color.par_hole_color;
                break;
            case 1:
                StrokeColor = R.color.one_over_color;
                break;
            case 2:
                StrokeColor = R.color.two_over_color;
                break;
            case 3:
                StrokeColor = R.color.three_over_color;
                break;
            case 4:
                StrokeColor = R.color.four_over_color;
                break;
            default:
                StrokeColor = R.color.other_over_color;
                break;
        }
        return (StrokeColor);
    }

    /*
This function will return the point quota for the score entered
 */
/*
This function will return the point quota for the score entered
 */
    public byte DeterminePointScoreQuotaScore(int PointQuotaHoleScore) {

        switch (PointQuotaHoleScore) {
            case -3:
                PointQuotaHoleScore = m_PointQuotaArray[PQ_ALBATROSS];
                break;
            case -2:
                PointQuotaHoleScore = m_PointQuotaArray[PQ_EAGLE];
                break;
            case -1:
                PointQuotaHoleScore = m_PointQuotaArray[PQ_BIRDEIS];
                break;
            case 0:
                PointQuotaHoleScore = m_PointQuotaArray[PQ_PAR];
                break;
            case 1:
                PointQuotaHoleScore = m_PointQuotaArray[PQ_BOGGY];
                break;
            case 2:
                PointQuotaHoleScore = m_PointQuotaArray[PQ_DOUBLE];
                break;

            default:        // other
                PointQuotaHoleScore = m_PointQuotaArray[PQ_OTHER];
                break;
        }
        return (byte) PointQuotaHoleScore;
    }

    /*
This function will display the player hole score on to the score card.
 */
    public void MoveScoreToScoreCard(int currentHole, int DisplayMode, DisplayScoreCardDetail.WhatNineIsDisplayed WhatNineIsBeingDisplayed) {
        byte ParForHole;
        long DigitTextColor;
        int ScoreValue;
        String color;
        // determine cell's back ground color
        DigitTextColor = m_HoleDetails[currentHole].getStrokeBackgroundColor();
        color = m_Context.getString(Integer.parseInt(String.valueOf(DigitTextColor)));    // set the current hole back to the default color - remove any team box
        m_HoleDetails[currentHole].m_tvScoreCell.setBackgroundColor(Color.parseColor(color));

        // calculate point quota score
        ParForHole = GetTeamTheParForThisHole(currentHole);
        ScoreValue = m_HoleDetails[currentHole].DisplayScoreCardScore(DisplayMode, ParForHole);
        // determine cell's text color
        DigitTextColor = getScoreCardNumberColor(ScoreValue);
        color = m_Context.getString(Integer.parseInt(String.valueOf(DigitTextColor)));    // set the current hole back to the color
        m_HoleDetails[currentHole].m_tvScoreCell.setTextColor(Color.parseColor(color));

        SetTeamBoxHighLightOnScoreCard(currentHole);           // set the score card high lights
        setPlayerMaskToDefaultColor();                       // clear the screen team high light for hole and player's name
        DisplayPlayerTotalScore(WhatNineIsBeingDisplayed, DisplayMode);
    }

    /*
This function will get the player gross score used for the 9's game
 */
    public int GetplayerGrossScore(int currentHole) {
        int GrossScore = m_HoleDetails[currentHole].getGrossScore();
        return GrossScore;
    }

    /*
  This function will get the player net score used for the 9's game
   */
    public int GetplayerNetScore(int currentHole) {
        int NetScore = m_HoleDetails[currentHole].getNetScore();
        return NetScore;
    }/*
    This function will save this player's 9 game score for this hole.
 */

    public void Assign_9_GameScore(int currentHole, int game_9_score) {
        m_HoleDetails[currentHole].setPlayer_9_GameScore((byte) game_9_score);
    }

    /*
    This function will display the total score for the player
     */
    public void DisplayPlayerTotalScore(DisplayScoreCardDetail.WhatNineIsDisplayed DisplayFrontOrBackScoreCard, int displayMode) {
        int StartHole = 0, TotalPlayerScore = 0;
        float PoinQuotaTargetValue;

        if (DisplayFrontOrBackScoreCard == DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY) {
            StartHole = NINETH_HOLE;
        }
        for (int ScreenHole = 0; ScreenHole < SCREENHOLES; ScreenHole++) {
            switch (displayMode) {
                case DISPLAY_MODE_GROSS:
                    TotalPlayerScore += m_HoleDetails[StartHole].getGrossScore();
                    break;

                case DISPLAY_MODE_NET:
                    TotalPlayerScore += m_HoleDetails[StartHole].getNetScore();
                    break;

                case DISPLAY_MODE_POINT_QUOTA:
                    TotalPlayerScore += m_HoleDetails[StartHole].getQuotaHoleScore();
                    break;

                case DISPLAY_MODE_STABLEFORD:
                    TotalPlayerScore += m_HoleDetails[StartHole].getStablefordHoleScore();
                    break;

                case DISPLAY_MODE_9_GAME:
                    TotalPlayerScore += m_HoleDetails[StartHole].get_9_GamePoints();
                    break;
            }
            StartHole++;
        }

        if (displayMode == DISPLAY_MODE_POINT_QUOTA) {
            PoinQuotaTargetValue = m_PoinQuotaTargetValue / 2;
            PoinQuotaTargetValue += TotalPlayerScore;
            String strTmp = String.format("%.1f", PoinQuotaTargetValue);
            m_tvTotal.setText(strTmp);
        } else {
            m_tvTotal.setText("" + TotalPlayerScore);       // end of the score card player's total score being displayed
        }

    }

    /*******************
     * Function used by the golf score summary screen - ie first screen that is displayed
     */

    /*
    This function will return the player's Name
     */
    public String getPlayerName() {
        return (m_PlayerDataBaseRecord.get_PlayerName());
    }

    /*
    This function will return the player's handicap
    */
    public int getPlayerHandicap() {
        return (m_PlayerDataBaseRecord.getM_Handicap());
    }
/*
This function will calculate the summary of the player's round used by the summary page functions
 */

    public void CalculatePlayerScoreSummary(int[] CoursePar, int[] courseHandicap) {
        int TeamHoleMask;
        int CurrentHole, OverUnderScore, PlayerHandicap, PlayerHoleScore, PlayerQuotaScore, PlayerStablefordScore;
        byte ShotsForThisHole;

        PlayerHandicap = getPlayerHandicap();
        m_SummaryStokeScore[SUM_QUOTA] = PlayerHandicap - m_PointQuotaArray[PQ_TARGET];  // player's total score will start off with a negative number (Handicap - Target) i.e.  19 - 36 = -19.

        for (CurrentHole = 0; CurrentHole < HOLES_18; CurrentHole++) {
            PlayerHoleScore = m_HoleDetails[CurrentHole].getGrossScore();

            if (PlayerHoleScore != 0) {
                TeamHoleMask = m_HoleDetails[CurrentHole].getTeamHoleMask();
                ShotsForThisHole = m_HoleDetails[CurrentHole].getNumberOfShotsForThisHole();
                TeamSaveGrossNetTeamScore(CurrentHole, (byte) PlayerHoleScore, TeamHoleMask, ShotsForThisHole);

                PlayerQuotaScore = m_HoleDetails[CurrentHole].getQuotaHoleScore();
                TeamSavePointQuotaTeamScore(CurrentHole, (byte) PlayerQuotaScore, (byte) TeamHoleMask);   // saving the team score that are highlighted on the card

                PlayerStablefordScore = m_HoleDetails[CurrentHole].getStablefordHoleScore();
                TeamSaveStablefordTeamScore(CurrentHole, (byte) PlayerStablefordScore, (byte) TeamHoleMask);    // saving the team score that are highlighted on the card

                m_SummaryStokeScore[SUM_SCORE] += PlayerHoleScore;
                m_SummaryStokeScore[SUM_QUOTA] += m_HoleDetails[CurrentHole].getQuotaHoleScore();       // point quota score total
                m_SummaryStokeScore[SUM_PTS] += m_HoleDetails[CurrentHole].get_9_GamePoints();          // 9 points game score
                m_SummaryStokeScore[SUM_STABLEFORD] += m_HoleDetails[CurrentHole].getStablefordHoleScore();          // stableford score

                OverUnderScore = PlayerHoleScore - CoursePar[CurrentHole];
                TeamSaveOverUnderTeamScore(CurrentHole, (byte) OverUnderScore, TeamHoleMask, ShotsForThisHole);

                switch (OverUnderScore) {
                    case -2:
                        m_SummaryStokeScore[SUM_EAGLE]++;
                        break;
                    case -1:
                        m_SummaryStokeScore[SUM_BIRDIE]++;
                        break;
                    case 0:
                        m_SummaryStokeScore[SUM_PAR]++;
                        break;
                    case 1:
                        m_SummaryStokeScore[SUM__BOGGEY]++;
                        break;
                    case 2:
                        m_SummaryStokeScore[SUM_DOUBLE]++;
                        break;
                    default:
                        m_SummaryStokeScore[SUM_OTHER]++;
                        break;
                }
            }
        }
    }

    /*
This function will get the  player's golf summary - "Score", "Quota", "Sbfd", "Eagle", "Birdies", " Pars", "Bog", "Dbl", "Othr", Pts
 */
    public int GetPlayerStrokeSummary(int StrokeType) {
        int StrokeValue = -1;

        if (StrokeType < SUM_END)
            StrokeValue = m_SummaryStokeScore[StrokeType];

        return (StrokeValue);
    }

    /*
    This function will get the front/back point quota total for this player used by the Summary screen of the app.
     */
    public float GetTotalPlayerPointQuotaTotal(int firstHole, int ninethHole) {
        float PointQuotaTotal = 0, PoinQuotaTargetValue;

        PoinQuotaTargetValue = m_PoinQuotaTargetValue / 2;

        for (int CurrentHole = firstHole; CurrentHole < ninethHole; CurrentHole++) {
            PointQuotaTotal += m_HoleDetails[CurrentHole].getQuotaHoleScore();
        }
        PointQuotaTotal += PoinQuotaTargetValue;  // the target value will alway start of a negative number
        return (PointQuotaTotal);
    }

    /*
    This function will get the used (High Lighted) players front/back point quota totals for this player used by the Summary screen of the app. ??
     */
    public float GetTotalUsedPlayerPointQuotaTotal(int firstHole, int ninethHole) {
        float PointQuotaTotal = 0, PoinQuotaTargetValue;

        PoinQuotaTargetValue = m_PoinQuotaTargetValue / 2;

        for (int CurrentHole = firstHole; CurrentHole < ninethHole; CurrentHole++) {

            PointQuotaTotal += getUsedTeamPointQuoteScore(CurrentHole);
        }
        PointQuotaTotal += PoinQuotaTargetValue;  // the target value will alway start of a negative number
        return (PointQuotaTotal);
    }

    /*
This function will calculated the team Stableford using the player's strokes selected for the hole.
*/
    public int GetTotalStablefordTotal(int firstHole, int ninethHole) {
        int PlayerTeamStablefordTotal = 0;

        for (int CurrentHole = firstHole; CurrentHole < ninethHole; CurrentHole++) {
            PlayerTeamStablefordTotal += getUsedTeamStablefordScore(CurrentHole);
        }
        return (PlayerTeamStablefordTotal);
    }

    /*
This function will calculated the player total Stableford
*/
    public int GetPlayerStablefordTotal(int firstHole, int ninethHole) {
        int PlayerTeamStablefordTotal = 0;

        for (int CurrentHole = firstHole; CurrentHole < ninethHole; CurrentHole++) {
            PlayerTeamStablefordTotal += m_HoleDetails[CurrentHole].getStablefordHoleScore();   // get the stableford point for this player -
        }
        return (PlayerTeamStablefordTotal);
    }
    /*
This function is used by the email player's score function - build the message body so the user can add it to a excel spreadsheet
 */
    public String getSpreadSheetScore() {
        String PlayersScore = " ", CourseParStr = "";
        int total = 0, score, NineHole = 0, ParForHole;
        Date today = new Date();

        //formatting date in Java using SimpleDateFormat
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");
        PlayersScore = DATE_FORMAT.format(today) + ",";

        for (int HoleNumber = 0; HoleNumber < HOLES_18; HoleNumber++) {
            if (HoleNumber == NINETH_HOLE) {
                PlayersScore += NineHole + ",";
                NineHole = 0;
            }
            score = this.m_HoleDetails[HoleNumber].getGrossScore();
            total += score;
            NineHole += score;
            PlayersScore += score + ",";
        }
        PlayersScore += NineHole + ",";
        PlayersScore += total + ",";
// now add the par for the course to the email
        total = 0;
        for (int HoleNumber = 0; HoleNumber < HOLES_18; HoleNumber++) {
            if (HoleNumber == NINETH_HOLE) {
                CourseParStr += NineHole + ",";
                NineHole = 0;
            }
            ParForHole = GetTeamTheParForThisHole(HoleNumber);
            total += ParForHole;
            NineHole += ParForHole;
            CourseParStr += ParForHole + ",";
        }
        CourseParStr += NineHole + ",";
        CourseParStr += total;
        PlayersScore += CourseParStr;


        return PlayersScore;
    }
}