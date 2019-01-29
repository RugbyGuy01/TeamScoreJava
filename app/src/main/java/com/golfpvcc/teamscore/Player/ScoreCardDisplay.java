package com.golfpvcc.teamscore.Player;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.golfpvcc.teamscore.Database.RealmScoreCardAccess;
import com.golfpvcc.teamscore.DisplayScoreCardDetail;
import com.golfpvcc.teamscore.R;
import com.golfpvcc.teamscore.Team.TeamScoreTotals;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.BACK_NINE_TOTAL_DISPLAYED;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_GROSS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_NET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_POINT_QUOTA;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.FRONT_NINE_TOTAL_DISPLAYED;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NAME_COL;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NINETH_HOLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NINETH_HOLE_ZB;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_COL;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.TOTAL_COL;

/*
This class will handle all of the Main Players score text views and buttons
 */
public class ScoreCardDisplay {
    String m_GolfCourseName;
    TextView m_TvHole[];      // text views for the hole numbers on score header table
    TextView m_TvPar[];         // par this hole
    TextView m_TvHandicap[];  // text views for the hole handicap on score header table
    TextView m_TvTeamScore[];   // text views for the team score on the botton of the score card
    TextView m_TvUsedScore[];   // text views for the team used score on the botton of the score card
    private TextView m_TvGolfCourseName, m_tvScoringDisplay, m_tvCurrentHole, m_tvNextNineBut;
    Context m_Context;        // view port
    RealmScoreCardAccess m_RealmScoreCardAccess;

    /*
Constructor for class - Save all of the pointer to the score cells
 */
    public ScoreCardDisplay(int ColumnCnt, TextView TvHole[], TextView TvPar[], TextView TvScoreCardHandicap[], RealmScoreCardAccess RealmScoreCardAccess) {

        m_TvHole = new TextView[ColumnCnt];
        m_TvPar = new TextView[ColumnCnt];
        m_TvHandicap = new TextView[ColumnCnt];

        m_RealmScoreCardAccess = RealmScoreCardAccess;

        for (int x = 0; x < ColumnCnt; x++) {
            m_TvHole[x] = TvHole[x];            // save the text view pointers
            m_TvPar[x] = TvPar[x];
            m_TvHandicap[x] = TvScoreCardHandicap[x];
        }
        m_GolfCourseName = m_RealmScoreCardAccess.getTodayGolfCoursename();        // get the current score for today's game
        // need add an error message here if course name is missing
    }

    /*
    This function will set the text view for the golf course name and the screen display mode - goss, net or point quota
     */
    public void SetTextViewCourseNameAndMode(DisplayScoreCardDetail context, View TvGolfCourseName, View TvScoreCardMode, View TvCurrentHole, View tvNextNineBut) {
        m_TvGolfCourseName = (TextView) TvGolfCourseName;      // displays the golf course name
        m_tvScoringDisplay = (TextView) TvScoreCardMode;        // display the screen current mode.
        m_tvCurrentHole = (TextView) TvCurrentHole;          // current hole being played
        m_tvNextNineBut = (TextView) tvNextNineBut;
        m_Context = context;
    }

    /*
    This function will display the curren display mode for the score card
     */
    public void DisplayHeader(DisplayScoreCardDetail.WhatNineIsDisplayed DisplayFrontOrBackScoreCard, int DisplayMode) {
        String DisplayModeText;

        m_TvGolfCourseName.setText(m_GolfCourseName);      // set the course name on the screen
        DisplayModeText = CurrentDisplayMode(DisplayMode);
        m_tvScoringDisplay.setText(DisplayModeText);        // what score is being display - gross, net, point quota

        m_RealmScoreCardAccess.ReadScoreCardFromDatabase(m_GolfCourseName);        // will read in the pars and handicaps from the current playing course

        m_TvHole[NAME_COL].setText("Hole");     // set the row headers
        m_TvPar[NAME_COL].setText("Par");
        m_TvHandicap[NAME_COL].setText("Hdcp");
        DisplayHoleParHandicapsOnScoreCard(DisplayFrontOrBackScoreCard);
    }

    /*
    This function will set the text next to golf course on what type scoring is being displayed
     */
    public void setScoreCardDisplayMode(int DisplayMode) {

        String DisplayModeText;

        DisplayModeText = CurrentDisplayMode(DisplayMode);
        m_tvScoringDisplay.setText(DisplayModeText);        // what score is being display - gross, net, point quota
    }

    /*
This function will display the Team's scores and Player's scores used for the hole.
The text views for the team and user scores are below the player's name on the score card.
 */
    public void DisplayFooter(int ColumnCnt, TextView TvScoreCardTeamScore[], TextView TvScoreCardUsedcore[]) {

        m_TvTeamScore = new TextView[ColumnCnt];
        m_TvUsedScore = new TextView[ColumnCnt];

        for (int Inx = 0; Inx < ColumnCnt; Inx++) {
            m_TvTeamScore[Inx] = TvScoreCardTeamScore[Inx];
            m_TvUsedScore[Inx] = TvScoreCardUsedcore[Inx];
        }
        m_TvTeamScore[NAME_COL].setText("Team");     // set the row headers
        m_TvUsedScore[NAME_COL].setText("Used");     // set the row headers
    }

    /*
    This function will display the front/back nine holes numbers and handicaps for the holes
     */
    public void DisplayHoleParHandicapsOnScoreCard(DisplayScoreCardDetail.WhatNineIsDisplayed DisplayFrontOrBackScoreCard) {
        String strTmp, color;
        int TvInx, ScoreCardHole, DataBaseHole, TotalPar = 0;

        if (DisplayFrontOrBackScoreCard == DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED) {
            ScoreCardHole = 1;       // record are one base
            m_tvNextNineBut.setText("Back Nine");
        } else {
            ScoreCardHole = 10;
            m_tvNextNineBut.setText("Front Nine");
        }
        color = m_Context.getString(Integer.parseInt(String.valueOf(R.color.default_hole_color)));    // set the current hole back to the default color
        DataBaseHole = ScoreCardHole - 1;       // the database records are zero based

        for (TvInx = 1; TvInx < 10; TvInx++, ScoreCardHole++, DataBaseHole++) {

            m_TvHole[TvInx].setText("" + ScoreCardHole);       // the score card holes are one based

            strTmp = m_RealmScoreCardAccess.GetGolfCourseHoleParStr(DataBaseHole); // this is the hole Par on the score card, database records are zero based
            m_TvPar[TvInx].setText(strTmp);     // set the par on the score card
            TotalPar += Integer.parseInt(strTmp);

            strTmp = m_RealmScoreCardAccess.GetGolfCourseHoleHandicapStr(DataBaseHole);    // this is the hole handicap on the score card
            m_TvHandicap[TvInx].setText(strTmp);

            m_TvHole[TvInx].setBackgroundColor(Color.parseColor(color));
        }
        m_TvHole[TvInx].setText("Total");
        m_TvHole[TvInx].setBackgroundColor(Color.parseColor(color));

        m_TvPar[TvInx].setText("" + TotalPar);     // set the par on the score card
    }

    /*
    This function will high light the current hole on the score card.
     */
    public void ActiveCurrentScoreCardHole(int CurrentHole) {
        String color;

        color = m_Context.getString(Integer.parseInt(String.valueOf(R.color.active_hole_color)));    // set the current hole back to the color

        if (-1 < CurrentHole && CurrentHole < HOLES_18) {
            CurrentHole %= SCREEN_COL;       // only 9 hole are displayed on the screen
            m_TvHole[(CurrentHole + 1)].setBackgroundColor(Color.parseColor(color));
        } else {
            m_TvHole[TOTAL_COL].setBackgroundColor(Color.parseColor(color));
        }
    }

    /*
    This function will return the current hole the player is on.
     */
    public int getTheCurrentHoleFromScoreCard() {
        String strTmp;
        byte CurrentHole;

        strTmp = m_tvCurrentHole.getText().toString(); // Screen Display States = display Totals ('_' front or '-' back)
        CurrentHole = (byte) strTmp.indexOf(FRONT_NINE_TOTAL_DISPLAYED);
        if (-1 < CurrentHole) {
            CurrentHole = FRONT_NINE_TOTAL_DISPLAYED;
        } else {
            CurrentHole = (byte) strTmp.indexOf(BACK_NINE_TOTAL_DISPLAYED);
            if (-1 < CurrentHole) {
                CurrentHole = BACK_NINE_TOTAL_DISPLAYED;
            } else {
                CurrentHole = (byte) Integer.parseInt(strTmp);
                CurrentHole--;          // set the current hole to zero base
            }
        }

        return (CurrentHole);
    }

    /*
    This function will set the current hole on the score card and high lite the score card hole being played ????
     */
    public void setTheCurrentHoleOnScoreCard(byte currentHole) {
        String tmpStr;

        if (-1 < currentHole && currentHole < 18)
            m_tvCurrentHole.setText("" + (currentHole + 1));
        else {
            tmpStr = String.format("%c", currentHole);
            m_tvCurrentHole.setText(tmpStr);
        }
    }

    /*
    This function will return the course handicap for the current hole
     */
    public int getCourseHoleHandicap(int CurrentHole) {
        int CourseHoleHandicap;

        CourseHoleHandicap = m_RealmScoreCardAccess.GetGolfCourseHoleHandicapInt(CurrentHole);
        return (CourseHoleHandicap);
    }

    /*
    This function will return the text for the current display mode
     */
    private String CurrentDisplayMode(int DisplayMode) {
        String DisplayModeText = "Not Set";
        switch (DisplayMode) {
            case DISPLAY_MODE_GROSS:
                DisplayModeText = "Gross Score";
                break;

            case DISPLAY_MODE_NET:
                DisplayModeText = "Net Score";
                break;

            case DISPLAY_MODE_POINT_QUOTA:
                DisplayModeText = "Point Quota";
                break;
        }

        return DisplayModeText;
    }

    /*
This function will display the previous hole on the score card.
*/
    public int PrevHoleOnScoreCard(int CurrentHole) {

        ClearCurrentHoleOnScoreCard((byte) CurrentHole);    // Set the last active hole on the score card back to the default color

        if (CurrentHole == FRONT_NINE_TOTAL_DISPLAYED) {
            CurrentHole = NINETH_HOLE_ZB;            // hole 8 is hole 9 on the score card
        } else if (CurrentHole == BACK_NINE_TOTAL_DISPLAYED) {
            CurrentHole = 17;            // hole 17 is hole 18 on the score card
        } else {
            CurrentHole--;
        }

        switch (CurrentHole) {
            case HOLES_18:
                setTheCurrentHoleOnScoreCard((byte) BACK_NINE_TOTAL_DISPLAYED);     // display the '-' (0x2D)on the hole being played
                ActiveCurrentScoreCardHole((byte) BACK_NINE_TOTAL_DISPLAYED);    // the current hole on the score card
                CurrentHole = BACK_NINE_TOTAL_DISPLAYED;
                break;

            default:
                setTheCurrentHoleOnScoreCard((byte) CurrentHole);    // display what hole we are playing
                ActiveCurrentScoreCardHole((byte) CurrentHole);    // the current hole on the score card
                break;
        }
        return (CurrentHole);
    }

    /*
  This function will advance the score card on to the next hole
   */
    public int NextHoleOnScoreCard(int CurrentHole) {

        ClearCurrentHoleOnScoreCard((byte) CurrentHole);    // Set the last active hole on the score card back to the default color
        CurrentHole++;
        switch (CurrentHole) {
            case NINETH_HOLE:
                setTheCurrentHoleOnScoreCard((byte) FRONT_NINE_TOTAL_DISPLAYED);
                ActiveCurrentScoreCardHole((byte) BACK_NINE_TOTAL_DISPLAYED);    // the current hole on the score card
                CurrentHole = FRONT_NINE_TOTAL_DISPLAYED;
                break;

            case HOLES_18:
                setTheCurrentHoleOnScoreCard((byte) BACK_NINE_TOTAL_DISPLAYED);
                ActiveCurrentScoreCardHole((byte) FRONT_NINE_TOTAL_DISPLAYED);    // the current hole on the score card
                CurrentHole = BACK_NINE_TOTAL_DISPLAYED;
                break;

            default:
                setTheCurrentHoleOnScoreCard((byte) CurrentHole);    // display what hole we are playing
                ActiveCurrentScoreCardHole((byte) CurrentHole);    // the current hole on the score card
                break;
        }
        return (CurrentHole);
    }

    /*
    This function will start at the first or the tenth hole on the score - we just finished showing the score totals.
     */
    public void HighLightTheFirstHoleOnScoreCard(byte CurrentHole) {
        setTheCurrentHoleOnScoreCard(CurrentHole);    // display what hole we are playing
        ActiveCurrentScoreCardHole(CurrentHole);    // the current hole on the score card
    }

    /*
 Set the last active hole on the score card back to the default color
 */
    private void ClearCurrentHoleOnScoreCard(byte CurrentHole) {

        String color = m_Context.getString(Integer.parseInt(String.valueOf(R.color.default_hole_color)));    // set the current hole back to the default color

        if (-1 < CurrentHole && CurrentHole < HOLES_18) {
            CurrentHole %= SCREEN_COL;       // only 9 hole are displayed on the screen
            m_TvHole[(CurrentHole + 1)].setBackgroundColor(Color.parseColor(color));
        } else {
            m_TvHole[TOTAL_COL].setBackgroundColor(Color.parseColor(color));
        }

    }

    /*----------------------------------- Team scoring functions ---------------------------*/
    /*
    This function will save the Par for each hole in the player's team hole class and load the player's point quota scores in the hole class
     */
    public void TeamScoringSetup(DisplayPlayerScoreData DisplayPlayerScore[], int NumberOfPlayers) {
        int ParForHole;

        for (int hole = 0; hole < HOLES_18; hole++) {

            ParForHole = m_RealmScoreCardAccess.GetGolfCourseHoleParInt(hole); // this is the hole Par on the score card, database records are zero based
            for (int Inx = 0; Inx < NumberOfPlayers; Inx++) {
                DisplayPlayerScore[Inx].setTeamTheParForEachHole(hole, (byte) ParForHole);
                DisplayPlayerScore[Inx].LoadPlayerQuotaScores();
            }
        }
    }

    /*
    This function will update the score card with the player's team scores and scores used on each hole.
     */
    public void DisplayTeamScoresOnScoreCard(TeamScoreTotals teamScoreTotals, DisplayScoreCardDetail.WhatNineIsDisplayed displayFrontOrBackScoreCard) {
        int ScoreCardHole, TeamHole, TeamHoleScore, TeamUsedScore, TeamNineHoleScore = 0, TeamNineHoleUsedTotal = 0, TvInx;

        if (displayFrontOrBackScoreCard == DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED) {
            ScoreCardHole = 1;       // Score Card screen record are one base
        } else {
            ScoreCardHole = 10;
        }
        TeamHole = ScoreCardHole - 1;       // the database records are zero based

        for (TvInx = 1; TvInx < 10; TvInx++, ScoreCardHole++, TeamHole++) {
            TeamHoleScore = teamScoreTotals.getTeamHoleScore(TeamHole);
            TeamNineHoleScore += TeamHoleScore;

            TeamUsedScore = teamScoreTotals.getTeamUsedScore(TeamHole);
            TeamNineHoleUsedTotal += TeamUsedScore;

            m_TvTeamScore[TvInx].setText("" + TeamHoleScore);       // text views for the team score on the botton of the score card
            m_TvUsedScore[TvInx].setText("" + TeamUsedScore);
        }
        m_TvTeamScore[TvInx].setText("" + TeamNineHoleScore);       // text views for the team score on the botton of the score card
        m_TvUsedScore[TvInx].setText("" + TeamNineHoleUsedTotal);
    }

}
