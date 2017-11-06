package com.example.xyzreader.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.xyzreader.ui.ArticleDetailActivity;

/**
 * Created by Luka on 6.11.2017.
 */

public class PageModel implements Parcelable {
    private int id;
    private String title, imageurl, imageurltn, content, date, author;
    private int color;

    public PageModel() {

    }


    protected PageModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        imageurl = in.readString();
        imageurltn = in.readString();
        //content = in.readString();
        date = in.readString();
        author = in.readString();
        color = in.readInt();
    }

    public static final Creator<PageModel> CREATOR = new Creator<PageModel>() {
        @Override
        public PageModel createFromParcel(Parcel in) {
            return new PageModel(in);
        }

        @Override
        public PageModel[] newArray(int size) {
            return new PageModel[size];
        }
    };

    public static PageModel buildPage(Cursor cursor) {
        PageModel pageModel = new PageModel();
        pageModel.title = cursor.getString(ArticleLoader.Query.TITLE);
        pageModel.imageurl = cursor.getString(ArticleLoader.Query.PHOTO_URL);
        pageModel.imageurltn = cursor.getString(ArticleLoader.Query.THUMB_URL);
       // pageModel.content = cursor.getString(ArticleLoader.Query.BODY);
        pageModel.date = cursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
        pageModel.author = cursor.getString(ArticleLoader.Query.AUTHOR);
        pageModel.id = cursor.getInt(ArticleLoader.Query._ID);
        pageModel.color = 0;
        return pageModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(imageurl);
        parcel.writeString(imageurltn);
       // parcel.writeString(content);
        parcel.writeString(date);
        parcel.writeString(author);
        parcel.writeInt(color);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getImageurltn() {
        return imageurltn;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public int getColor() {
        return color;
    }
}
