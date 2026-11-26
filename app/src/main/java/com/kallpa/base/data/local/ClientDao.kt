package com.kallpa.base.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY nombre ASC")
    suspend fun list(): List<ClientEntity>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun get(id: String): ClientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(client: ClientEntity)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun delete(id: String)
}
