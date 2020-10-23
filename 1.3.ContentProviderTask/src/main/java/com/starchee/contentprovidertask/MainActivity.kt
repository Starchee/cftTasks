package com.starchee.contentprovidertask

import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_rv_contacts.text = getContact().toString()
    }


    private fun getContact(): List<Contact> {
        val contacts = ArrayList<Contact>()


        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1",
            null,
            ContactsContract.Contacts.DISPLAY_NAME
        )

        cursor?.use {
            while (it.moveToNext()) {
                val contactId =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phones = getPhoneNumber(contactId)
                contacts.add(Contact(id = contactId, name = name, phones = phones))
            }
        }
        return contacts
    }

    private fun getPhoneNumber(contactId: String): List<String> {
        val phones = ArrayList<String>()
        val phonesCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $contactId",
            null,
            null
        )

        phonesCursor?.use {
            while (it.moveToNext()) {
                val phone =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))
                if (!phones.contains(phone)) {
                    phones.add(phone)
                }
            }
        }
        return phones
    }

}


