package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.bitat.state.ImagePreviewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ImagePreviewViewModel : ViewModel() {
    private val _imagePreviewState = MutableStateFlow(ImagePreviewState())
    val imagePreviewState: StateFlow<ImagePreviewState> get() = _imagePreviewState.asStateFlow()


    fun setImagePreView(imgAar: Array<String>) {
        _imagePreviewState.update {
            it.imgList.clear()
            it.imgList.addAll(imgAar)
            it
        }
    }


}