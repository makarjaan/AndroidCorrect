package ru.itis.homework5.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.DropdownMenu
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.itis.homework5.R
import ru.itis.homework5.utils.Constance


@Composable
fun MainScreen() {
    val context = LocalContext.current
    var coroutineCount by remember { mutableStateOf("") }
    val radioOptionsType = listOf(Constance.SEQUENTIAL, Constance.PARALLEL)
    val radioOptionsWork = listOf(Constance.CANCEL_WORK, Constance.CONTINUE_WORK)
    val (selectedType, onTypeSelected) = remember { mutableStateOf(radioOptionsType[0]) }
    val (selectedWork, onWorkSelected) = remember { mutableStateOf(radioOptionsWork[0]) }
    var expanded by remember { mutableStateOf(false) }
    val menuOptions = listOf(Constance.DEFAULT, Constance.MAIN, Constance.UNCONFINED, Constance.IO)
    var selectedMenuOption by remember { mutableStateOf(menuOptions[0]) }
    val scope = rememberCoroutineScope()
    var cancelledCount = 0
    val jobs = remember { mutableStateListOf<Job>() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val selectedWorkState = rememberUpdatedState(selectedWork)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = coroutineCount,
            onValueChange = { coroutineCount = it },
            label = { Text(stringResource(R.string.input_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        RadioGroup(
            title = stringResource(R.string.courotine_type),
            options = radioOptionsType,
            selectedOption = selectedType,
            onOptionSelected = onTypeSelected
        )

        RadioGroup(
            title = stringResource(R.string.courotine_work),
            options = radioOptionsWork,
            selectedOption = selectedWork,
            onOptionSelected = onWorkSelected
        )

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_PAUSE && selectedWorkState.value == Constance.CANCEL_WORK) {
                    cancelledCount = jobs.count { it.isActive }
                    jobs.forEach { it.cancel() }
                    jobs.clear()
                    Log.d("TEST-TAG", "Отменено корутин: $cancelledCount")
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        DropdownSelector(
            options = menuOptions,
            selectedOption = selectedMenuOption,
            onOptionSelected = { selectedMenuOption = it }
        )

        FilledTonalButton(
            onClick = {
                val count = coroutineCount.toIntOrNull() ?: 0
                if (count <= 0) {
                    Toast.makeText(context, R.string.not_fine_count, Toast.LENGTH_SHORT).show()
                    return@FilledTonalButton
                }

                val dispatcher = when (selectedMenuOption) {
                    Constance.MAIN -> Dispatchers.Main
                    Constance.IO -> Dispatchers.IO
                    Constance.UNCONFINED -> Dispatchers.Unconfined
                    else -> Dispatchers.Default
                }

                cancelledCount = 0

                Log.d("TEST-TAG", "coroutineCount: $coroutineCount")

                if (selectedType.equals(Constance.PARALLEL)) {
                    runCatching {
                        repeat(count) {
                            val job = scope.launch(dispatcher) {
                                try {
                                    delay(2000)
                                    Log.d("TEST-TAG", "Parallel ${it}")
                                } catch (e: Exception) {
                                    Log.e("TEST-TAG", "Error in parallel task ${it}: ${e.message}")
                                    throw e
                                }
                            }
                            jobs.add(job)
                        }
                    }.onFailure { exception ->
                        Log.e("TEST-TAG", "Error occurred: ${exception.message}")
                        scope.launch(Dispatchers.Main) {
                            Toast.makeText(context, "${R.string.mistake} ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runCatching {
                        val job = scope.launch(dispatcher) {
                            repeat(count) {
                                try {
                                    val inner = launch {
                                        delay(2000)
                                        Log.d("TEST-TAG", "SEQ ${it}")
                                    }
                                    jobs.add(inner)
                                    inner.join()
                                } catch (e: Exception) {
                                    Log.e("TEST-TAG", "Error in sequential task ${it}: ${e.message}")
                                    throw e
                                }
                            }
                        }
                        jobs.add(job)
                    }.onFailure { exception ->
                        Log.e("TEST-TAG", "Error occurred: ${exception.message}")
                        scope.launch(Dispatchers.Main) {
                            Toast.makeText(context, "${R.string.mistake} ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(stringResource(id = R.string.courotine_start))
        }


        FilledTonalButton(
            onClick = {
                if (jobs.isNotEmpty()) {
                    cancelledCount = jobs.filter { it.isActive }.size
                    Log.d("TEST-TAG", "${cancelledCount}")
                    jobs.forEach { it.cancel() }
                    Toast.makeText(
                        context,
                        "Отменено корутин: $cancelledCount",
                        Toast.LENGTH_SHORT
                    ).show()
                    jobs.clear()
                } else {
                    Toast.makeText(context, R.string.hope, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.courotine_stop))
        }
    }
}


@Composable
fun RadioGroup(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
    )

    Column(Modifier.selectableGroup()) {
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = { onOptionSelected(option) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = { onOptionSelected(option) }
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}


@Composable
fun DropdownSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(16.dp)) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = selectedOption)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
