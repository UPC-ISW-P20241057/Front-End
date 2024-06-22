package com.project.medibox.home.controller.fragments

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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.project.medibox.R
import com.project.medibox.controllers.activities.CustomizeAlarmActivity
import com.project.medibox.home.controller.activities.HomeActivity
import com.project.medibox.identitymanagement.controller.activities.EditProfileActivity
import com.project.medibox.medication.controller.activities.NewScheduleActivity


class VoiceCommandsFragment : Fragment() {
    private lateinit var mCommandsList: MutableList<String>
    private lateinit var mSpeechRecognizer: SpeechRecognizer
    private var mIsListening = false // this will be needed later
    private lateinit var tvTouchMe: TextView
    private lateinit var homeActivity: HomeActivity

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


    }

    private fun initCommands() {
        mCommandsList = ArrayList()
        mCommandsList.add("edit profile")
        mCommandsList.add("create reminder")
        mCommandsList.add("change alarm")
        mCommandsList.add("log out")
        mCommandsList.add("exit")
        mCommandsList.add("close")
    }

    private fun createIntent(): Intent {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-UK")
        return i
    }

    private fun handleSpeechBegin() {
        // start audio session
        tvTouchMe.text = "Listening..."
        mIsListening = true
        mSpeechRecognizer.startListening(createIntent())
    }

    private fun handleSpeechEnd() {
        // end audio session
        tvTouchMe.text = "Touch me!"
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
                }
            }

            override fun onPartialResults(partialResults: Bundle) {
                // Called when partial recognition results are available, this callback will be
                // called each time a partial text result is ready while the user is speaking.
                val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.size > 0) {
                    // handle partial speech results
                    val partialText = matches[0]
                    Log.d("Command voice", partialText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle) {}
        })
    }

    private fun handleCommand(command: String) {
        if (mCommandsList.contains(command)) {
            // Successful utterance, notify user
            when(command) {
                "edit profile" -> {
                    val intent = Intent(requireContext(), EditProfileActivity::class.java)
                    startActivity(intent)
                }
                "create reminder" -> {
                    val intent = Intent(requireContext(), NewScheduleActivity::class.java)
                    startActivity(intent)
                }
                "change alarm" -> {
                    val intent = Intent(requireContext(), CustomizeAlarmActivity::class.java)
                    startActivity(intent)
                }
                "log out" -> {
                    homeActivity.signOut()
                }
                "exit" -> {
                    homeActivity.finish()
                }
                "close" -> {
                    homeActivity.finish()
                }
            }
        } else {
            // Unsucessful utterance, show failure message on screen
            Toast.makeText(requireContext(), "Could not recognize command $command", Toast.LENGTH_LONG).show()
        }
    }
}