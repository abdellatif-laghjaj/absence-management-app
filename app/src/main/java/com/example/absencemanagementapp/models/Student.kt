package com.example.absencemanagementapp.models

class Student() : User() {
    var cne: String = ""
    var filiere: String = ""
    var semester: String = ""

    constructor(
        first_name: String,
        last_name: String,
        cin: String,
        cne: String,
        filiere: String,
        semester: String,
        email: String,
        password: String
    ) : this() {
        this.first_name = first_name
        this.last_name = last_name
        this.cin = cin
        this.cne = cne
        this.filiere = filiere
        this.semester = semester
        this.email = email
        this.password = password
    }
}
