package com.emotion.detector.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emotion.detector.adapter.CustomAdapter
import com.emotion.detector.databinding.FragmentHistoryBinding
import com.emotion.detector.model.History
import com.emotion.detector.model.State

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val viewModel by viewModels<HistoryViewModel>()
    private var histories: List<History>? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.historyState.observe(viewLifecycleOwner){
            historiesState ->
                when(historiesState.state){
                    State.loading->{
                        Log.d("emotion", "[HistoryFragment] Loading")
                    }
                    State.success-> {
                        Log.d("emotion", "[HistoryFragment] Success")
                        displayHistory(historiesState.data!!)
                    }
                    State.error->{
                        Log.d("emotion", "[HistoryFragment] Error")
                    }
                }
        }

        if(histories != null){
            displayHistory(histories!!)
        }else{
            viewModel.loadHistory()
        }

    }

    private fun displayHistory(histories: List<History>){
        val recyclerView = binding.historyRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        val data = ArrayList<History>()
        data.addAll(histories)
        val adapter = CustomAdapter(data)
        recyclerView.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}