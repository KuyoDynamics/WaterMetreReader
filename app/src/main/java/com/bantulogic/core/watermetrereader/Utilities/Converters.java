package com.bantulogic.core.watermetrereader.Utilities;

import java.util.Date;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null : new Date(dateLong);
    }
    @TypeConverter
    public static Long toLong(Date date){
        return date == null ? null  : date.getTime();
    }
}
