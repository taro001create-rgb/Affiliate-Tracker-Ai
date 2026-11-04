package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberPending
import com.example.ui.theme.BlueApproved

enum class CommissionStatus(
    val id: String,
    val displayName: String,
    val color: Color
) {
    PENDING("pending", "Pending", AmberPending),
    APPROVED("approved", "Approved", BlueApproved),
    PAID("paid", "Paid", AccentGreen);

    companion object {
        fun fromId(id: String): CommissionStatus {
            return entries.find { it.id == id || it.name.equals(id, ignoreCase = true) } ?: PENDING
        }
    }
}
