package com.org.saolonguinho.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.org.saolonguinho.R;

public class HelpActivity extends AppIntro {

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.welcome),
                getResources().getString(R.string.description), R.drawable.icon_app_512,
                getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.how_works),
                getResources().getString(R.string.example_usage), R.drawable.question,
                getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.how_works_2),
                getResources().getString(R.string.end_help), R.drawable.time_saving,
                getResources().getColor(R.color.colorPrimary)));
        showSkipButton(false);
        ((Button) doneButton).setText("Certo!");
        ((Button) doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setProgressButtonEnabled(true);
    }
}

