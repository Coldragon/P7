package com.openclassroom.go4lunch.Utils.EX;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.openclassroom.go4lunch.Repository.Repository;

import org.jetbrains.annotations.NotNull;

abstract public class ViewModelEX extends AndroidViewModel {

    public Repository getRepository() {
        return mRepository;
    }

    private final Repository mRepository;

    public ViewModelEX(@NonNull @NotNull Application application) {
        super(application);
        mRepository = Repository.getRepository();
    }

    @NonNull
    public Application requireApplication() {
        return getApplication();
    }
}