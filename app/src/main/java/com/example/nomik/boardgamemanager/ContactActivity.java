package com.example.nomik.boardgamemanager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    ArrayList<ContactItem> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        data = getContactList();
        ContactAdapter contactAdapter = new ContactAdapter(data);
        contactAdapter.notifyDataSetChanged();
        final ListView listView = findViewById(R.id.contact_ListView);
        listView.setAdapter(contactAdapter);
        Button button = findViewById(R.id.contact_Button_complete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ContactItem> players = new ArrayList<>();
                Intent intent = new Intent();
                for(int i = 0; i < data.size(); i++) {
                    if(data.get(i).selected)
                        players.add(data.get(i));
                }
                intent.putExtra("players", players);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public ArrayList<ContactItem> getContactList() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts._ID
        };
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor cursor = getContentResolver().query(uri, projection, null, selectionArgs, sortOrder);
        ArrayList<ContactItem> arrayList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do{
                ContactItem contactItem = new ContactItem();
                contactItem.number = cursor.getString(0);
                contactItem.name = cursor.getString(1);
                contactItem.photo_id = cursor.getLong(2);
                contactItem.person_id = cursor.getLong(3);
                arrayList.add(contactItem);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }
}
