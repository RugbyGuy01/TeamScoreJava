package com.golfpvcc.teamscore.Player;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.NINE_PLAYERS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PLAYER_1_INX;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PLAYER_2_INX;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PLAYER_3_INX;

/*
Sort scores  h = 5 M = 3  L = 1

Case 1	a - 3	b - 3	c - 3
Case 2	a - 3	b - 3	c - 4
Case 3	a - 3	b - 4	c - 4
Case 4	a - 3	b - 4	c - 5

if a == b {
	if a == c {
		(a = b = c = 3)		Case 1
	}
	else
	{
		a = b = 4			Case 2
		c = 1
	}
}
else
{
	a = 5
	if( b == c) {
		b = c = 2			Case 3
	}
	else
	{
		b = 3				Case 4
		c = 1
	}
}
 */
/*
    The m_Player_9_Score[][] array will act like a 1 byte record, the player array and the score array
    The m_Player_9_Score[PlayInx][ScoreInx] = (Player gross score)  The score
 */
public class NineGame {

    Player_9_game[] PlayerScoreArray;


    /*
Constructor
 */
    public NineGame() {
        PlayerScoreArray = new Player_9_game[NINE_PLAYERS];
        for (int x = 0; x < NINE_PLAYERS; x++) {
            PlayerScoreArray[x] = new Player_9_game();  // allocate the player's class for the 9 game
        }
    }

    public void ClearTotals() {
        for (int x = 0; x < NINE_PLAYERS; x++) {
            PlayerScoreArray[x].ClearScore();
        }
    }

    /*
    this function will sort the player score from low to high
     */
    public void sort_9_Scores() {
        int out, in, inScore, OutScore;

        for (out = NINE_PLAYERS - 1; out > 0; out--) {  // outer loop (backward)
            for (in = 0; in < out; in++)    // inner loop (forward)
            {
                inScore = PlayerScoreArray[in].getHoleScore();
                OutScore = PlayerScoreArray[in + 1].getHoleScore();
                if (inScore > OutScore)    // out of order?
                    swap(in, in + 1);     // swap them
            }
        }   // end bubbleSort()
        CalculateScores();
    }

    /*
    This function will swap the two classes
     */
    private void swap(int one, int two) {
        Player_9_game temp = PlayerScoreArray[one];
        PlayerScoreArray[one] = PlayerScoreArray[two];
        PlayerScoreArray[two] = temp;
    }

    /*
    This function will calculate the player scores based on the sort of the lowest score is at the top of the array.
     */
    private void CalculateScores() {

        if (0 < PlayerScoreArray[PLAYER_1_INX].getHoleScore()) {
            if (PlayerScoreArray[PLAYER_1_INX].getHoleScore() == PlayerScoreArray[PLAYER_2_INX].getHoleScore()) {
                if (PlayerScoreArray[PLAYER_1_INX].getHoleScore() == PlayerScoreArray[PLAYER_3_INX].getHoleScore()) {
                    PlayerScoreArray[PLAYER_1_INX].set_NineScore(3);
                    PlayerScoreArray[PLAYER_2_INX].set_NineScore(3);
                    PlayerScoreArray[PLAYER_3_INX].set_NineScore(3);
                } else {
                    PlayerScoreArray[PLAYER_1_INX].set_NineScore(4);
                    PlayerScoreArray[PLAYER_2_INX].set_NineScore(4);
                    PlayerScoreArray[PLAYER_3_INX].set_NineScore(1);
                }
            } else {
                PlayerScoreArray[PLAYER_1_INX].set_NineScore(5);   // lowest score of the three players
                if (PlayerScoreArray[PLAYER_2_INX].getHoleScore() == PlayerScoreArray[PLAYER_3_INX].getHoleScore()) {
                    PlayerScoreArray[PLAYER_2_INX].set_NineScore(2);
                    PlayerScoreArray[PLAYER_3_INX].set_NineScore(2);
                } else {
                    PlayerScoreArray[PLAYER_2_INX].set_NineScore(3);
                    PlayerScoreArray[PLAYER_3_INX].set_NineScore(1);
                }
            }
        }
        // else hole not scored yet
    }

    public int Get_9_GameScore(int Inx) {
        int Score = 0;

        for (int x = 0; x < NINE_PLAYERS; x++) {
            if (PlayerScoreArray[x].getPlayerInx() == Inx) {
                Score = PlayerScoreArray[x].getNineScore();
            }
        }

        return Score;
    }

    /*
    Set the player's hole score the 9's game. Only 3 player can play
     */
    public void AddPlayerGrossScore(int Inx, byte GrossScore) {
        PlayerScoreArray[Inx].setHoleScore(Inx, GrossScore);
    }

    public class Player_9_game {
        private int m_PlayerInx;            // The player's index from the score card - First player is index 0
        private int m_HoleScore;     // the player's gross score for the current hole
        private int m_NineScore;     // the calculate score for the 9 game.

        public Player_9_game() {
            ClearScore();
        }

        private void ClearScore() {
            m_PlayerInx = 0;
            m_HoleScore = 0;
            m_NineScore = 0;
        }

        public void setHoleScore(int Inx, byte GrossScore) {
            this.m_HoleScore = GrossScore;
            m_PlayerInx = Inx;
        }

        public int getPlayerInx() {
            return m_PlayerInx;
        }

        public int getHoleScore() {
            return m_HoleScore;
        }

        public int getNineScore() {
            return m_NineScore;
        }

        public void set_NineScore(int NineScore) {
            this.m_NineScore = NineScore;
        }

    }
}
