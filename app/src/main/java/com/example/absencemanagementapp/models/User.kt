package com.example.absencemanagementapp.models

open class User {
    var first_name: String = ""
    var last_name: String = ""
    var cin: String = ""
    var email: String = ""
    var password: String = ""

    constructor()
    constructor(
        first_name: String,
        last_name: String,
        cin: String,
        email: String,
        password: String
    ) {
        this.first_name = first_name
        this.last_name = last_name
        this.cin = cin
        this.email = email
        this.password = password
    }

    override fun toString(): String {
        return "User(first_name='$first_name', last_name='$last_name', cin='$cin', email='$email', password='$password')"
    }


}
