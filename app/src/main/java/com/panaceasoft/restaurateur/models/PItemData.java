package com.panaceasoft.restaurateur.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 8/2/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class PItemData implements Parcelable {

    public int id;

    public int cat_id;

    public int sub_cat_id;

    public int shop_id;

    public int discount_type_id;

    public String discount_percent;

    public String discount_name;

    public String name;

    public String description;

    public float unit_price;

    public String search_tag;

    public int is_published;

    public String added;

    public String updated;

    public String like_count;

    public String review_count;

    public String inquiries_count;

    public String rating_count;

    public String currency_symbol;

    public String currency_short_form;

    public ArrayList<PReviewData> reviews;

    public ArrayList<PImageData> images;

    public ArrayList<PAttributesData> attributes;

    public PItemData(int id, int cat_id, int sub_cat_id, int shop_id, int discount_type_id, String discount_percent, String discount_name, String name, String description, float unit_price, String search_tag, int is_published, String added, String updated, String like_count, String review_count, String inquiries_count, String rating_count, String currency_symbol, String currency_short_form, ArrayList<PReviewData> reviews, ArrayList<PImageData> images, ArrayList<PAttributesData> attributes) {
        this.id = id;
        this.cat_id = cat_id;
        this.sub_cat_id = sub_cat_id;
        this.shop_id = shop_id;
        this.discount_type_id = discount_type_id;
        this.discount_percent = discount_percent;
        this.discount_name = discount_name;
        this.name = name;
        this.description = description;
        this.unit_price = unit_price;
        this.search_tag = search_tag;
        this.is_published = is_published;
        this.added = added;
        this.updated = updated;
        this.like_count = like_count;
        this.review_count = review_count;
        this.inquiries_count = inquiries_count;
        this.rating_count = rating_count;
        this.currency_symbol = currency_symbol;
        this.currency_short_form = currency_short_form;
        this.reviews = reviews;
        this.images = images;
        this.attributes = attributes;
    }


    protected PItemData(Parcel in) {
        id = in.readInt();
        cat_id = in.readInt();
        sub_cat_id = in.readInt();
        shop_id = in.readInt();
        discount_type_id = in.readInt();
        discount_name = in.readString();
        discount_percent = in.readString();
        name = in.readString();
        description = in.readString();
        unit_price = in.readFloat();
        search_tag = in.readString();
        is_published = in.readInt();
        added = in.readString();
        updated = in.readString();
        like_count = in.readString();
        review_count = in.readString();
        inquiries_count = in.readString();
        rating_count = in.readString();
        currency_symbol = in.readString();
        currency_short_form = in.readString();

        if (in.readByte() == 0x01) {
            reviews = new ArrayList<PReviewData>();
            in.readList(reviews, PReviewData.class.getClassLoader());
        } else {
            reviews = null;
        }

        if (in.readByte() == 0x01) {
            images = new ArrayList<PImageData>();
            in.readList(images, PImageData.class.getClassLoader());
        } else {
            images = null;
        }

        if (in.readByte() == 0x01) {
            attributes = new ArrayList<PAttributesData>();
            in.readList(attributes, PAttributesData.class.getClassLoader());
        } else {
            attributes = null;
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
        dest.writeInt(sub_cat_id);
        dest.writeInt(shop_id);
        dest.writeInt(discount_type_id);
        dest.writeString(discount_name);
        dest.writeString(discount_percent);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeFloat(unit_price);
        dest.writeString(search_tag);
        dest.writeInt(is_published);
        dest.writeString(added);
        dest.writeString(updated);
        dest.writeString(like_count);
        dest.writeString(review_count);
        dest.writeString(inquiries_count);
        dest.writeString(rating_count);
        dest.writeString(currency_symbol);
        dest.writeString(currency_short_form);


        if (reviews == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(reviews);
        }

        if (images == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(images);
        }

        if (attributes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(attributes);
        }

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PItemData> CREATOR = new Parcelable.Creator<PItemData>() {
        @Override
        public PItemData createFromParcel(Parcel in) {
            return new PItemData(in);
        }

        @Override
        public PItemData[] newArray(int size) {
            return new PItemData[size];
        }
    };
}