package edu.stanford.baseline.sharkpulse3;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by emazzilli on 9/13/14.
 */


public class Record implements Parcelable {
    protected String mEmail;
    protected String mGuessSpecies;
    protected String mNotes;
    protected double mLatitude;
    protected double mLongitude;
    protected Date mDate;
    protected String mTime;
    protected Bitmap mBitmap;

    public Record() {

    }
    public Record(String mEmail, String mGuessSpecies, String mNotes) {
        this.mEmail = mEmail;
        this.mGuessSpecies = mGuessSpecies;
        this.mNotes = mNotes;
    }

    protected Record(Parcel in) {
        mEmail = in.readString();
        mGuessSpecies = in.readString();
        mNotes = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mDate = (Date) in.readSerializable();
        mTime = in.readString();
        mBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

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

    public Record setImage(Bitmap bitmap) {
        mBitmap = bitmap;

        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEmail);
        dest.writeString(mGuessSpecies);
        dest.writeString(mNotes);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeSerializable(mDate);
        dest.writeString(mTime);
        dest.writeParcelable(mBitmap, 0);
    }
}
