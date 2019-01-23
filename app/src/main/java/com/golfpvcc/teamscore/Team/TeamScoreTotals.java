package com.golfpvcc.teamscore.Team;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;

/*
The Team Score Total is a collection of all the players team scores for each hole and the number time a score was used for a hole
 */
public class TeamScoreTotals {
    private byte[] mTeamHoleScore, mTeamHolesUsedByPlayers;

    /*
    Constructor
     */
    public TeamScoreTotals() {
        mTeamHoleScore = new byte[HOLES_18];        // keeps track of player totals for each hole
        mTeamHolesUsedByPlayers = new byte[HOLES_18];   // Keeps track of how many times a score was used.
    }
    /*******
     *  This function will clear all of the totals
     */
    public void ClearAll() {

        for( int Inx = 0; Inx < HOLES_18; Inx++) {
            mTeamHoleScore[Inx] = 0;
            mTeamHolesUsedByPlayers[Inx] = 0;
        }
    }
    /*******
     *  This function will clear the Team Hole score
     */
    public void ClearHole(int HoleNumber) {

        mTeamHoleScore[HoleNumber] = 0;
        mTeamHolesUsedByPlayers[HoleNumber] = 0;
    }
    /*
    This function will add a player's team score for the hole to the Team Score Total Class
     */
    public void AddPlayerTeamHoleScore( int HoleNumber, byte TeamScore) {

        mTeamHoleScore[HoleNumber] += TeamScore;
    }

    /*
    This function will add a player's team used score for the hole to the Team Score Total Class
     */
    public void AddPlayerTeamUsedScore( int HoleNumber, byte TeamUsedScore) {

        mTeamHolesUsedByPlayers[HoleNumber] += TeamUsedScore;
    }
    /*
    This function will get the team total score for each hole
     */
    public int getTeamHoleScore( int HoleNumber){
        return (mTeamHoleScore[HoleNumber]);
    }
    /*
    This function will get the team total score for each hole
     */
    public int getTeamUsedScore( int HoleNumber){
        return (mTeamHolesUsedByPlayers[HoleNumber]);
    }
}