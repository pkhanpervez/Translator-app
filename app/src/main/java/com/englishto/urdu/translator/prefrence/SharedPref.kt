package com.englishto.urdu.translator.prefrence

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.englishto.urdu.translator.Data
import com.englishto.urdu.translator.prefrence.Constants.FAVORITE
import com.englishto.urdu.translator.prefrence.Constants.HISTORY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPref {

    companion object {
        private val sharePref = SharedPref()
        private lateinit var sharedPreferences: SharedPreferences
        private val DATA = "DATA"
        private val gson = Gson()

        fun getInstance(context: Context): SharedPref {
            if (!Companion::sharedPreferences.isInitialized) {
                synchronized(SharedPref::class.java) {
                    if (!Companion::sharedPreferences.isInitialized) {
                        sharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
                    }
                }
            }
            return sharePref
        }
    }


    fun saveHistoryList(dataList: List<Data>) {
        val jsonString = gson.toJson(dataList)
        sharedPreferences.edit().putString(HISTORY, jsonString).apply()
    }

    fun getHistoryList(): List<Data> {
        val jsonString = sharedPreferences.getString(HISTORY, null)
        return try {
            if (jsonString != null) {
                val type = object : TypeToken<List<Data>>() {}.type
                gson.fromJson(jsonString, type)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
//            Log.e("HistoryList", "Failed to parse history list", e)
            emptyList() // Return an empty list in case of error
        }
    }

    fun deleteHistory(item: Data) {
        val dataList = getHistoryList().toMutableList()
        val updatedList = dataList.filter { it != item }
        saveHistoryList(updatedList)
    }

    fun deleteAllHistory() {
        // Remove the entire list from SharedPreferences
        sharedPreferences.edit().remove(HISTORY).apply()
    }


    //Favorite
    fun saveFavoriteList(dataList: List<Data>) {
        val jsonString = gson.toJson(dataList)

        sharedPreferences.edit().putString(FAVORITE, jsonString).apply()
    }

    fun getFavoriteList(): List<Data> {
        val jsonString = sharedPreferences.getString(FAVORITE, null)
        return try {
            if (jsonString != null) {
                val type = object : TypeToken<List<Data>>() {}.type
                gson.fromJson(jsonString, type)
            } else {
                emptyList() // If no data exists, return empty list
            }
        }catch (ex: Exception) {
            emptyList()
        }
    }

    fun deleteFavorite(item: Data) {
        val dataList = getFavoriteList().toMutableList()
        val updatedList = dataList.filter { it != item }
        saveFavoriteList(updatedList)
    }

    fun deleteAllFavorite() {
        // Remove the entire list from SharedPreferences
        sharedPreferences.edit().remove(FAVORITE).apply()
    }


    fun setInSession(name: String, value: String) {
        sharedPreferences.edit()
            .putString(name, value)
            .apply()
    }

    fun getFromSession(name: String, defValue: String): String {
        return ""+ sharedPreferences.getString(name, defValue)
    }

    fun removeData(name: String) {
        sharedPreferences.edit().remove(name).apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }



}