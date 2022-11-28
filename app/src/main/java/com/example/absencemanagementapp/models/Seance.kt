package com.example.absencemanagementapp.models

data class Seance(
    var id: String? = null,
    var date: String? = null,
    var start_time: String? = null,
    var end_time: String? = null,
    var type: String? = "Cour",
    var n_salle: String? = "A4",
    var n_module: Int? = 0,
    var total_absences: Int = 0
) {
    override fun toString(): String {
        return "Seance(id=$id, date=$date, start_time=$start_time, end_time=$end_time, type=$type, n_salle=$n_salle, n_module=$n_module, total_absences=$total_absences)"
    }
}