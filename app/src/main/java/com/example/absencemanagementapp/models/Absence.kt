package com.example.absencemanagementapp.models

data class Absence(
    var id: String,
    var cne: String,
    var seance_id: String,
    var is_present: Boolean
) {
    constructor() : this("", "", "", false)
}