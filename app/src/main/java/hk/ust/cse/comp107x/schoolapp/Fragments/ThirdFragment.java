package hk.ust.cse.comp107x.schoolapp.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.ust.cse.comp107x.schoolapp.R;

/**
 * Created by blessingorazulume on 3/17/16.
 */
public class ThirdFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.third_fragment, container, false);

        return v;
    }

    public static ThirdFragment newInstance(int page, String title) {

        ThirdFragment fragmentThird = new ThirdFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentThird.setArguments(args);
        return fragmentThird;
    }
}

