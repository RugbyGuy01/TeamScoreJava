package com.golfpvcc.teamscore.Database;

import android.widget.TextView;

import com.golfpvcc.teamscore.R;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.JUST_RAW_SCORE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PLAYER_TOTAL;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.TEAM_GROSS_SCORE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.TEAM_NET_SCORE;

/**
 * Created by vinnie on 12/3/2016.
 * This class will handle all of the database functions dealing with the score card record. The score card record contains the current game, course and players.
 * Each player record contains the name, handicap and scores for each hole. Use this class to access and save the score card record - there is only one record.
 */

public class RealmScoreCardAccess {
    private Realm m_realm;
    String[] mHoleHandicap, mHolePar;   // an array of handicaps in string format
    ScoreCardRecord mScoreCard;
    int[] mTeamHoleScore, mTeamTotalScore, mHoleUsedByPlayers;
    TextView[] mtvTeamHighLite;

    /*
    Constructor for class
     */
    public RealmScoreCardAccess(Realm realm) {
        this.m_realm = realm;
        mScoreCard = new ScoreCardRecord();
        mTeamHoleScore = new int[PLAYER_TOTAL];
        mtvTeamHighLite = new TextView[PLAYER_TOTAL];
        mTeamTotalScore = new int[HOLES_18];
        mHoleUsedByPlayers = new int[HOLES_18];
    }
/*
This function will return the total for the front nine score :
starthole = 0 - endHole = 9
Or
This function will return the total for the back nine score :
starthole = 9 - endHole = 18
 */

    public void setmTeamTotalScore(int Hole, int TeamScore) {
        this.mTeamTotalScore[Hole] = TeamScore;
    }


    public void setHoleUsedByPlayers(int Hole, int HolesUsed) {
        this.mHoleUsedByPlayers[Hole] = HolesUsed;
    }

    public void AddPlayerToScoreCard(PlayerRecord mPlayer) {
        m_realm.beginTransaction();
        mScoreCard.getPlayers().add(mPlayer);
        m_realm.copyToRealmOrUpdate(mScoreCard);
        m_realm.commitTransaction();
    }

    public PlayerRecord GetPlayerFromScoreCard(int Inx) {
        PlayerRecord mPlayerRecord = null;

        if (Inx < mScoreCard.getPlayers().size())
            mPlayerRecord = mScoreCard.getPlayers().get(Inx);
        return (mPlayerRecord);
    }

    public int PlayerRecordCnt() {
        RealmResults<PlayerRecord> results;
        int PlayerCnt;
        results = m_realm.where(PlayerRecord.class).findAll();
        PlayerCnt = mScoreCard.getPlayers().size();
        return (PlayerCnt);
    }

    public int ReadTodayScoreCardRecord() {
        RealmResults<ScoreCardRecord> ScoreCardRecords;
        ScoreCardRecords = m_realm.where(ScoreCardRecord.class).findAll(); // should only find one record unless this is the first time
        if (0 < ScoreCardRecords.size())
            mScoreCard = ScoreCardRecords.get(0);
        return (ScoreCardRecords.size());
    }

    public void DeleteAllPlayerName() {
        RealmResults<PlayerRecord> results;
        results = m_realm.where(PlayerRecord.class).findAll();
        if (0 < results.size()) {
            m_realm.beginTransaction();
            results.deleteAllFromRealm();
            m_realm.commitTransaction();
        }
    }

    /*
    This function will update/save the player's score in the database
     */
    public void SaveThePlayerHoleScore(PlayerRecord PlayerDataBaseRecord, int hole, int Score) {

        m_realm.beginTransaction();

        byte[] mByteScore = PlayerDataBaseRecord.getmByteScore();
        mByteScore[hole] = (byte) Score;
        PlayerDataBaseRecord.setmByteScore(mByteScore);

        m_realm.copyToRealmOrUpdate(mScoreCard);
        m_realm.commitTransaction();
    }


    /*
    This function will return Player's handicap
     */
    public int GetPlayerHandicap(int PlayerInx) {
        int PlayerHandicap = 0;

        if (PlayerInx < mScoreCard.getPlayers().size()) {
            PlayerRecord Player = GetPlayerFromScoreCard(PlayerInx);
            if (Player != null) {
                PlayerHandicap = Player.getM_Handicap();
            }
        }
        return (PlayerHandicap);
    }

    /*
    This function will set the color of the player's hole score on the score card - red for birdie, ...
     */
    public long GetTextColorForHole(int PlayerInx, int hole) {
        long StrokeColor = R.color.par_hole_color;
        int PlayerScore = 0, GrossScore;
        int ParForHole = 0;

        if (PlayerInx < mScoreCard.getPlayers().size()) {
            PlayerRecord Player = GetPlayerFromScoreCard(PlayerInx);
            if (Player != null) {
                byte[] mByteScore = Player.getmByteScore();
                PlayerScore = (int) (JUST_RAW_SCORE & mByteScore[hole]);
                ParForHole = Integer.parseInt(mHolePar[hole]);
                GrossScore = PlayerScore - ParForHole;
                switch (GrossScore) {
                    case -2:
                        StrokeColor = R.color.two_under_color;
                        break;
                    case -1:
                        StrokeColor = R.color.one_under_color;
                        break;
                    case -0:
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
            }
        }
        return (StrokeColor);
    }

    /*
    This function will get the player's current raw score for the hole played from the database
     */
    public int GetRawPlayerHoleScore(int PlayerInx, int hole) {
        int PlayerScore = 0;

        if (PlayerInx < mScoreCard.getPlayers().size()) {
            PlayerRecord Player = GetPlayerFromScoreCard(PlayerInx);
            if (Player != null) {
                byte[] mByteScore = Player.getmByteScore();
                PlayerScore = (int) mByteScore[hole];
            }
        }
        return (PlayerScore);
    }


    /*
This function will get the player's total score up to hole played from the database
 */
    public String GetPlayerHoleTotalScore(int PlayerInx, int hole) {
        int intPlayerScore = 0, PlayerScoreForHole;
        String strPlayerScore = " ";

        if (PlayerInx < mScoreCard.getPlayers().size()) {
            PlayerRecord Player = GetPlayerFromScoreCard(PlayerInx);
            if (Player != null) {
                byte[] mByteScore = Player.getmByteScore();
                for (int x = 0; x <= hole; x++) {
                    PlayerScoreForHole = mByteScore[x];
                    intPlayerScore += (int) (JUST_RAW_SCORE & PlayerScoreForHole);     // add up the total score for the holes played
                    if (((PlayerScoreForHole & TEAM_GROSS_SCORE) == TEAM_GROSS_SCORE) || (PlayerScoreForHole & TEAM_NET_SCORE) == TEAM_NET_SCORE) {// check for the team score flags
                        mTeamTotalScore[x] += this.GetTeamScoreForThisPlayer(PlayerInx, x, PlayerScoreForHole);
                        mHoleUsedByPlayers[x]++;    // this player score was used for the team total
                    }
                }
                strPlayerScore = Integer.toString(intPlayerScore);
            }
        }
        return strPlayerScore;
    }

    /*
The score record is always save to the 'today' primary key.
This function will get today's score record (should only be one record) with the player's names and handicaps.
The Today's score card record also contains the course they are playing, get that course name record so you have the handicaps
 and pars for today course.
 */
    public int ReadScoreCardFromDatabase(String CourseName) {

        RealmResults<CourseListRecord> CourseListRecords;      // find the current course record, we need the par and handicap values for that course
        CourseListRecord CurrentCourseRecord;
        int Status = 0;

        // find the golf course record and save the hole pars and handicaps into local variables
        CourseListRecords = m_realm.where(CourseListRecord.class).equalTo("m_courseName", CourseName).findAll();
        if (0 < CourseListRecords.size()) {
            CurrentCourseRecord = CourseListRecords.get(0);     // this should be the course we are playing

            String HandicapString = CurrentCourseRecord.getM_HolesHandicap();   // "1,3,5,7,9,11,13,15,17,2,4,6,8,10,12,14,16,18" this is the record format
            mHoleHandicap = HandicapString.split(",");

            String ParString = CurrentCourseRecord.getM_HolesPar();
            mHolePar = ParString.split(",");
        } else {
            Status = -1;
        }
        return (Status);
    }

    /************
     * This function will set the score card name.
     *
     * @param coursename
     */
    public void setTodayGolfCoursename(String coursename) {
        m_realm.beginTransaction();
        mScoreCard.setM_courseName(coursename);
        m_realm.copyToRealmOrUpdate(mScoreCard);
        m_realm.commitTransaction();
    }

    /*
    This function will return the current score card golf course name the boys are playing today
     */
    public String getTodayGolfCoursename() {
        RealmResults<ScoreCardRecord> ScoreCardRecords;
        ScoreCardRecords = m_realm.where(ScoreCardRecord.class).findAll(); // should only find one record unless this is the first time

        if (0 < ScoreCardRecords.size())
            mScoreCard = ScoreCardRecords.first();
        else
            mScoreCard.setM_courseName(" ");
        return mScoreCard.getM_courseName();
    }

    /*
    This function will return the string for the par on the hole
     */
    public String GetGolfCourseHoleParStr(int Inx) {
        if (Inx < HOLES_18)
            return mHolePar[Inx];
        else
            return " ";
    }

    /*
    This function will return the interger for the par on the hole
     */
    public int GetGolfCourseHoleParInt(int Inx) {
        if (Inx < HOLES_18)
            return Integer.parseInt(mHolePar[Inx]);
        else
            return 0;
    }

    /*
       This function will return the string for the Handicap on the hole
        */
    public String GetGolfCourseHoleHandicapStr(int Hole) {
        if (Hole < HOLES_18)
            return mHoleHandicap[Hole];
        else
            return "Err";
    }

    /*
       This function will return the integer for the Handicap on the hole
        */
    public int GetGolfCourseHoleHandicapInt(int Hole) {
        if (Hole < HOLES_18)
            return Integer.parseInt(mHoleHandicap[Hole]);
        else
            return 0;
    }

    /*
    This function will get the curent hole being played on the score card
     */
    public int getCurrentGolfHoleBeingPlayed() {
        return (mScoreCard.getmCurrentHole());
    }

    /*
This function will set the last hole being played on the score card
 */
    public void setCurrentGolfHoleBeingPlayed(int CurrentHole) {
        m_realm.beginTransaction();
        mScoreCard.setmCurrentHole(CurrentHole);
        m_realm.commitTransaction();
    }

    /*
    This function will get the curent Machine state of the display on the score card
     */
    public int getCurrentMachineState() {
        int MachineState;
        MachineState = mScoreCard.getMachineState();
        return (MachineState);
    }

    /*
This function will set the last hole being played on the score card
 */
    public void setCurrentMachineState(int MachineState) {
        m_realm.beginTransaction();
        mScoreCard.setMachineState(MachineState);
        m_realm.commitTransaction();
    }

    /*
    This function will save the score card into the database, a player's record has been updated, now save it
     */
    public void SaveScoreRecordToDatabase() {
        m_realm.beginTransaction();
        m_realm.copyToRealmOrUpdate(mScoreCard);
        m_realm.commitTransaction();
    }

    /*
    This function will get the team score for this player - check if the player gets a stroke before returning the team score.
     */
    public int GetTeamScoreForThisPlayer(int PlayerInx, int currentHole, int intScore) {
        int TeamScoreForHole = intScore & JUST_RAW_SCORE;

        if ((intScore & TEAM_NET_SCORE) == TEAM_NET_SCORE) {
            int PlayerHandicap = GetPlayerHandicap(PlayerInx);
            int HoleHandicap = GetGolfCourseHoleHandicapInt(currentHole);
            if (HoleHandicap <= PlayerHandicap) {
                TeamScoreForHole--; // the player get a stroke on this hole
                if (HOLES_18 < PlayerHandicap) {
                    if (HoleHandicap <= (PlayerHandicap % HOLES_18))
                        TeamScoreForHole--; // the player gets two strokes on this hole
                }
            }
        }
        int Par = GetGolfCourseHoleParInt(currentHole);
        TeamScoreForHole -= Par;
        return (TeamScoreForHole);
    }

    /*
    This function will return the number of birdies a player had for the round.
     */
    public String getBirdieTotal(int PlayerInx) {
        String strBirdieTotal;
        int Par, Hole, HoleScore, intBirdieTotal = 0;

        for (Hole = 0; Hole < HOLES_18; Hole++) {
            HoleScore = JUST_RAW_SCORE & GetRawPlayerHoleScore(PlayerInx, Hole);
            Par = GetGolfCourseHoleParInt(Hole);
            if (0 < HoleScore && HoleScore < Par)
                intBirdieTotal++;       // we could have an eagle, next version
        }

        strBirdieTotal = Integer.toString(intBirdieTotal);

        return strBirdieTotal;
    }

}