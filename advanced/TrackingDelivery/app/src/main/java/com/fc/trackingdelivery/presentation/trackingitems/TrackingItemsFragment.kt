package com.fc.trackingdelivery.presentation.trackingitems

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fc.trackingdelivery.R
import com.fc.trackingdelivery.data.entity.TrackingInformation
import com.fc.trackingdelivery.data.entity.TrackingItem
import com.fc.trackingdelivery.databinding.FragmentTrackingItemsBinding
import com.fc.trackingdelivery.extensions.toGone
import com.fc.trackingdelivery.extensions.toInvisible
import com.fc.trackingdelivery.extensions.toVisible
import org.koin.android.scope.ScopeFragment

class TrackingItemsFragment : ScopeFragment(), TrackingItemsContract.View {

    override val presenter: TrackingItemsContract.Presenter by inject()

    private var binding: FragmentTrackingItemsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTrackingItemsBinding.inflate(inflater)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        bindView()
        presenter.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroyView()
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
        binding?.refreshLayout?.isRefreshing = false
    }

    // 데이터가 없을 땐 리사이클러 뷰를 숨기고 추가할 수 있는 버튼이 있는 뷰 보이기
    override fun showNoDataDescription() {
        binding?.refreshLayout?.toInvisible()
        binding?.noDataContainer?.toVisible()
    }

    // 데이터 보여주기
    override fun showTrackingItemInformation(trackingItemInformation: List<Pair<TrackingItem, TrackingInformation>>) {
        binding?.refreshLayout?.toVisible()
        binding?.noDataContainer?.toGone()
        (binding?.recyclerView?.adapter as? TrackingItemsAdapter)?.apply {
            this.data = trackingItemInformation
            notifyDataSetChanged()
        }
    }

    private fun initViews() {
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = TrackingItemsAdapter()
        }
    }

    private fun bindView() {
        // 리프레시 했을 때 동작
        binding?.refreshLayout?.setOnRefreshListener {
            presenter.refresh()
        }

        // 프래그먼트 navigate
        binding?.addTrackingItemButton?.setOnClickListener {
            findNavController().navigate(R.id.to_add_tracking_item)
        }
        // 프래그먼트 navigate
        binding?.addTrackingItemFloatingActionButton?.setOnClickListener { _ ->
            findNavController().navigate(R.id.to_add_tracking_item)
        }
    }
}