package com.hal.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "registration_table")
data class RegistrationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serialNo: String,
    val binNo: String
)


@Entity(tableName = "issuance_table")
data class IssuanceItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serialNo: String,
    val userName: String
)

@Entity(tableName = "bin_table")
data class BinItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serialNo: String,
    val binItem: String
)



