package hr.algebra.footballermanager.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
@Entity(tableName="footballers")
data class Footballer(
    @PrimaryKey(autoGenerate = true)
    var _id : Long? = null,
    var fullName : String? = null,
    var overall : String? = null,
    var position  : String? = null,
    var picturePath: String? = null,
    var birthDate: LocalDate = LocalDate.now()

){
    override fun toString() = "$fullName";
    }

