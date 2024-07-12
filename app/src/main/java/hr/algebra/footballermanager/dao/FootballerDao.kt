package hr.algebra.footballermanager.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface FootballerDao {

    @Query("select * from footballers")
    fun getFootballers(): MutableList<Footballer>

    @Query("select * from footballers where _id=:id")
    fun getFootballer(id: Long): Footballer?

    @Insert
    fun insert(footballer: Footballer)
    @Update
    fun update(footballer: Footballer)
    @Delete
    fun delete(footballer: Footballer)

}