package com.fc.gradingmovie.presentation.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fc.gradingmovie.databinding.FragmentMovieReviewsBinding
import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.domain.model.Review
import org.koin.android.scope.ScopeFragment
import org.koin.core.parameter.parametersOf

class MovieReviewsFragment : ScopeFragment(), MovieReviewsContract.View {

    override val presenter: MovieReviewsContract.Presenter by inject { parametersOf(arguments.movie) }

    // navigation args (Reviewed movie객체)
    private val arguments: MovieReviewsFragmentArgs by navArgs()
    private var binding: FragmentMovieReviewsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMovieReviewsBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        presenter.onViewCreated()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showLoadingIndicator() {
        binding?.progressBar?.isVisible = true
    }

    override fun hideLoadingIndicator() {
        binding?.progressBar?.isVisible = false
    }

    override fun showErrorDescription(message: String) {
        binding?.recyclerView?.isVisible = false
        binding?.errorDescriptionTextView?.isVisible = true
        binding?.errorDescriptionTextView?.text = message
    }

    override fun showMovieInformation(movie: Movie) {
        // 어댙터 정의
        binding?.recyclerView?.adapter = MovieReviewsAdapter(movie)
    }

    // presenter에서 호출 됨
    override fun showReviews(reviews: List<Review>) {
        // 리뷰를 보여주기 위해 리사이클러뷰를 보여주고
        binding?.recyclerView?.isVisible = true
        // 에러 메세지 닫음
        binding?.errorDescriptionTextView?.isVisible = false
        // 어댑터에 리뷰를 전달하고 ui 갱신
        (binding?.recyclerView?.adapter as? MovieReviewsAdapter)?.apply {
            this.reviews = reviews
            notifyDataSetChanged()
        }
    }

    private fun initViews() {
        // 리사이클러뷰 레이아웃 매니저 정의
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(
                this.context,
                RecyclerView.VERTICAL,
                false
            )
        }
    }
}