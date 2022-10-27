package com.example.absencemanagementapp.models

open class User {
    var id: Int = 0
    var first_name: String = ""
    var last_name: String = ""
    var cin: String = ""
    var filiere: String = ""
    var semester: String = ""
    var email: String = ""
    var password: String = ""

    constructor()
    constructor(
        id: Int,
        first_name: String,
        last_name: String,
        cin: String,
        filiere: String,
        semester: String,
        email: String,
        password: String
    ) {
        this.id = id
        this.first_name = first_name
        this.last_name = last_name
        this.cin = cin
        this.filiere = filiere
        this.semester = semester
        this.email = email
        this.password = password
    }
}
