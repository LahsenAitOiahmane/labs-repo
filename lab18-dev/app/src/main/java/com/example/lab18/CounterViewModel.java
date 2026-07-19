package com.example.lab18;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class CounterViewModel extends ViewModel {

    // MutableLiveData = modifiable depuis le ViewModel
    private final MutableLiveData<Integer> countLiveData = new MutableLiveData<>();
    private final SavedStateHandle savedStateHandle;

    public CounterViewModel(SavedStateHandle handle) {
        this.savedStateHandle = handle;
        // Restaurer la valeur depuis le SavedStateHandle, sinon initialiser à 0
        Integer savedCount = savedStateHandle.get("count");
        countLiveData.setValue(savedCount != null ? savedCount : 0);
    }

    public void increment() {
        Integer current = countLiveData.getValue();
        int newCount = (current != null) ? current + 1 : 1;
        
        Log.d("CounterViewModel", "Incrementing counter to: " + newCount);
        countLiveData.setValue(newCount);
        savedStateHandle.set("count", newCount);
    }

    public void decrement() {
        Integer current = countLiveData.getValue();
        if (current != null) {
            int newCount = current - 1;
            Log.d("CounterViewModel", "Decrementing counter to: " + newCount);
            countLiveData.setValue(newCount);
            savedStateHandle.set("count", newCount);
        } else {
            Log.w("CounterViewModel", "Attempted to decrement a null counter state.");
        }
    }

    public void reset() {
        Log.i("CounterViewModel", "Resetting counter to 0");
        countLiveData.setValue(0);
        savedStateHandle.set("count", 0);
    }

    // Getter exposé à l'Activity (lecture seule = bonne pratique)
    public LiveData<Integer> getCount() {
        return countLiveData;
    }

    // Bonus 1 - postValue depuis un thread background
    public void incrementFromBackground() {
        new Thread(() -> {
            Integer current = countLiveData.getValue();
            if (current != null) {
                int newCount = current + 1;
                countLiveData.postValue(newCount);  // safe depuis n'importe quel thread
                // Note: SavedStateHandle.set() doit idéalement être appelé sur le main thread
                // mais pour cet exemple, ce n'est pas bloquant car l'activité est toujours en vie.
            }
        }).start();
    }
}
