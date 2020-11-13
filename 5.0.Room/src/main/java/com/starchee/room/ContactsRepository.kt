package com.starchee.room

import android.content.Context
import androidx.room.Room

object ContactsRepository {

    private const val DATABASE_NAME = "contacts_database"
    private lateinit var contactsDatabase: ContactsDatabase

    operator fun invoke(context: Context): ContactsRepository {
        if (!this::contactsDatabase.isInitialized) {
            contactsDatabase =
                Room.databaseBuilder(context, ContactsDatabase::class.java, DATABASE_NAME).build()
        }
        return this
    }

    fun uploadContacts(contactsList: List<Contact>) =
        contactsDatabase.contactsDao().insertContacts(contactList = contactsList)

    fun loadContacts() = contactsDatabase.contactsDao().getAllContacts()
}