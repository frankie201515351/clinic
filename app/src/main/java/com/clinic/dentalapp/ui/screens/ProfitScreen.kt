package com.clinic.dentalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clinic.dentalapp.data.ClinicRepository

@Composable
fun ProfitScreen(repository: ClinicRepository) {
    val payments by repository.paymentDao.getAll().collectAsState(initial = emptyList())

    // تجميع الدفعات (وليس المستحقات) حسب الشهر
    val monthly = remember(payments) {
        payments.filter { it.type == "دفعة" }
            .groupBy { it.date.take(7) } // yyyy-MM
            .mapValues { entry -> entry.value.groupBy { it.currency }.mapValues { g -> g.value.sumOf { it.amount } } }
            .toSortedMap(compareByDescending { it })
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("الأرباح الشهرية", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        if (monthly.isEmpty()) {
            Text("لا يوجد بيانات دفعات مسجّلة بعد", color = MaterialTheme.colorScheme.secondary)
        }

        LazyColumn {
            items(monthly.entries.toList()) { (month, byCurrency) ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(month, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        byCurrency.forEach { (currency, total) ->
                            Text("$total $currency", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
