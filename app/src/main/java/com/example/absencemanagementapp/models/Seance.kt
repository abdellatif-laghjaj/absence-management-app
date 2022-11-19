package com.example.absencemanagementapp.models

class Seance {
    var id: String? = null
    var date: String? = null
    var start_time: String? = null
    var end_time: String? = null
    var type: String? = "Cour"
    var n_salle: String? = "A4"
    var n_module: Int? = 0
    var total_absences: Int = 0

    constructor()


    constructor(
        id: String?,
        date: String?,
        start_time: String?,
        end_time: String?,
        type: String?,
        branch: String?,
        n_salle: String?,
        semester: Int?,
        n_module: Int?
    ) {
        this.id = id
        this.date = date
        this.start_time = start_time
        this.end_time = end_time
        this.type = type
        this.n_salle = n_salle
        this.n_module = n_module
    }

    constructor(date: String?, type: String?, total_absences: Int) {
        this.date = date
        this.type = type
        this.total_absences = total_absences
    }
}