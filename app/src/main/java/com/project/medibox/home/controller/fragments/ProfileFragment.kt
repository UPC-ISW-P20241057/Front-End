package com.project.medibox.home.controller.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.project.medibox.R
import com.project.medibox.home.controller.activities.HomeActivity
import com.project.medibox.identitymanagement.controller.activities.EditProfileActivity
import com.project.medibox.shared.StateManager

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onResume() {
        super.onResume()
        loadProfileData(requireView())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnEditProfile = view.findViewById<Button>(R.id.btnEditProfile)
        val btnSignOut = view.findViewById<Button>(R.id.btnSignOut)
        loadProfileData(view)
        btnEditProfile.setOnClickListener {
            goToEditProfileActivity(view)
        }
        btnSignOut.setOnClickListener {
            val homeActivity = activity as HomeActivity
            homeActivity.signOut()
        }
    }
    private fun loadProfileData(view: View) {
        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvProfileLastname = view.findViewById<TextView>(R.id.tvProfileLastname)
        val tvProfileEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val tvProfileCellphone = view.findViewById<TextView>(R.id.tvProfileCellphone)

        tvProfileName.text = StateManager.loggedUser.name
        tvProfileLastname.text = StateManager.loggedUser.lastName
        tvProfileEmail.text = StateManager.loggedUser.email
        tvProfileCellphone.text = StateManager.loggedUser.phone
    }

    private fun goToEditProfileActivity(view: View) {
        val intent = Intent(view.context, EditProfileActivity::class.java)
        startActivity(intent)
    }
}
