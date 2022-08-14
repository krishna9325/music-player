package krishnaapps.com.musicapp.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.list_item.view.*
import krishnaapps.com.musicapp.R
import kotlinx.android.synthetic.main.swipe_item.view.*
import kotlinx.android.synthetic.main.swipe_item.view.tvPrimary
import javax.inject.Inject

class SwipeSongAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter(R.layout.swipe_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val text = song.title
            val subTitle = song.subTitle
            tvPrimary.text = text
            tvSecondarySwipe.text = subTitle
            glide.load(song.imageUrl).into(ivCurSongImage)

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }

}



















