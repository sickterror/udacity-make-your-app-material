package com.example.xyzreader.ui;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.request.transition.Transition;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.PageModel;
import com.example.xyzreader.util.GlideApp;
import com.example.xyzreader.util.PalleteParser;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";
    private static final float PARALLAX_FACTOR = 1.25f;

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private int mMutedColor = 0xFF333333;
    private ObservableScrollView mScrollView;
    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    private ColorDrawable mStatusBarColorDrawable;

    private int mTopInset;
    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private int mScrollY;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
    private ImageView mImageView;
    private PageModel pageModel;
    private TextView bodyView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(PageModel pageModel) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_ITEM_ID, pageModel);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            pageModel = getArguments().getParcelable(ARG_ITEM_ID);
        }
        setHasOptionsMenu(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail_v2, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) mRootView.findViewById(R.id.toolbar));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        GlideApp.with(this).asBitmap().load(pageModel.getImageurl()).into(new PalleteParser(pageModel.getImageurl(), Color.BLACK) {
            @Override
            public void onPalleteReady(Palette palete) {

            }

            @Override
            protected void onDarkMutedReady(int darkMutedColor, Palette.Swatch swatch) {
                super.onDarkMutedReady(darkMutedColor, swatch);
                mRootView.findViewById(R.id.title_layout).setBackgroundColor(darkMutedColor);
            }

            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);
                if (resource != null)
                    mImageView.setImageBitmap(resource);
            }
        });

        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    private void bindViews() {
        if (mRootView == null) {
            return;
        }
        TextView titleView = (TextView) mRootView.findViewById(R.id.article_title);
        TextView bylineView = (TextView) mRootView.findViewById(R.id.article_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        bodyView = (TextView) mRootView.findViewById(R.id.article_body);
        mImageView = (ImageView) mRootView.findViewById(R.id.ivMain);
        titleView.setText(pageModel.getTitle());
        bylineView.setText(pageModel.getAuthor());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(pageModel.getTitle());

        new Thread(new Runnable() {
            public String content = "";

            @Override
            public void run() {
                Uri uri = ItemsContract.Items.buildDirUri();
                Cursor c = getActivity().getContentResolver().query(uri, null, ItemsContract.Items._ID + " = ?", new String[]{pageModel.getId() + ""}, null);
                if (c != null) {
                    if (c.moveToFirst()) {
                        content = c.getString(c.getColumnIndex(ItemsContract.Items.BODY));
                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public Spanned body;

                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            body = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
                        } else {
                            body = Html.fromHtml(content);
                        }
                        bodyView.setText(body);
                    }
                });
            }
        }).start();
    }
}
