package com.learnncode.mediachooser;

public class GalleryRetainCache {
    private static GalleryRetainCache sSingleton;
    public GalleryCache mRetainedCache;

    public static GalleryRetainCache getOrCreateRetainableCache() {
        if (sSingleton == null) {
            sSingleton = new GalleryRetainCache();
        }
        return sSingleton;
    }

}
