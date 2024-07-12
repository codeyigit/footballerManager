package hr.algebra.footballermanager

import android.app.Application
import hr.algebra.footballermanager.dao.FootballerDao
import hr.algebra.footballermanager.dao.FootballersDatabase

class App: Application() {
    private lateinit var footballerDao: FootballerDao

    override fun onCreate() {
        super.onCreate()
        footballerDao = FootballersDatabase.getInstance(this)
            .footballerDao()
    }
    fun getFootballerDao()= footballerDao
}