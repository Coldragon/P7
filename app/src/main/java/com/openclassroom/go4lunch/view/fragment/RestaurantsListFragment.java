package com.openclassroom.go4lunch.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.openclassroom.go4lunch.model.SearchValidationData;
import com.openclassroom.go4lunch.model.nearbysearchapi.NearbySearchResult;
import com.openclassroom.go4lunch.model.placedetailsapi.DetailsResult;
import com.openclassroom.go4lunch.utils.ex.FragmentEX;
import com.openclassroom.go4lunch.view.activity.MainActivity;
import com.openclassroom.go4lunch.view.recyclerview.RestaurantsListAdapter;
import com.openclassroom.go4lunch.viewmodel.SearchViewModel;
import com.openclassroom.go4lunch.databinding.FragmentListviewBinding;

import java.util.ArrayList;

public class RestaurantsListFragment extends FragmentEX {

    private FragmentListviewBinding mBinding;
    private RestaurantsListAdapter mRestaurantsListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        mBinding = FragmentListviewBinding.inflate(inflater, container, false);

        mRestaurantsListAdapter = new RestaurantsListAdapter(requireActivity(), new ArrayList<>());
        mBinding.restaurantList.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.restaurantList.setAdapter(mRestaurantsListAdapter);

        searchViewModel.getAddRestaurantToList().addObserver((o, arg) -> {
            if (arg instanceof NearbySearchResult)
                mRestaurantsListAdapter.addToRestaurantList((NearbySearchResult) arg);
        });

        searchViewModel.getClearRestaurantList().addObserver((o, arg) -> {
            mRestaurantsListAdapter.clearRestaurantList();
        });

        SearchValidationData svd = new SearchValidationData();
        svd.searchMethod = SearchValidationData.SearchMethod.CLOSER;
        svd.viewType = MainActivity.MainViewTypes.LIST;
        searchViewModel.setSearchValidationDataViewMutable(svd);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}