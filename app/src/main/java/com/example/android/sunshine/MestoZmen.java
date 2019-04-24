package com.example.android.sunshine;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.HashMap;
import java.util.List;

public class MestoZmen extends AppCompatActivity {

    private DBHelper dbh;
    private ListView lv;
    private SimpleCursorAdapter myAdapter;
    private List<HashMap<String, String>> zoznam;
    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesto_zmen);
        setTitle("Zmeň mesto");

        dbh = new DBHelper(this);
        zoznam = dbh.getMesto();

        PripojAdapter();
    }

    private void PripojAdapter() {
        myAdapter = new SimpleCursorAdapter(
                this,
                R.layout.mesto_layout,
                dbh.VratKurzor2(),
                new String[]{MyContrast.Mesto.COLUMN_ID,
                        MyContrast.Mesto.COLUMN_NAZOV},
                new int[]{R.id.item1_id, R.id.item2_nazov},
                0);

        lv = (ListView) findViewById(R.id.listview2);
        lv.setAdapter(myAdapter);

        // vrati ID noveho mesta
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) findViewById(R.id.listview2);
                Cursor cursor = ((SimpleCursorAdapter) lv.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                int ID = cursor.getInt(cursor.getColumnIndex(MyContrast.Mesto.COLUMN_ID));

                Intent intentNavrat = new Intent();
                intentNavrat.putExtra("idMesto", ID);
                setResult(RESULT_OK, intentNavrat);
                finish();
            }
        });

        // otvori novu aktivitu na upravu mesta alebo jeho vymazanie
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) findViewById(R.id.listview2);
                Cursor cursor = ((SimpleCursorAdapter) lv.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                int ID = cursor.getInt(cursor.getColumnIndex(MyContrast.Mesto.COLUMN_ID));

                Intent newIntent = new Intent(MestoZmen.this, UpravMesto.class);
                newIntent.putExtra("idMesto", ID);
                startActivityForResult(newIntent, 1);
                return false;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    PripojAdapter();
                }
                break;
        }
    }

    // pridelenie menu pre tuto aplikaciu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mesto, menu);
        return true;
    }

    // otvori alertDialog pre zadanie noveho mesta
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mesto_pridaj:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setMessage("Zadajte názov nového mesta: ")
                        .setTitle("Nové mesto")
                        .setPositiveButton("Potvrď", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_Text = input.getText().toString().trim();
                                Mesto m = new Mesto(1, m_Text);
                                dbh.addMesto(m);
                                PripojAdapter();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Zavrieť", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
