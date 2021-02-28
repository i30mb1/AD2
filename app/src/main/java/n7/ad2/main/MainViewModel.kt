package n7.ad2.main

import android.app.Application
import androidx.lifecycle.ViewModel
import n7.ad2.AD2Logger
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val application: Application,
    private val logger: AD2Logger,
) : ViewModel() {


}
