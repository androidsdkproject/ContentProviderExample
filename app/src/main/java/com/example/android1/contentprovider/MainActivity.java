package com.example.android1.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Button Addname,RetriveData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Addname=(Button)findViewById(R.id.AddName);
        RetriveData=(Button)findViewById(R.id.Retrivedata);

        Addname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddName(v);
            }
        });


        RetriveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRetrieveStudent();

            }
        });
    }

    public void onClickAddName(View view) {

        ContentValues values = new ContentValues();
        values.put(MyContentProvider.NAME,
                ((EditText)findViewById(R.id.editText2)).getText().toString());

        values.put(MyContentProvider.GRADE,
                ((EditText)findViewById(R.id.editText3)).getText().toString());


        values.put(MyContentProvider.EMAIL,"kamalverma1207@gmail.com");
        values.put(MyContentProvider.MOBILE,"9509371638");


        Uri uri = getContentResolver().insert(
                MyContentProvider.CONTENT_URI, values);

        Toast.makeText(getBaseContext(),
                uri.toString(), Toast.LENGTH_LONG).show();
    }
    public void onClickRetrieveStudent() {// Retrieve student records
        String URL = "content://com.example.android1.contentprovider.MyContentProvider";

        Uri students = Uri.parse(URL);
        Cursor c = managedQuery(students, null, null, null, "name");

        if (c.moveToFirst()) {
            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(MyContentProvider._ID)) +
                                ", " +  c.getString(c.getColumnIndex( MyContentProvider.NAME)) +
                                ", " + c.getString(c.getColumnIndex( MyContentProvider.GRADE)),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
    }

}
