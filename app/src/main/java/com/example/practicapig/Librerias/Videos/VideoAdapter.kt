package com.example.practicapig.Librerias.Videos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practicapig.databinding.ItemVideoBinding
import java.io.File

class VideoAdapter(
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private val videos = mutableListOf<File>()

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: File) {
            binding.textVideo.text = file.name

            // preview con glide
            Glide.with(binding.root.context)
                .load(file)
                .frame(1_000_000) // 1 segundo
                .centerCrop()
                .into(binding.imgPreview)

            binding.root.setOnClickListener {
                onClick(file)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    fun setVideos(lista: List<File>) {
        videos.clear()
        videos.addAll(lista)
        notifyDataSetChanged()
    }
}
