package com.example.core_data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.model.Actividad
import com.example.core_data.repository.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GestorViewModel (private val repository: Repo): ViewModel() {

    private val _Acts = MutableStateFlow<List<Actividad>>(emptyList())

    val Actividad: StateFlow<List<Actividad>> = _Acts.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllActs().collectLatest { actsFromDb ->
                _Acts.value = actsFromDb
            }
        }
    }


    fun updateAct(Act: Actividad) {
        viewModelScope.launch {
            repository.update(Act)
        }
    }

    fun insertAct(Act: Actividad) {
        viewModelScope.launch {
            repository.insert(Act)
        }
    }

    fun deleteAct(Act: Actividad) {
        viewModelScope.launch {
            repository.delete(Act)
        }
    }

    // TODO --- REMOVE --- Tests
    fun insertFakeData() {
        viewModelScope.launch {
            repository.insertFakeData()
        }

    }
}