package com.example.core_data.viewmodel  // mejor en minúsculas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.model.Actividad
import com.example.core_data.repository.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GestorViewModel(
    private val repository: Repo
) : ViewModel() {

    private val _acts = MutableStateFlow<List<Actividad>>(emptyList())
    val acts: StateFlow<List<Actividad>> = _acts.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllActs().collectLatest { actsFromDb ->
                _acts.value = actsFromDb
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

    fun syncFromFirebase() {
        viewModelScope.launch {
            repository.syncFromFirebase()
        }
    }

    // TODO --- REMOVE --- Tests
    fun insertFakeData() {
        viewModelScope.launch {
            repository.insertFakeData()
        }

    }

    fun deleteAllData() {
        viewModelScope.launch {
            acts.value.forEach {
                repository.deleteAllData(it)
            }
        }
    }
}