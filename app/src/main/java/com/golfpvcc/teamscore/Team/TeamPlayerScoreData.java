package com.golfpvcc.teamscore.Team;

import com.golfpvcc.teamscore.DisplayScoreCardDetail;
import com.golfpvcc.teamscore.R;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.DOUBLE_TEAM_SCORE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NINETH_HOLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREENHOLES;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.TEAM_NET_SCORE;

/**
 *  This class will hold of the team scores for each player depending on the team selection when saving the player score.  The Team Score Totals class will collect each player's
 *  Team score to calculate the totals for each hole.
 */
public class TeamPlayerScoreData {
    private byte[] mTeamNetHoleScore, mTeamOverUnderHoleScore, mTeamHolesUsedByPlayers, mTeamPointQuotaScore, mTeamStablefordScore, m_HolePar;
    private byte mTeamHoleMask;

    /*
    Constructor
     */

    public TeamPlayerScoreData() {
        mTeamHoleMask = 0;
        mTeamOverUnderHoleScore = new byte[HOLES_18];        // keeps track of gross under stroke, net total stroke and total team point quotes
        mTeamNetHoleScore = new byte[HOLES_18];        // keeps track of gross under stroke, net total stroke and total team point quotes
        mTeamHolesUsedByPlayers = new byte[HOLES_18];   // Keeps track of if the team used the team score once or twice or the point quote score for the player's selected
        m_HolePar = new byte[HOLES_18];                 // Golf course par for this hole - used for the over/under team score on the card
        mTeamPointQuotaScore = new byte[HOLES_18];      // will save the player's team point quote score
        mTeamStablefordScore = new byte[HOLES_18];      // will save the player's team Stableford score
    }
    /*
    This function will clear the 18 hole array - set all elements to zero
     */
    public void TeamClearHoleMask() {

        for( int CurrentHole = 0; CurrentHole < HOLES_18; CurrentHole++) {
            mTeamNetHoleScore[CurrentHole] = 0;
            mTeamOverUnderHoleScore[CurrentHole] = 0;
            mTeamHolesUsedByPlayers[CurrentHole] = 0;
            mTeamPointQuotaScore[CurrentHole] = 0;      // this is the player's point quote team score - the TeamScoreTotals class will be used add the players scores.
            mTeamStablefordScore[CurrentHole] = 0;      // this is the player's Stableford team score - the TeamScoreTotals class will be used add the players scores.
        }
    }
        /*
      This function will be used to classify the player's score for the hole. how did the user classify the player's score Gross or net
       */
    public void TeamSaveGrossNetTeamScore(int HoleNumber, byte PlayerHoleScore, int TeamScoreMask, byte ShotsForHole) {

        if( 0 < TeamScoreMask) {
            mTeamNetHoleScore[HoleNumber] = PlayerHoleScore;  // how did the user classify the player's score Gross or net
            if((TEAM_NET_SCORE & TeamScoreMask) == TEAM_NET_SCORE ) {
                mTeamNetHoleScore[HoleNumber] -= ShotsForHole;         // this will calculate the net score for the hole
            }
            if ((TeamScoreMask & DOUBLE_TEAM_SCORE) == DOUBLE_TEAM_SCORE){
                mTeamHolesUsedByPlayers[HoleNumber] = 2;               // use this score twice
                mTeamNetHoleScore[HoleNumber] *= 2;
            }
            else {
                mTeamHolesUsedByPlayers[HoleNumber] = 1;               // use this score once
            }
        }
        else {
            mTeamNetHoleScore[HoleNumber] = 0;
            mTeamHolesUsedByPlayers[HoleNumber] = 0;               // do not use this score
        }
    }
    /*
  This function will be used to by the summary page for calculating the team over/under score
   */
    public void TeamSaveOverUnderTeamScore(int HoleNumber, byte PlayerHoleScore, int TeamScoreMask, byte ShotsForHole) {

        if( 0 < TeamScoreMask) {
            mTeamOverUnderHoleScore[HoleNumber] = PlayerHoleScore;  // how did the user classify the player's score Gross or net
            if((TEAM_NET_SCORE & TeamScoreMask) == TEAM_NET_SCORE ) {
                mTeamOverUnderHoleScore[HoleNumber] -= ShotsForHole;         // this will calculate the net score for the hole
            }
            if ((TeamScoreMask & DOUBLE_TEAM_SCORE) == DOUBLE_TEAM_SCORE){
                mTeamHolesUsedByPlayers[HoleNumber] = 2;               // use this score twice
                mTeamOverUnderHoleScore[HoleNumber] *= 2;
            }
            else {
                mTeamHolesUsedByPlayers[HoleNumber] = 1;               // use this score once
            }
        }
        else {
            mTeamOverUnderHoleScore[HoleNumber] = 0;
            mTeamHolesUsedByPlayers[HoleNumber] = 0;               // use this score once
        }
    }
    /*
This function will save the team point quote score for all of the players in the team row and only save the select point quote score in the used colunms
 */
    public void TeamSavePointQuotaTeamScore(int currentHole, byte scoreToSave, byte teamHoleMask) {
        if (0 < teamHoleMask) {
            mTeamPointQuotaScore[currentHole] = scoreToSave;               // use this score once
            mTeamHolesUsedByPlayers[currentHole] = scoreToSave;               // use this score once
            if (DOUBLE_TEAM_SCORE == (teamHoleMask & DOUBLE_TEAM_SCORE)) {
                mTeamPointQuotaScore[currentHole] *= 2;      // use this score twice
                mTeamHolesUsedByPlayers[currentHole] *= 2;      // use this score twice
            }
        }
    }

    /*
    This function will get the saved teampointQuota score for this player - the player score for the hole must have been high light
     */
    public int getUsedTeamPointQuoteScore(int currentHole) {
        int UsedTeamPointQuoteScore;
        UsedTeamPointQuoteScore = mTeamPointQuotaScore[currentHole];
        return (UsedTeamPointQuoteScore);
    }

    /*
   This function will save the team point quote score for all of the players in the team row and only save the select point quote score in the used colunms
    */
    public void TeamSaveStablefordTeamScore(int currentHole, byte scoreToSave, byte teamHoleMask) {
        if (0 < teamHoleMask) {
            mTeamStablefordScore[currentHole] = scoreToSave;               // use this score once
            mTeamHolesUsedByPlayers[currentHole] = scoreToSave;               // use this score once
            if (DOUBLE_TEAM_SCORE == (teamHoleMask & DOUBLE_TEAM_SCORE)) {
                mTeamStablefordScore[currentHole] *= 2;      // use this score twice
                mTeamHolesUsedByPlayers[currentHole] *= 2;      // use this score twice
            }
        }
    }

    /*
    This function will get the saved team Stableford score for this player - the player score for the hole must have been high light
     */
    public int getUsedTeamStablefordScore(int currentHole) {
        int UsedTeamStablefordScore;

        UsedTeamStablefordScore = mTeamStablefordScore[currentHole];
        return (UsedTeamStablefordScore);
    }
    /*
This function will save the golf course par for the hole
 */
    public void setTeamTheParForEachHole(int HoleNumber, byte CourseHolePar ) {

        m_HolePar[HoleNumber] = CourseHolePar;
    }
    /*
    This function will save the golf course par for the hole
     */
    public byte GetTeamTheParForThisHole(int HoleNumber ) {

        return (m_HolePar[HoleNumber]);
    }
    /*
    This function will get the team score for this hole
     */
    public byte getTeamScoreForHole( int HoleNumber){

        return (mTeamNetHoleScore[HoleNumber]);
    }

    /*
    This function will set the team score for this hole
    the mTeamNetHoleScore variable used to calculate total team score for all of the players
    */
    public void setTeamScoreForHole(int HoleNumber, byte TeamHoleScore) {

        mTeamNetHoleScore[HoleNumber] = TeamHoleScore;
    }

    /*
    This function will return the number of time this score was used for this hole
     */
    public byte getTeamScoreUsedByHole( int HoleNumber){

        return (mTeamHolesUsedByPlayers[HoleNumber]);
    }
    /*
    This function will collect the team total for each hole for this player
     */
    public void updateTeamScoreTotals(TeamScoreTotals teamScoreTotals, DisplayScoreCardDetail.WhatNineIsDisplayed displayFrontOrBackScoreCard, int displayMode) {
        int StartHole = 0;
        byte PlayerTeamScore;

        if( displayFrontOrBackScoreCard == DisplayScoreCardDetail.WhatNineIsDisplayed.BACK_NINE_DISPLAY)
        {
            StartHole = NINETH_HOLE;
        }

        for( int ScreenHole = 0; ScreenHole < SCREENHOLES; ScreenHole++)
        {
            PlayerTeamScore = getTeamScoreForHole(StartHole);
            teamScoreTotals.AddPlayerTeamHoleScore(StartHole, PlayerTeamScore);                     // save the player team score
            teamScoreTotals.AddPlayerTeamUsedScore(StartHole, getTeamScoreUsedByHole(StartHole));   // save the number of score used by the hole
            StartHole++;        // next hole on the score card ??
        }
    }
    /*
    This function will display a team box around player score on the score card
     */
    public int DisplayGrossDoubleBox( int ShotPerHole) {
        int ID_BoxHighLight = 0;

        switch (ShotPerHole) {
            case 0:
                ID_BoxHighLight = R.drawable.tv_team_twice_gross_no_stroke_box;   // white back ground with purple box
                break;
            case 1:
                ID_BoxHighLight = R.drawable.tv_team_twice_gross_one_stroke_box;  // Yellow back ground with purple box
                break;
            case 2:
                ID_BoxHighLight = R.drawable.tv_team_twice_gross_two_stroke_box;  // Orange back ground with purple box
                break;
        }
        return ID_BoxHighLight;
    }
    /*
    This function will display a team box around player score on the score card
     */
    public int DisplayNetDoubleBox( int ShotPerHole) {
        int ID_BoxHighLight = 0;

        switch (ShotPerHole) {
            case 0:
                ID_BoxHighLight = R.drawable.tv_team_twice_net_no_stroke_box;   // white back ground with purple box
                break;
            case 1:
                ID_BoxHighLight = R.drawable.tv_team_twice_net_one_stroke_box;  // Yellow back ground with purple box
                break;
            case 2:
                ID_BoxHighLight = R.drawable.tv_team_twice_net_two_stroke_box;  // Orange back ground with purple box
                break;
        }
        return ID_BoxHighLight;
    }

    /*
    This function will display a team box around player score on the score card
     */
    public int DisplayNetBox( int ShotPerHole) {
        int ID_BoxHighLight = 0;

        switch (ShotPerHole) {
            case 0:
                ID_BoxHighLight = R.drawable.tv_team_net_no_stroke_box;        // white back ground with blue box
                break;
            case 1:
                ID_BoxHighLight = R.drawable.tv_team_net_one_stroke_box;       // Yellow back ground with blue box
                break;
            case 2:
                ID_BoxHighLight = R.drawable.tv_team_net_two_stroke_box;       // Orange back ground with blue box
                break;
        }
        return ID_BoxHighLight;
    }
    /*
    This function will display a team box around player score on the score card
     */
    public int DisplayGrossBox( int ShotPerHole) {
        int ID_BoxHighLight = 0;

        switch (ShotPerHole){
            case 0:
                ID_BoxHighLight = R.drawable.tv_team_gross_no_stroke_box;  // white back ground with grean box
                break;
            case 1:
                ID_BoxHighLight = R.drawable.tv_team_gross_one_stroke_box; // Yellow back ground with grean box
                break;
            case 2:
                ID_BoxHighLight = R.drawable.tv_team_gross_two_stroke_box;       // Orange back ground with grean box -- neeed to fix
                break;
        }
        return ID_BoxHighLight;
    }
    /*
This function will calculated the team over/under using the player's strokes selected for the hole.
*/
    public int GetTotalPlayerStrokesUseTotal(int firstHole, int ninethHole) {
        int PlayerTeamTotalFrontNineStrokeScore= 0;

        for (int CurrentHole = firstHole; CurrentHole < ninethHole; CurrentHole++) {
            PlayerTeamTotalFrontNineStrokeScore += getTeamScoreForHole(CurrentHole);
        }
        return (PlayerTeamTotalFrontNineStrokeScore);
    }

    /*
This function will get the player's over/under score total for the nine holes used by team score only on the summary page
 */
    public int GetTotalPlayerUnderOverUseTotal(int firstHole, int ninethHole) {
        int UnderOverTotal = 0;

        for (int CurrentHole = firstHole; CurrentHole < ninethHole; CurrentHole++) {
            UnderOverTotal += mTeamOverUnderHoleScore[CurrentHole];
        }
        return (UnderOverTotal);
    }

}
