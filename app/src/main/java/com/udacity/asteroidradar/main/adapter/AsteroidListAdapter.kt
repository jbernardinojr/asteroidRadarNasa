package com.udacity.asteroidradar.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding
import com.udacity.asteroidradar.domain.Asteroid

class AsteroidListAdapter(
    private val clickListener: AsteroidListener
) : ListAdapter<Asteroid, AsteroidListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AsteroidListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.also {
            it.itemView.setOnClickListener {
                clickListener.onClick(item)
            }
            it.bind(item)
        }
        holder.tvAsteroidName.text = item.codename
        holder.tvApproachDate.text = item.closeApproachDate
        holder.ivStatus.setImageResource(setStatus(item.isPotentiallyHazardous))
    }

    class AsteroidListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }

    private fun setStatus(isPotentiallyHazardous: Boolean) = if (isPotentiallyHazardous) {
        R.drawable.ic_status_potentially_hazardous
    } else {
        R.drawable.ic_status_normal
    }

    inner class ViewHolder(var binding: AsteroidListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvAsteroidName: TextView = binding.tvAsteroidName
        val tvApproachDate: TextView = binding.tvCloseApproachDate
        val ivStatus: ImageView = binding.ivStatus

        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
            binding.executePendingBindings()
        }

        override fun toString(): String {
            return super.toString() + " '" + tvAsteroidName.text + "'"
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }
}