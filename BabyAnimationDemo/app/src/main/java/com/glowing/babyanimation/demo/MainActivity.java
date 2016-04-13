package com.glowing.babyanimation.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      AnimationFragment animationFragment = new AnimationFragment();
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.baby_animation, animationFragment, "Animation")
          .commitAllowingStateLoss();
    }
  }





}
