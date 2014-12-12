package edu.stanford.baseline.sharkpulse;

import java.util.Date;

/**
 * Created by emazzilli on 9/13/14.
 */

//Record object to store record of picture
public class Record {
    protected String mEmail;
    protected String mGuessSpecies;
    protected String mNotes;
    protected double mLatitude;
    protected double mLongitude;
    protected Date mDate;
    protected String mTime;
    protected String mImagePath;

    public Record() {

    }
    public Record(String mEmail, String mGuessSpecies, String mNotes) {
        this.mEmail = mEmail;
        this.mGuessSpecies = mGuessSpecies;
        this.mNotes = mNotes;
    }

    public Record setCoordinates (double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
        return this;
    }

    public Record setDate (Date date) {
        mDate = date;
        return this;
    }

    public Record setCurrentDate () {
        mDate = new Date(System.currentTimeMillis());
        return this;
    }

    public Record setImage(String imagePath) {
        mImagePath = imagePath;

        // todo get the info from the picture if it's from gallery. Here or server side?

        return this;
    }
}
