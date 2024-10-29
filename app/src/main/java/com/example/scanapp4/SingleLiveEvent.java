package com.example.scanapp4;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class SingleLiveEvent <T> extends MutableLiveData<T> {
    private static final String TAG = "SingleLiveEvent";

    private final AtomicBoolean mPending = new AtomicBoolean(false);
    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        if (hasActiveObservers()) {
        }
        // Observe the internal MutableLiveData
        super.observe(owner, t -> {
            if (!mPending.get()) {
                observer.onChanged(t);
                mPending.set(true);
            }
        });
    }
    @MainThread
    public void setValue(@Nullable T t) {
        mPending.set(false);
        super.setValue(t);
    }
    public void postValue(@Nullable T t) {
        mPending.set(false);
        super.postValue(t);
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    public void call() {
        setValue(null);
    }
}