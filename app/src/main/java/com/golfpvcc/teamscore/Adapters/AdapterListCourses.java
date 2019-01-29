package com.golfpvcc.teamscore.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.golfpvcc.teamscore.Database.CourseListRecord;
import com.golfpvcc.teamscore.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by vinnie on 8/17/2016. The recycler view is an Adstract class which means you have to supply the your
 * class with the create, view and count functions.
 */
public class AdapterListCourses extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeListener {
    private static final String TAG = "Vin";
    private LayoutInflater mInflaterRowView;
    Realm mRealm;
    private RealmResults<CourseListRecord> m_CourseListResults;
    RealmResults<CourseListRecord> mItems;
    public static final int ITEM = 0;
    public static final int FOOTER = 1;
    private AddCourseListener mAddNewCourseListener;

    /*
    Construct for the adapter
     */
    public AdapterListCourses(Context context, Realm RealmFileHandle, RealmResults<CourseListRecord> Results) {
        mInflaterRowView = LayoutInflater.from(context);
        mRealm = RealmFileHandle;
        update(Results);
    }

    /*
    Construct for the adapter
    The Add course listener is an interface between the adapter class and the course list activity
     */
    public AdapterListCourses(Context context, Realm RealmFileHandle, RealmResults<CourseListRecord> Results, AddCourseListener mListener) {
        mInflaterRowView = LayoutInflater.from(context);
        mRealm = RealmFileHandle;

        update(Results);
        mAddNewCourseListener = mListener;
    }

    /*
    This function will update the adapter (display) when the database results haved changed
     */
    public void update(RealmResults<CourseListRecord> Results) {
        m_CourseListResults = Results;
        notifyDataSetChanged();     // tells the display to update the screen
    }

    /*
    The view type is a course record view or the last item which is the add course footer button
     */
    @Override
    public int getItemViewType(int position) {

        if (m_CourseListResults == null || position < m_CourseListResults.size()) {
            return ITEM;
        } else {
            return FOOTER;
        }
    }

    /*
       This function will create one record view for the recycler view.
    */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == FOOTER) {
            View RowView = mInflaterRowView.inflate(R.layout.footer_add_course, parent, false); // this is the XML file name
            return (new FooterAddCourseHolder(RowView, mAddNewCourseListener));

        } else {
            View CourseRowView = mInflaterRowView.inflate(R.layout.course_list_row, parent, false);
            return (new CourseHolder(CourseRowView, mAddNewCourseListener));
        }
    }

    /*
    The binder view holder display one course record in the recycler view
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CourseHolder) {
            CourseHolder CourseRecordHolder = (CourseHolder) holder; // this is the xml file that holds on course record on the screen

            CourseListRecord CourseRecord = m_CourseListResults.get(position);  // this is the course record for this position
            CourseRecordHolder.mCourseName.setText(CourseRecord.getM_courseName()); // now display the course name on the scrren
        }
    }

    @Override
    public int getItemCount() {
        if (m_CourseListResults == null || m_CourseListResults.isEmpty())
            return 0;
        else
            return m_CourseListResults.size() + 1;  // the one is for the footer (Add Course Record Button)
    }

    /*
    This is the interface for the swipe listener
     */
    @Override
    public void onSwipe(int position) {
        if (position < m_CourseListResults.size()) {   // make sure the footer is not getting updated
            mRealm.beginTransaction();
            m_CourseListResults.get(position).deleteFromRealm();
            mRealm.commitTransaction();
            notifyItemRemoved(position);
        }
    }

    /*
    This function is use to return an Item just in case the sort feature has zero results
     */
    @Override
    public long getItemId(int position) {
        if (position < m_CourseListResults.size()) {
            return m_CourseListResults.get(position).getAddedDated();   // need an Id for each record
        }
        return RecyclerView.NO_ID;
    }

    /*
    This function will get the current record selected by the user.
     */
    public CourseListRecord getCurrentRecord(int position) {
        return m_CourseListResults.get(position);
    }

    /*
        The Course holder displays one golf course in the recycler view
    */
    public static class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mCourseName, mCourseStatus;
        AddCourseListener mEditCourseListener;

        /*
        Constructor
         */
        public CourseHolder(View itemView, AddCourseListener EditCourseListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            mCourseName = (TextView) itemView.findViewById(R.id.courseName);
            mCourseStatus = (TextView) itemView.findViewById(R.id.courseStatus);
            mEditCourseListener = EditCourseListener;
        }

        /*
        This function call when a recycler view item is selected - The getAdapterPosition() will return the record index into the database.
        */
        @Override
        public void onClick(View view) {

            mEditCourseListener.EditCourse(getAdapterPosition());
        }
    }

    /*
    The Footer holder displays add course button in the recycler view
    */
    public static class FooterAddCourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button AddCourseBtn;
        AddCourseListener mAddNewCourseListener;

        /*
        Constructor
         */
        public FooterAddCourseHolder(View itemView) {
            super(itemView);
            AddCourseBtn = (Button) itemView.findViewById(R.id.btn_footer);
            AddCourseBtn.setOnClickListener(this);
        }

        /*
        Constructor
         */
        public FooterAddCourseHolder(View itemView, AddCourseListener AddNewCourseListener) {
            super(itemView);
            AddCourseBtn = (Button) itemView.findViewById(R.id.btn_footer);
            AddCourseBtn.setOnClickListener(this);
            mAddNewCourseListener = AddNewCourseListener;
        }

        /*
        When the "Add a Course " buttom is click, the on Click function is called which calls the function in the course setup activity
        */
        @Override
        public void onClick(View view) {

            mAddNewCourseListener.AddNewCourse();
        }
    }
}
