package rs.reviewer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;
import rs.reviewer.MainActivity;
import rs.reviewer.R;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        int SPLASH_TIME_OUT = 3000;

        /*
        * Timer je jedna od komponenti koja nam omogucava da zakazmeo
        * zadatke koji treba da se izvrse u buducnosti, jednom ili periodicno.
        * Medjutim kada aplikacija nije aktivna, on nece izvrsiti svoj posao.
        * Idealna primena ovoga je za Splash screen ili pozdravni ili uvodni
        * ekran za korisnika.
        * posao koji timer treba izvrsi se opsiuje u run() metodi, a kao drugi
        * parametar kazemo koliko Timer treba da ceka da bi se posao izvrsio.
        * */
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish(); // da nebi mogao da ode back na splash
            }
        }, SPLASH_TIME_OUT);
    }
}