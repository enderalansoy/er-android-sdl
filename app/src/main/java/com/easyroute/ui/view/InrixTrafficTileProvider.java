package com.easyroute.ui.view;

import com.google.android.gms.maps.model.UrlTileProvider;
import com.inrix.sdk.TileManager;
import com.inrix.sdk.TileManager.TileOptions;

import java.net.MalformedURLException;
import java.net.URL;
/**
 * Created by imenekse on 23/02/17.
 */

public class InrixTrafficTileProvider extends UrlTileProvider {

    private TileManager mTileManager;
    private TileOptions mTileOptions;

    public InrixTrafficTileProvider(TileManager tileManager, long dateTimeMillis) {
        super(TileManager.TILE_DEFAULT_WIDTH, TileManager.TILE_DEFAULT_HEIGHT);
        mTileManager = tileManager;
        mTileOptions = new TileOptions();
        mTileOptions.setOpacity(100);
        mTileOptions.setStartTime(dateTimeMillis);
    }

    @Override
    public URL getTileUrl(int x, int y, int zoom) {
        if (!this.mTileManager.showTrafficTiles(zoom)) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(this.mTileManager.getTileUrl(x, y, zoom, mTileOptions));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
