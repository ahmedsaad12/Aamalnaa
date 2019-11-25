package com.creative.share.apps.aamalnaa.activities_fragments.activity_sign_in.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.creative.share.apps.aamalnaa.R;
import com.creative.share.apps.aamalnaa.activities_fragments.activity_sign_in.fragments.Fragment_Language;
import com.creative.share.apps.aamalnaa.activities_fragments.activity_sign_in.fragments.Fragment_Sign_In;
import com.creative.share.apps.aamalnaa.activities_fragments.activity_sign_in.fragments.Fragment_Sign_Up;
import com.creative.share.apps.aamalnaa.language.Language;
import com.creative.share.apps.aamalnaa.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    private int fragment_count = 0;
    private Fragment_Sign_In fragment_sign_in;
    private Fragment_Sign_Up fragment_sign_up;
    private Fragment_Language fragment_language;
    private String cuurent_language;
    private Preferences preferences;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Paper.init(this);


        initView();
        if (savedInstanceState == null) {
           /* if (!preferences.isLanguageSelected(this))
            {
                DisplayFragmentLanguage();
            }else
            {
                DisplayFragmentSignIn();

            }*/
            DisplayFragmentSignIn();

        }

getDataFromIntent();
    }
    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null && intent.hasExtra("sign_up"))
        {
            boolean isSign_in = intent.getBooleanExtra("sign_up",true);
            if (!isSign_in)
            {
                DisplayFragmentSignUp();

            }
        }
    }
    private void initView() {
        Paper.init(this);
        preferences = Preferences.getInstance();
        cuurent_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        fragmentManager = this.getSupportFragmentManager();

    }

    public void DisplayFragmentSignIn() {
        fragment_count += 1;
        fragment_sign_in = Fragment_Sign_In.newInstance();
        if (fragment_sign_in.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_sign_in).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_sign_in, "fragment_sign_in").addToBackStack("fragment_sign_in").commit();
        }
    }

    public void DisplayFragmentSignUp() {
        fragment_count += 1;
        fragment_sign_up = Fragment_Sign_Up.newInstance();
        if (fragment_sign_up.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_sign_up).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_sign_up, "fragment_sign_up").addToBackStack("fragment_sign_up").commit();
        }
    }

    public void DisplayFragmentLanguage() {
        fragment_language = Fragment_Language.newInstance();
        if (fragment_language.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_language).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_language, "fragment_language").addToBackStack("fragment_language").commit();
        }
    }


    public void RefreshActivity(String selected_language) {
        Paper.book().write("lang", selected_language);
        Language.setNewLocale(this, selected_language);
        preferences.setIsLanguageSelected(this);


        Intent intent = getIntent();
        finish();
        startActivity(intent);


    }
    @Override
    public void onBackPressed() {
        Back();
    }

    public void Back() {
        if (fragment_language!=null&&fragment_language.isAdded()&&fragment_language.isVisible())
        {
            finish();

        }else
        {
            if (fragment_count >1) {
                fragment_count -= 1;
                super.onBackPressed();


            } else  {

                finishAffinity();

            }
        }


    }
}
