package com.example.nomik.boardgamemanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class FragmentContact extends Fragment {
    ArrayList<ContactItem> players = new ArrayList<>();
    ListView listView;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ArrayList<ContactItem> temp = (ArrayList<ContactItem>)data.getSerializableExtra("players");
                for (int i = 0; i < temp.size(); i++) {
                    players.add(temp.get(i));
                }
                selectReset();
                ContactAdapter contactAdapter = new ContactAdapter(players);
                contactAdapter.notifyDataSetChanged();
                listView.setAdapter(contactAdapter);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_contact, container, false);
        listView = view.findViewById(R.id.listView_contact_selected);
        ContactItem contactItem = new ContactItem();
        contactItem.name = ((GameActivity)getActivity()).getMyName();
        players.add(contactItem);
        ContactAdapter contactAdapter = new ContactAdapter(players);
        contactAdapter.notifyDataSetChanged();
        listView.setAdapter(contactAdapter);

        ImageButton imageButton = view.findViewById(R.id.button_contact_add_from_contact);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ContactActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        imageButton = view.findViewById(R.id.button_contact_add_self);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("이름을 입력하세요");
                final EditText name = new EditText(getContext());
                alert.setView(name);

                alert.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContactItem contactItem = new ContactItem();
                        contactItem.name = name.getText().toString();
                        players.add(contactItem);
                        ContactAdapter contactAdapter = new ContactAdapter(players);
                        contactAdapter.notifyDataSetChanged();
                        listView.setAdapter(contactAdapter);
                    }
                });

                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alert.show();
            }
        });

        imageButton = view.findViewById(R.id.button_contact_delete);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ContactItem> temp = new ArrayList<>();
                for(int i = 0; i < players.size(); i++) {
                    if (!players.get(i).selected) {
                        temp.add(players.get(i));
                    }
                }
                players.clear();
                players.addAll(temp);
                ContactAdapter contactAdapter = new ContactAdapter(players);
                contactAdapter.notifyDataSetChanged();
                listView.setAdapter(contactAdapter);
            }
        });

        imageButton = view.findViewById(R.id.button_contact_complete);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(players.size() == 0) {
                    Toast.makeText(getContext(), "플레이어가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<String> player = new ArrayList<>();
                    for(int i = 0; i < players.size(); i++) {
                        player.add(players.get(i).name);
                    }
                    ((GameActivity)getActivity()).SetPlayers(player);
                    ((GameActivity)getActivity()).SetFragment("gaming");
                }
            }
        });
        return view;
    }
    private void selectReset() {
        for(int i = 0; i < players.size(); i++) {
            players.get(i).selected = false;
        }
    }
}
