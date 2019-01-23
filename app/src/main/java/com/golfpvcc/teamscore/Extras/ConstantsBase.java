package com.golfpvcc.teamscore.Extras;

/**
 * Created by vinnie on 11/27/2016.
 * DESCRIPTION:
 * Use interface to define '#define' constants' equivalent.
 * This file defines a Java Interface which contains a number
 * of compile time constants.
*/
public interface ConstantsBase  {
    int SCREEN_GAME_SUMMARY = 100, SCREEN_COURSE_LIST = 101, SCREEN_COURSE_SELECTED = 102, SCREEN_COURSE_SETUP = 103,
            SCREEN_PLAYERS_SETUP = 104, SCREEN_LOFT_GAME = 105, SCREEN_GAME_ON = 106;
    String NEXT_SCREEN = "NextScreen", COURSE_NAME = "CourseName";
    byte FRONT_NINE_TOTAL_DISPLAYED = '_', BACK_NINE_TOTAL_DISPLAYED = '-';
    int HOLES_18 = 18, SCREENHOLES = 9, NINETH_HOLE = 9, FIRST_HOLE = 0, NINETH_HOLE_ZB = 8;    // 8 is the nineth Hole Zero Based
    int PAR_3 = 3, PAR_4 = 4, PAR_5 = 5, MIM_COURSE_NAME = 2, PLAYER_TOTAL = 4, TOTAL_SCORE_CELL = 9;
    int HOLE_ROW = 0, PAR_ROW = 1, HANDICAP_ROW = 2, PLAYER1_ROW = 3;
    int NAME_COL = 0, SCORE_COL = 1, TOTAL_COL = 10;
    int DISPLAY_FRONT_NINE = 50, SCREEN_COL = 9;
    int DISPLAY_MODE_GROSS = 60, DISPLAY_MODE_NET = 61, DISPLAY_MODE_POINT_QUOTA = 62, DISPLAY_MODE_POINT_HANDICAP_QUOTA = 63;
    int TEAM_GROSS_SCORE = 0x10, TEAM_NET_SCORE = 0x20, DOUBLE_TEAM_SCORE = 0x40, BIT_NOT_USED = 0x80, TEAM_SCORE = 0x70, JUST_RAW_SCORE = 0x0F;
    int PQ_TARGET = 0, PQ_EAGLE = 1, PQ_BIRDEIS = 2, PQ_PAR = 3, PQ_BOGGY = 4, PQ_DOUBLE = 5, PQ_OTHER = 6, PQ_END = 7; // indexes into the point quaota array
    int SCORE_USED_ONCE_NO_STROKE = 10, SCORE_USED_ONCE_ONE_STROKE = 11, SCORE_USED_ONCE_TWO_STROKE = 12;
    int SCORE_USED_TWICE_NO_STROKE = 13, SCORE_USED_TWICE_ONE_STROKE = 14, SCORE_USED_TWICE_TWO_STROKE = 15;
    int GROSS_SCORE_USED_ONCE_NO_STROKE = 16, GROSS_SCORE_USED_ONCE_ONE_STROKE = 17;


    int DISPLAY_SPACE = 100, TEST=20;
    // golf summary - "Score", "Quota", "Eagle", "Birdies", " Pars", "Bog", "Dbl", "Othr"
    int SUM_SCORE = 0, SUM_QUOTA = 1, SUM_EAGLE = 2, SUM_BIRDIE =3, SUM_PAR = 4, SUM__BOGGEY = 5, SUM_DOUBLE = 6, SUM_OTHER = 7, SUM_END = 8;
    String QUOTA_TARGET = "Target", QUOTA_EAGLE = "Eagle", QUOTA_BIRDIE = "Birdie", QUOTA_PAR = "Par", QUOTA_BOGGEY = "Boggey", QUOTA_DOUBLE = "Double", QUOTA_OTHER = "Other";

}
