package com.example.mvpparaandroid.network

import com.example.mvpparaandroid.model.ApiResponse
import com.example.mvpparaandroid.model.Student
import com.example.mvpparaandroid.model.StudentSubmission
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SheetApiService {
    @GET("exec")
    suspend fun getStudents(): List<Student>

    @POST("exec")
    suspend fun registerStudent(@Body student: StudentSubmission): ApiResponse
}
