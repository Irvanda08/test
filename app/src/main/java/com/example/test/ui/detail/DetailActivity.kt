package com.example.test.ui.detail

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.test.databinding.ActivityDetailBinding
import com.example.test.ui.ViewModelFactory
import com.example.test.ui.welcome.WelcomeActivity

class DetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailBinding
    private var token = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story"

        val id = intent.getStringExtra(ID).toString()
        val detailViewModel = obtainViewModel(this@DetailActivity)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token
                Log.d(ContentValues.TAG, "token detail: $token")
                Log.d(ContentValues.TAG, "id: $id")
                detailViewModel.getDetailStory(token, id)
                detailViewModel.story.observe(this) { storyList ->
                    Log.d(ContentValues.TAG, "Story: $storyList")
                    binding.apply {
                        tvUsername.text = storyList.name
                        tvDesc.text = storyList.description
                        Glide.with(binding.root.context)
                            .load(storyList.photoUrl)
                            .into(binding.ivPhoto)

                    }
                }

                detailViewModel.isLoading.observe(this) {
                    showLoading(it)
                }
            }
        }

    }

    private fun showLoading(state: Boolean) {
        binding.ProgressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): DetailViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[DetailViewModel::class.java]
    }


    companion object {
        const val ID = "id"
    }

}