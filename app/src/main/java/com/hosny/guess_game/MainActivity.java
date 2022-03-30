package com.hosny.guess_game;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.XMLFormatter;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, ShakeDetector.ShakeListener {
    TextView tvwrong,tvcounter;
    Button btstart,bthint;
    Button btshake;
    ImageView imagesound;
    Random r=new Random();
    int x,g;
    byte wrong=0;
    boolean gamestart,mute;
    boolean [] select=new boolean[10];
    List<TextView>select_anotherway=new ArrayList<>();
    TextToSpeech tts;
    TextView [] allview;
    List<String>nol= Arrays.asList("1","2","3","4","5","6","7","8","9");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvwrong=findViewById(R.id.tvwrong);
        imagesound=findViewById(R.id.soundimg);
        tvcounter=findViewById(R.id.tvcounter);
        btstart=findViewById(R.id.start);
        bthint=findViewById(R.id.bthint);
        btshake=findViewById(R.id.btshake);
        allview= new TextView[]{findViewById(R.id.tv1), findViewById(R.id.tv2), findViewById(R.id.tv3), findViewById(R.id.tv4), findViewById(R.id.tv5), findViewById(R.id.tv6), findViewById(R.id.tv7), findViewById(R.id.tv8), findViewById(R.id.tv9)};
        tts=new TextToSpeech(this,this);
        Sensey.getInstance().init(this);
    }
    public void hint(View view) {
        if(x<=4){
            Toast.makeText(this, "is smaller than 4 or eqaul", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "is bigger than 4 or eqaul", Toast.LENGTH_SHORT).show();
        }
    }
    public void start(View view) {
        //to shuffle number
        Collections.shuffle(nol);
        for (int i = 0; i <allview.length ; i++) {
            allview[i].setText(nol.get(i));
        }
        btstart.setEnabled(false);
        //to get use number
        for(TextView c:select_anotherway){
            c.setEnabled(true);
        }
        //to delet old number
        select_anotherway.clear();
        /*
        or
        for (int i = 0; i <10 ; i++) {
            select[i]=false;
        }
         */
        //or usel like collection interface but with array*/
        //Arrays.fill(select,false);
        gamestart=true;
        x=r.nextInt(9)+1;
       // Toast.makeText(this, "guess="+x, Toast.LENGTH_SHORT).show();//just for dev
        Log.d("TAG", "winner number: "+x);
    }

    public void answer(View view) {
            if (gamestart == false) {
                //Toast.makeText(this, "please click start", Toast.LENGTH_SHORT).show();
                YoYo.with(Techniques.Shake).duration(500).repeat(1).playOn(btstart);
                return;
            }
            TextView tv = (TextView) view;//picture
            YoYo.with(Techniques.Flash).duration(500).repeat(1).playOn(tv);
            int no = Integer.parseInt(tv.getText().toString());
                if (mute == false) {
                    tts.speak("" + no, TextToSpeech.QUEUE_FLUSH, null, null);
                }
        /*
        if(select[no]==true){
            Toast.makeText(this, "selected before", Toast.LENGTH_SHORT).show();
            return;
        }
        select[no]=true;
         */
                select_anotherway.add(tv);
                tv.setEnabled(false);
                if (no == x) {
                    tvwrong.setText("" + "right");
                    gamestart = false;
                    btshake.setEnabled(true);
                    bthint.setEnabled(false);
                    btstart.setEnabled(true);
                    wrong = 0;
                } else {
                    if (wrong == 1) {
                        bthint.setEnabled(true);
                    }
                    wrong++;
                    if (wrong == 3) {
                        wrong = 0;
                        Toast.makeText(this, "you dead!", Toast.LENGTH_SHORT).show();
                        gamestart = false;
                        btshake.setEnabled(true);
                        bthint.setEnabled(false);
                        btstart.setEnabled(true);
                    }
                    tvwrong.setText("wrong");
                    tvcounter.setText("" + wrong);
                }
            }

    @Override
    public void onInit(int i) {
        //tts.setSpeechRate(0.7f);//speed
        //tts.setPitch(0.7f)//freq sound
        //will run with deauflt
    }
    public void shake(View view) {
       // answer((View) view); way to call method
        btstart.setEnabled(false);
        btshake.setEnabled(false);
        Sensey.getInstance().startShakeDetection(this);//start shake event
        x=r.nextInt(9)+1;
        Toast.makeText(this, "guess="+x, Toast.LENGTH_SHORT).show();//just for dev
    }

    @Override
    public void onShakeDetected() {
        //increase by value it will make it work times by shake not accurcy away
    }

    @Override
    public void onShakeStopped() {
       // Toast.makeText(this, "shake by start", Toast.LENGTH_SHORT).show();
        //start(btstart);to call button
         g=r.nextInt(9)+1;//get by luck
        if(x/*random taker*/==g){
            wrong=0;
            tvwrong.setText("right");
            Sensey.getInstance().stopShakeDetection(this);//to stop
            btshake.setEnabled(true);
            btstart.setEnabled(true);
            gamestart=false;
        }
        else {
            tvwrong.setText("wrong");
            wrong++;
            if(wrong==3){
                wrong=0;
                btshake.setEnabled(true);
                btstart.setEnabled(true);
                Sensey.getInstance().stopShakeDetection(this);//to stop
            }
            tvcounter.setText(""+wrong);
            Toast.makeText(getApplicationContext(), "you lose"+g, Toast.LENGTH_SHORT).show();
        }
    }

    public void imagesound(View view) {
        if(mute==false) {
            imagesound.setImageResource(R.drawable.ic_music_off);
            mute = true;
        }
        else{
            imagesound.setImageResource(R.drawable.ic_music_video);
            mute=false;
        }
    }
}