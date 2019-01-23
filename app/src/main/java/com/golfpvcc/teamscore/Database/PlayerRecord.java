package com.golfpvcc.teamscore.Database;

import io.realm.RealmObject;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;

/**
 * Created by vinnie on 11/29/2016.
 * This is Java record for 1 player
 */

public class PlayerRecord extends RealmObject {
    private String m_Name;
    private int m_Handicap;
    private byte[] mByteScore;

    public PlayerRecord() {
    }

    public PlayerRecord(int Handicap, String Name) {

        this.m_Handicap = Handicap;
        this.m_Name = Name;
        mByteScore = new byte[HOLES_18];
    }

    public int getM_Handicap() {
        return m_Handicap;
    }

    public String  get_PlayerName() {
        return m_Name;
    }

    public byte[] getmByteScore() {
        return mByteScore;
    }

    public void setmByteScore(byte[] mByteScore) {
        this.mByteScore = mByteScore;
    }

}