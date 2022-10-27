package com.example.absencemanagementapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.absencemanagementapp.fragments.StudentRegisterFragment
import com.example.absencemanagementapp.fragments.TeacherRegisterFragment

class RegisterTabsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> StudentRegisterFragment()
            else -> TeacherRegisterFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Student"
            else -> "Teacher"
        }
    }
}