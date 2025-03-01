package com.example.led_strip_control.home.view_model

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.led_strip_control.database.ColorUiState
import com.example.led_strip_control.pojo.ColorEntity
import com.example.led_strip_control.repository.ColorRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ColorViewModel(private val repository: ColorRepositoryInterface) : ViewModel() {

    private val _uiState = MutableStateFlow<ColorUiState>(ColorUiState.Idle)
    val uiState: StateFlow<ColorUiState> = _uiState

    private val _colors = MutableStateFlow<List<ColorEntity>>(emptyList())
    val colors: StateFlow<List<ColorEntity>> = _colors

    private val _selectedColor = MutableStateFlow<ColorEntity?>(null)
    val selectedColor: StateFlow<ColorEntity?> = _selectedColor

    init {
        viewModelScope.launch {
            repository.getAllColors().collectLatest { colorList ->
                _colors.value = colorList
            }
        }
    }

    fun setSelectedColor(color: ColorEntity) {
        _selectedColor.value = color
    }


    fun addColor(color: ColorEntity) {
        viewModelScope.launch {
            val added = repository.addColor(color)
            _uiState.value =
                if (added) ColorUiState.ColorAdded else ColorUiState.ColorAlreadyExists
        }
    }

    fun deleteColor(id: Int) {
        viewModelScope.launch {
            repository.deleteColor(id)
            _uiState.value = ColorUiState.ColorRemoved(id)
        }
    }

    fun resetUiState() {
        _uiState.value = ColorUiState.Idle
    }
}
