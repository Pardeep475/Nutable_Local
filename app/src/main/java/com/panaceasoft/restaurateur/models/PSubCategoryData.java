package com.panaceasoft.restaurateur.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 8/8/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class PSubCategoryData implements Parcelable {

    public int id;

    public int cat_id;

    public int shop_id;

    public String name;

    public int is_published;

    public int ordering;

    public String added;

    public String updated;

    public String cover_image_file;

    public int cover_image_width;

    public int cover_image_height;

    public ArrayList<PItemData> items;

    protected PSubCategoryData(Parcel in) {
        id = in.readInt();
        cat_id = in.readInt();
        shop_id = in.readInt();
        name = in.readString();
        is_published = in.readInt();
        ordering = in.readInt();
        added = in.readString();
        updated = in.readString();
        cover_image_file = in.readString();
        cover_image_width = in.readInt();
        cover_image_height = in.readInt();
        if (in.readByte() == 0x01) {
            items = new ArrayList<PItemData>();
            in.readList(items, PItemData.class.getClassLoader());
        } else {
            items = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(cat_id);
        dest.writeInt(shop_id);
        dest.writeString(name);
        dest.writeInt(is_published);
        dest.writeInt(ordering);
        dest.writeString(added);
        dest.writeString(updated);
        dest.writeString(cover_image_file);
        dest.writeInt(cover_image_width);
        dest.writeInt(cover_image_height);
        if (items == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(items);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PSubCategoryData> CREATOR = new Parcelable.Creator<PSubCategoryData>() {
        @Override
        public PSubCategoryData createFromParcel(Parcel in) {
            return new PSubCategoryData(in);
        }

        @Override
        public PSubCategoryData[] newArray(int size) {
            return new PSubCategoryData[size];
        }
    };
}
