package com.fc.gradingmovie.presentation.reviews

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fc.gradingmovie.databinding.FragmentMovieReviewsBinding
import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.domain.model.MovieReviews
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
        binding?.recyclerView?.adapter = MovieReviewsAdapter(movie).apply {
            // submit 버튼시 리뷰 추가 리스너 & 키보드 숨기기
            onReviewSubmitButtonClickListener = { content, score ->
                presenter.requestAddReview(content, score)
                hideKeyboard()
            }
            // delete 버튼 클릭시 리뷰 삭제 리스너
            onReviewDeleteButtonClickListener = { review ->
                // 한번더 추가 확인 alertdialog
                showDeleteConfirmDialog(review)
            }
        }
    }

    private fun showDeleteConfirmDialog(review: Review) {
        AlertDialog.Builder(requireContext())
            .setMessage("정말로 리뷰를 삭제하시겠어요?")
            .setPositiveButton("삭제할래요") { _, _ ->
                // 삭제 요청
                presenter.requestRemoveReview(review)
            }
            .setNegativeButton("안할래요") { _, _ -> }
            .show()
    }

    // presenter에서 호출 됨
    override fun showReviews(reviews: MovieReviews) {
        // 리뷰를 보여주기 위해 리사이클러뷰를 보여주고
        binding?.recyclerView?.isVisible = true
        // 에러 메세지 닫음
        binding?.errorDescriptionTextView?.isVisible = false
        // 어댑터에 리뷰를 전달하고 ui 갱신
        (binding?.recyclerView?.adapter as? MovieReviewsAdapter)?.apply {
            this.myReview = reviews.myReview
            this.reviews = reviews.othersReview
            notifyDataSetChanged()
        }
    }

    // 에러 시 토스트
    override fun showErrorToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }
}