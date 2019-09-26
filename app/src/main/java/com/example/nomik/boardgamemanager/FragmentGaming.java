package com.example.nomik.boardgamemanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class FragmentGaming extends Fragment {
    private long startTime;
    private TextView textView_time;
    private TextView textView_dice_eye;
    private TextView textView_dice;
    private TextView textView_coin;
    private int dice_eye, dice, coin;
    Thread thread;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_gaming, container, false);
        startTime = System.currentTimeMillis();
        textView_time = view.findViewById(R.id.textView_gaming_time);
        textView_dice_eye = view.findViewById(R.id.TextView_gaming_dice_eye);
        textView_dice = view.findViewById(R.id.TextView_gaming_dice);
        textView_coin = view.findViewById(R.id.TextView_gaming_coin);
        dice_eye = 6;
        dice = 1;
        coin = 1;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update();
                            }
                        });
                    } catch (InterruptedException e) {
                    } catch (NullPointerException e) {
                        Thread.interrupted();
                    }
                }
            }
        });
        thread.start();

        Button button = view.findViewById(R.id.Button_gaming_dice_eye_minus5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_dice_eye.getText().equals("눈의 수")) {
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                } else if(dice_eye > 5) {
                    dice_eye = dice_eye - 5;
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                } else {
                    dice_eye = 1;
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_dice_eye_minus1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_dice_eye.getText().equals("눈의 수")) {
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                } else if(dice_eye > 1) {
                    dice_eye = dice_eye - 1;
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                } else {
                    dice_eye = 1;
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_dice_eye_plus1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textView_dice_eye.getText().equals("눈의 수")) {
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                } else {
                    dice_eye = dice_eye + 1;
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_dice_eye_plus5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_dice_eye.getText().equals("눈의 수")) {
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                } else {
                    dice_eye = dice_eye + 5;
                    textView_dice_eye.setText(String.valueOf(dice_eye));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_dice_minus5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_dice.getText().equals("굴릴 개수")) {
                    textView_dice.setText(String.valueOf(dice));
                } else if(dice > 5) {
                    dice = dice - 5;
                    textView_dice.setText(String.valueOf(dice));
                } else {
                    dice = 1;
                    textView_dice.setText(dice);
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_dice_minus1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_dice.getText().equals("굴릴 개수")) {
                    textView_dice.setText(String.valueOf(dice));
                } else if(dice > 1) {
                    dice = dice - 1;
                    textView_dice.setText(String.valueOf(dice));
                } else {
                    dice = 1;
                    textView_dice.setText(String.valueOf(dice));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_dice_plus1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_dice.getText().equals("굴릴 개수")) {
                    textView_dice.setText(String.valueOf(dice));
                } else {
                    dice = dice + 1;
                    textView_dice.setText(String.valueOf(dice));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_dice_plus5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_dice.getText().equals("굴릴 개수")) {
                    dice = 5;
                    textView_dice.setText(String.valueOf(dice));
                } else {
                    dice = dice + 5;
                    textView_dice.setText(String.valueOf(dice));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_dice_roll);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_dice_eye.getText().equals("눈의 수") || textView_dice.getText().equals("굴릴 개수")) {
                    Toast.makeText(getContext(), "주사위 눈이나 개수를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Random random = new Random();
                    String result = new String();
                    result = "주사위 결과: " + (random.nextInt(dice_eye)+1);
                    for(int i = 0; i < dice - 1; i++) {
                        result = result + ", " + (random.nextInt(dice_eye)+1);
                    }
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_coin_minus5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_coin.getText().equals("던질 개수")) {
                    textView_coin.setText(String.valueOf(coin));
                } else if(coin > 5) {
                    coin = coin - 5;
                    textView_coin.setText(String.valueOf(coin));
                } else {
                    coin = 1;
                    textView_coin.setText(String.valueOf(coin));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_coin_minus1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_coin.getText().equals("던질 개수")) {
                    textView_coin.setText(String.valueOf(coin));
                } else if(coin > 1) {
                    coin = coin - 1;
                    textView_coin.setText(String.valueOf(coin));
                } else {
                    coin = 1;
                    textView_coin.setText(String.valueOf(coin));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_coin_plus1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_coin.getText().equals("던질 개수")) {
                    textView_coin.setText(String.valueOf(coin));
                } else {
                    coin = coin + 1;
                    textView_coin.setText(String.valueOf(coin));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_coin_plus5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_coin.getText().equals("던질 개수")) {
                    textView_coin.setText(String.valueOf(coin));
                } else {
                    coin = coin + 5;
                    textView_coin.setText(String.valueOf(coin));
                }
            }
        });

        button = view.findViewById(R.id.Button_gaming_coin_toss);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView_coin.getText().equals("던질 개수")) {
                    Toast.makeText(getContext(), "동전 개수를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Random random = new Random();
                    String result = new String();
                    if (random.nextBoolean())
                        result = "동전 결과: 앞";
                    else
                        result = "동전 결과: 뒤";
                    for(int i = 0; i < coin -  1; i++) {
                        if (random.nextBoolean())
                            result = result + ", 앞";
                        else
                            result = result + ", 뒤";
                    }
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                }
            }
        });

        Button button_complete = view.findViewById(R.id.Button_gaming_complete);
        button_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread.interrupt();
                ((GameActivity)getActivity()).SetTime(textView_time.getText().toString());
                ((GameActivity)getActivity()).SetFragment("score");
            }
        });

        return view;
    }

    private void update() {
        long milliTime = System.currentTimeMillis() - startTime;
        long h = (milliTime / (1000 * 60 * 60));
        long m = (milliTime % (1000 * 60 * 60)) / (1000 * 60);
        long s= ((milliTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        textView_time.setText(String.format("%02d:%02d:%02d", h, m, s));
    }
}
