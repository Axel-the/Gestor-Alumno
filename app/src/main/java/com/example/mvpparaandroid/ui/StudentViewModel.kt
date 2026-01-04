package com.example.mvpparaandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvpparaandroid.model.Student
import com.example.mvpparaandroid.model.StudentSubmission
import com.example.mvpparaandroid.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class StudentUiState {
    object Loading : StudentUiState()
    data class Success(val students: List<Student>) : StudentUiState()
    data class Error(val message: String) : StudentUiState()
    object Idle : StudentUiState()
}

sealed class RegistrationUiState {
    object Idle : RegistrationUiState()
    object Loading : RegistrationUiState()
    object Success : RegistrationUiState()
    data class Error(val message: String) : RegistrationUiState()
}

class StudentViewModel : ViewModel() {
    private val repository = StudentRepository()

    private val _studentListState = MutableStateFlow<StudentUiState>(StudentUiState.Idle)
    val studentListState: StateFlow<StudentUiState> = _studentListState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Derived state for filtered list
    val filteredStudents: StateFlow<List<Student>> =
            combine(_studentListState, _searchQuery) { state, query ->
                        if (state is StudentUiState.Success) {
                            if (query.isBlank()) {
                                state.students
                            } else {
                                state.students.filter {
                                    it.nombres.contains(query, ignoreCase = true) ||
                                            it.apellidos.contains(query, ignoreCase = true) ||
                                            it.nivel.contains(query, ignoreCase = true)
                                }
                            }
                        } else {
                            emptyList()
                        }
                    }
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _registrationState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
    val registrationState: StateFlow<RegistrationUiState> = _registrationState.asStateFlow()

    private var _selectedStudent: Student? = null
    val selectedStudent: Student?
        get() = _selectedStudent

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun selectStudent(student: Student) {
        _selectedStudent = student
    }

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                // Fetch silently (don't show full screen loading)
                fetchStudents(
                        showLoading =
                                _studentListState.value is StudentUiState.Idle ||
                                        _studentListState.value is StudentUiState.Error
                )
                kotlinx.coroutines.delay(10000) // Poll every 10 seconds
            }
        }
    }

    fun fetchStudents(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _studentListState.value = StudentUiState.Loading
            }

            val result = repository.getStudents()
            result
                    .onSuccess { students ->
                        _studentListState.value = StudentUiState.Success(students)
                    }
                    .onFailure { e ->
                        // Only show error if we were in a loading state or don't have data
                        if (showLoading) {
                            _studentListState.value =
                                    StudentUiState.Error(e.message ?: "Unknown error")
                        }
                        // If we already have data, we just ignore the background error to keep the
                        // UI stable
                    }
        }
    }

    fun registerStudent(student: StudentSubmission) {
        viewModelScope.launch {
            _registrationState.value = RegistrationUiState.Loading
            val result = repository.registerStudent(student)
            handleResult(result)
        }
    }

    fun updateStudent(student: StudentSubmission) {
        viewModelScope.launch {
            _registrationState.value = RegistrationUiState.Loading
            val result = repository.updateStudent(student)
            handleResult(result)
        }
    }

    fun deleteStudent(nombres: String, apellidos: String) {
        viewModelScope.launch {
            _registrationState.value = RegistrationUiState.Loading
            val result = repository.deleteStudent(nombres, apellidos)
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<*>) {
        result
                .onSuccess {
                    _registrationState.value = RegistrationUiState.Success
                    fetchStudents()
                }
                .onFailure { e ->
                    _registrationState.value =
                            RegistrationUiState.Error(e.message ?: "Operation failed")
                }
    }

    fun resetRegistrationState() {
        _registrationState.value = RegistrationUiState.Idle
    }

    fun clearSelection() {
        _selectedStudent = null
    }
}
