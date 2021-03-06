@file:Suppress("ClassName", "DEPRECATION")

package com.example.whatsup.adaptor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.whatsup.fragments.chatf
import com.example.whatsup.fragments.postf
import com.example.whatsup.fragments.statusf

class pageadeptor(fm: FragmentManager, private var count:Int) : FragmentPagerAdapter(fm, count) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> postf()
            2 -> statusf()
            else -> chatf()
        }
    }
    override fun getCount(): Int {
        return count
    }
}