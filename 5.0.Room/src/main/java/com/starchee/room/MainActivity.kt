package com.starchee.room

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    companion object {
        private const val READ_CONTACTS_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                READ_CONTACTS_PERMISSION_REQUEST_CODE
            )

        }
        init()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_CONTACTS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    notifyAboutContactsPermission()
                }
            }
        }
    }


    private fun init() {
        val contactsAdapter = ContactsAdapter { phone -> callTo(phone) }
        contacts_rv_main.apply {
            adapter = contactsAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }


        ContactsRepository(this).loadContacts().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                contactsAdapter.setupContacts(it)
                Log.d("BOLL", "Update")
            }, {

            })

        contacts_swipeRefresh_main.setOnRefreshListener {
            if (contactsAdapter.contactList != getContact().sortedBy { it.name }) {
                ContactsRepository(this).uploadContacts(getContact()).subscribeOn(Schedulers.io())
                    .delay(3, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { contacts_swipeRefresh_main.isRefreshing = false }
            } else {
                contacts_swipeRefresh_main.isRefreshing = false
            }
        }
    }

    private fun callTo(phone: String) {
        startActivity(Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        })
    }

    private fun getContact(): List<Contact> {
        val contacts = ArrayList<Contact>()
        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            notifyAboutContactsPermission()
            return contacts
        }

        val phonesCursor = CursorLoader(
            this,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER} = 1",
            null,
            null
        ).loadInBackground()

        phonesCursor?.use {
            while (it.moveToNext()) {
                val contactId =
                    it.getLong(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var phone =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))
                if (phone == null) {
                    phone =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            .replace("-", "", true)
                }
                if (contacts.none { contact -> contact.id == contactId }) {
                    contacts.add(Contact(contactId, name, mutableListOf(phone)))
                } else {
                    val contactPhones = contacts.filter { contact -> contact.id == contactId }[0]

                    if (!contactPhones.phones.contains(phone)) {
                        contactPhones.phones.add(phone)
                    }
                }
            }
        }
        return contacts
    }

    private fun notifyAboutContactsPermission() {
        Toast.makeText(
            this,
            getString(R.string.read_contacts_permission_toast),
            Toast.LENGTH_LONG
        ).show()
    }
}
