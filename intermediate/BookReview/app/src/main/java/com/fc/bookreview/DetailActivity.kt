package com.fc.bookreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.bumptech.glide.Glide
import com.fc.bookreview.databinding.ActivityDetailBinding
import com.fc.bookreview.model.Book
import com.fc.bookreview.model.Review

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "BookSearchDB").build()

        val model = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTv.text = model?.title.orEmpty()
        binding.descriptionTv.text = model?.description
        Glide.with(binding.coverIv.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.coverIv)

        Thread{
            val review = db.reviewDao().getOne(model?.id.orEmpty())
            runOnUiThread {
                binding.reviewEt.setText(review?.review?.orEmpty())
            }
        }.start()

        binding.saveBtn.setOnClickListener {
            Thread{
                db.reviewDao().saveReview(
                    Review(model?.id ?: "0", binding.reviewEt.text.toString())
                )
            }.start()
        }
    }
}