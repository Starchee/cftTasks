package com.starchee.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.contacts_item.view.*

class ContactsAdapter(val phoneOnClick: (String) -> Unit) :
    RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    var contactList: ArrayList<Contact> = ArrayList()
        private set

    fun setupContacts(contactList: List<Contact>) {
        this.contactList
        this.contactList.clear()
        this.contactList.addAll(contactList.sortedBy { it.name })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.contacts_item, parent, false)
        return ContactsViewHolder(itemView = itemView)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(contact = contactList[position])
    }

    override fun getItemCount(): Int {
        return contactList.count()
    }


    inner class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(contact: Contact) {
            itemView.apply {
                name_contacts_item.text = contact.name
                phone_1_contacts_item.apply {
                    text = contact.phones[0]
                    setOnClickListener { phoneOnClick(text.toString()) }
                }
            }
            when (contact.phones.count()) {
                1 -> {
                    itemView.apply {
                        phone_2_group_item.visibility = View.GONE
                        phone_3_group_item.visibility = View.GONE
                    }
                }
                2 -> {
                    itemView.apply {
                        phone_2_contacts_item.apply {
                            text = contact.phones[1]
                            setOnClickListener {
                                phoneOnClick(text.toString())
                            }
                        }
                        phone_2_group_item.visibility = View.VISIBLE
                        phone_3_group_item.visibility = View.GONE

                    }
                }
                else -> {
                    itemView.apply {
                        phone_2_contacts_item.apply {
                            text = contact.phones[1]
                            setOnClickListener {
                                phoneOnClick(text.toString())
                            }
                        }
                        phone_3_contacts_item.apply {
                            text = contact.phones[2]
                            setOnClickListener {
                                phoneOnClick(text.toString())
                            }
                        }
                        phone_2_group_item.visibility = View.VISIBLE
                        phone_3_group_item.visibility = View.VISIBLE

                    }
                }
            }
        }
    }
}