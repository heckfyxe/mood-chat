package com.vk.sdk.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

public class VKApiChatSettings extends VKApiModel implements Parcelable {
    public static Parcelable.Creator<VKApiChatSettings> CREATOR = new Parcelable.Creator<VKApiChatSettings>() {
        @Override
        public VKApiChatSettings createFromParcel(Parcel source) {
            return new VKApiChatSettings(source);
        }

        @Override
        public VKApiChatSettings[] newArray(int size) {
            return new VKApiChatSettings[size];
        }
    };
    public String title;
    public int members_count;
    public State state;
    public VKApiPhotos photo;
    public int[] active_ids = new int[4];
    public boolean is_group_channel;
    public VKApiMessage pinned_message;

    public VKApiChatSettings(JSONObject source) {
        parse(source);
    }

    public VKApiChatSettings(Parcel in) {
        title = in.readString();
        members_count = in.readInt();
        state = (State) in.readSerializable();
        photo = in.readParcelable(VKApiPhotos.class.getClassLoader());
        in.readIntArray(active_ids);
        is_group_channel = in.readByte() == 1;
        pinned_message = in.readParcelable(VKApiMessage.class.getClassLoader());
    }

    @Override
    public VKApiChatSettings parse(JSONObject source) {
        title = source.optString("title");
        members_count = source.optInt("members_count");
        switch (source.optString("state")) {
            case "in":
                state = State.IN;
                break;
            case "kicked":
                state = State.KICKED;
                break;
            case "left":
                state = State.LEFT;
                break;
        }
        if (source.has("photo")) {
            photo = new VKApiPhotos(source.optJSONObject("photo"));
        }
        JSONArray activeIdsArray = source.optJSONArray("active_ids");
        for (int i = 0; i < 4; i++) {
            active_ids[i] = activeIdsArray.optInt(i);
        }
        is_group_channel = source.optBoolean("is_group_channel");
        if (source.has("pinned_message")) {
            pinned_message = new VKApiMessage(source.optJSONObject("pinned_message"));
        }
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(members_count);
        dest.writeSerializable(state);
        dest.writeParcelable(photo, flags);
        dest.writeIntArray(active_ids);
        dest.writeByte(is_group_channel ? (byte) 1 : 0);
        dest.writeParcelable(pinned_message, flags);
    }

    public enum State {
        IN, KICKED, LEFT
    }
}
