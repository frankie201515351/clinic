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
fun FinanceScreen(repository: ClinicRepository) {
    var currency by remember { mutableStateOf("SYP") }
    val balances by repository.paymentDao.getOutstandingBalances(currency).collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("الحسابات المالية", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row {
            FilterChip(selected = currency == "SYP", onClick = { currency = "SYP" }, label = { Text("ليرة سورية") })
            Spacer(Modifier.width(8.dp))
            FilterChip(selected = currency == "USD", onClick = { currency = "USD" }, label = { Text("دولار") })
        }
        Spacer(Modifier.height(12.dp))

        if (balances.isEmpty()) {
            Text("لا يوجد مرضى عليهم رصيد مستحق حالياً", color = MaterialTheme.colorScheme.secondary)
        }

        LazyColumn {
            items(balances) { b ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(b.patientName, fontWeight = FontWeight.Bold)
                        Text("${b.balance} $currency", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
