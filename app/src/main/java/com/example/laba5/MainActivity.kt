package com.example.laba5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReliabilityCalculatorApp()
        }
    }
}

@Composable
fun ReliabilityCalculatorApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                var singleCircuitReliability by remember { mutableStateOf(0.0) }
                var doubleCircuitReliability by remember { mutableStateOf(0.0) }
                var singleTransformerLoss by remember { mutableStateOf(0.0) }

                Text("Калькулятор для систем електропередачі", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))

                ReliabilitySection(
                    onCalculate = { p0, p1 ->
                        singleCircuitReliability = calculateSingleCircuitReliability(p0)
                        doubleCircuitReliability = calculateDoubleCircuitReliability(p0, p1)
                    },
                    singleCircuitReliability = singleCircuitReliability,
                    doubleCircuitReliability = doubleCircuitReliability
                )

                Spacer(modifier = Modifier.height(32.dp))

                LossCalculationSection(
                    onCalculate = { failureRate, downtime, costPerDowntime ->
                        singleTransformerLoss = calculateSingleTransformerLoss(failureRate, downtime, costPerDowntime)
                    },
                    singleTransformerLoss = singleTransformerLoss
                )
            }
        }
    }
}

@Composable
fun ReliabilitySection(
    onCalculate: (Double, Double) -> Unit,
    singleCircuitReliability: Double,
    doubleCircuitReliability: Double
) {
    var p0 by remember { mutableStateOf("") }
    var p1 by remember { mutableStateOf("") }

    Column {
        Text("Надійність систем електропередачі", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = p0,
            onValueChange = { p0 = it },
            label = { Text("Ймовірність відмови одного кола (P0)") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = p1,
            onValueChange = { p1 = it },
            label = { Text("Ймовірність відмови другого кола (P1)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val p0Value = p0.toDoubleOrNull() ?: 0.0
            val p1Value = p1.toDoubleOrNull() ?: 0.0
            onCalculate(p0Value, p1Value)
        }) {
            Text("Розрахувати")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Надійність одноколової системи: ${"%.1f".format(singleCircuitReliability * 100)}%")
        Text("Надійність двоколової системи: ${"%.1f".format(doubleCircuitReliability * 100)}%")
    }
}

@Composable
fun LossCalculationSection(
    onCalculate: (Double, Double, Double) -> Unit,
    singleTransformerLoss: Double
) {
    var failureRate by remember { mutableStateOf("") }
    var downtime by remember { mutableStateOf("") }
    var costPerDowntime by remember { mutableStateOf("") }

    Column {
        Text("Розрахунок збитків від перерв електропостачання", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = failureRate,
            onValueChange = { failureRate = it },
            label = { Text("Інтенсивність відмов (λ), раз/рік") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = downtime,
            onValueChange = { downtime = it },
            label = { Text("Час відновлення (T), годин") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = costPerDowntime,
            onValueChange = { costPerDowntime = it },
            label = { Text("Втрати за 1 годину перерви (C), грн") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val lambda = failureRate.toDoubleOrNull() ?: 0.0
            val T = downtime.toDoubleOrNull() ?: 0.0
            val C = costPerDowntime.toDoubleOrNull() ?: 0.0
            onCalculate(lambda, T, C)
        }) {
            Text("Розрахувати")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Збитки: ${"%.2f".format(singleTransformerLoss)} грн")
    }
}

// Формули для розрахунків
fun calculateSingleCircuitReliability(p0: Double): Double {
    return 1 - p0
}

fun calculateDoubleCircuitReliability(p0: Double, p1: Double): Double {
    return 1 - (p0 * p1)
}

fun calculateSingleTransformerLoss(failureRate: Double, downtime: Double, costPerDowntime: Double): Double {
    return failureRate * downtime * costPerDowntime
}
