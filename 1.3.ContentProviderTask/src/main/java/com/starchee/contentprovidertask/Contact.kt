package com.starchee.contentprovidertask

data class Contact(
    val id: String,
    val name:String,
    val phones:List<String>) {
    override fun toString(): String {
        return "$id $name $phones \n"
    }
}