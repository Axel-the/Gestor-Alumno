package com.example.mvpparaandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvpparaandroid.ui.StudentViewModel
import com.example.mvpparaandroid.ui.screens.RegisterStudentScreen
import com.example.mvpparaandroid.ui.screens.StudentListScreen
import com.example.mvpparaandroid.ui.theme.MVPParaAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVPParaAndroidTheme {
                val navController = rememberNavController()
                val viewModel: StudentViewModel = viewModel()

                NavHost(navController = navController, startDestination = "student_list") {
                    composable("student_list") {
                        val state by viewModel.studentListState.collectAsState()
                        val filteredList by viewModel.filteredStudents.collectAsState()
                        val searchQuery by viewModel.searchQuery.collectAsState()

                        StudentListScreen(
                                uiState = state,
                                filteredStudents = filteredList,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                                onFabClick = {
                                    viewModel.resetRegistrationState()
                                    viewModel.clearSelection() // Clear for new student
                                    navController.navigate("register_student")
                                },
                                onStudentClick = { student ->
                                    viewModel.resetRegistrationState()
                                    viewModel.selectStudent(student) // Load student for editing
                                    navController.navigate("register_student")
                                },
                                onRefresh = { viewModel.fetchStudents() }
                        )
                    }
                    composable("register_student") {
                        val state by viewModel.registrationState.collectAsState()
                        RegisterStudentScreen(
                                uiState = state,
                                selectedStudent = viewModel.selectedStudent,
                                onRegisterClick = { student ->
                                    if (student.action == "update") {
                                        viewModel.updateStudent(student)
                                    } else {
                                        viewModel.registerStudent(student)
                                    }
                                },
                                onDeleteClick = { name, lastName ->
                                    viewModel.deleteStudent(name, lastName)
                                },
                                onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
