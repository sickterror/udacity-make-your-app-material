package com.example.xyzreader.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Luka on 12.10.2017.
 * <p>
 * Simple class to parse Glide image pallete.
 * <p>
 * It has an in memory cache. Holds 500 pallets
 */

public abstract class PalleteParser extends SimpleTarget<Bitmap> {


    private static Map<String, Palette> palleteCache = new LinkedHashMap<>();
    private String id;
    private int defaultColor = Color.WHITE;

    private static final int MAX_PALETTE_CACHE_SIZE = 500;

    public PalleteParser(String id) {
        this.id = id;
    }

    public PalleteParser(String id, int defaultColor) {
        this.id = id;
        this.defaultColor = defaultColor;
    }

    @Override
    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
        Palette p = extractColor(resource);
        onPalleteReady(p);
        onLightVibrantReady(p.getDarkVibrantColor(defaultColor), p.getVibrantSwatch());
        onVibrantReady(p.getVibrantColor(defaultColor), p.getVibrantSwatch());
        onDarkMutedReady(p.getDarkMutedColor(defaultColor), p.getDarkVibrantSwatch());
        onDarkVibrantReady(p.getDarkVibrantColor(defaultColor), p.getDarkVibrantSwatch());
        onLightMutedReady(p.getLightMutedColor(defaultColor), p.getLightMutedSwatch());
        onDarkMutedReady(p.getDarkMutedColor(defaultColor), p.getDarkMutedSwatch());
    }

    public abstract void onPalleteReady(Palette palete);


    private Palette extractColor(Bitmap b) {
        Palette palette;
        if (palleteCache.containsKey(this.id)) {
            palette = palleteCache.get(id);
        } else {
            palette = Palette.from(b).generate();
        }
        putToCache(palette);
        return palette;
    }

    protected void onLightVibrantReady(int darkVibrantColor, Palette.Swatch swatch) {
    }

    protected void onVibrantReady(int vibrantColor, Palette.Swatch swatch) {
    }

    protected void onDarkVibrantReady(int darkVibrantColor, Palette.Swatch swatch) {
    }

    protected void onLightMutedReady(int lightMutedColor, Palette.Swatch swatch) {
    }

    protected void onMutedReady(Palette.Swatch swatch) {
    }

    protected void onDarkMutedReady(int darkMutedColor, Palette.Swatch swatch) {
    }

    private void putToCache(Palette palette) {
        if (palleteCache.size() > MAX_PALETTE_CACHE_SIZE) {
            Object key = palleteCache.keySet().iterator().next();
            palleteCache.remove(key);
        }
        palleteCache.put(id, palette);
    }
}
