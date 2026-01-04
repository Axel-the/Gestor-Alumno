package com.example.mvpparaandroid.model

import com.google.gson.annotations.SerializedName

data class Student(
        @SerializedName("Nombres") val nombres: String,
        @SerializedName("Apellidos") val apellidos: String,
        @SerializedName("Apoderado") val apoderado: String = "",
        @SerializedName("Email") val email: String = "",
        @SerializedName("Teléfono") val telefono: String = "",
        @SerializedName("Nivel") val nivel: String = "",
        @SerializedName("Grupo") val grupo: String = "",
        @SerializedName("Dias") val dias: String = "",
        @SerializedName("Horario") val horario: String = "",
        @SerializedName("Fecha Inicio") val fechaInicio: String = "",
        @SerializedName("Fecha fin") val fechaFin: String = "",
        @SerializedName("Mensualidad") val mensualidad: String = "",
        @SerializedName("Fecha Registro") val fechaRegistro: String = "",
        @SerializedName("Estado") val estado: String = ""
)

data class StudentSubmission(
        val action: String = "create", // create, update, delete

        // Identifiers for update/delete (original values)
        val original_nombres: String? = null,
        val original_apellidos: String? = null,
        val nombres: String,
        val apellidos: String,
        val apoderado: String = "",
        val email: String = "",
        val telefono: String = "",
        val nivel: String = "",
        val grupo: String = "",
        val dias: String = "",
        val horario: String = "",
        val fecha_inicio: String = "",
        val fecha_fin: String = "",
        val mensualidad: String = "",
        val fecha_registro: String = "",
        val estado: String = "activo"
)

data class ApiResponse(val status: String, val message: String?)
