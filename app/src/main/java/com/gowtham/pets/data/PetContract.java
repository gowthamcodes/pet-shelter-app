package com.gowtham.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {

    public static final String CONTENT_AUTHORITY = "com.gowtham.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";

    public static final class PetEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        public final static String TABLE_NAME = "pets";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PET_NAME = "name";
        public final static String COLUMN_PET_BREED = "breed";
        public final static String COLUMN_PET_AGE = "age";
        public final static String COLUMN_PET_ADOPTED = "isAdopted";
        public final static String COLUMN_PET_GENDER = "gender";
        public final static String COLUMN_PET_WEIGHT = "weight";
        public final static String COLUMN_PET_HEIGHT = "height";
        public final static String COLUMN_PET_HEALTH_NOTE = "healthNote";

        public final static int GENDER_UNKNOWN = 0;
        public final static int GENDER_MALE = 1;
        public final static int GENDER_FEMALE = 2;

        public final static int STATUS_ADOPTION_FALSE = 0;
        public final static int STATUS_ADOPTION_TRUE = 1;

        public static boolean isValidGender(Integer gender) {
            return gender == GENDER_MALE || gender == GENDER_FEMALE || gender == GENDER_UNKNOWN;
        }

        public static boolean isValidStatus(Integer isAdopted) {
            return isAdopted == STATUS_ADOPTION_TRUE || isAdopted == STATUS_ADOPTION_FALSE;
        }
    }

}
