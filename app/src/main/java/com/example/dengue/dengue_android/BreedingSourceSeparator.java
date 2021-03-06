package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by nana on 2016/8/23.
 */
public class BreedingSourceSeparator extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity Main = this;;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.breeding_source_sptr);
            new menu(this, 2);

            if (null == savedInstanceState) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, breedingSourceFor6.newInstance())
                        .commit();
            }
        }
        else {
            Intent intent = new Intent();
            intent.setClass(BreedingSourceSeparator.this, BreedingSource.class);
            startActivity(intent);
            Main.finish();
        }
    }
    public void getImg(String path, int rotate)
    {
        Bundle bundle = new Bundle();
        bundle.putString("img", path);
        bundle.putInt("degree", rotate);
        Log.i("dengue",String.valueOf(rotate));

        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(BreedingSourceSeparator.this, BreedingSourceSubmit.class);
        startActivity(intent);
    }

}
