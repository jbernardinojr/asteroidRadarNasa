package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.main.adapter.AsteroidListAdapter

class MainFragment : Fragment(), LifecycleOwner {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private val asteroidAdapter =
        AsteroidListAdapter(AsteroidListAdapter.AsteroidListener { asteroid ->
            viewModel.onAsteroidClicked(asteroid)
        })

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        lifecycle.addObserver(viewModel)

        setupAdapter()
        setupObservers()

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setupObservers() {
        viewModel.navigateToDetailAsteroid.observe(viewLifecycleOwner) { asteroid ->
            if (asteroid != null) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                viewModel.onAsteroidNavigated()
            }
        }

        viewModel.asteroidList.observe(viewLifecycleOwner) { asteroidList ->
            asteroidAdapter.submitList(asteroidList)
        }

        viewModel.pictureOfDay.observe(viewLifecycleOwner) { image ->
            if (image != null) {
                Picasso.get().load(image.url).into(binding.activityMainImageOfTheDay)
            } else {
                binding.activityMainImageOfTheDay.setImageResource(R.drawable.placeholder_picture_of_day)
            }
        }

        viewModel.showLoadingRecyclerView.observe(viewLifecycleOwner) { showProgressBar ->
            if (showProgressBar) {
                binding.statusLoadingWheel.visibility = View.VISIBLE
            } else {
                binding.statusLoadingWheel.visibility = View.GONE
            }
        }

        viewModel.showErrorMessage.observe(viewLifecycleOwner) { isErrorScreen ->
            if (isErrorScreen) {
                Toast.makeText(
                    context,
                    getString(R.string.error_retrieving_asteroid_data),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.onShowedErrorScreen()
            }
        }
    }

    private fun setupAdapter() {
        val linearLayoutManager = LinearLayoutManager(
            activity?.applicationContext,
            RecyclerView.VERTICAL,
            false
        )

        binding.asteroidRecycler.layoutManager = linearLayoutManager
        binding.asteroidRecycler.adapter = asteroidAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_asteroids -> viewModel.onWeekAsteroidsClicked()
            R.id.show_today_asteroids -> viewModel.onTodayAsteroidsClicked()
            R.id.show_saved_asteroids -> viewModel.onSavedAsteroidsClicked()
        }
        return true
    }
}
