package com.example.nomik.boardgamemanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

public class FragmentGaming extends Fragment {
    private long startTime = 0;
    private TextView textView_time;
    private TextView textView_dice_eye;
    private TextView textView_dice;
    private TextView textView_coin;
    private int dice_eye, dice, coin, hasTable;
    private ArrayList<TrackerItem> data = new ArrayList<>();
    private static ListView trackers;
    private Thread timeThread;
    private int time = 0;
    static DisplayMetrics dm;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_gaming, container, false);
        if(startTime == 0)
            startTime = System.currentTimeMillis();
        textView_time = view.findViewById(R.id.textView_gaming_time);
        textView_dice_eye = view.findViewById(R.id.TextView_gaming_dice_eye);
        textView_dice = view.findViewById(R.id.TextView_gaming_dice);
        textView_coin = view.findViewById(R.id.TextView_gaming_coin);
        dice_eye = 6;
        dice = 1;
        coin = 1;
        hasTable = ((GameActivity)getActivity()).getHasTable();
        trackers = view.findViewById(R.id.listView_gaming_tracker);
        dm = getContext().getResources().getDisplayMetrics();

        if(data.size() > 0) {
            TrackerAdapter trackerAdapter = new TrackerAdapter(data);
            trackers.setAdapter(trackerAdapter);
            trackers.deferNotifyDataSetChanged();
            setListViewHeightBasedOnChildren(trackers);
        }
        timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update();
                            }
                        });
                        Thread.sleep(1000);
                        time++;
                    } catch (InterruptedException e) {
                        return;
                    } catch (NullPointerException e) {
                        Thread.interrupted();
                    }
                }
            }
        });
        timeThread.start();

        ImageButton imageButton = view.findViewById(R.id.imageButton_gaming_time);
        final ImageButton finalImageButton = imageButton;
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timeThread.isAlive()) {
                    timeThread.interrupt();
                    finalImageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    timeThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!Thread.interrupted()) {
                                try {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            update();
                                        }
                                    });
                                    Thread.sleep(1000);
                                    time++;
                                } catch (InterruptedException e) {
                                    return;
                                } catch (NullPointerException e) {
                                    Thread.interrupted();
                                }
                            }
                        }
                    });
                    finalImageButton.setImageResource(R.drawable.ic_pause_black_24dp);
                    timeThread.start();
                }
            }
        });
        imageButton = view.findViewById(R.id.addTracker);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackerItem trackerItem = new TrackerItem();
                data.add(trackerItem);
                TrackerAdapter trackerAdapter = new TrackerAdapter(data);
                trackers.setAdapter(trackerAdapter);
                trackers.deferNotifyDataSetChanged();
                setListViewHeightBasedOnChildren(trackers);
            }
        });
        imageButton = view.findViewById(R.id.removeTracker);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<TrackerItem> temp = new ArrayList<>();
                for (TrackerItem ti: data) {
                    if(!ti.getSelecet()) {
                        temp.add(ti);
                    }
                }
                data.clear();
                data.addAll(temp);
                TrackerAdapter trackerAdapter = new TrackerAdapter(data);
                trackers.setAdapter(trackerAdapter);
                trackers.deferNotifyDataSetChanged();
                setListViewHeightBasedOnChildren(trackers);
            }
        });

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
                /*if (hasTable == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("서버에 점수 테이블이 없습니다.\n만드시려면 테이블을 입력해주세요.(','로 구분)");
                    final EditText editText = new EditText(getContext());
                    builder.setView(editText);
                    builder.setPositiveButton("테이블 만들기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(((GameActivity)getActivity()).SetTable(editText.getText().toString())) {
                                dialogInterface.dismiss();
                                ((GameActivity)getActivity()).SetTime(textView_time.getText().toString());
                                ((GameActivity)getActivity()).SetFragment("makeScoreTable");
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "테이블이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    builder.setNegativeButton("건너뛰기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ((GameActivity)getActivity()).SetTime(textView_time.getText().toString());
                            ((GameActivity)getActivity()).SetFragment("score");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("서버에 점수 테이블이 있습니다.\n사용하시겠습니까?");
                    builder.setPositiveButton("테이블 사용하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ((GameActivity)getActivity()).SetTime(textView_time.getText().toString());
                            ((GameActivity)getActivity()).SetFragment("scoreTable");
                        }
                    });
                    builder.setNegativeButton("건너뛰기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ((GameActivity)getActivity()).SetTime(textView_time.getText().toString());
                            ((GameActivity)getActivity()).SetFragment("score");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }*/
                timeThread.interrupt();
                ((GameActivity)getActivity()).SetTime(textView_time.getText().toString());
                ((GameActivity)getActivity()).SetFragment("score");
            }
        });

        return view;
    }

    private void update() {
        /*long milliTime = System.currentTimeMillis() - startTime;
        long h = (milliTime / (1000 * 60 * 60));
        long m = (milliTime % (1000 * 60 * 60)) / (1000 * 60);
        long s= ((milliTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000;*/
        int h = (time / (60 * 60));
        int m = (time % (60 * 60)) / (60);
        int s= ((time % (60 * 60)) % (60));

        textView_time.setText(String.format("%02d:%02d:%02d", h, m, s));
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, dm);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (height * listAdapter.getCount()) + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
