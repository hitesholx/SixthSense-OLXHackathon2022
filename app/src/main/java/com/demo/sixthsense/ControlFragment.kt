package com.demo.sixthsense

import androidx.fragment.app.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ControlFragment : Fragment() {

    lateinit var rootView: View
    lateinit var onQuaternionChangedListener: OnQuaternionChangedListener

    var value1: Float = 10.0f
    var value2: Float = 10.0f
    var value3: Float = 10.0f
    var value4: Float = 10.0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_control, container, false)
        setupControls()
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onQuaternionChangedListener = activity as OnQuaternionChangedListener

        } catch (exception: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement onVector3ChangedListener")
        }
    }


    private fun setupControls() {
        rootView.apply {
            findViewById<FloatingActionButton>(R.id.left_button).setOnClickListener {
                value1 += 50f
                onQuaternionChangedListener.onLeft(value1)
            }

            findViewById<FloatingActionButton>(R.id.right_button).setOnClickListener {
                value2 += 50f
                onQuaternionChangedListener.onRight(value2)
            }

            findViewById<FloatingActionButton>(R.id.top_button).setOnClickListener {
                value3 += 10f
                onQuaternionChangedListener.onTop(value3)
            }

            findViewById<FloatingActionButton>(R.id.bottom_button).setOnClickListener {
                value4 += 10f
                onQuaternionChangedListener.onBottom(value4)
            }
        }
    }


    interface OnQuaternionChangedListener {
        fun onLeft(value: Float)
        fun onRight(value: Float)
        fun onTop(value: Float)
        fun onBottom(value: Float)
    }
}