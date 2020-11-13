package com.starchee.room

import androidx.room.TypeConverter

class Converter {
    @TypeConverter
    fun fromString(value: String) = value.split(",").map { it }

    @TypeConverter
    fun toString(value: List<String>) = value.joinToString(separator = ",")
}