package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.db.AsteroidRoomDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepositoryImpl
import com.udacity.asteroidradar.utils.Utils
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application),
    LifecycleEventObserver {
    private val _navigateToDetailAsteroid = MutableLiveData<Asteroid?>()
    val navigateToDetailAsteroid: LiveData<Asteroid?>
        get() = _navigateToDetailAsteroid

    private val _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _showLoadingRecyclerView = MutableLiveData<Boolean>()
    val showLoadingRecyclerView: LiveData<Boolean>
        get() = _showLoadingRecyclerView

    private val _showErrorMessage = MutableLiveData<Boolean>()
    val showErrorMessage: LiveData<Boolean>
        get() = _showErrorMessage

    private val dataBase = AsteroidRoomDatabase.getDatabase(application)
    private val repository = AsteroidRepositoryImpl(dataBase)

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            initAsteroidListData()
            initPictureOfDay()
        }
    }

    private fun initAsteroidListData() {
        _showLoadingRecyclerView.value = true
        onWeekAsteroidsClicked()
        viewModelScope.launch {
            try {
                repository.getAsteroidList()
            } catch (e: Exception) {
                Timber.i("Exception refreshing data initAsteroidListData: $e.message")
                _showErrorMessage.value = true
            }
        }
    }

    private fun initPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = repository.getPictureOfDay()
                _showLoadingRecyclerView.value = false
            } catch (e: Exception) {
                Timber.i("Exception refreshing data initPictureOfDay: $e.message")
            }
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetailAsteroid.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetailAsteroid.value = null
    }

    fun onShowedErrorScreen() {
        _showLoadingRecyclerView.value = false
        _showErrorMessage.value = false
    }

    fun onWeekAsteroidsClicked() {
        viewModelScope.launch {
            dataBase.asteroidDao.getAsteroidsByCloseApproachDate(
                Utils.getTodayFormatted(),
                Utils.getSevenDaysAheadFormatted()
            ).onEmpty {
                _showErrorMessage.value = true
            }.collect { asteroids ->
                    _asteroidList.value = asteroids
                }
        }
    }

    fun onTodayAsteroidsClicked() {
        viewModelScope.launch {
            dataBase.asteroidDao.getAsteroidsByCloseApproachDate(
                Utils.getTodayFormatted(),
                Utils.getTodayFormatted()
            )
                .collect { asteroids ->
                    _asteroidList.value = asteroids
                }
        }
    }

    fun onSavedAsteroidsClicked() {
        viewModelScope.launch {
            dataBase.asteroidDao.getAllAsteroids().collect { asteroids ->
                _asteroidList.value = asteroids
            }
        }
    }
}