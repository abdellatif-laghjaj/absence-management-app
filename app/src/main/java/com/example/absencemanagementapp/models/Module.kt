package com.example.absencemanagementapp.models

data class Module(
    var id: Int? = null,
    var intitule: String? = null,
    var abrv: String? = null,
    var semestre: Any? = null,
    var formation: String? = "GI",
    var respo_id: String
) {
    override fun toString(): String {
        return "Module(id=$id, intitule=$intitule, abrv=$abrv, semestre=$semestre, formation=$formation, respo_id='$respo_id')"
    }
}