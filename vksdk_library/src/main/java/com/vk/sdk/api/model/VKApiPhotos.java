package com.vk.sdk.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class VKApiPhotos extends VKApiModel implements Parcelable {

    public String photo_50;
    public String photo_100;
    public String photo_200;

    public VKApiPhotos() {}

    public VKApiPhotos(JSONObject from) {
        parse(from);
    }

    public VKApiPhotos(Parcel in) {
        photo_50 = in.readString();
        photo_100 = in.readString();
        photo_200 = in.readString();
    }

    @Override
    public VKApiPhotos parse(JSONObject response) {
        photo_50 = response.optString("photo_50");
        photo_100 = response.optString("photo_100");
        photo_200 = response.optString("photo_200");
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photo_50);
        dest.writeString(photo_100);
        dest.writeString(photo_200);
    }

    private static Creator<VKApiPhotos> CREATOR = new Creator<VKApiPhotos>() {
        @Override
        public VKApiPhotos createFromParcel(Parcel source) {
            return new VKApiPhotos(source);
        }

        @Override
        public VKApiPhotos[] newArray(int size) {
            return new VKApiPhotos[size];
        }
    };
}
