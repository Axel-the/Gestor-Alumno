package com.example.mvpparaandroid.repository

import com.example.mvpparaandroid.model.ApiResponse
import com.example.mvpparaandroid.model.Student
import com.example.mvpparaandroid.model.StudentSubmission
import com.example.mvpparaandroid.network.RetrofitClient

class StudentRepository {
    private val api = RetrofitClient.instance

    suspend fun getStudents(): Result<List<Student>> {
        return try {
            val response = api.getStudents()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerStudent(student: StudentSubmission): Result<ApiResponse> {
        return try {
            val response = api.registerStudent(student)
            if (response.status == "success") {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Unknown API error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudent(student: StudentSubmission): Result<ApiResponse> {
        return registerStudent(student.copy(action = "update"))
    }

    suspend fun deleteStudent(nombres: String, apellidos: String): Result<ApiResponse> {
        // We reuse the submission object but set action to delete
        // We only really need names to identify the row to delete
        val submission =
                StudentSubmission(
                        action = "delete",
                        nombres = nombres,
                        apellidos = apellidos,
                        // Empty other fields
                        apoderado = "",
                        email = "",
                        telefono = "",
                        nivel = "",
                        grupo = "",
                        dias = "",
                        horario = "",
                        fecha_inicio = "",
                        fecha_fin = "",
                        mensualidad = ""
                )
        return registerStudent(submission)
    }
}
