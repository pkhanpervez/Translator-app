package com.englishto.urdu.translator

import java.io.Serializable


data class Data(
    val id: Int,
    val english: String,
    val urdu: String
) : Serializable