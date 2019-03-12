package group2019022.me.guideme.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import group2019022.me.guideme.MainActivity;

public class BaseFragment extends Fragment {

    private MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            activity = (MainActivity) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.getClass().getSimpleName() + "should be an instance of MainActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public MainActivity getMainActivity() {
        return activity;
    }

}
