package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AffiliatePlatform
import com.example.data.model.CommissionStatus

class Converters {
    @TypeConverter
    fun fromPlatform(platform: AffiliatePlatform): String {
        return platform.name
    }

    @TypeConverter
    fun toPlatform(value: String): AffiliatePlatform {
        return try {
            AffiliatePlatform.valueOf(value)
        } catch (_: Exception) {
            AffiliatePlatform.OTHER
        }
    }

    @TypeConverter
    fun fromStatus(status: CommissionStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): CommissionStatus {
        return try {
            CommissionStatus.valueOf(value)
        } catch (_: Exception) {
            CommissionStatus.PENDING
        }
    }
}
