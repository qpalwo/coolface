package com.hustunique.coolface.fusion

import androidx.lifecycle.ViewModel
import com.hustunique.coolface.model.repo.PictureRepo

class FusionViewModel: ViewModel() {
    fun getNewPictureFile() = PictureRepo.getInstance().getNewFile()
}