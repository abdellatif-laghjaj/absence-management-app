package com.example.absencemanagementapp.models

class Teacher() : User() {

    constructor(
        first_name: String,
        last_name: String,
        cin: String,
        email: String
    ) : this() {
        this.first_name = first_name
        this.last_name = last_name
        this.cin = cin
        this.email = email
    }

    constructor(
        first_name: String,
        last_name: String,
        cin: String,
        email: String,
        password: String
    ) : this() {
        this.first_name = first_name
        this.last_name = last_name
        this.cin = cin
        this.email = email
        this.password = password
    }
}