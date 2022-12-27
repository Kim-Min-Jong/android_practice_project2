package com.fc.trackingdelivery.presentation.addtrackingitem

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.fc.trackingdelivery.data.entity.ShippingCompany
import com.fc.trackingdelivery.databinding.FragmentAddTrackingItemBinding
import com.fc.trackingdelivery.extensions.toGone
import com.fc.trackingdelivery.extensions.toVisible
import com.google.android.material.chip.Chip
import org.koin.android.scope.ScopeFragment

class AddTrackingItemFragment : ScopeFragment(), AddTrackingItemsContract.View {

    override val presenter: AddTrackingItemsContract.Presenter by inject()

    private var binding: FragmentAddTrackingItemBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddTrackingItemBinding.inflate(inflater)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
        presenter.onViewCreated()

        changeInvoiceIfAvailable()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 프래그먼트끼리의 전환시 이전 프래그먼트의 키보드가 자동으로 안숨겨 질 수 있어서 키보드 숨김 명시
        hideKeyboard()
        presenter.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showShippingCompaniesLoadingIndicator() {
        binding?.shippingCompanyProgressBar?.toVisible()
    }

    override fun hideShippingCompaniesLoadingIndicator() {
        binding?.shippingCompanyProgressBar?.toGone()
    }

    override fun showRecommendCompanyLoadingIndicator() {
        binding?.recommendProgressBar?.visibility = View.VISIBLE
    }

    override fun hideRecommendCompanyLoadingIndicator() {
        binding?.recommendProgressBar?.visibility = View.GONE
    }

    override fun showSaveTrackingItemIndicator() {
        binding?.saveButton?.apply {
            text = null
            isEnabled = false
        }
        binding?.saveProgressBar?.toVisible()
    }

    override fun hideSaveTrackingItemIndicator() {
        binding?.saveButton?.apply {
            text = "저장하기"
            isEnabled = true
        }
        binding?.saveProgressBar?.toGone()
    }

    override fun showCompanies(companies: List<ShippingCompany>) {
        // 회사 목록들을 돌면서
        companies.forEach { company ->
            // 칩 그룹에 뷰 컴포넌트를 추가 해줌
            binding?.chipGroup?.addView(
                Chip(context).apply {
                    // 칩의 내용은 회사명으로
                    text = company.name
                }
            )
        }
    }

    override fun showRecommendCompany(company: ShippingCompany) {
        // 칩 그룹 바인딩
        binding?.chipGroup
            // 칩 안의 하위 뷰들에 대해 순차적으로 접근함
            ?.children
            // 뷰 중에서 Chip인 컴포넌트들만 필터링해서
            ?.filterIsInstance(Chip::class.java)
            // 그 중을 순회 하면서
            ?.forEach { chip ->
                // 칩안의 텍스트와 추천 택배사가 같으면
                if (chip.text == company.name) {
                    // 체크를 해서 추천 택배사를 정해줌
                    binding?.chipGroup?.apply { check(chip.id) }
                    return@forEach
                }
            }
    }

    override fun enableSaveButton() {
        binding?.saveButton?.isEnabled = true
    }

    override fun disableSaveButton() {
        binding?.saveButton?.isEnabled = false
    }

    override fun showErrorToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun finish() {
        findNavController().popBackStack()
    }

    private fun bindView() {
        // 칩그룹에서 체크가 되면 이벤트 발생
        binding?.chipGroup?.setOnCheckedChangeListener { group, checkedId ->
            // 택배사 변경
            presenter.changeSelectedShippingCompany(group.findViewById<Chip>(checkedId).text.toString())
        }
        // 텍스트 입력시 이벤트 발생
        binding?.invoiceEditText?.addTextChangedListener { editable ->
            // 운송장 변경
            presenter.changeShippingInvoice(editable.toString())
        }
        // 정보 저장
        binding?.saveButton?.setOnClickListener { _ ->
            presenter.saveTrackingItem()
        }
    }

    // 클립보드에 복사 된 것이 있을 떄
    private fun changeInvoiceIfAvailable() {
        // 클립보드 객체를 가져옴
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 클립보드의 텍스트를 가져옴
        val invoice = clipboard.plainTextClip()
        // 텍스트가 있으면 다이얼로그를 통해 그 텍스트를 송장 번호로 쓸 지 선택할 수 있게 해줌
        if (!invoice.isNullOrBlank()) {
            AlertDialog.Builder(requireActivity())
                .setTitle("클립 보드에 있는 $invoice 를 운송장 번호로 추가하시겠습니까?")
                .setPositiveButton("추가할래요") { _, _ ->
                    binding?.invoiceEditText?.setText(invoice)
                    presenter.fetchRecommendShippingCompany()
                }
                .setNegativeButton("안할래요") { _, _ -> }
                .create()
                .show()
        }
    }
    private fun ClipboardManager.plainTextClip(): String? =
        // text 타입의 클립이 있으면 첫번째 텍스트를 가져옴
        if (hasPrimaryClip() && (primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true)) {
            primaryClip?.getItemAt(0)?.text?.toString()
        } else {
            null
        }
    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }
}