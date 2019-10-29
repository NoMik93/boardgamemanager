package com.example.nomik.boardgamemanager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactAddAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<ContactItem> data;
    private int nList;

    public ContactAddAdapter(ArrayList<ContactItem> _data) {
        data = _data;
        nList = data.size();
    }

    @Override
    public int getCount() {
        return nList;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final int index = i;
        if(view == null) {
            if(inflater == null)
                inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_contact_listview_item, viewGroup, false);
        }
        TextView textView = view.findViewById(R.id.contact_ListView_name);
        textView.setText(data.get(i).name);
        textView = view.findViewById(R.id.contact_ListView_number);
        if (data.get(i).number != null)
            textView.setText(data.get(i).number);
        ImageView imageView = view.findViewById(R.id.contact_ListView_image);
        Bitmap image = loadPhoto(context.getContentResolver(), data.get(i).person_id, data.get(i).photo_id);
        if (image != null)
            imageView.setImageBitmap(image);
        final ImageView imageViewSelect = view.findViewById(R.id.imageView_select);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.get(index).selected) {
                    view.setBackgroundColor(Color.WHITE);
                    imageViewSelect.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                    data.get(index).selected = false;
                }
                else {
                    view.setBackgroundColor(Color.BLUE);
                    imageViewSelect.setImageResource(R.drawable.ic_check_box_black_24dp);
                    data.get(index).selected = true;
                }
            }
        });
        return view;
    }

    public Bitmap loadPhoto(ContentResolver cr, long id, long photo_id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if(inputStream != null)
            return resizingPhoto(BitmapFactory.decodeStream(inputStream));

        byte[] photoBytes = null;
        Uri photoUri= ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
        Cursor c = cr.query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
        try {
            if(c.moveToFirst())
                photoBytes = c.getBlob(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }

        if(photoBytes != null)
            return resizingPhoto(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));

        return null;
    }

    public Bitmap resizingPhoto(Bitmap bitmap) {
        if(bitmap == null)
            return null;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float resizing_size = 120;
        Bitmap result = null;
        if(width > resizing_size) {
            float mWidth = (float) (width / 100);
            float fScale = (float) (resizing_size / mWidth);
            width = width * (fScale / 100);
            height = height * (fScale / 100);
        } else if (height > resizing_size) {
            float mHeight = (float) (height / 100);
            float fScale = (float) (resizing_size / mHeight);
            width = width * (fScale / 100);
            height = height * (fScale / 100);
        }
        result = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
        return result;
    }
}
