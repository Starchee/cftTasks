package com.starchee.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface ContactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContacts(contactList: List<Contact>): Completable

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): Observable<List<Contact>>
}