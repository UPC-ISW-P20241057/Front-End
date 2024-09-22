package com.project.medibox.home.controller.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.project.medibox.R
import com.project.medibox.pillboxmanagement.controller.activities.CustomizeAlarmActivity
import com.project.medibox.home.controller.activities.HomeActivity
import com.project.medibox.identitymanagement.controller.activities.EditProfileActivity
import com.project.medibox.medication.controller.activities.NewScheduleActivity
import com.project.medibox.pillboxmanagement.controller.activities.WiFiInstructionsActivity


class VoiceCommandsFragment : Fragment() {
    private lateinit var mCommandsList: MutableList<String>
    private lateinit var mSpeechRecognizer: SpeechRecognizer
    private var mIsListening = false // this will be needed later
    private lateinit var tvTouchMe: TextView
    private lateinit var homeActivity: HomeActivity
    private var activityStarted: Boolean = false

    override fun onResume() {
        super.onResume()
        activityStarted = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice_commands, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ivMicrophone = view.findViewById<ImageView>(R.id.ivMicrophone)
        tvTouchMe = view.findViewById(R.id.tvTouchMe)
        homeActivity = requireActivity() as HomeActivity
        initCommands()
        createSpeechRecognizer()
        ivMicrophone.setOnClickListener {
            if (mIsListening) {
                handleSpeechEnd()
            } else {
                handleSpeechBegin()
            }
        }
        val btnCommands = view.findViewById<Button>(R.id.btnCommands)
        btnCommands.setOnClickListener {
            showVoiceCommandsDialog(view)
        }

    }

    private fun showVoiceCommandsDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle(getString(R.string.which_are_available_voice_commands))
            .setMessage("Editar perfil: Abre la pantalla de editar perfil\nCrear recordatorio: Abre la pantalla para crear recordatorio\nCambiar alarma: Abre la pantalla de cambiar alarma del pastillero\nPastillero: Abre la pantalla que muestra los pasos para conectar el pastillero a Wi-Fi\nCerrar sesión: Cierra la sesión del usuario\nCerrar: Cierra la aplicación\nSalir: Cierra la aplicación")
            .setPositiveButton(getString(R.string.accept)) { _, _ ->

            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun initCommands() {
        mCommandsList = ArrayList()
        mCommandsList.add(getString(R.string.voice_edit_profile))
        mCommandsList.add(getString(R.string.voice_create_reminder))
        mCommandsList.add(getString(R.string.voice_change_alarm))
        mCommandsList.add(getString(R.string.voice_log_out))
        mCommandsList.add(getString(R.string.voice_exit))
        mCommandsList.add(getString(R.string.voice_close))
        mCommandsList.add(getString(R.string.voice_pillbox))
    }

    private fun createIntent(): Intent {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
        return i
    }

    private fun handleSpeechBegin() {
        // start audio session
        tvTouchMe.text = getString(R.string.listening_three_points)
        mIsListening = true
        mSpeechRecognizer.startListening(createIntent())
    }

    private fun handleSpeechEnd() {
        // end audio session
        tvTouchMe.text = getString(R.string.touch_me)
        mIsListening = false
        mSpeechRecognizer.cancel()
    }

    private fun createSpeechRecognizer() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        mSpeechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray) {}

            override fun onEndOfSpeech() {
                handleSpeechEnd()
            }

            override fun onError(error: Int) {
                handleSpeechEnd()
            }

            override fun onResults(results: Bundle) {
                // Called when recognition results are ready. This callback will be called when the
                // audio session has been completed and user utterance has been parsed.

                // This ArrayList contains the recognition results, if the list is non-empty,
                // handle the user utterance
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.size > 0) {
                    // The results are added in decreasing order of confidence to the list
                    val command = matches[0]
                    handleCommand(command.lowercase())
                    Log.d("VoiceCommands", command.lowercase())
                }
            }

            override fun onPartialResults(partialResults: Bundle) {
                // Called when partial recognition results are available, this callback will be
                // called each time a partial text result is ready while the user is speaking.
                val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.size > 0) {
                    // handle partial speech results
                    val partialText = matches[0]
                    handleCommand(partialText.lowercase())
                    Log.d("VoiceCommandsP", partialText.lowercase())
                }
            }

            override fun onEvent(eventType: Int, params: Bundle) {}
        })
    }

    private fun handleCommand(command: String) {
        if (mCommandsList.contains(command) && !activityStarted) {
            // Successful utterance, notify user
            activityStarted = true
            when(command) {
                getString(R.string.voice_edit_profile) -> {
                    val intent = Intent(requireContext(), EditProfileActivity::class.java)
                    startActivity(intent)
                }
                getString(R.string.voice_create_reminder) -> {
                    val intent = Intent(requireContext(), NewScheduleActivity::class.java)
                    startActivity(intent)
                }
                getString(R.string.voice_change_alarm) -> {
                    val intent = Intent(requireContext(), CustomizeAlarmActivity::class.java)
                    startActivity(intent)
                }
                getString(R.string.voice_pillbox) -> {
                    val intent = Intent(requireContext(), WiFiInstructionsActivity::class.java)
                    startActivity(intent)
                }
                getString(R.string.voice_log_out) -> {
                    homeActivity.signOut()
                }
                getString(R.string.voice_exit) -> {
                    homeActivity.finish()
                }
                getString(R.string.voice_close) -> {
                    homeActivity.finish()
                }
            }
        }
    }
}