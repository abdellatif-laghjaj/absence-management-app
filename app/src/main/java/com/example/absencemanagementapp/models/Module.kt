package com.example.absencemanagementapp.models

data class Module(
    var id: Int,
    var intitule: String,
    var abrv: String,
    var semestre: Int,
    var formation: String,
    var respo_id: String
) {


   constructor() : this(
       -1,
       "",
       "",
       -1,
       "",
       ""
   )

    override fun toString(): String {
        return "Module(id=$id, intitule=$intitule, abrv=$abrv, semestre=$semestre, formation=$formation, respo_id='$respo_id')"
    }
}