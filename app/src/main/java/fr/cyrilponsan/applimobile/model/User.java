package fr.cyrilponsan.applimobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class User implements Parcelable {

     private final int mUserId;
     private String mToken;

     public User(JSONObject user) {
          mUserId = user.optInt("userId");
          mToken = user.optString("token");
     }

     protected User(Parcel in) {
          mUserId = in.readInt();
          mToken = in.readString();
     }

     public static final Creator<User> CREATOR = new Creator<User>() {
          @Override
          public User createFromParcel(Parcel in) {
               return new User(in);
          }

          @Override
          public User[] newArray(int size) {
               return new User[size];
          }
     };

     public int getUserId() {
          return mUserId;
     }

     public String getToken() {
          return mToken;
     }

     @Override
     public int describeContents() {
          return 0;
     }

     @Override
     public void writeToParcel(Parcel parcel, int i) {
          parcel.writeInt(mUserId);
          parcel.writeString(mToken);
     }
}
