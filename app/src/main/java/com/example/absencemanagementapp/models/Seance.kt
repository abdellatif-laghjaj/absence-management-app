package com.example.absencemanagementapp.models

class Seance {
    var id: String? = null
    private var date: String? = null
    private var start_time: String? = null
    private var end_time: String? = null
    private var type: String? = null
    private var n_salle: Int? = null
    private var n_module: Int? = null

    constructor()

    constructor(
        id: String?,
        date: String?,
        start_time: String?,
        end_time: String?,
        type: String?,
        branch: String?,
        n_salle: Int?,
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
}