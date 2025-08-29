package com.example.resourceflow.ui.classroom.model

import com.example.resourceflow.network.ClassroomDto
import com.example.resourceflow.ui.classroom.model.ClassroomUi
import java.util.Locale

fun ClassroomDto.toUi(): ClassroomUi {
    fun norm(s: String) = s.trim().lowercase(Locale.ROOT)
        .replace(".", "")
        .replace("-", " ")
        .replace("_", " ")
        .replace(Regex("\\s+"), " ")

    val map = resources.associate { norm(it.name) to it.quantity }

    fun get(vararg keys: String): Int {
        for (k in keys) {
            val nk = norm(k)
            if (map.containsKey(nk)) return map[nk] ?: 0
        }
        return 0
    }

    val capacity = get("capacity", "student capacity", "seats", "students")
    val powerOutlets = get("power outlets", "plug points", "plugs", "sockets", "power")
    val tables = get("tables", "desks")
    val chairs = get("chairs", "seats", "stools")

    val hasProjector = get("projector") > 0
    val hasAc = get("ac", "air conditioner", "air conditioning") > 0
    val hasFan = get("fan", "ceiling fan") > 0
    val hasWifi = get("wifi", "wi fi", "wi-fi", "internet") > 0

    return ClassroomUi(
        id = id,
        name = name,
        capacity = capacity,
        powerOutlets = powerOutlets,
        tables = tables,
        chairs = chairs,
        hasProjector = hasProjector,
        hasAc = hasAc,
        hasFan = hasFan,
        hasWifi = hasWifi
    )
}
