package com.study.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.study.app.domain.model.Grade

@Entity(
    tableName = "grades",
    indices = [Index("name")]
)
data class GradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = Grade(id, name, order, createdAt)

    companion object {
        fun fromDomain(grade: Grade) = GradeEntity(
            id = grade.id,
            name = grade.name,
            order = grade.order,
            createdAt = grade.createdAt
        )
    }
}
