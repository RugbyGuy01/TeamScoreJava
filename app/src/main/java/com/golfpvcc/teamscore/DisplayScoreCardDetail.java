package com.golfpvcc.teamscore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.golfpvcc.teamscore.Database.PlayerRecord;
import com.golfpvcc.teamscore.Database.RealmScoreCardAccess;
import com.golfpvcc.teamscore.Player.DisplayPlayerScoreData;
import com.golfpvcc.teamscore.Player.ScoreCardDisplay;
import com.golfpvcc.teamscore.Team.TeamScoreTotals;

import io.realm.Realm;

import static android.widget.Toast.LENGTH_SHORT;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.BACK_NINE_TOTAL_DISPLAYED;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_GROSS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_NET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_POINT_QUOTA;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.FIRST_HOLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.FRONT_NINE_TOTAL_DISPLAYED;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.HANDICAP_ROW;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLE_ROW;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NEXT_SCREEN;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NINETH_HOLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NINETH_HOLE_ZB;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PAR_ROW;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PLAYER1_ROW;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PLAYER_TOTAL;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_BIRDEIS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_BOGGY;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_DOUBLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_EAGLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_END;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_OTHER;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_PAR;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_TARGET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_BIRDIE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_BOGGEY;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_DOUBLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_EAGLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_OTHER;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_PAR;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_TARGET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_COURSE_LIST;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_GAME_SUMMARY;

public class DisplayScoreCardDetail extends AppCompatActivity {
    public enum WhatNineIsDisplayed {FRONT_NINE_DISPLAYED, BACK_NINE_DISPLAY}

    Realm m_Realm;
    RealmScoreCardAccess m_RealmScoreCardAccess;
    DisplayScoreCardDetail.WhatNineIsDisplayed m_WhatNineIsBeingDisplayed;    // must keep track of which screen is being display to the user
    TeamScoreTotals m_TeamScoreTotals;
    TableLayout m_TableLayoutCreate;       // score card table
    int m_DisplayMode = DISPLAY_MODE_GROSS;
    int m_CurrentHole, m_NumberOfPlayers;            // the number of players on the sacore card
    int m_ColumnCnt = 11;                   // 9 holes, Name, total
    int m_RowCount = 5;       // Hole, Par, Hdcp. Team, and used must have rows
    ScoreCardDisplay m_ScoreCardDisplay;
    DisplayPlayerScoreData m_DisplayPlayerScore[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_card_detail);     //xml file for diplaying the score card
        TextView[][] ScoreCardTable;         // score grid of Text Views for displaing the player's scores
        int[] PointQuotaArray;

        m_Realm = Realm.getDefaultInstance();
        m_RealmScoreCardAccess = new RealmScoreCardAccess(m_Realm);
        m_RealmScoreCardAccess.ReadTodayScoreCardRecord();           // get the current score card from the database for today's game
        m_NumberOfPlayers = m_RealmScoreCardAccess.PlayerRecordCnt();  // how many player on the score card
        String GolfCourseName = m_RealmScoreCardAccess.getTodayGolfCoursename();        // must have a course name to build the score card otherwise see ya

        // build player's classes if no player exit screen
        if (0 < m_NumberOfPlayers && 3 < GolfCourseName.length()) {
            m_RowCount += m_NumberOfPlayers;
            m_CurrentHole = m_RealmScoreCardAccess.getCurrentGolfHoleBeingPlayed();         // get the hole the app was on when the app exit


            if ((m_CurrentHole < 0 || HOLES_18 < m_CurrentHole) && (m_CurrentHole != FRONT_NINE_TOTAL_DISPLAYED) && (m_CurrentHole != BACK_NINE_TOTAL_DISPLAYED)) {
                m_CurrentHole = 0;  // just in case of garbage in the database
            }

            if (m_CurrentHole < NINETH_HOLE || (m_CurrentHole == FRONT_NINE_TOTAL_DISPLAYED)) // set the score card
                m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED;
            else
                m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY;

            PointQuotaArray = LoadPointQuotaData();
            ScoreCardTable = new TextView[m_RowCount][m_ColumnCnt];                // row col -  keeps track of all of the cells text views

            m_TableLayoutCreate = createTableLayout(m_RowCount, m_ColumnCnt, ScoreCardTable);
            TableLayout VintableLayout = (TableLayout) findViewById(R.id.playerScoreCard);
            VintableLayout.addView(m_TableLayoutCreate);    // tie the table to the screen display

            // build score card header and then display it
            m_ScoreCardDisplay = new ScoreCardDisplay(m_ColumnCnt, ScoreCardTable[HOLE_ROW], ScoreCardTable[PAR_ROW], ScoreCardTable[HANDICAP_ROW], m_RealmScoreCardAccess);

            m_ScoreCardDisplay.SetTextViewCourseNameAndMode(this, findViewById(R.id.textCourseName), findViewById(R.id.tvScoringDisplay),
                    findViewById(R.id.tvCurrentHole), findViewById(R.id.butNextNine));  // set the text view pointer

            m_ScoreCardDisplay.DisplayHeader(m_WhatNineIsBeingDisplayed, m_DisplayMode);
            m_ScoreCardDisplay.DisplayFooter(m_ColumnCnt, ScoreCardTable[PLAYER1_ROW + m_NumberOfPlayers], ScoreCardTable[PLAYER1_ROW + m_NumberOfPlayers + 1]);


            BuildPlayerClass(m_RealmScoreCardAccess, m_NumberOfPlayers, ScoreCardTable, PointQuotaArray);
            InitPlayersScreenRecord(m_NumberOfPlayers);
            AddNamesToScoreCard(m_NumberOfPlayers);                          // add the names to the lower part of screen score card
            AddButtonListerner();

            m_TeamScoreTotals = new TeamScoreTotals();      // wholes the team total score
            m_ScoreCardDisplay.TeamScoringSetup(m_DisplayPlayerScore, m_NumberOfPlayers);

            RedisplayScoreCardFrontOrBackNine(m_WhatNineIsBeingDisplayed, m_NumberOfPlayers, m_DisplayMode);
            SetCurrentActiveHoleBackGroundColor(m_CurrentHole);
        } else {
            Intent intentSendDataBack = new Intent();   // data sent back to the calling activity
            intentSendDataBack.putExtra(NEXT_SCREEN, SCREEN_COURSE_LIST);
            setResult(RESULT_OK, intentSendDataBack);

            finish();       // close this screen or exit this screen
        }
    }

    /*
  This function will create the score table with the names and scores
   */
    private TableLayout createTableLayout(int rowCount, int columnCount, TextView[][] ScoreCardTable) {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setBackgroundColor(Color.BLACK);

        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(2, 2, 2, 2);  // the width of the grid lines
        tableRowParams.weight = 1;

        for (int Row = 0; Row < rowCount; Row++) {
            // 3) create tableRow
            TableRow tableRow = new TableRow(this);         // new table Row
            tableRow.setBackgroundColor(Color.BLACK);
            for (int Col = 0; Col < columnCount; Col++) {
                // 4) create textView
                TextView textView = new TextView(this);     // create the child view for the text
                textView.setBackgroundColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.getResources().getDimension(R.dimen.font_normal_cell));
                textView.setPadding(3, 3, 3, 3);
                textView.setLayoutParams(tableRowParams);           // set the margins for the text view

                ScoreCardTable[Row][Col] = textView;   // save the cell text view so we can switch from front to back, 1 is hole 1 cell

                textView.setText(" ");
                tableRow.addView(textView, tableRowParams);     // 5) add textView to tableRow
            }
            tableLayout.addView(tableRow, tableLayoutParams);   // 6) add tableRow to tableLayout
        }

        return tableLayout;
    }

    /*
    This function will allocate the player class for the number of player, PLAYER_TOTAL
     */
    private void BuildPlayerClass(RealmScoreCardAccess realmScoreCardAccess, int numberOfPlayers, TextView[][] tvCells, int[] PointQuotaArray) {
        int Inx;
        PlayerRecord PlayerDatabaseRec;

        m_DisplayPlayerScore = new DisplayPlayerScoreData[numberOfPlayers];  // allocate the array of "DisplayPlayerScoreData" classes

        for (Inx = 0; Inx < numberOfPlayers; Inx++) {
            PlayerDatabaseRec = realmScoreCardAccess.GetPlayerFromScoreCard(Inx);  // find all of the current player database records on the score card
            m_DisplayPlayerScore[Inx] = new DisplayPlayerScoreData(this, PointQuotaArray, tvCells[PLAYER1_ROW + Inx], PlayerDatabaseRec);
        }
    }

    /*
 This function add the names to lower part of the score card
  */
    private void AddNamesToScoreCard(int NumberOfPlayers) {

        for (int Inx = 0; Inx < NumberOfPlayers; Inx++) {
            m_DisplayPlayerScore[Inx].AddNameToScoreCar();
        }

    }
/*
This function will populate the data entry of the score card lower screen, Player's name and current score of the current hole.
 */

    private void InitPlayersScreenRecord(int NumberOfPlayers) {
        String strTmp;
        int tv_Id, Inx, CourseHandicapForHole, CourseParForHole;
        TextView tv_Tmp;

        Resources res = getResources();

        for (Inx = 0; Inx < NumberOfPlayers; Inx++) {

            strTmp = "Player_hole_score_" + Inx;                            // net team score for the hole
            tv_Id = res.getIdentifier(strTmp, "id", getApplicationContext().getPackageName());
            tv_Tmp = (TextView) findViewById(tv_Id);
            m_DisplayPlayerScore[Inx].setTvPlayerLowerCurScore(tv_Tmp);

            strTmp = "Player_Name_" + Inx;                                       // the player's name used for the gross team scoring
            tv_Id = res.getIdentifier(strTmp, "id", getApplicationContext().getPackageName());
            tv_Tmp = (TextView) findViewById(tv_Id);
            m_DisplayPlayerScore[Inx].setTvPlayerLowerName(tv_Tmp);

            for (int Hole = 0; Hole < HOLES_18; Hole++) {
                CourseHandicapForHole = m_ScoreCardDisplay.getCourseHoleHandicap(Hole);
                m_DisplayPlayerScore[Inx].setStrokeHoles(Hole, CourseHandicapForHole);      // the hole with a strokes
            }
            m_DisplayPlayerScore[Inx].LoadPlayerScoresIntoHoleClass();            // Load the player's scores from the database
        }

        for (; Inx < PLAYER_TOTAL; Inx++) {             // remove the unused players from the screen
            RemovePlayerInformationFromCard("Player_hole_score_" + Inx);
            RemovePlayerInformationFromCard("Player_Name_" + Inx);
            RemovePlayerInformationFromCard("Player_minus_" + Inx);
            RemovePlayerInformationFromCard("Player_plus_" + Inx);
        }

    }

    /*
    This function will remove the non-player's information from the body of the form.
     */
    private void RemovePlayerInformationFromCard(String strTmp) {
        TextView tv_Tmp;

        int id = getResources().getIdentifier(strTmp, "id", getApplicationContext().getPackageName());
        tv_Tmp = (TextView) findViewById(id);
        tv_Tmp.setVisibility(TextView.GONE);        // Gone will remove the view from the screen
    }

    /*
    This function will add a common button Listerner to all of the button widgets
     */
    private void AddButtonListerner() {
        Button butAddLister;
        TextView tvAddLister;

        butAddLister = (Button) findViewById(R.id.butPrevHole); // previous hole button
        butAddLister.setOnClickListener(butNavigate);
        butAddLister = (Button) findViewById(R.id.butNextHole); // Next hole button
        butAddLister.setOnClickListener(butNavigate);

        butAddLister = (Button) findViewById(R.id.butNextNine);     // Display the front or back score card
        butAddLister.setOnClickListener(butNavigate);

        butAddLister = (Button) findViewById(R.id.butDisplayMode);  // screen display mode
        butAddLister.setOnClickListener(butDisplayMode);

        butAddLister = (Button) findViewById(R.id.butGameSummary);      // game over will exit the screen back to the main screen
        butAddLister.setOnClickListener(butNavigate);


        butAddLister = findViewById(R.id.Player_minus_0);      // Player 1 minus a stroke on the lower score card
        butAddLister.setOnClickListener(butPlayersScore);
        butAddLister = findViewById(R.id.Player_plus_0);       // Player 1 add a stroke on the lower score card
        butAddLister.setOnClickListener(butPlayersScore);

        butAddLister = findViewById(R.id.Player_minus_1);      // Player 2 minus a stroke on the lower score card
        butAddLister.setOnClickListener(butPlayersScore);
        butAddLister = findViewById(R.id.Player_plus_1);       // Player 2 add a stroke on the lower score card
        butAddLister.setOnClickListener(butPlayersScore);

        butAddLister = findViewById(R.id.Player_minus_2);      // Player 3 minus a stroke on the lower score card
        butAddLister.setOnClickListener(butPlayersScore);
        butAddLister = findViewById(R.id.Player_plus_2);       // Player 3 add a stroke on the lower score card
        butAddLister.setOnClickListener(butPlayersScore);

        butAddLister = findViewById(R.id.Player_minus_3);      // Player 4 minus a stroke on the lower score card
        butAddLister.setOnClickListener(butPlayersScore);
        butAddLister = findViewById(R.id.Player_plus_3);       // Player 4 add a stroke on the lower score card
        butAddLister.setOnClickListener(butPlayersScore);


        for (int i = 0; i < m_NumberOfPlayers; i++) {
            tvAddLister = m_DisplayPlayerScore[i].getTvPlayerLowerName();   // click on the player name
            tvAddLister.setOnClickListener(tvTeamNetScoring);
            tvAddLister.setLongClickable(true);
            tvAddLister.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    tvPlayNameDoubleNetLongTeamScoring(v);    // long click on the player name
                    return true;

                }
            });

            tvAddLister = m_DisplayPlayerScore[i].getTvPlayerLowerCurScore();   // click on player current score
            tvAddLister.setOnClickListener(tvTeamGrossScoring);
            tvAddLister.setLongClickable(true);
            tvAddLister.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    tvPlayCurScoreDoubleGrossTeamScoring(v);  // long click on player current score
                    return true;

                }
            });
        }
    }

    /*
     This function will high light the current active hole on the score card
      */
    private void SetCurrentActiveHoleBackGroundColor(int ScoreCardHole) {
        int currentHoleDisplay;

        SetPlayerScoreEntryToDefualtColor();    // clear the player's team entry to the default color

        m_ScoreCardDisplay.setTheCurrentHoleOnScoreCard((byte) ScoreCardHole);    //display the hole being played
        m_ScoreCardDisplay.ActiveCurrentScoreCardHole(ScoreCardHole);            // high lite the hole being played on the score card

        currentHoleDisplay = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();

        if (currentHoleDisplay != FRONT_NINE_TOTAL_DISPLAYED && currentHoleDisplay != BACK_NINE_TOTAL_DISPLAYED) {
            for (int Inx = 0; Inx < m_NumberOfPlayers; Inx++) {
                m_DisplayPlayerScore[Inx].MoveNextHoleScoreToLowerHalfOfScreen(currentHoleDisplay); // Display the player's current hole score in the lower half of the screen

            }
        }
    }


    /*
    This function will set the player's name and hole score to the default color.
     */
    private void SetPlayerScoreEntryToDefualtColor() {
        for (int Inx = 0; Inx < m_NumberOfPlayers; Inx++) {
            m_DisplayPlayerScore[Inx].setPlayerMaskToDefaultColor();
        }
    }

    /*
    This function handles the navigation on the main score card screen - Prev & Prev buttons, Front/Back nine, gross or net or Pt Quota and summary buttons
     */
    private View.OnClickListener butNavigate = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int ButtonId = view.getId();

            switch (ButtonId) {
                case R.id.butPrevHole:
                    PrevButtonEvent();
                    break;

                case R.id.butNextHole:          // Save the scores for the player and move on to the next hole
                    NextButtonEvent();
                    break;

                case R.id.butNextNine:      // display the front or back of the score card, however we need to track what screen we are entering scores on (front or back)
                    DisplayNextNineHoles();
                    break;

                case R.id.butGameSummary:
                    DisplayGameSummaryexitThisScreen();
                    break;
                default:
                    Toast.makeText(DisplayScoreCardDetail.this, "butPlayersScore not coded " + ButtonId, LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /*
    This function will handle displaying the front or back on the score card
     */
    private void DisplayNextNineHoles() {

        if (m_WhatNineIsBeingDisplayed == DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED)
            m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY;
        else
            m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED;

        RedisplayScoreCardFrontOrBackNine(m_WhatNineIsBeingDisplayed, m_NumberOfPlayers, m_DisplayMode);

        int currentHoleDisplay = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();        // redisplay the current hole being played

        if (m_WhatNineIsBeingDisplayed == DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED) {
            if (currentHoleDisplay < NINETH_HOLE || currentHoleDisplay == FRONT_NINE_TOTAL_DISPLAYED) {
                m_ScoreCardDisplay.HighLightTheFirstHoleOnScoreCard((byte) currentHoleDisplay);
            }
        } else if (m_WhatNineIsBeingDisplayed == DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY) {
            if (NINETH_HOLE_ZB < currentHoleDisplay || currentHoleDisplay == BACK_NINE_TOTAL_DISPLAYED) {
                m_ScoreCardDisplay.HighLightTheFirstHoleOnScoreCard((byte) currentHoleDisplay);
            }
        }


    }

    /*
   This function will move back one hole on the score card
    */
    public void PrevButtonEvent() {
        int currentHoleDisplay;

        currentHoleDisplay = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();
        ValidatePrevScreenDisplay(currentHoleDisplay);      // make sure the user has the correct score displayed

        switch (currentHoleDisplay) {
            case NINETH_HOLE:                                    // Prev button moved the score card back to the front nine - need to redisplay score card and set the current hole to '-'
                PrevHandleTheFrontNineTotals(FRONT_NINE_TOTAL_DISPLAYED);      // holes are zero based, this is the tenth hole to display
                break;
            case FIRST_HOLE:     // the screen is displaying the first hole, need to display the back nine now
                PrevHandleTheFrontNineTotals(BACK_NINE_TOTAL_DISPLAYED);
                break;

            default:
                PrevHolePlayerScore(currentHoleDisplay, m_DisplayMode);
                break;
        }
    }

    /*
    This function will save the player's score to the database and add the score to the score card.
    Screen Display States: Front Nine Scores, Front Nine Totals, Back Nine Scores, Back Nine Totals
    Current Hole: is one based holes are  1 to 18
 */
    private void NextButtonEvent() {
        int currentHoleDisplay;

        currentHoleDisplay = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();
        ValidateNextScreenDisplay(currentHoleDisplay);      // make sure the user has the correct score displayed

        switch (currentHoleDisplay) {
            case FRONT_NINE_TOTAL_DISPLAYED:        // the screen is displaying the nine hole totals - move on to the back nine
                NextHandleTheFrontNineTotals(NINETH_HOLE);      // holes are zero based, this is the tenth hole to display
                MovePlayerScoreToLowerHalfOfScreen(NINETH_HOLE);
                break;

            case BACK_NINE_TOTAL_DISPLAYED:     // the screen is display the nine hole totals
                NextHandleTheFrontNineTotals(FIRST_HOLE);
                MovePlayerScoreToLowerHalfOfScreen(FIRST_HOLE);
                break;

            default:
                NextSavePlayerScore(currentHoleDisplay, m_DisplayMode);
                break;
        }

    }

    /*
    This function will validate the correct screen is being display to the user before handling the Prev button
    if  currentHoleDisplay < 9 || currentHoleDisplay == FRONT_NINE_TOTAL_DISPLAYED

     */
    private void ValidatePrevScreenDisplay(int currentHoleDisplay) {

        switch (currentHoleDisplay) {
            case FRONT_NINE_TOTAL_DISPLAYED:
            case BACK_NINE_TOTAL_DISPLAYED:
                // do nothing, displaying total cell, just move back one
                break;

            default:
                if (currentHoleDisplay < NINETH_HOLE && m_WhatNineIsBeingDisplayed == DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY) {
                    m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED;
                    RedisplayScoreCardFrontOrBackNine(m_WhatNineIsBeingDisplayed, m_NumberOfPlayers, m_DisplayMode);          // redisplay the front nine score
                } else if (NINETH_HOLE_ZB < currentHoleDisplay && m_WhatNineIsBeingDisplayed == DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED) {
                    m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY;
                    RedisplayScoreCardFrontOrBackNine(m_WhatNineIsBeingDisplayed, m_NumberOfPlayers, m_DisplayMode);          // redisplay the back nine score
                }

        }
    }

    /*
    This function will validate the correct screen is being display to the user before handling the Next button
     */
    private void ValidateNextScreenDisplay(int currentHoleDisplay) {

        if ((currentHoleDisplay < NINETH_HOLE || currentHoleDisplay == FRONT_NINE_TOTAL_DISPLAYED) && m_WhatNineIsBeingDisplayed == DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY) {
            m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED;
            RedisplayScoreCardFrontOrBackNine(m_WhatNineIsBeingDisplayed, m_NumberOfPlayers, m_DisplayMode);          // redisplay the front nine score
        } else if ((NINETH_HOLE_ZB < currentHoleDisplay || currentHoleDisplay == BACK_NINE_TOTAL_DISPLAYED) && m_WhatNineIsBeingDisplayed == DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED) {
            m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY;
            RedisplayScoreCardFrontOrBackNine(m_WhatNineIsBeingDisplayed, m_NumberOfPlayers, m_DisplayMode);          // redisplay the back nine score
        }
    }

    /*
    This function will display the previous hole on the score card
     */
    public void PrevHolePlayerScore(int CurrentHole, int DisplayMode) {
        int Inx;

        CurrentHole = m_ScoreCardDisplay.PrevHoleOnScoreCard(CurrentHole);     // the current hole display is Zero based

        for (Inx = 0; Inx < m_NumberOfPlayers; Inx++) {
            if (CurrentHole != FRONT_NINE_TOTAL_DISPLAYED && CurrentHole != BACK_NINE_TOTAL_DISPLAYED) {
                m_DisplayPlayerScore[Inx].setPlayerMaskToDefaultColor();                       // clear the screen high light for hole and player's name
                m_DisplayPlayerScore[Inx].MoveNextHoleScoreToLowerHalfOfScreen(CurrentHole);
            }
        }
    }

    /*
    This function will save the player's score to the database - team mask is set when the user selects which score will be a team score
     */
    public void NextSavePlayerScore(int CurrentHole, int DisplayMode) {
        int Inx, NextHole;

        m_TeamScoreTotals.ClearHole(CurrentHole);       // make sure the team score for this is set to zero
        for (Inx = 0; Inx < m_NumberOfPlayers; Inx++) {
            m_DisplayPlayerScore[Inx].SavePlayerHoleScoreToDatabase(m_RealmScoreCardAccess, CurrentHole); // record are zero based 0 to 8
            m_DisplayPlayerScore[Inx].MoveScoreToScoreCard(CurrentHole, DisplayMode, m_WhatNineIsBeingDisplayed);

            m_DisplayPlayerScore[Inx].MovePlayerTeamScoreToTeamScoreTotal(CurrentHole, m_TeamScoreTotals, DisplayMode);  // Save the player's team score to the master team class
        }
        m_ScoreCardDisplay.DisplayTeamScoresOnScoreCard(m_TeamScoreTotals, m_WhatNineIsBeingDisplayed);

        NextHole = m_ScoreCardDisplay.NextHoleOnScoreCard(CurrentHole);     // the current hole display is Zero based
        MovePlayerScoreToLowerHalfOfScreen(NextHole);
    }

    /*
    This function will move the player's score for the hole to lower half of the score entry area.
     */
    public void MovePlayerScoreToLowerHalfOfScreen(int CurrentHole) {

        for (int Inx = 0; Inx < m_NumberOfPlayers; Inx++) {
            if (CurrentHole != FRONT_NINE_TOTAL_DISPLAYED && CurrentHole != BACK_NINE_TOTAL_DISPLAYED) {
                m_DisplayPlayerScore[Inx].MoveNextHoleScoreToLowerHalfOfScreen(CurrentHole);
            }
        }

    }
    /*
    This function will handle the user pressing the prev button - the nine hole indicates the user is on the front nine of the score card
     */
    private void PrevHandleTheFrontNineTotals(int CurrentHole) {
        int Inx;

        if (CurrentHole == FRONT_NINE_TOTAL_DISPLAYED)
            m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED;
        else
            m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY;

        RedisplayScoreCardFrontOrBackNine(m_WhatNineIsBeingDisplayed, m_NumberOfPlayers, m_DisplayMode);          // display the Front or back nine score

        m_ScoreCardDisplay.HighLightTheFirstHoleOnScoreCard((byte) CurrentHole);     // the current hole display is Zero based
    }

    /*
            Screen Display States == Front Nine Totals
                Screen Display States = Back Nine Scores
                Display the back nine card
                Update player's current score's cell with Course Par or previous saved player's score.
                Highlight Current hole cell.
                Done
         */
    private void NextHandleTheFrontNineTotals(int CurrentHole) {
        int Inx;

        if (CurrentHole == NINETH_HOLE)
            m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY;
        else
            m_WhatNineIsBeingDisplayed = DisplayScoreCardDetail.WhatNineIsDisplayed.FRONT_NINE_DISPLAYED;

        RedisplayScoreCardFrontOrBackNine(m_WhatNineIsBeingDisplayed, m_NumberOfPlayers, m_DisplayMode);          // display the back nine score

        for (Inx = 0; Inx < m_NumberOfPlayers; Inx++) {
            m_DisplayPlayerScore[Inx].MoveScoreToScoreCard(CurrentHole, m_DisplayMode, m_WhatNineIsBeingDisplayed);
        }
        m_ScoreCardDisplay.HighLightTheFirstHoleOnScoreCard((byte) CurrentHole);     // the current hole display is Zero based
    }

    /*
   This function handles how the score card is displayed. ie Goss, Net/no Stokes, Net/with strokes (+1, E, -1) and point quota using menu values
   tvScoringDisplay
   */
    private View.OnClickListener butDisplayMode = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ButDisplayModeText = "";
            Button butDisplayMode = findViewById(R.id.butDisplayMode);

            switch (m_DisplayMode) {
                case DISPLAY_MODE_GROSS:
                    m_DisplayMode = DISPLAY_MODE_NET;       // display net score - button display's Point Quota
                    ButDisplayModeText = "Point Quota";
                    break;

                case DISPLAY_MODE_NET:
                    m_DisplayMode = DISPLAY_MODE_POINT_QUOTA;   // display PQ. score - button display's Gross Score
                    ButDisplayModeText = "Gross Score";
                    break;

                case DISPLAY_MODE_POINT_QUOTA:
                    m_DisplayMode = DISPLAY_MODE_GROSS;
                    ButDisplayModeText = "Net Score";
                    break;
            }
            butDisplayMode.setText(ButDisplayModeText);        // set the text on the display mode button
            m_ScoreCardDisplay.setScoreCardDisplayMode(m_DisplayMode);
            RedisplayScoreCardFrontOrBackNine(m_WhatNineIsBeingDisplayed, m_NumberOfPlayers, m_DisplayMode);   //update the screen score card after we change the display mode

            int currentHoleDisplay = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();        // redisplay the current hole being played
            m_ScoreCardDisplay.HighLightTheFirstHoleOnScoreCard((byte) currentHoleDisplay);

        }
    };

    /*
This function will update the score card with the hole numbers, handicap and par for the front or back nine holes.
The Set button text will be used to configure the button
*/
    private void RedisplayScoreCardFrontOrBackNine(DisplayScoreCardDetail.WhatNineIsDisplayed DisplayFrontOrBackScoreCard, int NumberOfPlayers, int DisplayMode) {
        m_TeamScoreTotals.ClearAll();

        m_ScoreCardDisplay.DisplayHoleParHandicapsOnScoreCard(DisplayFrontOrBackScoreCard);
        for (int Inx = 0; Inx < NumberOfPlayers; Inx++) {
            m_DisplayPlayerScore[Inx].TeamClearHoleMask();
            m_DisplayPlayerScore[Inx].SetBackgroundColorForStrokeHoles(DisplayFrontOrBackScoreCard, DisplayMode);
            m_DisplayPlayerScore[Inx].DisplayPlayerTotalScore(DisplayFrontOrBackScoreCard, DisplayMode);

            m_DisplayPlayerScore[Inx].updateTeamScoreTotals(m_TeamScoreTotals, DisplayFrontOrBackScoreCard, DisplayMode); // get the team score from each player
        }

        m_ScoreCardDisplay.DisplayTeamScoresOnScoreCard(m_TeamScoreTotals, DisplayFrontOrBackScoreCard);            // display the team scores on the score card

    }

    /*
This function handles the next and previous button click
*/
    private View.OnClickListener butPlayersScore = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int ButtonId = view.getId();

            switch (ButtonId) {
                case R.id.Player_minus_0:
                    m_DisplayPlayerScore[0].SubtractStrokeToCurrentScore();
                    break;
                case R.id.Player_plus_0:        // update Player 1 score
                    m_DisplayPlayerScore[0].AddStrokeToCurrentScore();
                    break;

                case R.id.Player_minus_1:
                    m_DisplayPlayerScore[1].SubtractStrokeToCurrentScore();
                    break;
                case R.id.Player_plus_1:        // update Player 1 score
                    m_DisplayPlayerScore[1].AddStrokeToCurrentScore();
                    break;

                case R.id.Player_minus_2:
                    m_DisplayPlayerScore[2].SubtractStrokeToCurrentScore();
                    break;

                case R.id.Player_plus_2:        // update Player 1 score
                    m_DisplayPlayerScore[2].AddStrokeToCurrentScore();
                    break;
                case R.id.Player_minus_3:
                    m_DisplayPlayerScore[3].SubtractStrokeToCurrentScore();
                    break;

                case R.id.Player_plus_3:        // update Player 1 score
                    m_DisplayPlayerScore[3].AddStrokeToCurrentScore();
                    break;

                default:
                    Toast.makeText(DisplayScoreCardDetail.this, "butPlayersScore not coded " + ButtonId, LENGTH_SHORT).show();
                    break;
            }
        }
    };
    /*
 This function will handle the team gross score from the player's current score. The user is high lighting the player's gross
  */
    private View.OnClickListener tvTeamGrossScoring = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int textId = view.getId();

            int CurrentHole = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();

            switch (textId) {
                case R.id.Player_hole_score_0: // selecting the player's name, the team score will use the handicap score for the hole
                    m_DisplayPlayerScore[0].SetTeamGrossScore(CurrentHole);
                    break;
                case R.id.Player_hole_score_1:
                    m_DisplayPlayerScore[1].SetTeamGrossScore(CurrentHole);
                    break;
                case R.id.Player_hole_score_2:
                    m_DisplayPlayerScore[2].SetTeamGrossScore(CurrentHole);
                    break;

                case R.id.Player_hole_score_3:
                    m_DisplayPlayerScore[3].SetTeamGrossScore(CurrentHole);
                    break;

                default:
                    Toast.makeText(DisplayScoreCardDetail.this, "tvTeamNetScoring not coded " + textId, LENGTH_SHORT).show();
            }
        }
    };
    /*
This function will handle the team score from the player's current score. The user is high lighting the player's Net
*/
    private View.OnClickListener tvTeamNetScoring = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int textId = view.getId();
            int CurrentHole = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();

            switch (textId) {
                case R.id.Player_Name_0: // selecting the player's name, the team score will use the handicap score for the hole
                    m_DisplayPlayerScore[0].SetTeamNetScore(CurrentHole);
                    break;
                case R.id.Player_Name_1:
                    m_DisplayPlayerScore[1].SetTeamNetScore(CurrentHole);
                    break;
                case R.id.Player_Name_2:
                    m_DisplayPlayerScore[2].SetTeamNetScore(CurrentHole);
                    break;
                case R.id.Player_Name_3:
                    m_DisplayPlayerScore[3].SetTeamNetScore(CurrentHole);
                    break;
                default:
                    Toast.makeText(DisplayScoreCardDetail.this, "tvTeamScoring not coded " + textId, LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /*
  This function will handler the long click for team scoring - the player's net score will be used twice - ie main in the box type of scoring
   */
    private void tvPlayCurScoreDoubleGrossTeamScoring(View view) {
        int textId = view.getId();   // the id of the text view being long clicked - Player's name used for team scoring using the score twice - in man in the box scoring
        int CurrentHole = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();

        switch (textId) {
            case R.id.Player_hole_score_0:  // selecting the player's current score, the team score will use the net score for the hole
                m_DisplayPlayerScore[0].SetTeamDoubleGrossScore(CurrentHole);
                break;
            case R.id.Player_hole_score_1:
                m_DisplayPlayerScore[1].SetTeamDoubleGrossScore(CurrentHole);
                break;
            case R.id.Player_hole_score_2:
                m_DisplayPlayerScore[2].SetTeamDoubleGrossScore(CurrentHole);
                break;
            case R.id.Player_hole_score_3:
                m_DisplayPlayerScore[3].SetTeamDoubleGrossScore(CurrentHole);
                break;

            default:
                Toast.makeText(DisplayScoreCardDetail.this, "tvTeamGrossScoring not coded " + textId, LENGTH_SHORT).show();
        }
    }

    /*
    This function will handler the long click for team scoring - the player's gross score will be used twice - ie man in the box type of scoring
     */
    private void tvPlayNameDoubleNetLongTeamScoring(View view) {
        int textId = view.getId();   // the id of the text view being long clicked - Player's name used for team scoring using the score twice - in man in the box scoring
        int CurrentHole = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();

        switch (textId) {
            case R.id.Player_Name_0:  // selecting the player's score, the team score will use the net score for the hole
                m_DisplayPlayerScore[0].SetTeamDoubleNetScore(CurrentHole);
                break;
            case R.id.Player_Name_1:
                m_DisplayPlayerScore[1].SetTeamDoubleNetScore(CurrentHole);
                break;
            case R.id.Player_Name_2:
                m_DisplayPlayerScore[2].SetTeamDoubleNetScore(CurrentHole);
                break;
            case R.id.Player_Name_3:
                m_DisplayPlayerScore[3].SetTeamDoubleNetScore(CurrentHole);
                break;
            default:
                Toast.makeText(DisplayScoreCardDetail.this, "tvPlayGrossLongTeamScoring not coded " + textId, LENGTH_SHORT).show();
        }
    }

    /*
        This function will load the Point Quote values into the point quato array - the user can set the values for the course select menu (upper right ...)
        */
    private int[] LoadPointQuotaData() {
        String Value_str;
        int[] PointQuota;
        PointQuota = new int[PQ_END];
        SharedPreferences pref = getSharedPreferences("PointQuota", MODE_PRIVATE);
        if (pref != null) {

            Value_str = pref.getString(QUOTA_TARGET, "36");
            PointQuota[PQ_TARGET] = Integer.parseInt(Value_str);
            Value_str = pref.getString(QUOTA_EAGLE, "8");
            PointQuota[PQ_EAGLE] = Integer.parseInt(Value_str);
            Value_str = pref.getString(QUOTA_BIRDIE, "4");
            PointQuota[PQ_BIRDEIS] = Integer.parseInt(Value_str);
            Value_str = pref.getString(QUOTA_PAR, "2");
            PointQuota[PQ_PAR] = Integer.parseInt(Value_str);
            Value_str = pref.getString(QUOTA_BOGGEY, "1");
            PointQuota[PQ_BOGGY] = Integer.parseInt(Value_str);
            Value_str = pref.getString(QUOTA_DOUBLE, "0");
            PointQuota[PQ_DOUBLE] = Integer.parseInt(Value_str);
            Value_str = pref.getString(QUOTA_OTHER, "0");
            PointQuota[PQ_OTHER] = Integer.parseInt(Value_str);
        }
        return PointQuota;
    }

    /*
This is a common exit point for this screen.
 */
    private void DisplayGameSummaryexitThisScreen() {
        Intent intentSendDataBack = new Intent();   // data sent back to the calling activity
        intentSendDataBack.putExtra(NEXT_SCREEN, SCREEN_GAME_SUMMARY);
        setResult(RESULT_OK, intentSendDataBack);

        finish();       // close this screen or exit this screen
    }

    /*
       This function make sure we close the database
        */
    @Override
    protected void onDestroy() {
        int EnumStateValue;
        super.onDestroy();
        if (m_Realm != null) {
            m_CurrentHole = m_ScoreCardDisplay.getTheCurrentHoleFromScoreCard();
            m_RealmScoreCardAccess.setCurrentGolfHoleBeingPlayed(m_CurrentHole);      // save the current hole in case we exit and come back
            m_Realm.close();
            m_Realm = null;
        }
    }

}
