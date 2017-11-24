package com.easyroute.utility;

import com.google.gson.JsonSyntaxException;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.lang.reflect.Type;
/**
 * Created by imenekse on 13/02/17.
 */

public class Xson {

    private static XmlParserCreator sXmlParserCreator;
    private static GsonXml sGsonXml;

    private XmlParserCreator getXmlParserCreator() {
        if (sXmlParserCreator == null) {
            sXmlParserCreator = new XmlParserCreator() {
                @Override
                public XmlPullParser createParser() {
                    try {
                        return XmlPullParserFactory.newInstance().newPullParser();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
        return sXmlParserCreator;
    }

    private GsonXml getGsonXml() {
        if (sGsonXml == null) {
            sGsonXml = new GsonXmlBuilder().setXmlParserCreator(getXmlParserCreator()).setSameNameLists(true).create();
        }
        return sGsonXml;
    }

    public <T> T fromXml(String json, Class<T> classOfT) throws JsonSyntaxException {
        try {
            return getGsonXml().fromXml(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T fromXml(final String json, final Type typeOfT) throws JsonSyntaxException {
        try {
            return getGsonXml().fromXml(json, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJson(Object src) {
        // Bu metod henuz kullanimda olmadigi icin doldurulmadi.
        return null;
    }
}
