package com.study.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.study.app.domain.model.Subject

@Entity(
    tableName = "subjects",
    indices = [Index("name")]
)
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = Subject(id, name, isDefault, createdAt)

    companion object {
        fun fromDomain(subject: Subject) = SubjectEntity(
            id = subject.id,
            name = subject.name,
            isDefault = subject.isDefault,
            createdAt = subject.createdAt
        )
    }
}
