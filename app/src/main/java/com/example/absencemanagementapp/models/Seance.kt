package com.example.absencemanagementapp.models

class Seance {
    var id: String? = null
    private var date: String? = null
    private var startTime: String? = null
    private var endTime: String? = null
    private var type: String? = null
    private var branch: String? = null
    private var nSalle: Int? = null
    private var semester: Int? = null
    private var nModule: Int? = null

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
        this.startTime = start_time
        this.endTime = end_time
        this.type = type
        this.branch = branch
        this.nSalle = n_salle
        this.semester = semester
        this.nModule = n_module
    }
}