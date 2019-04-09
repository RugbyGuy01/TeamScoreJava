package com.golfpvcc.teamscore.Player;

import android.widget.TextView;

import com.golfpvcc.teamscore.R;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_9_GAME;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_GROSS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_NET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_POINT_QUOTA;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.DISPLAY_MODE_STABLEFORD;

/*
This class will hole the detail information for the player's score card hole data - All the information for displaying a score on the score card.
 */
class HoleStokeDetails {
    public TextView m_tvScoreCell;      // the score card screen text views are zero based
    private byte m_GrossHoleScore;
    private byte m_NetHoleScore;
    private byte m_QuotaHoleScore;
    private byte m_ShotsForHole;            // how many shots this player gets for this hole
    private byte m_TeamHoleMask;
    private byte m_9_GamePoints;             // the player points the 9 game
    private byte m_StablefordHoleScore;

    /*
 Constructor for class - Save all of the pointer to the score cells
  */
    public HoleStokeDetails(TextView CellPointer) {
        m_tvScoreCell = CellPointer;         // text view for the score
        ClearHoleDetails();
    }

    public HoleStokeDetails() {
        ClearHoleDetails();
    }

    /*
    This function will clear all of the variables
     */
    public void ClearHoleDetails() {
        m_GrossHoleScore = 0;
        m_NetHoleScore = 0;
        m_QuotaHoleScore = 0;
        m_9_GamePoints = 0;
        m_TeamHoleMask = 0;
        m_ShotsForHole = 0;
        m_StablefordHoleScore = 0;
    }

    /*
    This function will get the player's Stable ford score for this hole
     */
    public byte getStablefordHoleScore() {
        return m_StablefordHoleScore;
    }

    /*
    This function will set the player's Stableford score for this hole
     */
    public void setStablefordHoleScore(byte Stableford) {
        this.m_StablefordHoleScore = Stableford;
    }

    /*
    This function will get the player's quota score for this hole
     */
    public byte getQuotaHoleScore() {
        return m_QuotaHoleScore;
    }

    /*
    This function will set the player's quota for this hole
     */
    public void setQuotaHoleScore(byte QuotaHoleScore) {
        this.m_QuotaHoleScore = QuotaHoleScore;
    }

    /*
    This function will save the player's gross hole score
     */
    public void setPlayerGrossScore(byte GrossHoleScore) {

        m_GrossHoleScore = GrossHoleScore;
        if (0 < GrossHoleScore)
            setPlayerNetScore(GrossHoleScore);
    }

    /*
   This function will retunr the player's gross score
    */
    public int getGrossScore() {
        return (m_GrossHoleScore);
    }

    /*
    This function will save the player's Net hole score
     */
    public void setPlayerNetScore(byte GrossHoleScore) {

        m_NetHoleScore = (byte) (GrossHoleScore - m_ShotsForHole);
    }

    /*
    This function will retunr the player's gross score
     */
    public int getNetScore() {
        return (m_NetHoleScore);
    }

    /*
    This function will save the player's 9 game hole score
     */
    public void setPlayer_9_GameScore(byte Game_9_Points) {

        m_9_GamePoints = Game_9_Points;
    }

    /*
    This function will retunr the player's 9 score for this hole
     */
    public int get_9_GamePoints() {
        return (m_9_GamePoints);
    }
    /*
    This function will set the team mask for this hole
     */
    public void setTeamHoleMask(byte TeamMask) {
        m_TeamHoleMask = TeamMask;
    }

    /*
    This function will set the team mask for this hole
     */
    public byte getTeamHoleMask() {
        return m_TeamHoleMask;

    }

    /*
        This function will set the player hole information which is used to the display the data onto the score card, How many shots the player get for this hole
     */
    public void setStrokeForHole(byte ShotsForHole) {

        m_ShotsForHole = ShotsForHole;
    }

    /*
    Return the number of shot the player gets for this hole
     */
    public byte getNumberOfShotsForThisHole() {
        return m_ShotsForHole;
    }

    /*
            This function will set the player hole information which is used to the display the data onto the score card. // Yellow for 1 shot or orange for 2 shots
         */
    public int getStrokeBackgroundColor() {
        int StrokeBackGroundColor = R.color.no_stroke_hole;

        if (m_ShotsForHole == 1) {
            StrokeBackGroundColor = R.color.one_stroke_hole;
        } else if (m_ShotsForHole == 2) {
            StrokeBackGroundColor = R.color.two_stroke_hole;
        }

        return (StrokeBackGroundColor);
    }

    /*
     this funtion will display the player's score on the score card.
      */
    public int DisplayScoreCardScore(int displayMode, byte parForHole) {
        int ScoreValue = 0;

        if (0 < m_GrossHoleScore) {
            switch (displayMode) {
                case DISPLAY_MODE_GROSS:
                    ScoreValue = m_GrossHoleScore - parForHole;     // how many over/under par is this score
                    m_tvScoreCell.setText("" + m_GrossHoleScore);
                    break;

                case DISPLAY_MODE_NET:
                    ScoreValue = m_NetHoleScore - parForHole;     // how many over/under par is this score
                    m_tvScoreCell.setText("" + m_NetHoleScore);
                    break;

                case DISPLAY_MODE_POINT_QUOTA:
                    m_tvScoreCell.setText("" + m_QuotaHoleScore);
                    break;

                case DISPLAY_MODE_9_GAME:
                    m_tvScoreCell.setText("" + m_9_GamePoints);
                    break;
                case DISPLAY_MODE_STABLEFORD:
                    m_tvScoreCell.setText("" + m_StablefordHoleScore);
                    break;
            }
        } else {
            m_tvScoreCell.setText(" ");
        }
        return ScoreValue;
    }

}