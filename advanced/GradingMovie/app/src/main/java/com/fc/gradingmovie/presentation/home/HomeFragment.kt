package com.fc.gradingmovie.presentation.home

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fc.gradingmovie.databinding.FragmentHomeBinding
import com.fc.gradingmovie.domain.model.FeaturedMovie
import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.extension.dip
import com.fc.gradingmovie.extension.toGone
import com.fc.gradingmovie.extension.toVisible
import com.fc.gradingmovie.presentation.home.HomeAdapter.Companion.ITEM_VIEW_TYPE_FEATURED
import com.fc.gradingmovie.presentation.home.HomeAdapter.Companion.ITEM_VIEW_TYPE_SECTION_HEADER
import org.koin.android.scope.ScopeFragment

class HomeFragment : ScopeFragment(), HomeContract.View {

    private var binding: FragmentHomeBinding? = null

    override val presenter: HomeContract.Presenter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        bindView()
        presenter.onViewCreated()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showLoadingIndicator() {
        binding?.progressBar?.toVisible()
    }

    override fun hideLoadingIndicator() {
        binding?.progressBar?.toGone()
    }

    override fun showErrorDescription(message: String) {
        binding?.recyclerView?.toGone()
        binding?.errorDescriptionTextView?.toVisible()
        binding?.errorDescriptionTextView?.text = message
    }

    override fun showMovies(featuredMovie: FeaturedMovie?, movies: List<Movie>) {
        binding?.recyclerView?.toVisible()
        binding?.errorDescriptionTextView?.toGone()
        (binding?.recyclerView?.adapter as? HomeAdapter)?.run {
            // 데이터 추가하고
            addData(featuredMovie, movies)
            // 변경 알림
            notifyDataSetChanged()
        }
    }
    private fun bindView() {
        (binding?.recyclerView?.adapter as? HomeAdapter)?.apply {
            onMovieClickListener = { movie ->
                val action = HomeFragmentDirections.toMovieReviewsAction(movie)
                findNavController().navigate(action)
            }
        }
    }
    private fun initViews() {
        // 리사이클러 뷰 세팅
        binding?.recyclerView?.apply {
            // 어탭터 설정
            adapter = HomeAdapter()
            // 커스텀 그리드 레이아웃 매니저 생성
            val gridLayoutManager = createGridLayoutManager()
            layoutManager = gridLayoutManager
            // 리사이클러 뷰의 각 아이템을 커스텀함
            addItemDecoration(GridSpacingItemDecoration(gridLayoutManager.spanCount, dip(6f)))
        }
    }

    private fun RecyclerView.createGridLayoutManager(): GridLayoutManager =
        // 3개 씩 수직으로 보여줌
        GridLayoutManager(context, 3, RecyclerView.VERTICAL, false).apply {
            // spanSizeLookUp -> 리사이클러뷰가 3열씩 보여져야하는데 추천영화는 맨위에 한 열만 보여주어야함
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    // 아이템 포지션의 타입을 보았을 때
                    when (adapter?.getItemViewType(position)) {
                        // 헤더와 추천일 때는 3열을 한번에 쓰겠다.
                        ITEM_VIEW_TYPE_SECTION_HEADER,
                        ITEM_VIEW_TYPE_FEATURED -> {
                            spanCount
                        }
                        // 그 외 영화는 1칸 씩 영화를 보여 주겠다.
                        else -> {
                            1
                        }
                    }
            }
        }
}