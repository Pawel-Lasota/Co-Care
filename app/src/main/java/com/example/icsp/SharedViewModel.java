package com.example.icsp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.icsp.homepage.CareRecipientModel;

/**
 * SharedViewModel Model Class
 * <p>
 * This class is responsible for ensuring that the selected recipient in the mainActivity toolbar is remembered throughout other fragments
 * when routing throughout different fragments where information changes depending on which care recipient is selected#
 * <p>
 * Thanks to Android Studio Developers website for insight into how to implement this model (Discussed and referenced in report)
 */
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<CareRecipientModel> selectedRecipient = new MutableLiveData<>();
    public void selectRecipient(CareRecipientModel recipient) {
        selectedRecipient.setValue(recipient);
    }
    public LiveData<CareRecipientModel> getSelectedRecipient() {
        return selectedRecipient;
    }
}
