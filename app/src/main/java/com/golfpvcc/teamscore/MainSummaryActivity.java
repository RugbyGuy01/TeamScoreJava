package com.golfpvcc.teamscore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.golfpvcc.teamscore.Database.PlayerRecord;
import com.golfpvcc.teamscore.Database.RealmScoreCardAccess;
import com.golfpvcc.teamscore.Extras.DialogEmailAddress;
import com.golfpvcc.teamscore.Extras.EmailScores;
import com.golfpvcc.teamscore.Player.DisplayPlayerScoreData;
import com.golfpvcc.teamscore.Player.NineGame;

import io.realm.Realm;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.COURSE_NAME;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.EMAIL_ADDRESS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.FIRST_HOLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NEXT_SCREEN;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NINETH_HOLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NINE_PLAYERS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PLAYER_TOTAL;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_ALBATROSS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_BIRDEIS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_BOGGY;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_DOUBLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_EAGLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_END;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_OTHER;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_PAR;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PQ_TARGET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_ALBATROSS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_BIRDIE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_BOGGEY;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_DOUBLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_EAGLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_OTHER;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_PAR;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_TARGET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_COURSE_LIST;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_COURSE_SELECTED;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_COURSE_SETUP;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_GAME_ON;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_GAME_SUMMARY;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_LOFT_GAME;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_PLAYERS_SETUP;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SUM_END;

/*
The screen Orientation is set in the apps manifest
android:screenOrientation="landscape"
 */
public class MainSummaryActivity extends AppCompatActivity implements DialogEmailAddress.EmailDialogListener {
    Realm m_Realm;
    RealmScoreCardAccess m_RealmScoreCardAccess;
    int[] m_PointQuota;                 // holds the point quota value for eagles, birdies, pars, boggy, double, others
    TextView[][] m_PlayerScoreSummaryTable;      // score grid for displaing the player's scores
    TextView[][] m_TeamScoreSummaryTable;      // score grid for displaing the Team's scores
    TableLayout m_PlayerTableLayoutCreate;       // Player score card table
    TableLayout m_TeamTableLayoutCreate;       // Team score card table
    DisplayPlayerScoreData[] m_PlayerScreenData;
    DisplayPlayerScoreData m_EmailPlayerScreenData;       // player score that will be emailed
    String SelectedCourse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_summary);

        if (true == OpenReamlDatabase()) {
            DisplayGameSummary();
        } else {        // golf course has been deleted or no players on the score card, display the select a golf course screen
            Intent intent = new Intent(this, CourseList.class);    // list the courses to select
            startActivityForResult(intent, 20);                    // display the new screen
        }
    }

    /*
The OnClick function will determine what the user what to do next:
Review the current game
Start a new Game.
Quit the App.
 */
    public void onClickGameSummary(View view) {
        int ButtonId;
        Intent intent = null;
        ButtonId = view.getId();

        switch (ButtonId) {
            case R.id.butSummaryGameOn:
                intent = new Intent(this, DisplayScoreCardDetail.class);    // Start keeping score for the current game being played
                break;

            case R.id.butSummaryNewGame:
                intent = new Intent(this, CourseList.class);    // list the courses to select
                break;

            case R.id.butSummaryGameOver:
                finish();       // close this screen or exit this screen
                break;
        }
        if (intent != null) {
            startActivityForResult(intent, 20);      // display the new screen
        }
    }

    /*
    RESULT_CANCELED
     */
    @Override
    protected void onActivityResult(int currentScreenDisplayed, int resultCode, Intent data) {
        super.onActivityResult(currentScreenDisplayed, resultCode, data);
        int NewScreen = SCREEN_GAME_SUMMARY;

        if (resultCode == RESULT_OK) {

            NewScreen = data.getIntExtra(NEXT_SCREEN, 0);
        }
        resultsStateMacnineScreen(NewScreen, resultCode, data);
    }

    /*
This function will branch to the next screen using the result for the current screen that just exited.
 */
    private void resultsStateMacnineScreen(int currentScreenDisplayed, int screenExitResults, Intent data) {
        Intent newIntent = null;

        switch (currentScreenDisplayed) {
            case SCREEN_GAME_SUMMARY:       // This function is called from the "Game On" screen
                DisplayGameSummary();
                break;

            case SCREEN_COURSE_LIST:
                newIntent = new Intent(this, CourseList.class);    // list the courses to select
                break;

            case SCREEN_COURSE_SELECTED:
                break;

            case SCREEN_COURSE_SETUP:
                break;

            case SCREEN_PLAYERS_SETUP:  // just selected the course from the course list menu
                SelectedCourse = data.getStringExtra(COURSE_NAME);
                newIntent = new Intent(this, PlayerSetup.class);    // list the courses to select
                newIntent.putExtra(COURSE_NAME, SelectedCourse);
                break;

            case SCREEN_LOFT_GAME:
                break;

            case SCREEN_GAME_ON:
                newIntent = new Intent(this, DisplayScoreCardDetail.class);    // Start keeping score for the current game being played
                break;
        }
        if (newIntent != null) {
            startActivityForResult(newIntent, 20);      // display the new screen
        }
    }

    /*
    This function will breakdown the game summary into players and team summary scores.
     */
    private void DisplayGameSummary() {
        int NumberOfPlayers;

        NumberOfPlayers = ValidateGolfCourseAndPlayers();
        if (0 < NumberOfPlayers) {
            BuildSummaryTable(NumberOfPlayers);            // build screen tables
            InitPlayersScreenRecord(m_PointQuota, NumberOfPlayers);      // setup the player's database record class
            DisplayPlayersSummary(NumberOfPlayers);
            DisplayTeamSummary(NumberOfPlayers);
        } else {        // golf course has been deleted or no players on the score card, display the select a golf course screen
            Toast.makeText(this, "Exiting .. no golf courses or players configured. ", Toast.LENGTH_LONG).show();
            finish();       // close this screen or exit this screen
        }
    }

    /*
    The player summary will used the DisplayPlayerHoleData which contains all of the player's information
    This function will display the player's golf summary - "Score", "Quota", "Stfd", "Eagle", "Bird", " Pars", "Bog", "Dbl", "Othr", "Pts"
     */
    private void DisplayPlayersSummary(int NumberOfPlayers) {
        String PlayerName;
        int PlayerHandicap, Row = 0, Col, SummaryScore;

        for (int Inx = 0; Inx < NumberOfPlayers; Inx++) {      // row zero has the table header
            Col = 0;
            Row++;      // next row on the score card,  row zero has the table header
            PlayerName = m_PlayerScreenData[Inx].getPlayerName();
            PlayerHandicap = m_PlayerScreenData[Inx].getPlayerHandicap();
            m_PlayerScoreSummaryTable[Row][Col++].setText(PlayerHandicap + " - " + PlayerName);

            for (int ScoreInx = 0; ScoreInx < SUM_END; ScoreInx++) {
                SummaryScore = m_PlayerScreenData[Inx].GetPlayerStrokeSummary(ScoreInx);
                m_PlayerScoreSummaryTable[Row][Col++].setText(Integer.toString(SummaryScore));      // will update the screen
            }
        }
    }

    /*
    The Team summary will used the DisplayPlayerHoleData which contains all of the player's information about the team's scores
     */
    private void DisplayTeamSummary(int NumberOfPlayers) {
        int Row = 1;        // row 0 is the header row
        DisplayTeamPointQuoteSummary(NumberOfPlayers, Row++);
        DisplayTeamTotalStokeSummary(NumberOfPlayers, Row++);
        DisplayTeamStablefordSummary(NumberOfPlayers, Row);
    }
    /*
This function will calculate the team point quota team scores
 */
    private void DisplayTeamPointQuoteSummary(int NumberOfPlayers, int DisplayRow) {
        float TeamTotalFrontNine, TeamTotalBackNine, TeamTotal, TeamUsedTotalFrontNine, TeamUsedTotalBackNine, TeamUsedTotal;
        int Col = 1;
        String strTeamTotalFront, strTeamTotalBack, strTeamToal;

        TeamTotalFrontNine = TeamTotalBackNine = TeamTotal = 0;
        TeamUsedTotalFrontNine = TeamUsedTotalBackNine = 0;

        for (int Inx = 0; Inx < NumberOfPlayers; Inx++) {
            TeamTotalFrontNine += m_PlayerScreenData[Inx].GetTotalPlayerPointQuotaTotal(FIRST_HOLE, NINETH_HOLE);    // get the front nine quota, will round down
            TeamTotalBackNine += m_PlayerScreenData[Inx].GetTotalPlayerPointQuotaTotal(NINETH_HOLE, HOLES_18);     // get the front nine quota, will round down

            TeamUsedTotalFrontNine += m_PlayerScreenData[Inx].GetTotalUsedPlayerPointQuotaTotal(FIRST_HOLE, NINETH_HOLE);
            TeamUsedTotalBackNine += m_PlayerScreenData[Inx].GetTotalUsedPlayerPointQuotaTotal(NINETH_HOLE, HOLES_18);
        }

        TeamTotal += TeamTotalFrontNine + TeamTotalBackNine;
        TeamUsedTotal = TeamUsedTotalFrontNine + TeamUsedTotalBackNine;

        strTeamTotalFront = Float.toString(TeamTotalFrontNine) + " (" + Float.toString(TeamUsedTotalFrontNine) + ")";
        strTeamTotalBack = Float.toString(TeamTotalBackNine) + " (" + Float.toString(TeamUsedTotalBackNine) + ")";
        strTeamToal = Float.toString(TeamTotal) + " (" + Float.toString(TeamUsedTotal) + ")";

        m_TeamScoreSummaryTable[DisplayRow][Col++].setText(strTeamTotalFront);      // will update the front nine total screen
        m_TeamScoreSummaryTable[DisplayRow][Col++].setText(strTeamTotalBack);      // will update the front back total screen
        m_TeamScoreSummaryTable[DisplayRow][Col].setText(strTeamToal);      // will update the team total screen
    }

    /*
    This function will calculate the team stableford team points ??
     */
    private void DisplayTeamStablefordSummary(int NumberOfPlayers, int DisplayRow) {
        int TeamStablefordFrontNine, TeamStablefordBackNine, TeamStablefordTotal, Col = 1;
        int PlayersStablefordFrontNine, PlayersStablefordBackNine, PlayersStablefordTotal;
        String strTeamStablefordFront, strTeamStablefordBack, strTeamStablefordTotal;

        TeamStablefordFrontNine = TeamStablefordBackNine = 0;
        PlayersStablefordFrontNine = PlayersStablefordBackNine = 0;

        for (int Inx = 0; Inx < NumberOfPlayers; Inx++) {
            TeamStablefordFrontNine += m_PlayerScreenData[Inx].GetTotalStablefordTotal(FIRST_HOLE, NINETH_HOLE);    // get the front nine quota
            TeamStablefordBackNine += m_PlayerScreenData[Inx].GetTotalStablefordTotal(NINETH_HOLE, HOLES_18);    // get the front nine quota

            PlayersStablefordFrontNine += m_PlayerScreenData[Inx].GetPlayerStablefordTotal(FIRST_HOLE, NINETH_HOLE);    // get the front nine quota
            PlayersStablefordBackNine += m_PlayerScreenData[Inx].GetPlayerStablefordTotal(NINETH_HOLE, HOLES_18);    // get the front nine quota
        }
        TeamStablefordTotal = TeamStablefordFrontNine + TeamStablefordBackNine;
        PlayersStablefordTotal = PlayersStablefordFrontNine + PlayersStablefordBackNine;

        strTeamStablefordFront = Integer.toString(PlayersStablefordFrontNine) + " (" + Integer.toString(TeamStablefordFrontNine) + ")";
        strTeamStablefordBack = Integer.toString(PlayersStablefordBackNine) + " (" + Integer.toString(TeamStablefordBackNine) + ")";
        strTeamStablefordTotal = Integer.toString(PlayersStablefordTotal) + " (" + Integer.toString(TeamStablefordTotal) + ")";

        m_TeamScoreSummaryTable[DisplayRow][Col++].setText(strTeamStablefordFront);      // will update the front nine total screen
        m_TeamScoreSummaryTable[DisplayRow][Col++].setText(strTeamStablefordBack);      // will update the back nine total screen
        m_TeamScoreSummaryTable[DisplayRow][Col].setText(strTeamStablefordTotal);            // will update the total screen

    }

    /*
    This function will display the team total strokes for the front, back and total 18 holes
     */
    private void DisplayTeamTotalStokeSummary(int NumberOfPlayers, int DisplayRow) {
        int FrontNineStokes, BackNineStokes, TeamTotalStokes, Col = 1;
        int FrontNineOverUnder, BackNineOverUnder, TotalOverUnder;
        String strTeamTotalFront, strTeamTotalBack, strTeamToal;

        FrontNineStokes = BackNineStokes = 0;
        FrontNineOverUnder = BackNineOverUnder = 0;

        for (int Inx = 0; Inx < NumberOfPlayers; Inx++) {
            FrontNineStokes += m_PlayerScreenData[Inx].GetTotalPlayerStrokesUseTotal(FIRST_HOLE, NINETH_HOLE);    // get the front nine quota
            BackNineStokes += m_PlayerScreenData[Inx].GetTotalPlayerStrokesUseTotal(NINETH_HOLE, HOLES_18);    // get the front nine quota

            FrontNineOverUnder += m_PlayerScreenData[Inx].GetTotalPlayerUnderOverUseTotal(FIRST_HOLE, NINETH_HOLE);    // get the front nine quota
            BackNineOverUnder += m_PlayerScreenData[Inx].GetTotalPlayerUnderOverUseTotal(NINETH_HOLE, HOLES_18);    // get the front nine quota
        }
        TeamTotalStokes = FrontNineStokes + BackNineStokes;
        TotalOverUnder = FrontNineOverUnder + BackNineOverUnder;

        strTeamTotalFront = Integer.toString(FrontNineStokes) + " (" + Integer.toString(FrontNineOverUnder) + ")";
        strTeamTotalBack = Integer.toString(BackNineStokes) + " (" + Integer.toString(BackNineOverUnder) + ")";
        strTeamToal = Integer.toString(TeamTotalStokes) + " (" + Integer.toString(TotalOverUnder) + ")";

        m_TeamScoreSummaryTable[DisplayRow][Col++].setText(strTeamTotalFront);      // will update the front nine total screen
        m_TeamScoreSummaryTable[DisplayRow][Col++].setText(strTeamTotalBack);      // will update the back nine total screen
        m_TeamScoreSummaryTable[DisplayRow][Col].setText(strTeamToal);            // will update the total screen
    }

    /*
    This function will init the Player's hold class - the class has access to the player's database record and keeps track of the player's total and team score.
     */
    private void InitPlayersScreenRecord(int[] PointQuota, int NumberOfPlayers) {
        PlayerRecord MyPlayer;
        int Inx, CurrentHole, Row;
        int[] CoursePar, CourseHandicap;
        NineGame m_Player_9_Game;

        CoursePar = new int[HOLES_18];
        CourseHandicap = new int[HOLES_18];
        for (CurrentHole = 0; CurrentHole < HOLES_18; CurrentHole++) {
            CoursePar[CurrentHole] = m_RealmScoreCardAccess.GetGolfCourseHoleParInt(CurrentHole);               // Save the course hole pars
            CourseHandicap[CurrentHole] = m_RealmScoreCardAccess.GetGolfCourseHoleHandicapInt(CurrentHole);               // Save the course hole handicap
        }

        for (Inx = 0; Inx < NumberOfPlayers; Inx++) {
            if (m_PlayerScreenData[Inx] == null) {          // load the all of the player scores into the app classes - need to calculate the 9 game points
                MyPlayer = m_RealmScoreCardAccess.GetPlayerFromScoreCard(Inx);  // find all of the current player database records on the score card
                m_PlayerScreenData[Inx] = new DisplayPlayerScoreData(this, PointQuota, MyPlayer, CoursePar, CourseHandicap);
            }
        }
        if (NumberOfPlayers == NINE_PLAYERS) {  //This function initialize the player's 9 game scores. We need to check all player's score to determine a player's 9 game points.
            int PlayerGrossScore, Game_9_Score;
            m_Player_9_Game = new NineGame();

            for (CurrentHole = 0; CurrentHole < HOLES_18; CurrentHole++) {
                m_Player_9_Game.ClearTotals();                  // clear the 9 game class totals
                for (Inx = 0; Inx < NumberOfPlayers; Inx++) {
                    PlayerGrossScore = m_PlayerScreenData[Inx].GetplayerNetScore(CurrentHole);  // get the player's gross score add it to thePlayer's 9 game class
                    m_Player_9_Game.AddPlayerGrossScore(Inx, (byte) PlayerGrossScore);
                }

                m_Player_9_Game.sort_9_Scores();    // Now calculate the player 9 game points for this hole
                for (Inx = 0; Inx < NINE_PLAYERS; Inx++) {
                    Game_9_Score = m_Player_9_Game.Get_9_GameScore(Inx);
                    m_PlayerScreenData[Inx].Assign_9_GameScore(CurrentHole, Game_9_Score);    // save the player's points for the 9 game
                }
            }
        }


        for (Inx = 0, Row = 1; Inx < NumberOfPlayers; Inx++, Row++) {
            if (m_PlayerScreenData[Inx] != null) {
                m_PlayerScreenData[Inx].CalculatePlayerScoreSummary(CoursePar, CourseHandicap);     // this loads the player score into the hole classes
                AddTextViewListerner(Inx, Row); // use for emailing score to user
            }
        }
    }

    /*
           This function will add text view of the player's name on the score card, used to email player.
            */
    private void AddTextViewListerner(int Inx, int Row) {
        TextView tv_PlayerName;

        tv_PlayerName = m_PlayerScoreSummaryTable[Row][0];      // text view from screen
        tv_PlayerName.setOnClickListener(tvEmailScoring);
        String strTmp = "summary_player_tv_" + Inx;                            // net team score for the hole

        int tv_Id = getResources().getIdentifier(strTmp, "id", getPackageName());

        tv_PlayerName.setId(tv_Id);      // id used in the email function

        m_PlayerScreenData[Inx].setTvPlayerLowerCurScore(tv_PlayerName);
    }

    /*

     */
    private View.OnClickListener tvEmailScoring = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int textId = view.getId(), Inx = -1;

            switch (textId) {
                case R.id.summary_player_tv_0:
                    Inx = 0;
                    break;
                case R.id.summary_player_tv_1:
                    Inx = 1;
                    break;
                case R.id.summary_player_tv_2:
                    Inx = 2;
                    break;
                case R.id.summary_player_tv_3:
                    Inx = 3;
                    break;
            }
            if (-1 < Inx) {
                m_EmailPlayerScreenData = m_PlayerScreenData[Inx];
                m_EmailPlayerScreenData.getTvPlayerLowerCurScore().setTextColor(getResources().getColor(R.color.one_under_color));
                GetEmailAddressFromSystem();
                m_EmailPlayerScreenData.getTvPlayerLowerCurScore().setTextColor(getResources().getColor(R.color.light_green));
            }
        }
    };

    /*
    This function will use the phone's email app to email the player's score.
     */
    public void SendEmailToUser(DisplayPlayerScoreData tmpPlayerScreenData, String EmailTo) {
        EmailScores MyEmailApp;

        String Subject = "Player's Score: ";
        Subject += tmpPlayerScreenData.getPlayerName();
        Subject += "  - " + m_RealmScoreCardAccess.getTodayGolfCoursename();        // get the current score for today's game
        String Body = tmpPlayerScreenData.getSpreadSheetScore();


        MyEmailApp = new EmailScores(this);
        MyEmailApp.SetEmailAddress(EmailTo);
        MyEmailApp.SetEmailSubject(Subject);
        MyEmailApp.SetEmailBody(Body);
        MyEmailApp.ToPostOffice();
    }

    /*
    This function will get the stored email address in the phone.
     */
    private String GetEmailAddressFromSystem() {
        String EmailAddress = "VGamble";

        final SharedPreferences pref = getSharedPreferences(EMAIL_ADDRESS, MODE_PRIVATE);
        EmailAddress = pref.getString(EMAIL_ADDRESS, "V");
        if (!isEmailValid(EmailAddress)) {
            DialogEmailAddress EmailDialog = new DialogEmailAddress();

            EmailDialog.show(getSupportFragmentManager(), "Configure Email");
        } else {
            SendEmailToUser(m_EmailPlayerScreenData, EmailAddress);
        }
        return EmailAddress;
    }

    /*
    The email address send by the DialogEmailAddress class interface
     */
    @Override
    public void SendEmailAddress(String EmailAddressForUser) {
        final SharedPreferences pref = getSharedPreferences(EMAIL_ADDRESS, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();          // get the editor for the databse
        editor.putString(EMAIL_ADDRESS, EmailAddressForUser);   // save the new email address
        editor.apply();

        SendEmailToUser(m_EmailPlayerScreenData, EmailAddressForUser);
    }

    /*
    This function validates the email address
    */
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

            Value_str = pref.getString(QUOTA_ALBATROSS, "8");
            PointQuota[PQ_ALBATROSS] = Integer.parseInt(Value_str);
            Value_str = pref.getString(QUOTA_EAGLE, "6");
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
    This function will setup the real databse and get the course information
     */
    boolean OpenReamlDatabase() {
        boolean status = false;
        int NumberOfPlayers;

        m_PointQuota = LoadPointQuotaData();
        m_Realm = Realm.getDefaultInstance();
        if (m_Realm != null) {
            m_RealmScoreCardAccess = new RealmScoreCardAccess(m_Realm);

            NumberOfPlayers = ValidateGolfCourseAndPlayers();
            if (0 < NumberOfPlayers)
                status = true;
        }
        return status;
    }

    /*
    This function check to make sure the user did not delete the golf course or players after playing around.
     */
    int ValidateGolfCourseAndPlayers() {
        String GolfCourseName, GolfCourseHeader = "Course: ";
        int NumberOfPlayers = 0, RecordStatus;
        m_RealmScoreCardAccess.ReadTodayScoreCardRecord();           // get the current score card from the database for today's game
        GolfCourseName = m_RealmScoreCardAccess.getTodayGolfCoursename();        // get the current score for today's game
        if (GolfCourseName != null) {
            if (GolfCourseName.length() > 2) {
                RecordStatus = m_RealmScoreCardAccess.ReadScoreCardFromDatabase(GolfCourseName);        // will read in the pars and handicaps from the current playing course
                if (RecordStatus == 0) {
                    m_PointQuota = LoadPointQuotaData();
                    NumberOfPlayers = m_RealmScoreCardAccess.PlayerRecordCnt();  // how many player on the score card

                    TextView tvPlayerSummary = findViewById(R.id.textCourseName);
                    GolfCourseHeader += GolfCourseName;
                    tvPlayerSummary.setText(GolfCourseHeader);

                    m_PlayerScreenData = new DisplayPlayerScoreData[PLAYER_TOTAL];   // have to deal with a player being added after the round starts
                } else
                    Toast.makeText(this, "Resume failed, golf course has been deleted! ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Please configure golf course.", Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(this, "Resume failed, golf course has been deleted! ", Toast.LENGTH_LONG).show();
        return (NumberOfPlayers);
    }

    /*
    This function will clear the player and summary tables on the summary screen
     */
    private void ClearPlayerAndSummaryTables() {

        if (m_PlayerTableLayoutCreate != null) {
            int count = m_PlayerTableLayoutCreate.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = m_PlayerTableLayoutCreate.getChildAt(i);
                if (child instanceof TableRow)
                    ((ViewGroup) child).removeAllViews();
            }
        }
        if (m_TeamTableLayoutCreate != null) {
            int count = m_TeamTableLayoutCreate.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = m_TeamTableLayoutCreate.getChildAt(i);
                if (child instanceof TableRow)
                    ((ViewGroup) child).removeAllViews();
            }
        }
        m_PlayerScoreSummaryTable = null;
        m_TeamScoreSummaryTable = null;
    }

    /*
    This function will build the summary table for the player's scores
     */
    private void BuildSummaryTable(int NumberOfPlayers) {
        String[] PlayerColumnText = {"Player", "Score", "Quota", "Sbfd", "Eagle", "Bird", "Pars", "Bog", "Dbl", "Othr", "9 Pts"};
        String[] PlayerRowText = {"", "Player 1", "Player 2", "Player 3", "Player 4"};
        String[] TeamColumnText = {"Team", "Front", "Back", "Total"};
        String[] TeamRowText = {"", "Pt. Quota (Used)", "Score (O/U)", "Stableford (Used)"};

        ClearPlayerAndSummaryTables();

        int rl = PlayerRowText.length;
        int cl = PlayerColumnText.length;
        rl -= (PLAYER_TOTAL - NumberOfPlayers);
        m_PlayerScoreSummaryTable = new TextView[rl][cl];                // row col -  keeps track of all of the cells text views

        m_PlayerTableLayoutCreate = createSummaryTableLayout(m_PlayerScoreSummaryTable, PlayerRowText, PlayerColumnText, rl, cl);
        TableLayout PlayertableLayout = findViewById(R.id.playerSummary);    // find the table layout in the xml file
        PlayertableLayout.addView(m_PlayerTableLayoutCreate);

        rl = TeamRowText.length;
        cl = TeamColumnText.length;
        m_TeamScoreSummaryTable = new TextView[rl][cl];                // row col -  keeps track of all of the cells text views

        m_TeamTableLayoutCreate = createSummaryTableLayout(m_TeamScoreSummaryTable, TeamRowText, TeamColumnText, rl, cl);
        TableLayout TeamtableLayout = findViewById(R.id.teamSummary);    // find the table layout in the xml file
        TeamtableLayout.addView(m_TeamTableLayoutCreate);
    }

    /*
      This function will create the summary score table with the names and scores
       */
    private TableLayout createSummaryTableLayout(TextView[][] Table, String[] rv, String[] cv, int rowCount, int columnCount) {

        // 1) Create a tableLayout and its params
        TableLayout tableLayout = new TableLayout(this);
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
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
                if (Row == 0) {
                    textView.setBackgroundColor(Color.LTGRAY);
                } else
                    textView.setBackgroundColor(Color.WHITE);

                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(20);
                textView.setPadding(3, 3, 3, 3);

                Table[Row][Col] = textView;
                if (Row == 0) {
                    textView.setText(cv[Col]);       // Display the colunm headers
                } else if (Col == 0) {
                    textView.setText(rv[Row]);       // Display the row headers
                } else {
                    textView.setText(" ");
                }
                tableRow.addView(textView, tableRowParams);     // 5) add textView to tableRow
            }
            tableLayout.addView(tableRow, tableLayoutParams);   // 6) add tableRow to tableLayout
        }
        return tableLayout;
    }

}

