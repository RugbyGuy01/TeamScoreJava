package com.golfpvcc.teamscore;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.golfpvcc.teamscore.Adapters.ScreenCardValues;
import com.golfpvcc.teamscore.Database.CourseListRecord;
import com.golfpvcc.teamscore.Database.CurrentCardValues;

import io.realm.Realm;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.HOLES_18;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.MIM_COURSE_NAME;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PAR_3;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PAR_4;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.PAR_5;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREENHOLES;

/**
 * Created by vinnie on 8/10/2016.
 * The Course Setup screen will allow the user to add a new golf course to the database. The user will enter the course name, the par and hole handicap for
 * each hole. The screen is broken down to the front and back nine holes.
 */
public class CourseSetup extends DialogFragment {

    private Button mButSave;          // the save button for the database add/update record function
    private Button mButCancel;        // the cancel button on the screen, nothing is saved
    private Button mButUse;           // the use button on the screen will save any changes make to the course then move on to the next screen
    private Button mButFlip;             // Will flip the handicap number from odd to even or even to odd

    private EditText mInputCourseName;      // the text edit for the course name we are saving
    Button[] mButtonsHandicap;               // the list of the nine handicap buttons on the sscreen
    CurrentCardValues[] mCardRecord;         // the 18 hole/handicap record that will be saved in the database
    ScreenCardValues[] mCardOnScreens;       // the list of 9 holes on the screen text view pointers, struct of textviews for handicap. par, hole and button used
    //    TextView mHoleTv;                        // the current text view for the hole we are dealing with now.
    int mCurrentScreenHole = 0, mCurrentCardHole = 0;
    String mCourseName;
    String[] mHandicapRecord, mParRecord;

    private boolean mFlipFrontBackNine = false;

    /*
    We need a constructor for a dialog window.
     */
    public CourseSetup() {
    }

    /*
    This function is called when we display the add/update golf course record.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme); // used to set custom theme for the dialog
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCourseName = arguments.getString("Name", "");
            if (mCourseName.length() > MIM_COURSE_NAME) {                     // Course name must be greater than 3 chars
                String HandicapString = arguments.getString("Handicap", "");    // the course record is passed into this function
                mHandicapRecord = HandicapString.split(",");
                String ParString = arguments.getString("Par", "");
                mParRecord = ParString.split(",");
                int HandicapIsOdd = Integer.parseInt(mHandicapRecord[0]);     // check if the course has the front & back nine handicaps flip
                mFlipFrontBackNine = (HandicapIsOdd % 2) == 0;
            } else {
                mHandicapRecord = new String[HOLES_18];
                mParRecord = new String[HOLES_18];
                for (int x = 0; x < HOLES_18; x++) {    // set the arrays to charactor zero
                    mHandicapRecord[x] = "0";
                    mParRecord[x] = "0";
                }
            }
        }
    }

    /*
    The view is beening created in code
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.course_setup_layout, container, false);
    }

    /*
        The onCreateView is called when the dialog is displayed to the user.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SetupParButtonListeners(view);          //Setup the listeners for prev/next hole and par buttons.
        SetupScreenTextPointers(view);          // the list of 9 holes on the screen text view pointers, struct of textviews for handicap. par, hole and button used
        setOnScreenCurrentHole();
        boolean flipNine = mFlipFrontBackNine ? false : true;
        changeNines(flipNine, true);      //  back to the front nine
    }

    /*
    This function will flip the handicap numbers from Odd to Even or even to odd. - this is due to the Duke golf course had the front nine with even handicap holes
     */
    private View.OnClickListener mButOnClickListenerFlip = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //??
            mFlipFrontBackNine = !mFlipFrontBackNine;
            boolean flipNine = mFlipFrontBackNine ? false : true;
            changeNines(flipNine, true);      //  back to the front nine
        }
    };
    /*
    This function will be called when the user click's the Save or Use button - onClick from the XML will look for the function in the activity not the fragment
     */
    private View.OnClickListener mButOnClickListenerExit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText tvCourseName;
            String courseName = "";
            boolean Status = true;

            int id = view.getId();
            switch ((id)) {
                case R.id.courseSetupButSave:
                case R.id.courseSetupButUse:
                    tvCourseName = (EditText) getView().findViewById((R.id.courseSetupName));   // get the course name
                    courseName = tvCourseName.getText().toString();

                    Status = AddGolfCourseToDatabase(courseName);
                    break;
            }
            if (Status == true) {
                CourseList courseList = (CourseList) getActivity(); // get the activity that called this dialog window.
                courseList.onCourseSetupComplete(id, courseName);
                dismiss();      // close the dialog window
            }
        }
    };
    /*
     This function will be called when the user click's the any handicap button
      The user select a handicap button, update the screen and make that button disappear - the view is select button not the layout view
  */
    private View.OnClickListener mButOnClickListenerHandicap = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button handicapButton;
            String handicapHole;
            TextView tmp_TextView;
            Button buttonsHandicap; // this is the button pressed for this hole

            handicapButton = (Button) view.findViewById(view.getId());     // the handicap button that was pushed
            handicapHole = handicapButton.getText().toString();     // get the handicap text for this button

            tmp_TextView = mCardOnScreens[mCurrentScreenHole].getM_handicapTv();
            tmp_TextView.setText(handicapHole);                                 // set the screen handicap - screen index is 0 to 8


            if (mCardRecord[mCurrentCardHole].getM_handicap() != 0) {           // check to see if this hole has the handicap set.
                if (mCardOnScreens[mCurrentScreenHole].getM_buttonsHandicap() != null) {
                    buttonsHandicap = mCardOnScreens[mCurrentScreenHole].getM_buttonsHandicap();
                    buttonsHandicap.setVisibility(Button.VISIBLE);   // return button to the pool
                }
            }
            mCardOnScreens[mCurrentScreenHole].setM_buttonsHandicap(handicapButton);      // save the handicap button that was used

            if (mCardRecord[mCurrentCardHole].getM_par() == 0) {
                mCardRecord[mCurrentCardHole].setM_par(PAR_4);  // this is the default for most holes
                tmp_TextView = mCardOnScreens[mCurrentScreenHole].getM_parTv();
                tmp_TextView.setText("" + PAR_4);                // set the par text in the window
            }

            mCardRecord[mCurrentCardHole].setM_handicap(Byte.valueOf(handicapHole)); // save the int value of the handicap
            handicapButton.setVisibility(Button.INVISIBLE);         // make the button go away
            setNextHole(view);
        }
    };

    /*
    This function will allow the user to back track on the handicap setting
     */
    public void setNextHole(View v) {

        mButFlip.setVisibility(Button.INVISIBLE);       // you can't change the handicap assignments after you configure one
        ScoreCardHoleNumberBackGroundColor(mCurrentScreenHole, R.color.course_setup_table_background);
        mCurrentScreenHole++;
        mCurrentCardHole++;
        if (SCREENHOLES == mCurrentScreenHole) {       // next hole to set the handicap
            mCurrentScreenHole = 0;
            if (HOLES_18 == mCurrentCardHole) {
                mCurrentCardHole = 0;
                boolean flipNine = mFlipFrontBackNine ? false : true;
                changeNines(flipNine, true);      //  back to the front nine
            } else {
                boolean flipNine = mFlipFrontBackNine ? true : false;
                changeNines(flipNine, false);   // display the back nine
            }
        }
        setOnScreenCurrentHole();
    }

    /*
    This function will set the back ground color of the selected hole
     */
    private void ScoreCardHoleNumberBackGroundColor(int HoleNumber, long MyColor) {
        String color;

        color = getString(Integer.parseInt(String.valueOf(MyColor)));    // set the current hole color

        TextView tmp_TextView = mCardOnScreens[HoleNumber].getM_holeTv();     // clear high lite hole on the screen
        tmp_TextView.setBackgroundColor(Color.parseColor(color));
        ;
    }

    /*
        Set the current on screen hole text in one place
     */
    private void setOnScreenCurrentHole() {
        TextView currentHoleTv;
        String CurrentHoleText;

        currentHoleTv = (TextView) getView().findViewById((R.id.courseSetupCurrentHole));  // what hole to set the handicap
        CurrentHoleText = String.format("%2d", mCurrentCardHole + 1);
        currentHoleTv.setText(CurrentHoleText);

        ScoreCardHoleNumberBackGroundColor(mCurrentScreenHole, R.color.colorAccent);
    }

    /*
     This function will set the par for the current hole manually
      */
    private View.OnClickListener mButOnClickListenerSetPar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int ParForHole = 2;

            switch (view.getId()) {
                case R.id.courseSetupButPar3:
                    ParForHole = PAR_3;
                    break;

                case R.id.courseSetupButPar4:
                    ParForHole = PAR_4;
                    break;

                case R.id.courseSetupButPar5:
                    ParForHole = PAR_5;
                    break;
            }
            mCardRecord[mCurrentCardHole].setM_par(ParForHole);  // this is the default for most holes
            mCardOnScreens[mCurrentScreenHole].getM_parTv().setText("" + ParForHole);
        }
    };
    /*
    This function will allow the user to back track on the handicap setting. Set the current hole back one,
    then get the handicap for that hole and set the button visable.
     */
    private View.OnClickListener mButOnClickListenerSetPrevHole = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ScoreCardHoleNumberBackGroundColor(mCurrentScreenHole, R.color.course_setup_table_background);

            mCurrentScreenHole--;
            mCurrentCardHole--;
            if (mCurrentScreenHole < 0) {       // next hole to set the handicap
                mCurrentScreenHole = SCREENHOLES - 1;

                if (mCurrentCardHole < 0) {       // next hole to set the handicap
                    mCurrentCardHole = HOLES_18 - 1;
                    boolean flipNine = mFlipFrontBackNine ? true : false;

                    changeNines(flipNine, false);          // display the back nine
                } else {
                    boolean flipNine = mFlipFrontBackNine ? false : true;
                    changeNines(flipNine, true);          // display the front nine
                }
            }
            setOnScreenCurrentHole();

            if (mCardOnScreens[mCurrentScreenHole].getM_buttonsHandicap() != null) {
                setHandicapButton(mCurrentScreenHole);
                mCardRecord[mCurrentCardHole].setM_handicap(0);      // this is the data that will be save in the course file.
            }
        }
    };
    /*
    This function will allow the user to back track on the handicap setting. the view is select button not the layout view
    */
    private View.OnClickListener mButOnClickListenerSetNextHole = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mButFlip.setVisibility(Button.INVISIBLE);       // you can't change the handicap assignments after you configure one
            ScoreCardHoleNumberBackGroundColor(mCurrentScreenHole, R.color.course_setup_table_background);

            mCurrentScreenHole++;
            mCurrentCardHole++;
            if (SCREENHOLES == mCurrentScreenHole) {       // next hole to set the handicap
                mCurrentScreenHole = 0;
                if (HOLES_18 == mCurrentCardHole) {
                    mCurrentCardHole = 0;
                    boolean flipNine = mFlipFrontBackNine ? false : true;
                    changeNines(flipNine, true);      //  back to the front nine
                } else {
                    boolean flipNine = mFlipFrontBackNine ? true : false;
                    changeNines(flipNine, false);     // display the back nine
                }
            }
            setOnScreenCurrentHole();
        }
    };

    /*
    The par button need have on click listener - this will call into this frangment class function
     */
    private void SetupParButtonListeners(View view) {
        Button mButPar;

        mButSave = (Button) view.findViewById(R.id.courseSetupButSave);
        mButUse = (Button) view.findViewById(R.id.courseSetupButUse);
        mButCancel = (Button) view.findViewById(R.id.courseSetupButCancel);
        mButFlip = (Button) view.findViewById(R.id.courseSetupButFlip);

        mInputCourseName = (EditText) view.findViewById(R.id.courseSetupName);
        mInputCourseName.setText(mCourseName);
        mButCancel.setOnClickListener(mButOnClickListenerExit);         // the listener to call when the user wants to exit the dialog
        mButSave.setOnClickListener(mButOnClickListenerExit);          // the listener to call when the user wants to exit the dialog
        mButUse.setOnClickListener(mButOnClickListenerExit);           // the listener to call when the user wants to exit the dialog

        if (mHandicapRecord[0] == "0")         // new course, other modifying an old course
            mButFlip.setOnClickListener(mButOnClickListenerFlip);
        else
            mButFlip.setVisibility(Button.INVISIBLE);

        mButPar = (Button) view.findViewById(R.id.courseSetupButPar3);
        mButPar.setOnClickListener(mButOnClickListenerSetPar);

        mButPar = (Button) view.findViewById(R.id.courseSetupButPar4);
        mButPar.setOnClickListener(mButOnClickListenerSetPar);

        mButPar = (Button) view.findViewById(R.id.courseSetupButPar5);
        mButPar.setOnClickListener(mButOnClickListenerSetPar);
// setup the next / previous hole button listeners
        mButPar = (Button) view.findViewById(R.id.courseSetupPrevHole);
        mButPar.setOnClickListener(mButOnClickListenerSetPrevHole);

        mButPar = (Button) view.findViewById(R.id.courseSetupNextHole);
        mButPar.setOnClickListener(mButOnClickListenerSetNextHole);
    }

    /*
        Get all of the button address for the handicap button that are on the screen
        Get all of the text views address for the hole, handicap and pars that are on the screen
        Setup the card will be saved in a file for this course - pars and hole handicaps
     */
    private void SetupScreenTextPointers(View view) {
        mButtonsHandicap = new Button[SCREENHOLES];          // get all of the button address for the handicap button that are on the screen
        for (int i = 0; i < mButtonsHandicap.length; i++) {
            String buttonID = "courseSetupBut" + (i + 1);     // handicap button ids on the Course setup layout file  getPackageName()

            int resID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
            mButtonsHandicap[i] = ((Button) view.findViewById(resID));
            mButtonsHandicap[i].setOnClickListener(mButOnClickListenerHandicap);     // setup the on click listener for the handicap buttons
        }
        mCardOnScreens = new ScreenCardValues[SCREENHOLES];          // get all of the text views address for the hole, handicap and pars that are on the screen
        for (int i = 0; i < mCardOnScreens.length; i++) {
            TextView holeTv, handicapTv, parTv;

            String textViewID = "courseSetupHoleView" + (i + 1);
            int resID = getResources().getIdentifier(textViewID, "id", getActivity().getPackageName());
            holeTv = ((TextView) view.findViewById(resID));

            textViewID = "courseSetupHdcpView" + (i + 1);
            String packageNameVpg = getActivity().getPackageName();
            resID = getResources().getIdentifier(textViewID, "id", packageNameVpg);
            handicapTv = ((TextView) view.findViewById(resID));

            textViewID = "courseSetupParView" + (i + 1);
            resID = getResources().getIdentifier(textViewID, "id", getActivity().getPackageName());
            parTv = ((TextView) view.findViewById(resID));

            mCardOnScreens[i] = new ScreenCardValues(holeTv, handicapTv, parTv);    // Class of textviews for handicap. par, hole and button used
        }
        mCardRecord = new CurrentCardValues[HOLES_18];      // this card will be saved in a file for this course - pars and hole handicaps
        for (int x = 0; x < HOLES_18; x++) {
            mCardRecord[x] = new CurrentCardValues();
            mCardRecord[x].setM_par(Integer.parseInt(mParRecord[x]));           // set to a string zero or data passed in for "use this Course record"
            mCardRecord[x].setM_handicap(Integer.parseInt(mHandicapRecord[x]));
        }
    }

    /*
    This function will display the front or back nine holes on the screen - true for the front nine and false for the back
    The course may have the handicaps odd/Even on the front or back nine.
    The code uses 2 indexes,
        scoreCardHoleInx use to get data from the score card record ( 0 to 17).
        'x' use to index the screen grid (0 to 8)
        The screen grid has 9 records that holds the information about the screen - each record Text View for hole, par, handicap and the button used to display the handicap.
    1. Set all of the handicap buttons visible with handicap numbers - maybe odd or even numbers
    2. Display the hole numbers on the screen grid - 1 to 9 or 10 to 18
    3. Display the Par for the hole using the score Card Hole Inx record if set otherwise display "-"
    4. Display the Handicap for the hole using the score Card Hole Inx record if set otherwise display "-"
        4a. If the handicap is set for the hole then make that handicap button invisible
     */
    public void changeNines(boolean HandicapButFlipNine, boolean FrontSideHoles) {
        int x, HandicapButtonNumbers, ScoreCardHoleInx, hdcpInx, HandicapForHole;
        TextView tmp_TextView;

        HandicapButtonNumbers = HandicapButFlipNine ? 1 : 2;     // Handicap button labels -
        ScoreCardHoleInx = FrontSideHoles ? 0 : 9;             // front side hole numbers - true = 1 to 9 false is 10 to 18
        for (x = 0; x < mButtonsHandicap.length; x++) {
            mButtonsHandicap[x].setVisibility(Button.VISIBLE);   // display all of the nine handicap buttons with the old or even numbers
            mButtonsHandicap[x].setText("" + HandicapButtonNumbers);
            HandicapButtonNumbers += 2;     // all of the buttons are odd or even numbers
        }

        for (x = 0; x < mCardOnScreens.length; x++, ScoreCardHoleInx++) {
            tmp_TextView = mCardOnScreens[x].getM_holeTv();
            tmp_TextView.setText("" + (ScoreCardHoleInx + 1));       // the hole numbers on the screen card 1 to 9 or 10 to 18

            tmp_TextView = mCardOnScreens[x].getM_parTv();          // get the text view for the Par on the score card
            if (mCardRecord[ScoreCardHoleInx].getM_par() == 0) {    // Class of Par & Handicap
                tmp_TextView.setText("-");
            } else {
                tmp_TextView.setText("" + mCardRecord[ScoreCardHoleInx].getM_par());
            }

            HandicapForHole = mCardRecord[ScoreCardHoleInx].getM_handicap();
            tmp_TextView = mCardOnScreens[x].getM_handicapTv();         // get the text view for the Handicap on the score card
            if (HandicapForHole == 0) {   // Handicap for this hole
                tmp_TextView.setText("-");
            } else {
                tmp_TextView.setText("" + HandicapForHole); // now figure out which button we need to make invisible

                if((HandicapForHole % 2) == 0){         // if the handicap is even
                    hdcpInx = (HandicapForHole - 2) / 2;
                }else {
                    hdcpInx = (HandicapForHole - 1) / 2;
                }
                // figure out which of the nine button this handicap - front nine would be  but1 = 1,  but2 = 3, but 3 = 5
                // back nine would be  but1 = 2,  but2 = 4, but 3 = 6
                mCardOnScreens[x].setM_buttonsHandicap(mButtonsHandicap[hdcpInx]);          // Save the Button Text View port
                mCardOnScreens[x].getM_buttonsHandicap().setVisibility(Button.INVISIBLE);   // make the button go away on the scrren
            }
        }
        tmp_TextView = mCardOnScreens[mCurrentScreenHole].getM_holeTv();     // high lite the first hole on the screen
        tmp_TextView.setBackgroundColor(Color.MAGENTA);     // set the gross score on the card

    }
    /*
  This function will set the handicap buttons to Visible
   */
    private void setHandicapButton(int CurrentHoleInx) {
        mCardOnScreens[CurrentHoleInx].getM_buttonsHandicap().setVisibility(Button.VISIBLE);
        mCardOnScreens[CurrentHoleInx].setM_buttonsHandicap(null);
        mCardOnScreens[CurrentHoleInx].getM_handicapTv().setText("-");
    }

    /*
    This function will open / close and write the record to the database
     */
    private boolean AddGolfCourseToDatabase(String courseName) {
        boolean status = true;
        Realm realm;

        realm = Realm.getDefaultInstance();
        if (realm != null) {

            String CourseParRecord = "", CourseHandicapRecord = "", tmpStr;
            int CardValue;
            CourseListRecord courseRecord = new CourseListRecord();

            for (int x = 0; x < HOLES_18 && status == true; x++) {
                CardValue = mCardRecord[x].getM_par();
                tmpStr = String.format("%d,", CardValue);   // save the par value for the hole
                CourseParRecord += tmpStr;

                CardValue = mCardRecord[x].getM_handicap();
                tmpStr = String.format("%d,", CardValue);   // save the par value for the hole
                CourseHandicapRecord += tmpStr;
                if (CardValue < 1) {
                    status = false;             // course not setup correctly
                }
            }

            if (status == true && MIM_COURSE_NAME < courseName.length()) {
                courseRecord.setM_courseName(courseName);
                courseRecord.setM_HolesHandicap(CourseHandicapRecord);
                courseRecord.setM_HolesPar(CourseParRecord);// update the realm objects

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(courseRecord);
                realm.commitTransaction();
            } else {
                status = false;
                Toast.makeText(getContext(), "Course not saved! must completely configured", Toast.LENGTH_LONG).show();
            }
            realm.close();
        }
        return (status);
    }
}

