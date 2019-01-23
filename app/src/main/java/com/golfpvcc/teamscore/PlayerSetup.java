package com.golfpvcc.teamscore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.golfpvcc.teamscore.Database.PlayerRecord;
import com.golfpvcc.teamscore.Database.RealmScoreCardAccess;

import io.realm.Realm;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.COURSE_NAME;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_FRONT_NINE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NEXT_SCREEN;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PLAYER_TOTAL;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_COURSE_LIST;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_GAME_ON;

public class PlayerSetup extends AppCompatActivity {
    String PackageName, mCourseName;
    Button ButSelect;
    Realm m_realm;
    TextView tv_CourseName;
    RealmScoreCardAccess mRealmScoreCardAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players_setup_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PackageName = getCallingActivity().getPackageName();

        ButSelect = (Button) findViewById(R.id.selectGame);
        ButSelect.setOnClickListener(mButOnClickListenerExit);      // this button will now call that function - mButOnClickListenerExit

        ButSelect = (Button) findViewById(R.id.cancel);
        ButSelect.setOnClickListener(mButOnClickListenerExit);      // this button will now call that function - mButOnClickListenerExit

        ButSelect = (Button) findViewById(R.id.update);
        ButSelect.setOnClickListener(mButOnClickListenerExit);      // this button will now call that function - mButOnClickListenerExit

        mCourseName = getIntent().getStringExtra(COURSE_NAME);
        tv_CourseName = (TextView) findViewById(R.id.course_name);
        tv_CourseName.setText(mCourseName);
        ReadPlayersFromDatabase();
    }

    /*
    This function will read the player names and handicaps from the last game played
     */
    private void ReadPlayersFromDatabase() {
        EditText ET_Player, ET_Handicap;
        int PlayerCount;
        PlayerRecord Player;

        try {
            m_realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            Log.e("MYAPP", "exception", e);
        }
        mRealmScoreCardAccess = new RealmScoreCardAccess(m_realm);
        mRealmScoreCardAccess.ReadTodayScoreCardRecord();           // get the current score card for today's game

        PlayerCount = mRealmScoreCardAccess.PlayerRecordCnt();
        if (0 < PlayerCount) {
            for (int x = 0; (x < PlayerCount) && x < PLAYER_TOTAL; x++) {
                Player = mRealmScoreCardAccess.GetPlayerFromScoreCard(x);

                String PlayerTextID = "player_" + (x + 1);
                int PlayerresID = getResources().getIdentifier(PlayerTextID, "id", PackageName);
                ET_Player = ((EditText) findViewById(PlayerresID));
                ET_Player.setText(Player.get_PlayerName()); // looad player's name

                String HandicapTextID = "handicap_" + (x + 1);
                int HndcappresID = getResources().getIdentifier(HandicapTextID, "id", PackageName);
                ET_Handicap = ((EditText) findViewById(HndcappresID));
                ET_Handicap.setText("" + Player.getM_Handicap());    // will set the handicap for an int to a string
            }
        }
    }

    View.OnClickListener mButOnClickListenerExit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText ET_Player, ET_Handicap;
            String PlayerName, HndCap;
            PlayerRecord[] Players;
            byte[][] PlayerScores;
            int id = view.getId(), CurrentTotalPlayer = 0;

            Intent intentSendDataBack = new Intent();   // data sent back to the calling activity

            if (id == R.id.selectGame || id == R.id.update) {
                Players = new PlayerRecord[PLAYER_TOTAL];
                PlayerScores = new byte[PLAYER_TOTAL][HOLES_18];

                if (id == R.id.update) { // need to save the scores for each play so we can update the name & handicap
                    CurrentTotalPlayer = mRealmScoreCardAccess.PlayerRecordCnt();
                    for (int x = 0; x < CurrentTotalPlayer; x++) {
                        PlayerScores[x] = mRealmScoreCardAccess.GetPlayerFromScoreCard(x).getmByteScore();      // save the player scores
                    }
                }
                mRealmScoreCardAccess.DeleteAllPlayerName();                // remove all player and add the new ones
                mRealmScoreCardAccess.setTodayGolfCoursename(mCourseName);  // Will use the course name to find the handicap and pars for todays game
                mRealmScoreCardAccess.setCurrentGolfHoleBeingPlayed(0);     // start on hole one.
                mRealmScoreCardAccess.setCurrentMachineState(DISPLAY_FRONT_NINE);

                for (int x = 0; x < PLAYER_TOTAL; x++) {
                    String PlayerTextID = "player_" + (x + 1);
                    int PlayerresID = getResources().getIdentifier(PlayerTextID, "id", PackageName);
                    ET_Player = ((EditText) findViewById(PlayerresID));
                    PlayerName = ET_Player.getText().toString();

                    String HandicapTextID = "handicap_" + (x + 1);
                    int HndcappresID = getResources().getIdentifier(HandicapTextID, "id", PackageName);
                    ET_Handicap = ((EditText) findViewById(HndcappresID));
                    HndCap = ET_Handicap.getText().toString();

                    if (2 < PlayerName.length()) {
                        Players[x] = new PlayerRecord(Integer.parseInt(HndCap), PlayerName);
                        if (id == R.id.update) { // need to save the scores for each play so we can update the name & handicap
                            Players[x].setmByteScore(PlayerScores[x]);
                        }
                        mRealmScoreCardAccess.AddPlayerToScoreCard(Players[x]);
                    }
                }

                intentSendDataBack.putExtra(NEXT_SCREEN, SCREEN_GAME_ON);
                setResult(RESULT_OK, intentSendDataBack);
            } else {    // user cancel
                intentSendDataBack.putExtra(NEXT_SCREEN, SCREEN_COURSE_LIST);
                setResult(RESULT_OK, intentSendDataBack);
            }
            if (m_realm != null)
                m_realm.close();

            finish();       // close this screen or exit this screen
        }
    };

    /*
    This function make sure we close the database
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

