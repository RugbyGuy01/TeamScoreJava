package com.golfpvcc.teamscore.Database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vinnie on 8/11/2016.
 * String - Course Name
 * Par for the holes are in this format #p,p,p,... for 18 holes - It's easier to store the data in the database.
 * Handicap record is the same has the Par String record
 */
public class CourseListRecord extends RealmObject {
    @PrimaryKey
    private String m_courseName = "";      // this is the database key
    private String m_HolesPar = "";
    private String m_HolesHandicap = "";
    private long mAddedDated;

    public CourseListRecord() {
    }

    public int getM_holeHandicape(int hole) {
        return m_HolesHandicap.charAt(hole);
    }


    public String getM_courseName() {
        return m_courseName;
    }

    public void setM_courseName(String courseName) {
        this.m_courseName = courseName;
        mAddedDated = System.currentTimeMillis();    // need a unit number
    }

    public String getM_HolesPar() {
        return m_HolesPar;
    }

    public long getAddedDated() {
        return mAddedDated;
    }

    public void setM_HolesPar(String HolesPar) {
        this.m_HolesPar = HolesPar;
    }

    public String getM_HolesHandicap() {
        return m_HolesHandicap;
    }

    public void setM_HolesHandicap(String m_HolesHandicap) {
        this.m_HolesHandicap = m_HolesHandicap;
    }
}