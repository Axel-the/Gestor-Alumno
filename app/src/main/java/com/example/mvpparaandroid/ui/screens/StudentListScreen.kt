package com.example.mvpparaandroid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mvpparaandroid.model.Student
import com.example.mvpparaandroid.ui.StudentUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
        uiState: StudentUiState,
        filteredStudents: List<Student>,
        searchQuery: String,
        onSearchQueryChange: (String) -> Unit,
        onFabClick: () -> Unit,
        onStudentClick: (Student) -> Unit,
        onRefresh: () -> Unit
) {
    val listState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { onRefresh() }

    Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    androidx.compose.foundation.Image(
                                            painter =
                                                    androidx.compose.ui.res.painterResource(
                                                            id =
                                                                    com.example
                                                                            .mvpparaandroid
                                                                            .R
                                                                            .drawable
                                                                            .logo
                                                    ),
                                            contentDescription = "Logo",
                                            modifier =
                                                    Modifier.size(32.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Gestor Alumno", fontWeight = FontWeight.Bold)
                                }
                            },
                            actions = {
                                IconButton(onClick = onRefresh) {
                                    Icon(
                                            Icons.Default.Refresh,
                                            contentDescription = "Actualizar lista"
                                    )
                                }
                            },
                            colors =
                                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.primaryContainer,
                                            titleContentColor =
                                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                            actionIconContentColor =
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                    )
                    // Search Bar Area
                    TextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .padding(16.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                            placeholder = { Text("Buscar por nombre...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            },
                            colors =
                                    TextFieldDefaults.colors(
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent
                                    ),
                            singleLine = true
                    )
                }
            },
            floatingActionButton = {
                Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Arrow Up
                    SmallFloatingActionButton(
                            onClick = { scope.launch { listState.animateScrollToItem(0) } },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) { Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll Up") }

                    // Arrow Down
                    SmallFloatingActionButton(
                            onClick = {
                                scope.launch {
                                    if (filteredStudents.isNotEmpty()) {
                                        listState.animateScrollToItem(filteredStudents.size - 1)
                                    }
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Scroll Down") }

                    // Add Button
                    FloatingActionButton(
                            onClick = onFabClick,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                    ) { Icon(Icons.Default.Add, contentDescription = "Add Student") }
                }
            }
    ) { paddingValues ->
        BoxWithConstraints(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
        ) {
            val isWideScreen = maxWidth > 600.dp
            val gridCells = if (isWideScreen) GridCells.Adaptive(300.dp) else GridCells.Fixed(1)

            when (uiState) {
                is StudentUiState.Loading -> CircularProgressIndicator()
                is StudentUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRefresh, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Reintentar")
                        }
                    }
                }
                is StudentUiState.Success -> {
                    if (filteredStudents.isEmpty()) {
                        if (searchQuery.isNotEmpty()) {
                            Text("No se encontraron resultados para '$searchQuery'.")
                        } else {
                            Text("No hay alumnos registrados.", color = Color.Gray)
                        }
                    } else {
                        LazyVerticalGrid(
                                columns = gridCells,
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                    items = filteredStudents,
                                    key = { student ->
                                        "${student.nombres}_${student.apellidos}_${student.nivel}"
                                    }
                            ) { student ->
                                StudentItem(student, onClick = { onStudentClick(student) })
                            }
                        }
                    }
                }
                is StudentUiState.Idle -> {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun StudentItem(student: Student, onClick: () -> Unit) {
    Card(
            modifier =
                    Modifier.fillMaxWidth()
                            .clickable(onClick = onClick)
                            .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    clip = false
                            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border =
                    BorderStroke(
                            0.5.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = "${student.nombres} ${student.apellidos}",
                            style =
                                    MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                    ),
                            color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                            text =
                                    student.nivel.ifEmpty { "N/A" } +
                                            " • Grupo " +
                                            student.grupo.ifEmpty { "-" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(student.estado)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    LabelValueItem("Horario", "${student.dias} • ${student.horario}")
                }
                Column(modifier = Modifier.weight(1f)) {
                    LabelValueItem("Mensualidad", "S/. ${student.mensualidad}")
                }
            }
        }
    }
}

@Composable
fun LabelValueItem(label: String, value: String) {
    Column {
        Text(
                text = label.uppercase(),
                style =
                        MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                        ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    // Deprecated in new design but kept if referenced elsewhere or legacy
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) =
            when (status.lowercase()) {
                "activo" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32) // Green
                "suspendido" -> Color(0xFFFFEBEE) to Color(0xFFC62828) // Red
                "renovado" -> Color(0xFFE3F2FD) to Color(0xFF1565C0) // Blue
                else -> Color.LightGray.copy(alpha = 0.3f) to Color.Black
            }

    Surface(
            color = bgColor,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
                text = status.capitalize(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
        )
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
