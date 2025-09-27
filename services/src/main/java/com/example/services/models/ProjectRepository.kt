package com.example.services.data

import com.example.services.models.VolunteerProject

object ProjectRepository {
    private val projects = mutableListOf(
        VolunteerProject(1, "Reforestación Urbana", "Plantación de árboles en parques locales."),
        VolunteerProject(2, "Banco de Alimentos", "Clasificación y entrega de alimentos a familias."),
        VolunteerProject(3, "Apoyo Escolar", "Tutorías comunitarias para niños en situación vulnerable.")
    )

    private var nextId = projects.size + 1

    fun getProjects(): List<VolunteerProject> = projects.toList()

    fun getProjectById(id: Int): VolunteerProject? = projects.find { it.id == id }

    fun addProject(title: String, description: String) {
        projects.add(VolunteerProject(nextId++, title, description))
    }

    fun updateProject(id: Int, title: String, description: String) {
        getProjectById(id)?.apply {
            this.title = title
            this.description = description
        }
    }

    fun deleteProject(id: Int) {
        projects.removeAll { it.id == id }
    }
}