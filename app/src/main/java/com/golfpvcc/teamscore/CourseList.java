package com.golfpvcc.teamscore;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.golfpvcc.teamscore.Adapters.AdapterListCourses;
import com.golfpvcc.teamscore.Adapters.AddCourseListener;
import com.golfpvcc.teamscore.Adapters.CourseListRecyclerView;
import com.golfpvcc.teamscore.Adapters.Divider;
import com.golfpvcc.teamscore.Adapters.SimpleTouchCallback;
import com.golfpvcc.teamscore.Database.CourseListRecord;
import com.golfpvcc.teamscore.Database.RealmScoreCardAccess;
import com.golfpvcc.teamscore.Extras.DialogEmailAddress;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static com.golfpvcc.teamscore.Extras.ConstantsBase.COURSE_NAME;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.EMAIL_ADDRESS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.NEXT_SCREEN;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.POINT_QUOTA_DB;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_ALBATROSS;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_BIRDIE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_BOGGEY;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_DOUBLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_EAGLE;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_OTHER;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_PAR;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.QUOTA_TARGET;
import static com.golfpvcc.teamscore.Extras.ConstantsBase.SCREEN_PLAYERS_SETUP;

public class CourseList extends AppCompatActivity implements DialogEmailAddress.EmailDialogListener {
    private static final String TAG = "Vin";
    Button mBtnAddCourse;
    Toolbar mToolBar;
    View mEmptyCourseList;
    AdapterListCourses mAdapterCourseList;
    CourseListRecyclerView mCourseListRecyclerView;
    private Realm m_Realm;
    RealmResults<CourseListRecord> mCourseListResults;
    /*
     The interface class - this functiion is called by the footer button "Add a Course" in the AdapterListCourses file
     */
    private AddCourseListener mAddCourseListener = new AddCourseListener() {
        @Override
        public void AddNewCourse() {
            showDialogAddNewCourse("", "", "");       // user can now add a new course record.
        }

        /*
        This function is called when a user selects a golf course from the recycle list in the AdapterListCourses file
         */
        @Override
        public void EditCourse(int postion) {
            CourseListRecord SelectCourseRecord;

            SelectCourseRecord = mAdapterCourseList.getCurrentRecord(postion);  // this is the course record for this position
            showDialogAddNewCourse(SelectCourseRecord.getM_courseName(), SelectCourseRecord.getM_HolesHandicap(), SelectCourseRecord.getM_HolesPar());
        }
    };
    /*
    User on the empty course list screen wants to add a new course
     */
    View.OnClickListener mButAddCourseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            showDialogAddNewCourse("", "", "");
        }
    };
    /*
    Add realm state listener -  will be called when the database has finished reading
     */
    private RealmChangeListener CourseRecordChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
//            Log.d(TAG, "onChange: was called");
            mAdapterCourseList.update(mCourseListResults); // automatically is updated by realm
        }
    };

    /*
    Display the Course Add/Edit screen
     */
    private void showDialogAddNewCourse(String CourseName, String HandicapRecord, String ParRecord) {
        CourseSetup dialogAdd = new CourseSetup();

        Bundle bundle = new Bundle();
        bundle.putString("Name", CourseName);
        bundle.putString("Handicap", HandicapRecord);
        bundle.putString("Par", ParRecord);
        dialogAdd.setArguments(bundle);

        dialogAdd.show(getSupportFragmentManager(), "Add");
    }

    /*
    Results from the course setup dialog window - Use this course, Save or they cancel = stay on this screen,
     */
    public void onCourseSetupComplete(int selectedValue, String courseName) {

        if (selectedValue == R.id.courseSetupButUse) {
            Intent intentSendDataBack = new Intent();   // data sent back to the calling activity
            intentSendDataBack.putExtra(COURSE_NAME, courseName);
            intentSendDataBack.putExtra(NEXT_SCREEN, SCREEN_PLAYERS_SETUP);
            setResult(RESULT_OK, intentSendDataBack);

            finish();       // close this screen or exit this screen
        }
    }

    /*
    The user select a New Game, display the course list to selected from
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_layout);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);              // need the tool bar to have an menu list
        mEmptyCourseList = findViewById(R.id.empty_course);


        m_Realm = Realm.getDefaultInstance();
        mCourseListResults = m_Realm.where(CourseListRecord.class).findAll();
        mCourseListResults = mCourseListResults.sort("m_courseName");   // sort the courses by course name


        mAdapterCourseList = new AdapterListCourses(this, m_Realm, mCourseListResults, mAddCourseListener);
        mAdapterCourseList.setHasStableIds(true);           // needed for animation

        mCourseListRecyclerView = (CourseListRecyclerView) findViewById(R.id.course_list_recycler_view);// rv_course_list);
        mCourseListRecyclerView.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL)); // draws the divider between the course records
        mCourseListRecyclerView.hideIfEmpty(mToolBar);
        mCourseListRecyclerView.showIfEmpty(mEmptyCourseList);

        mCourseListRecyclerView.setAdapter(mAdapterCourseList);
        SimpleTouchCallback callback = new SimpleTouchCallback(mAdapterCourseList); // setting up the swipe interface
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mCourseListRecyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mCourseListRecyclerView.setLayoutManager(manager);

        mBtnAddCourse = (Button) findViewById(R.id.butAddNewCourse);  //this takes the xml file and turns it into java code.
        mBtnAddCourse.setOnClickListener(mButAddCourseListener);

        initBackgroundImage();
    }

    /*
    Links the menu xml to this activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    This function is called when the user enters a new email address, save the address.
     */
    @Override
    public void SendEmailAddress(String EmailAddressForUser) {
        final SharedPreferences pref = getSharedPreferences(EMAIL_ADDRESS, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();          // get the editor for the databse
        editor.putString(EMAIL_ADDRESS, EmailAddressForUser);   // save the new email address
        editor.apply();
    }

    /*
        This function is called when the user selects a menu tool bar option
         */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = true; //indicated we handled the menu selection
        int id = item.getItemId();      // the item that was selected.
        DialogEmailAddress DisplayGetEmailDialog;

        switch (id) {
            case R.id.action_point_quota:
                DialogPointQuota();
                break;

            case R.id.action_about:
                Toast.makeText(CourseList.this, "Rev 3.0", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_contact:
                Toast.makeText(CourseList.this, "Vinnie Gamble \n VGamble@golfpvcc.com", Toast.LENGTH_LONG).show();
                break;

            case R.id.action_players:
                UpdatedPlayersHandicapMenuSelection();
                break;

            case R.id.action_email: // display the dialog for getting the user email address
                DialogEmailAddress EmailDialog = new DialogEmailAddress();
                EmailDialog.show(getSupportFragmentManager(), "Configure Email");
                break;

            default:
                handled = false;
                break;
        }
        return handled;
    }

    /*
    This function will handle the user selecting the menu option for updating the player's handicap - need to validate the user has picked a golf course before ending this function.
     */
    void UpdatedPlayersHandicapMenuSelection() {
        RealmScoreCardAccess realmScoreCardAccess;
        String GolfCourseName;

        realmScoreCardAccess = new RealmScoreCardAccess(m_Realm);
        realmScoreCardAccess.ReadTodayScoreCardRecord();           // get the current score card from the database for today's game
        GolfCourseName = realmScoreCardAccess.getTodayGolfCoursename();        // get the current score for today's game
        if (GolfCourseName != null) {
            Intent intentSendDataBack = new Intent();   // data sent back to the calling activity
            intentSendDataBack.putExtra(COURSE_NAME, GolfCourseName);
            intentSendDataBack.putExtra(NEXT_SCREEN, SCREEN_PLAYERS_SETUP);
            setResult(RESULT_OK, intentSendDataBack);
            finish();       // close this screen or exit this screen
        } else {
            Toast.makeText(this, "Player update failed, golf course has been deleted! ", Toast.LENGTH_LONG).show();
        }
    }
    /*
    This function will handle the point quota dialog window - the player's target value and points for eagles, birdies ...
    and the values into the local apps database
     */

    private void DialogPointQuota() {
        final Dialog MenuDialog = new Dialog(CourseList.this);
        final SharedPreferences pref = getSharedPreferences(POINT_QUOTA_DB, MODE_PRIVATE);

        MenuDialog.setTitle("Setup Point Quota");
        MenuDialog.setContentView(R.layout.dialog_pt_quota);
        MenuDialog.show();
        final EditText etTargetValue = (EditText) MenuDialog.findViewById(R.id.etQuotaTarget);
        // Albatross
        final EditText etAlbatrossValue = (EditText) MenuDialog.findViewById(R.id.etAlbatross);
        final EditText etEagleValue = (EditText) MenuDialog.findViewById(R.id.etEagle);
        final EditText etBirdieValue = (EditText) MenuDialog.findViewById(R.id.etBirdie);
        final EditText etParValue = (EditText) MenuDialog.findViewById(R.id.etPar);
        final EditText etBoggeyValue = (EditText) MenuDialog.findViewById(R.id.etBogey);
        final EditText etDoubleValue = (EditText) MenuDialog.findViewById(R.id.etDouble);
        final EditText etOtherValue = (EditText) MenuDialog.findViewById(R.id.etOther);

        PointQuotaLoadValues(pref, etTargetValue, QUOTA_TARGET, "36");     // load the dialog window with save shared preference values
        PointQuotaLoadValues(pref, etEagleValue, QUOTA_ALBATROSS, "8");
        PointQuotaLoadValues(pref, etEagleValue, QUOTA_EAGLE, "6");
        PointQuotaLoadValues(pref, etBirdieValue, QUOTA_BIRDIE, "4");
        PointQuotaLoadValues(pref, etParValue, QUOTA_PAR, "2");
        PointQuotaLoadValues(pref, etBoggeyValue, QUOTA_BOGGEY, "1");
        PointQuotaLoadValues(pref, etDoubleValue, QUOTA_DOUBLE, "0");
        PointQuotaLoadValues(pref, etOtherValue, QUOTA_OTHER, "-1");

        Button butQuotaSave = (Button) MenuDialog.findViewById(R.id.butSaveQuota);
        Button butQuotaCanel = (Button) MenuDialog.findViewById(R.id.butCancelQuota);

        // user clicked save point quota stoke values
        butQuotaSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ButtonView) {
                SharedPreferences.Editor editor = pref.edit();          // get the editor for the database
                SavePointQuoteData(editor, QUOTA_ALBATROSS, etAlbatrossValue.getText().toString());
                SavePointQuoteData(editor, QUOTA_EAGLE, etEagleValue.getText().toString());
                SavePointQuoteData(editor, QUOTA_BIRDIE, etBirdieValue.getText().toString());
                SavePointQuoteData(editor, QUOTA_PAR, etParValue.getText().toString());
                SavePointQuoteData(editor, QUOTA_BOGGEY, etBoggeyValue.getText().toString());
                SavePointQuoteData(editor, QUOTA_DOUBLE, etDoubleValue.getText().toString());
                SavePointQuoteData(editor, QUOTA_OTHER, etOtherValue.getText().toString());
                SavePointQuoteData(editor, QUOTA_TARGET, etTargetValue.getText().toString());

                editor.apply();
                Toast.makeText(CourseList.this, "Point Quota values saved.", Toast.LENGTH_SHORT).show();
                MenuDialog.cancel();
            }

            /*
This function will validate the data entred by the user is validate - a blank line will set the string to null which cause a app crash
 */
            private void SavePointQuoteData(SharedPreferences.Editor Editor, String KeyStr, String Value) {

                if (Value.length() < 1) {
                    Value = "0";
                }
                Editor.putString(KeyStr, Value);
            }

        });
        // user select the cancel button
        butQuotaCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuDialog.cancel();
            }
        });
    }            /*

    /*
    This function will load the Point Quote dialog window from the app shared prefereances
     */

    private void PointQuotaLoadValues(SharedPreferences pref, EditText EditTextValue, String quotaKeyString, String defaultValue) {
        int len;

        String Value_str = pref.getString(quotaKeyString, defaultValue);
        EditTextValue.setText(Value_str);
        len = Value_str.length();
        EditTextValue.setSelection(len);       // set the cursor at the end of the field
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (CourseRecordChangeListener != null) {
            mCourseListResults.addChangeListener(CourseRecordChangeListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CourseRecordChangeListener != null) {
            mCourseListResults.removeChangeListener(CourseRecordChangeListener);
        }
        if (m_Realm != null) {
            m_Realm.close();      // close the database
            m_Realm = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void initBackgroundImage() {
        ImageView mImageView = (ImageView) findViewById(R.id.iv_logo);
        Glide.with(this)
                .load(R.drawable.logo)
                .centerCrop()
                .into(mImageView);

    }

}
