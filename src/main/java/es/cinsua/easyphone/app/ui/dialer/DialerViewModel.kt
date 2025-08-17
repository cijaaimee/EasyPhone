package es.cinsua.easyphone.app.ui.dialer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import es.cinsua.easyphone.app.contacts.ContactInfo
import es.cinsua.easyphone.app.contacts.ContactRepository
import es.cinsua.easyphone.app.telecom.TelecomDialer
import es.cinsua.easyphone.app.tts.TtsEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DialerUiState(
    val isLoading: Boolean = true,
    val currentIndex: Int = 0,
    val contacts: List<ContactInfo> = listOf()
)

class DialerViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(DialerUiState(isLoading = true))
  val uiState = _uiState.asStateFlow()
  val tts = TtsEngine(application)

  init {
    loadContacts()
  }

  private fun loadContacts() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      withContext(Dispatchers.IO) {
        val contacts = ContactRepository.getAll(application)
        withContext(Dispatchers.Main) {
          _uiState.update { it.copy(contacts = contacts, currentIndex = 0, isLoading = false) }
          sayCurrentContactName()
        }
      }
    }
  }

  fun onNextContact() {
    _uiState.update { state ->
      if (state.contacts.isEmpty()) return@update state
      val nextIndex =
          if (state.currentIndex == state.contacts.size - 1) {
            0
          } else {
            state.currentIndex + 1
          }
      state.copy(currentIndex = nextIndex)
    }
    sayCurrentContactName()
  }

  fun onPreviousContact() {
    _uiState.update { state ->
      if (state.contacts.isEmpty()) return@update state
      val prevIndex =
          if (state.currentIndex == 0) {
            state.contacts.size - 1
          } else {
            state.currentIndex - 1
          }
      state.copy(currentIndex = prevIndex)
    }
    sayCurrentContactName()
  }

  private fun sayCurrentContactName() {
    val contact = uiState.value.contacts[uiState.value.currentIndex]
    tts.speak(contact.displayName)
  }

  fun onContactDial() {
    val contact = uiState.value.contacts[uiState.value.currentIndex]
    tts.speak("Llamando a ${contact.displayName}") {
      TelecomDialer.dial(application, contact.phoneNumber)
    }
  }
}
