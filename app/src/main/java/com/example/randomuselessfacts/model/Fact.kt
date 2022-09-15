package com.example.randomuselessfacts.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "Facts"
)
data class Fact(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("permalink")
    val permalink: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("source_url")
    val sourceUrl: String,
    @SerializedName("text")
    val text: String
)