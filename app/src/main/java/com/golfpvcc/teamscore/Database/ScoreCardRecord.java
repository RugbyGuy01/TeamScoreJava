package com.golfpvcc.teamscore.Database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vinnie on 11/29/2016.
 * The score card record contains the current game, course and players.  Each player record contains the name, handicap and scores for each hole.
 */

public class ScoreCardRecord extends RealmObject {
    @PrimaryKey
    private String m_NameKey = "Today";      // this is the database key
    private String m_courseName;      // this is the database key for this course in the CourseListRecord class
    private String m_GameOptions;     // the game option will be string of comma characters use to determine the game. The first two variable will be the team score for the front & back nine
    private int mMachineState;            // the last hole that was played in the current "game On" activity" Use the this variable when returning for the resume selecion button.
    private int mCurrentHole;            // the last hole that was played in the current "game On" activity" Use the this variable when returning for the resume selecion button.
    private RealmList<PlayerRecord> players; // Declare one-to-many relationships


    public ScoreCardRecord() {
        players = new RealmList<PlayerRecord>();
        m_GameOptions = " ";
    }

    public int getMachineState() {
        return mMachineState;
    }

    public void setMachineState(int MachineState) {
        this.mMachineState = MachineState;
    }

    public int getmCurrentHole() {
        return mCurrentHole;
    }

    public void setmCurrentHole(int CurrentHole) {
        this.mCurrentHole = CurrentHole;
    }

    public String getM_courseName() {
        return m_courseName;
    }

    public void setM_courseName(String m_courseName) {
        this.m_courseName = m_courseName;
    }

    public RealmList<PlayerRecord> getPlayers() {
        return players;
    }
}