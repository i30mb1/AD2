package n7.ad2.hero.page.internal.responses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.feature.hero.page.ui.databinding.ItemResponseBodyBinding
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse


class ResponseBodyViewHolder private constructor(
    private val binding: ItemResponseBodyBinding,
    private val showDialogResponse: (VOResponse.Body) -> Unit,
    private val playSound: (VOResponse.Body) -> Unit,
    private val imagesAdapter: ResponsesImagesAdapter,
) : RecyclerView.ViewHolder(binding.root) {

    private var lastItem: VOResponse.Body? = null

    fun bind(isSaved: Boolean) {
        binding.ivIsSaved.isVisible = isSaved
        binding.pbProgress.progress = 0
    }

    fun bind(item: VOResponse.Body) {
        lastItem = item
        binding.tvText.text = item.title
        binding.ivIsSaved.isVisible = item.isSavedInMemory
        (binding.rv.layoutManager as GridLayoutManager).spanCount = clamp(item.icons.size, MIN_ICONS_IN_ROW, MAX_ICONS_IN_ROW)
        imagesAdapter.list = item.icons
        binding.root.setOnClickListener { playSound(item) }
        binding.root.setOnLongClickListener {
            showDialogResponse(item)
            true
        }
    }

    fun updateUploadProgress(downloadedBytes: Int, totalBytes: Int, downloadID: Long) {
        if (lastItem?.downloadID == downloadID) {
            binding.pbProgress.progress = downloadedBytes
            binding.pbProgress.max = totalBytes
        }
    }

    fun clear() = Unit

    companion object {
        private const val MAX_ICONS_IN_ROW = 3
        private const val MIN_ICONS_IN_ROW = 1
        private const val MAX_VIEWS_RESPONSE_IMAGE = 60
        private val viewPool = RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(R.layout.item_response_image, MAX_VIEWS_RESPONSE_IMAGE)
        }

        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            showDialogResponse: (VOResponse.Body) -> Unit,
            playSound: (VOResponse.Body) -> Unit,
            showPopup: () -> Unit,
        ): ResponseBodyViewHolder {
            val binding = ItemResponseBodyBinding.inflate(layoutInflater, parent, false)
            val imagesAdapter = ResponsesImagesAdapter(layoutInflater, showPopup)
            val gridLayoutManager = GridLayoutManager(parent.context, MAX_ICONS_IN_ROW).apply {
                recycleChildrenOnDetach = true
                initialPrefetchItemCount = 12
            }
            binding.rv.apply {
                setRecycledViewPool(viewPool)
                setHasFixedSize(true)
                layoutManager = gridLayoutManager
                adapter = imagesAdapter
                setItemViewCacheSize(MAX_VIEWS_RESPONSE_IMAGE)
            }
            return ResponseBodyViewHolder(binding, showDialogResponse, playSound, imagesAdapter)
        }
    }

}
