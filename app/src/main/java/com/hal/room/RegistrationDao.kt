package com.hal.room
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RegistrationDao {

    @Insert
    suspend fun insertItem(item: RegistrationItem)

    @Insert
    suspend fun insertIssuanceUser(item: IssuanceItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBinData(item: BinItem)

    @Query("SELECT * FROM bin_table WHERE serialNo = :serialNo LIMIT 1")
    suspend fun getBinById(serialNo: String): BinItem?

    @Query("SELECT * FROM registration_table")
    suspend fun getAllItems(): List<RegistrationItem>

    @Query("SELECT * FROM registration_table WHERE serialNo = :serialNo LIMIT 1")
    suspend fun getItemBySerialNo(serialNo: String): RegistrationItem?
}
