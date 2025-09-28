package com.example.core_data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.model.Actividad
import com.example.core_data.repository.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GestorViewModel(
    private val repository: Repo
) : ViewModel() {

    // Lista reactiva desde Room
    private val _acts = MutableStateFlow<List<Actividad>>(emptyList())
    val acts: StateFlow<List<Actividad>> = _acts.asStateFlow()

    // Conjunto de IDs inscritos (derivado de 'acts')
    val enrolledIds: StateFlow<Set<String>> =
        acts.map { list -> list.map { it.id }.toSet() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    init {
        viewModelScope.launch {
            repository.getAllActs().collectLatest { actsFromDb ->
                _acts.value = actsFromDb
            }
        }
    }

    // -------------------
    // CRUD básico
    // -------------------
    fun insertAct(act: Actividad) {
        viewModelScope.launch {
            repository.insert(act)
        }
    }

    fun updateAct(act: Actividad) {
        viewModelScope.launch {
            repository.update(act)
        }
    }

    fun deleteAct(act: Actividad) {
        viewModelScope.launch {
            repository.delete(act)
        }
    }

    // -------------------
    // Toggle de inscripción
    // -------------------

    fun isEnrolledNow(id: String): Boolean = enrolledIds.value.contains(id)

    /** Alterna la inscripción con el objeto completo de Actividad. */
    fun toggleEnrollment(act: Actividad) = viewModelScope.launch {
        if (isEnrolledNow(act.id)) {
            repository.delete(act)
        } else {
            repository.insert(act)
        }
    }



    // -------------------
    // Sincronizacion remota
    // -------------------
    fun syncFromFirebase() {
        viewModelScope.launch {
            repository.syncFromFirebase()
        }
    }
}
