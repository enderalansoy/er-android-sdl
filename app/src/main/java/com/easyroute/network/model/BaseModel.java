package com.easyroute.network.model;

import com.google.gson.Gson;

import java.io.Serializable;
/**
 * Created by macbookpro on 19/05/16.
 */
public class BaseModel implements Serializable {
    private static Gson sGson = new Gson();

    @Override
    public String toString() {
        return sGson.toJson(this);
    }
}
