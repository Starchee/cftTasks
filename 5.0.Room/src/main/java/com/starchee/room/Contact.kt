package com.starchee.room

import androidx.room.*

@Entity(tableName = "contacts")
@TypeConverters(Converter::class)
data class Contact(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phones")
    val phones: MutableList<String>
)