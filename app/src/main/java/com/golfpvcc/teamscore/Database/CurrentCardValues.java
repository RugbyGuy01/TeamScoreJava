package com.golfpvcc.teamscore.Database;

/**
 * Created by vinnie on 8/11/2016.
 * One record of the current card hole that user has entered. We have 18 records for each course
 */
public class CurrentCardValues {
    private int m_par;
    private int m_handicap;

    /*
    Get the par for the current hole
     */
    public int getM_par() {
        return m_par;
    }

    /*
    Set the par for the current hole
     */
    public void setM_par(int par) {
        this.m_par = par;
    }

    /*
    Get the current handicap for this hole
     */
    public int getM_handicap() {
        return m_handicap;
    }

    /*
    Set the handicap for the hole
     */
    public void setM_handicap(int handicap) {
        this.m_handicap = handicap;
    }
}
