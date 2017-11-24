package com.easyroute.content.constant;

/**
 * Created by imenekse on 25/02/17.
 */

public enum TollCollection {
    BOGAZ_ICI_KOPRUSU("BOGAZ_ICI_KOPRUSU", 8006256, 8006253, TollCollectionType.BRIDGE),
    FATIH_SULTAN_MEHMET_KOPRUSU("FATIH_SULTAN_MEHMET_KOPRUSU", 8007650, 8007692, TollCollectionType.BRIDGE),
    HAREM_FERIBOTU("HAREM_FERIBOTU", new int[]{8009371, 75495128}, null, TollCollectionType.BRIDGE),
    ANADOLU_GISESI("ANADOLU", 8007662, 8007811, TollCollectionType.ANATOLIA),
    SAMANDIRA_GISESI("SAMANDIRA", 8009415, 8011705, TollCollectionType.ANATOLIA),
    SULTANBEYLI_GISESI("SULTANBEYLİ", 25034879, 25034878, TollCollectionType.ANATOLIA),
    KURTKOY_GISESI("KURTKÖY", 8027223, 8008299, TollCollectionType.ANATOLIA),
    ORHANLI_GISESI("ORHANLI", 8011892, 8024471, TollCollectionType.ANATOLIA),
    SEKERPINAR_GISESI("Ş.PINAR", 8011237, 8053162, TollCollectionType.ANATOLIA),
    AKINCI_GISESI("AKINCI", -1, -1, TollCollectionType.EUROPE),
    MAHMUTBEY_GISESI("MAHMUTBEY", 75470178, 8008180, TollCollectionType.EUROPE),
    ISPARTAKULE_GISESI("ISPARTAKULE", new int[]{24819648, 24819657}, new int[]{-1}, TollCollectionType.EUROPE),
    AVCILAR_GISESI("AVCILAR", 8007004, 8007652, TollCollectionType.EUROPE),
    ESENYURT_GISESI("ESENYURT", new int[]{75535150, 75535885}, new int[]{25053093}, TollCollectionType.EUROPE),
    HADIMKOY_GISESI("HADIMKÖY", 24742926, 8009318, TollCollectionType.EUROPE),
    CATALCA_GISESI("ÇATALCA", 75470395, 8008331, TollCollectionType.EUROPE),
    KUMBURGAZ_GISESI("K.BURGAZ", 8008420, 8008422, TollCollectionType.EUROPE),
    SELIMPASA_GISESI("SELİMPAŞA", 8008402, 8008378, TollCollectionType.EUROPE),
    SILIVRI_GISESI("SİLİVRİ", 75470149, 8007771, TollCollectionType.EUROPE),
    KINALI_GISESI("KINALI", 8009472, 8008412, TollCollectionType.EUROPE),
    CERKEZKOY_GISESI("ÇERKEZKÖY", 8010627, 8010626, TollCollectionType.EUROPE),
    EDIRNE_GISESI("EDİRNE", -1, -1, TollCollectionType.EUROPE);

    public static final int ISPARTAKULE_LAST_SEGMENT = 8012029;

    private String mName;
    private int[] mEntrySegments;
    private int[] mExitSegments;
    private TollCollectionType mType;

    TollCollection(String name, int entrySegment, int exitSegment, TollCollectionType type) {
        mName = name;
        mEntrySegments = new int[]{entrySegment};
        mExitSegments = new int[]{exitSegment};
        mType = type;
    }

    TollCollection(String name, int[] entrySegments, int[] exitSegments, TollCollectionType type) {
        mName = name;
        mEntrySegments = entrySegments;
        mExitSegments = exitSegments;
        mType = type;
    }

    public TollCollectionType getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public boolean checkSegment(int segment) {
        for (int entrySegment : mEntrySegments) {
            if (entrySegment == segment) {
                return true;
            }
        }
        if (mType != TollCollectionType.BRIDGE) {
            for (int exitSegment : mExitSegments) {
                if (exitSegment == segment) {
                    return true;
                }
            }
        }
        return false;
    }
}
