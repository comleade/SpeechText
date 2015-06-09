package rgp.com.speechtext;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class menu_Fragment3 extends android.support.v4.app.Fragment {
    View rootview ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment3, container, false) ;
        return rootview ;

    }
}
