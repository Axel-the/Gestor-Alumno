package com.example.mvpparaandroid.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mvpparaandroid.model.Student
import com.example.mvpparaandroid.model.StudentSubmission
import com.example.mvpparaandroid.ui.RegistrationUiState
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterStudentScreen(
        uiState: RegistrationUiState,
        selectedStudent: Student?,
        onRegisterClick: (StudentSubmission) -> Unit,
        onDeleteClick: (String, String) -> Unit,
        onNavigateBack: () -> Unit
) {
        // Mode Logic
        val isExistingStudent = selectedStudent != null
        // If creating, editable by default. If viewing, locked by default.
        var isFormEditable by remember { mutableStateOf(!isExistingStudent) }

        // Form state
        var nombres by remember { mutableStateOf(selectedStudent?.nombres ?: "") }
        var apellidos by remember { mutableStateOf(selectedStudent?.apellidos ?: "") }
        var apoderado by remember { mutableStateOf(selectedStudent?.apoderado ?: "") }
        var email by remember { mutableStateOf(selectedStudent?.email ?: "") }
        var telefono by remember { mutableStateOf(selectedStudent?.telefono ?: "") }
        var nivel by remember { mutableStateOf(selectedStudent?.nivel ?: "") }
        var grupo by remember { mutableStateOf(selectedStudent?.grupo ?: "") }

        // Day Selection Logic
        val allDays =
                listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
        // Parse existing days string into list
        val initialDays = remember {
                selectedStudent?.dias?.split(", ")?.filter { it.isNotBlank() } ?: emptyList()
        }
        val selectedDays = remember { mutableStateListOf(*initialDays.toTypedArray()) }

        // Time Selection Logic
        val initialHorario = selectedStudent?.horario?.split(" - ")
        var horaInicio by remember { mutableStateOf(initialHorario?.getOrNull(0) ?: "") }
        var horaFin by remember { mutableStateOf(initialHorario?.getOrNull(1) ?: "") }

        var fechaInicio by remember { mutableStateOf(selectedStudent?.fechaInicio ?: "") }
        var fechaFin by remember { mutableStateOf(selectedStudent?.fechaFin ?: "") }
        var mensualidad by remember { mutableStateOf(selectedStudent?.mensualidad ?: "") }
        var estado by remember { mutableStateOf(selectedStudent?.estado ?: "activo") }
        val context = LocalContext.current
        val calendar = Calendar.getInstance()

        var fechaRegistro by remember {
                mutableStateOf(
                        if (selectedStudent != null && selectedStudent.fechaRegistro.isNotBlank()) {
                                selectedStudent.fechaRegistro
                        } else {
                                val d = calendar.get(Calendar.DAY_OF_MONTH)
                                val m = calendar.get(Calendar.MONTH) + 1
                                val y = calendar.get(Calendar.YEAR)
                                String.format("%02d/%02d/%d", d, m, y)
                        }
                )
        }
        val scrollState = rememberScrollState()

        // Helper for TimePicker
        fun showTimePicker(onTimeSelected: (String) -> Unit) {
                val h = calendar.get(Calendar.HOUR_OF_DAY)
                val m = calendar.get(Calendar.MINUTE)
                android.app.TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                        onTimeSelected(
                                                String.format("%02d:%02d", hourOfDay, minute)
                                        )
                                },
                                h,
                                m,
                                true
                        )
                        .show()
        }

        // Helper for DatePicker
        fun showDatePicker(onDateSelected: (String) -> Unit) {
                val y = calendar.get(Calendar.YEAR)
                val m = calendar.get(Calendar.MONTH)
                val d = calendar.get(Calendar.DAY_OF_MONTH)
                android.app.DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                        onDateSelected(
                                                String.format("%02d/%02d/%d", day, month + 1, year)
                                        )
                                },
                                y,
                                m,
                                d
                        )
                        .show()
        }

        Scaffold(
                // ... (TopBar remains same, ignoring for brevity in search replacement if possible,
                // but let's be safe)
                topBar = {
                        CenterAlignedTopAppBar(
                                title = {
                                        Text(
                                                if (isExistingStudent) "Detalle Alumno"
                                                else "Nuevo Alumno",
                                                style =
                                                        MaterialTheme.typography.titleLarge.copy(
                                                                fontWeight = FontWeight.SemiBold
                                                        )
                                        )
                                },
                                navigationIcon = {
                                        IconButton(onClick = onNavigateBack) {
                                                Icon(
                                                        Icons.Filled.Close,
                                                        contentDescription = "Cerrar"
                                                )
                                        }
                                },
                                actions = {
                                        if (isExistingStudent) {
                                                IconButton(
                                                        onClick = {
                                                                isFormEditable = !isFormEditable
                                                        }
                                                ) {
                                                        Icon(
                                                                imageVector =
                                                                        if (isFormEditable)
                                                                                Icons.Default
                                                                                        .LockOpen
                                                                        else Icons.Default.Lock,
                                                                contentDescription =
                                                                        if (isFormEditable)
                                                                                "Bloquear edición"
                                                                        else "Habilitar edición",
                                                                tint =
                                                                        if (isFormEditable)
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary
                                                                        else
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurface
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.6f
                                                                                        )
                                                        )
                                                }
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.centerAlignedTopAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.surface,
                                                titleContentColor =
                                                        MaterialTheme.colorScheme.onSurface
                                        )
                        )
                }
        ) { paddingValues ->
                BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        val isWideScreen = maxWidth > 600.dp

                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .verticalScroll(scrollState)
                                                .padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                                if (isWideScreen) {
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(24.dp)
                                        ) {
                                                // Left Column: Personal
                                                Column(
                                                        modifier = Modifier.weight(1f),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(24.dp)
                                                ) {
                                                        FormSection(title = "Personal") {
                                                                SimpleTextField(
                                                                        value = nombres,
                                                                        onValueChange = {
                                                                                nombres = it
                                                                        },
                                                                        label = "Nombres",
                                                                        enabled = isFormEditable
                                                                )
                                                                SimpleTextField(
                                                                        value = apellidos,
                                                                        onValueChange = {
                                                                                apellidos = it
                                                                        },
                                                                        label = "Apellidos",
                                                                        enabled = isFormEditable
                                                                )
                                                                SimpleTextField(
                                                                        value = apoderado,
                                                                        onValueChange = {
                                                                                apoderado = it
                                                                        },
                                                                        label = "Apoderado",
                                                                        enabled = isFormEditable
                                                                )
                                                                SimpleTextField(
                                                                        value = email,
                                                                        onValueChange = {
                                                                                email = it
                                                                        },
                                                                        label = "Email",
                                                                        keyboardType =
                                                                                KeyboardType.Email,
                                                                        enabled = isFormEditable
                                                                )
                                                                SimpleTextField(
                                                                        value = telefono,
                                                                        onValueChange = {
                                                                                telefono = it
                                                                        },
                                                                        label = "Teléfono",
                                                                        keyboardType =
                                                                                KeyboardType.Phone,
                                                                        enabled = isFormEditable
                                                                )
                                                        }
                                                }

                                                // Right Column: Academic & Admin
                                                Column(
                                                        modifier = Modifier.weight(1f),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(24.dp)
                                                ) {
                                                        // Section: Academic Info
                                                        FormSection(title = "Académico") {
                                                                Row(
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                12.dp
                                                                                        )
                                                                ) {
                                                                        SimpleTextField(
                                                                                value = nivel,
                                                                                onValueChange = {
                                                                                        nivel = it
                                                                                },
                                                                                label = "Nivel",
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        isFormEditable
                                                                        )
                                                                        SimpleTextField(
                                                                                value = grupo,
                                                                                onValueChange = {
                                                                                        grupo = it
                                                                                },
                                                                                label = "Grupo",
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        isFormEditable
                                                                        )
                                                                }

                                                                // Day Selection Chips
                                                                Column(
                                                                        verticalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                8.dp
                                                                                        )
                                                                ) {
                                                                        Text(
                                                                                text =
                                                                                        "Días de Clase",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodyMedium,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant
                                                                        )
                                                                        FlowRow(
                                                                                modifier =
                                                                                        Modifier.fillMaxWidth(),
                                                                                horizontalArrangement =
                                                                                        Arrangement
                                                                                                .spacedBy(
                                                                                                        8.dp
                                                                                                )
                                                                        ) {
                                                                                allDays.forEach {
                                                                                        day ->
                                                                                        val isSelected =
                                                                                                selectedDays
                                                                                                        .contains(
                                                                                                                day
                                                                                                        )
                                                                                        FilterChip(
                                                                                                selected =
                                                                                                        isSelected,
                                                                                                onClick = {
                                                                                                        if (isFormEditable
                                                                                                        ) {
                                                                                                                if (isSelected
                                                                                                                )
                                                                                                                        selectedDays
                                                                                                                                .remove(
                                                                                                                                        day
                                                                                                                                )
                                                                                                                else
                                                                                                                        selectedDays
                                                                                                                                .add(
                                                                                                                                        day
                                                                                                                                )
                                                                                                        }
                                                                                                },
                                                                                                label = {
                                                                                                        Text(
                                                                                                                day.take(
                                                                                                                        3
                                                                                                                )
                                                                                                        )
                                                                                                },
                                                                                                enabled =
                                                                                                        isFormEditable
                                                                                        )
                                                                                }
                                                                        }
                                                                }

                                                                // Time Selection (Split Start/End)
                                                                Row(
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                12.dp
                                                                                        )
                                                                ) {
                                                                        ClickableTextField(
                                                                                value = horaInicio,
                                                                                label =
                                                                                        "Hora Inicio",
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        isFormEditable,
                                                                                trailingIcon = {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .AccessTime,
                                                                                                null
                                                                                        )
                                                                                },
                                                                                onClick = {
                                                                                        showTimePicker {
                                                                                                horaInicio =
                                                                                                        it
                                                                                        }
                                                                                }
                                                                        )
                                                                        ClickableTextField(
                                                                                value = horaFin,
                                                                                label = "Hora Fin",
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        isFormEditable,
                                                                                trailingIcon = {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .AccessTime,
                                                                                                null
                                                                                        )
                                                                                },
                                                                                onClick = {
                                                                                        showTimePicker {
                                                                                                horaFin =
                                                                                                        it
                                                                                        }
                                                                                }
                                                                        )
                                                                }
                                                        }

                                                        HorizontalDivider(
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .outlineVariant
                                                                                .copy(alpha = 0.5f)
                                                        )

                                                        // Section: Administrative Details
                                                        FormSection(title = "Administrativo") {
                                                                Row(
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                12.dp
                                                                                        )
                                                                ) {
                                                                        ClickableTextField(
                                                                                value = fechaInicio,
                                                                                label = "Inicio",
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        isFormEditable,
                                                                                trailingIcon = {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .DateRange,
                                                                                                contentDescription =
                                                                                                        null
                                                                                        )
                                                                                },
                                                                                onClick = {
                                                                                        showDatePicker {
                                                                                                fechaInicio =
                                                                                                        it
                                                                                        }
                                                                                }
                                                                        )
                                                                        ClickableTextField(
                                                                                value = fechaFin,
                                                                                label = "Fin",
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        isFormEditable,
                                                                                trailingIcon = {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .DateRange,
                                                                                                contentDescription =
                                                                                                        null
                                                                                        )
                                                                                },
                                                                                onClick = {
                                                                                        showDatePicker {
                                                                                                fechaFin =
                                                                                                        it
                                                                                        }
                                                                                }
                                                                        )
                                                                }

                                                                Row(
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                12.dp
                                                                                        )
                                                                ) {
                                                                        SimpleTextField(
                                                                                value = mensualidad,
                                                                                onValueChange = {
                                                                                        mensualidad =
                                                                                                it
                                                                                },
                                                                                label =
                                                                                        "Mensualidad",
                                                                                keyboardType =
                                                                                        KeyboardType
                                                                                                .Number,
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        isFormEditable
                                                                        )
                                                                        SimpleTextField(
                                                                                value = estado,
                                                                                onValueChange = {
                                                                                        estado = it
                                                                                },
                                                                                label = "Estado",
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        ),
                                                                                enabled =
                                                                                        isFormEditable
                                                                        )
                                                                }

                                                                // Editable Registration Date
                                                                ClickableTextField(
                                                                        value = fechaRegistro,
                                                                        label = "Fecha de Registro",
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        enabled = isFormEditable,
                                                                        trailingIcon = {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .DateRange,
                                                                                        null
                                                                                )
                                                                        },
                                                                        onClick = {
                                                                                showDatePicker {
                                                                                        fechaRegistro =
                                                                                                it
                                                                                }
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                } else {
                                        // Portrait (Single Column) Logic
                                        // Section: Personal Info
                                        FormSection(title = "Personal") {
                                                SimpleTextField(
                                                        value = nombres,
                                                        onValueChange = { nombres = it },
                                                        label = "Nombres",
                                                        enabled = isFormEditable
                                                )
                                                SimpleTextField(
                                                        value = apellidos,
                                                        onValueChange = { apellidos = it },
                                                        label = "Apellidos",
                                                        enabled = isFormEditable
                                                )
                                                SimpleTextField(
                                                        value = apoderado,
                                                        onValueChange = { apoderado = it },
                                                        label = "Apoderado",
                                                        enabled = isFormEditable
                                                )

                                                Row(
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        SimpleTextField(
                                                                value = email,
                                                                onValueChange = { email = it },
                                                                label = "Email",
                                                                modifier = Modifier.weight(1.5f),
                                                                keyboardType = KeyboardType.Email,
                                                                enabled = isFormEditable
                                                        )
                                                        SimpleTextField(
                                                                value = telefono,
                                                                onValueChange = { telefono = it },
                                                                label = "Teléfono",
                                                                modifier = Modifier.weight(1f),
                                                                keyboardType = KeyboardType.Phone,
                                                                enabled = isFormEditable
                                                        )
                                                }
                                        }

                                        HorizontalDivider(
                                                color =
                                                        MaterialTheme.colorScheme.outlineVariant
                                                                .copy(alpha = 0.5f)
                                        )

                                        // Section: Academic Info
                                        FormSection(title = "Académico") {
                                                Row(
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        SimpleTextField(
                                                                value = nivel,
                                                                onValueChange = { nivel = it },
                                                                label = "Nivel",
                                                                modifier = Modifier.weight(1f),
                                                                enabled = isFormEditable
                                                        )
                                                        SimpleTextField(
                                                                value = grupo,
                                                                onValueChange = { grupo = it },
                                                                label = "Grupo",
                                                                modifier = Modifier.weight(1f),
                                                                enabled = isFormEditable
                                                        )
                                                }

                                                // Day Selection Chips
                                                Column(
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(8.dp)
                                                ) {
                                                        Text(
                                                                text = "Días de Clase",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                        FlowRow(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement =
                                                                        Arrangement.spacedBy(8.dp)
                                                        ) {
                                                                allDays.forEach { day ->
                                                                        val isSelected =
                                                                                selectedDays
                                                                                        .contains(
                                                                                                day
                                                                                        )
                                                                        FilterChip(
                                                                                selected =
                                                                                        isSelected,
                                                                                onClick = {
                                                                                        if (isFormEditable
                                                                                        ) {
                                                                                                if (isSelected
                                                                                                )
                                                                                                        selectedDays
                                                                                                                .remove(
                                                                                                                        day
                                                                                                                )
                                                                                                else
                                                                                                        selectedDays
                                                                                                                .add(
                                                                                                                        day
                                                                                                                )
                                                                                        }
                                                                                },
                                                                                label = {
                                                                                        Text(
                                                                                                day.take(
                                                                                                        3
                                                                                                )
                                                                                        )
                                                                                },
                                                                                enabled =
                                                                                        isFormEditable
                                                                        )
                                                                }
                                                        }
                                                }

                                                // Time Selection (Split Start/End)
                                                Row(
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        ClickableTextField(
                                                                value = horaInicio,
                                                                label = "Hora Inicio",
                                                                modifier = Modifier.weight(1f),
                                                                enabled = isFormEditable,
                                                                trailingIcon = {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .AccessTime,
                                                                                null
                                                                        )
                                                                },
                                                                onClick = {
                                                                        showTimePicker {
                                                                                horaInicio = it
                                                                        }
                                                                }
                                                        )
                                                        ClickableTextField(
                                                                value = horaFin,
                                                                label = "Hora Fin",
                                                                modifier = Modifier.weight(1f),
                                                                enabled = isFormEditable,
                                                                trailingIcon = {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .AccessTime,
                                                                                null
                                                                        )
                                                                },
                                                                onClick = {
                                                                        showTimePicker {
                                                                                horaFin = it
                                                                        }
                                                                }
                                                        )
                                                }
                                        }

                                        HorizontalDivider(
                                                color =
                                                        MaterialTheme.colorScheme.outlineVariant
                                                                .copy(alpha = 0.5f)
                                        )

                                        // Section: Administrative Details
                                        FormSection(title = "Administrativo") {
                                                Row(
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        ClickableTextField(
                                                                value = fechaInicio,
                                                                label = "Inicio",
                                                                modifier = Modifier.weight(1f),
                                                                enabled = isFormEditable,
                                                                trailingIcon = {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .DateRange,
                                                                                contentDescription =
                                                                                        null
                                                                        )
                                                                },
                                                                onClick = {
                                                                        showDatePicker {
                                                                                fechaInicio = it
                                                                        }
                                                                }
                                                        )
                                                        ClickableTextField(
                                                                value = fechaFin,
                                                                label = "Fin",
                                                                modifier = Modifier.weight(1f),
                                                                enabled = isFormEditable,
                                                                trailingIcon = {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .DateRange,
                                                                                contentDescription =
                                                                                        null
                                                                        )
                                                                },
                                                                onClick = {
                                                                        showDatePicker {
                                                                                fechaFin = it
                                                                        }
                                                                }
                                                        )
                                                }

                                                Row(
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        SimpleTextField(
                                                                value = mensualidad,
                                                                onValueChange = {
                                                                        mensualidad = it
                                                                },
                                                                label = "Mensualidad",
                                                                keyboardType = KeyboardType.Number,
                                                                modifier = Modifier.weight(1f),
                                                                enabled = isFormEditable
                                                        )
                                                        SimpleTextField(
                                                                value = estado,
                                                                onValueChange = { estado = it },
                                                                label = "Estado",
                                                                modifier = Modifier.weight(1f),
                                                                enabled = isFormEditable
                                                        )
                                                }

                                                // Editable Registration Date
                                                ClickableTextField(
                                                        value = fechaRegistro,
                                                        label = "Fecha de Registro",
                                                        modifier = Modifier.fillMaxWidth(),
                                                        enabled = isFormEditable,
                                                        trailingIcon = {
                                                                Icon(Icons.Default.DateRange, null)
                                                        },
                                                        onClick = {
                                                                showDatePicker {
                                                                        fechaRegistro = it
                                                                }
                                                        }
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Actions
                                if (isFormEditable) {
                                        fun createSubmission(): StudentSubmission {
                                                // Combine times logic
                                                val finalHorario =
                                                        if (horaInicio.isNotEmpty() &&
                                                                        horaFin.isNotEmpty()
                                                        ) {
                                                                "$horaInicio - $horaFin"
                                                        } else if (horaInicio.isNotEmpty()) {
                                                                horaInicio
                                                        } else {
                                                                ""
                                                        }

                                                return StudentSubmission(
                                                        action =
                                                                if (isExistingStudent) "update"
                                                                else "create",
                                                        original_nombres = selectedStudent?.nombres,
                                                        original_apellidos =
                                                                selectedStudent?.apellidos,
                                                        nombres = nombres,
                                                        apellidos = apellidos,
                                                        apoderado = apoderado,
                                                        email = email,
                                                        telefono = telefono,
                                                        nivel = nivel,
                                                        grupo = grupo,
                                                        dias = selectedDays.joinToString(", "),
                                                        horario = finalHorario,
                                                        fecha_inicio = fechaInicio,
                                                        fecha_fin = fechaFin,
                                                        mensualidad = mensualidad,
                                                        fecha_registro = fechaRegistro,
                                                        estado = estado
                                                )
                                        }

                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                when (uiState) {
                                                        is RegistrationUiState.Loading ->
                                                                Box(
                                                                        Modifier.fillMaxWidth(),
                                                                        contentAlignment =
                                                                                Alignment.Center
                                                                ) { CircularProgressIndicator() }
                                                        is RegistrationUiState.Success -> {
                                                                Text(
                                                                        "¡Guardado!",
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary,
                                                                        textAlign =
                                                                                TextAlign.Center,
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                )
                                                                Button(
                                                                        onClick = onNavigateBack,
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                ) { Text("Volver") }
                                                        }
                                                        is RegistrationUiState.Error -> {
                                                                Text(
                                                                        "Error: ${uiState.message}",
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .error
                                                                )
                                                                Button(
                                                                        onClick = {
                                                                                onRegisterClick(
                                                                                        createSubmission()
                                                                                )
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                ) { Text("Reintentar") }
                                                        }
                                                        else -> {
                                                                Button(
                                                                        onClick = {
                                                                                onRegisterClick(
                                                                                        createSubmission()
                                                                                )
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .height(
                                                                                                50.dp
                                                                                        ),
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                )
                                                                ) {
                                                                        Text(
                                                                                if (isExistingStudent
                                                                                )
                                                                                        "Guardar Cambios"
                                                                                else
                                                                                        "Registrar Alumno",
                                                                                fontSize = 16.sp
                                                                        )
                                                                }
                                                        }
                                                }
                                        }
                                }

                                // Delete Option - Only shown when editable to prevent accidents?
                                // Or always shown if it is an existing student, but protected by
                                // dialog (as
                                // implemented).
                                // Let's keep it consistent: Edit mode unlocks all modifications
                                // including
                                // delete.
                                if (isExistingStudent &&
                                                isFormEditable &&
                                                uiState !is RegistrationUiState.Success
                                ) {
                                        var showDeleteDialog by remember { mutableStateOf(false) }
                                        if (showDeleteDialog) {
                                                AlertDialog(
                                                        onDismissRequest = {
                                                                showDeleteDialog = false
                                                        },
                                                        title = { Text("Eliminar") },
                                                        text = {
                                                                Text(
                                                                        "¿Eliminar a $nombres $apellidos permanentemente?"
                                                                )
                                                        },
                                                        confirmButton = {
                                                                TextButton(
                                                                        onClick = {
                                                                                if (selectedStudent !=
                                                                                                null
                                                                                )
                                                                                        onDeleteClick(
                                                                                                selectedStudent
                                                                                                        .nombres,
                                                                                                selectedStudent
                                                                                                        .apellidos
                                                                                        )
                                                                                showDeleteDialog =
                                                                                        false
                                                                        },
                                                                        colors =
                                                                                ButtonDefaults
                                                                                        .textButtonColors(
                                                                                                contentColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .error
                                                                                        )
                                                                ) { Text("Eliminar") }
                                                        },
                                                        dismissButton = {
                                                                TextButton(
                                                                        onClick = {
                                                                                showDeleteDialog =
                                                                                        false
                                                                        }
                                                                ) { Text("Cancelar") }
                                                        }
                                                )
                                        }

                                        OutlinedButton(
                                                onClick = { showDeleteDialog = true },
                                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        ButtonDefaults.outlinedButtonColors(
                                                                contentColor =
                                                                        MaterialTheme.colorScheme
                                                                                .error
                                                        )
                                        ) { Text("Eliminar Alumno") }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                        }
                }
        }
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                        text = title,
                        style =
                                MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                )
                )
                content()
        }
}

@Composable
fun SimpleTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        keyboardType: KeyboardType = KeyboardType.Text,
        enabled: Boolean = true
) {
        OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = modifier.fillMaxWidth(),
                enabled = enabled,
                shape = RoundedCornerShape(12.dp),
                colors =
                        OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor =
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor =
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                disabledLabelColor =
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true
        )
}

@Composable
fun ClickableTextField(
        value: String,
        label: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        trailingIcon: @Composable (() -> Unit)? = null,
        enabled: Boolean = true
) {
        Box(modifier = modifier) {
                OutlinedTextField(
                        value = value,
                        onValueChange = {},
                        label = { Text(label) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        readOnly = true,
                        trailingIcon = trailingIcon,
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor =
                                                if (enabled)
                                                        MaterialTheme.colorScheme.outline.copy(
                                                                alpha = 0.5f
                                                        )
                                                else
                                                        MaterialTheme.colorScheme.outline.copy(
                                                                alpha = 0.2f
                                                        ),
                                        disabledLabelColor =
                                                MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.6f
                                                ),
                                        disabledTrailingIconColor =
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                        singleLine = true
                )
                if (enabled) {
                        Box(modifier = Modifier.matchParentSize().clickable(onClick = onClick))
                }
        }
}
