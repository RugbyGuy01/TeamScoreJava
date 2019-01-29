package com.golfpvcc.teamscore.Adapters;

import android.widget.Button;
import android.widget.TextView;

/**
 * Created by vinnie on 8/11/2016.
 * This record holds the text view port for each hole on the screen - Only nine records per screen
 */
public class ScreenCardValues {
    private TextView m_holeTv;
    private TextView m_handicapTv;
    private TextView m_parTv;
    private Button m_buttonsHandicap; // this is the button pressed for this hole

    public ScreenCardValues(TextView holeTv, TextView handicapTv, TextView parTv) {
        m_holeTv = holeTv;
        m_handicapTv = handicapTv;
        m_parTv = parTv;
        m_buttonsHandicap = null;
    }

    public TextView getM_holeTv() {
        return m_holeTv;
    }

    public TextView getM_handicapTv() {
        return m_handicapTv;
    }

    public TextView getM_parTv() {
        return m_parTv;
    }

    public Button getM_buttonsHandicap() {
        return m_buttonsHandicap;
    }

    public void setM_buttonsHandicap(Button m_buttonsHandicap) {
        this.m_buttonsHandicap = m_buttonsHandicap;
    }
}
