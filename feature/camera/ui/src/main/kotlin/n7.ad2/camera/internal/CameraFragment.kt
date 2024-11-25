package n7.ad2.camera.internal

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.app.logger.Logger
import n7.ad2.camera.internal.compose.Camera
import n7.ad2.camera.internal.compose.CameraEvent
import n7.ad2.camera.internal.di.DaggerCameraComponent
import n7.ad2.ui.content

internal class CameraFragment(
    override var dependenciesMap: DependenciesMap,
) : Fragment(), HasDependencies {

    @Inject lateinit var cameraViewModelFactory: CameraViewModel.Factory
    @Inject lateinit var logger: Logger

    private val viewModel: CameraViewModel by viewModels {
        viewModelFactory {
            initializer { cameraViewModelFactory.create() }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerCameraComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return content {
            val state by viewModel.state.collectAsState()
            val file = state.recordedFile?.get()
            if (file != null) openVideo(file)
            Camera(state, ::handleEvent)
        }
    }

    private fun openVideo(file: File) {
        val newInstance = VideoFragment.newInstance(file.toUri())
        parentFragmentManager.commit {
            add(android.R.id.content, newInstance, null)
            addToBackStack(null)
        }
    }

    private fun handleEvent(cameraEvent: CameraEvent): Unit = when (cameraEvent) {
        is CameraEvent.PreviewReady -> {
            viewModel.onUIBind(cameraEvent.surfaceProvider, cameraEvent.scaleType)
        }

        CameraEvent.Click -> {
            viewModel.startRecording()
        }

        is CameraEvent.GloballyPosition -> {
            viewModel.onGlobalPosition(cameraEvent.viewHeight, cameraEvent.viewWidth)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.getLogFlow()
            .onEach { Log.d("N7", it.toString()) }
            .launchIn(lifecycleScope)
        CameraPermission(requireActivity()) {

        }.run()
    }

    override fun onPause() {
//        viewModel.onUiUnBind()
        super.onPause()
    }

    override fun onDestroyView() {
        viewModel.onDestroyView()
        super.onDestroyView()
    }
}

class CameraX(
    private var context: Context,
    private var owner: LifecycleOwner,
) {
    private var imageCapture: ImageCapture? = null


    fun capturePhoto() = owner.lifecycleScope.launch {
        val imageCapture = imageCapture ?: return@launch

        imageCapture.takePicture(ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                owner.lifecycleScope.launch {
                    saveMediaToStorage(
                        image.toBitmap(),
                    )
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }
        })
    }

    private suspend fun saveMediaToStorage(bitmap: Bitmap) {
        val option = 0
        val photoName = "photo.jpg"
        val resolver = context.contentResolver
        when (option) {
            0 -> {
                // Saving to a File
                val file = File(context.filesDir, photoName)
                ImageCapture.OutputFileOptions.Builder(file).build()
            }

            1 -> {
                // Saving to MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, photoName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                ImageCapture.OutputFileOptions.Builder(
                    context.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ).build()
            }

            2 -> {
                // Saving to a File exposed via FileProvider URI
                val file = File(context.filesDir, photoName)
                val uri = FileProvider.getUriForFile(context, "com.example.fileprovider", file)
                ImageCapture.OutputFileOptions.Builder(file).build()
            }

            3 -> {
                // Saving to a Temporary File
                val file = File.createTempFile("captured_image", ".jpg", context.cacheDir)
                ImageCapture.OutputFileOptions.Builder(file).build()
            }
        }
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, photoName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val openOutputStream = resolver.openOutputStream(imageUri!!)!!

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, openOutputStream)

        Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show()
    }
}
