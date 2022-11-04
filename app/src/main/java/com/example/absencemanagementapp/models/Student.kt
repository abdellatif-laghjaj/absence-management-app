package com.example.absencemanagementapp.models

class Student(var cne: String, var filiere: String, var semester: String) : User() {
    constructor() : this("", "", "")
}
