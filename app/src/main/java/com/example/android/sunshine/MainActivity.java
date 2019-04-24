package com.example.android.sunshine;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    private int prefsMesto;

    private DBHelper dbh;
    private SimpleDateFormat sdf;
    private CustomCursorAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        dbh = new DBHelper(this);
        sdf = new SimpleDateFormat("dd-MM-yyyy");

        PripojAdapter();
    }

    // pridelenie menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pocasie, menu);
        return true;
    }

    // položky menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pocasie_pridaj:
                startActivityForResult(new Intent(this, PridajPocasie.class), 1);
                return true;
            case R.id.mesto_zmen:
                startActivityForResult(new Intent(this, MestoZmen.class), 2);
                return true;
            case R.id.pocasie_about:
                Toast.makeText(getBaseContext(), "Made by Ľudovít Laca", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // nastaví počasie na úvodnej obrazovke
    private void UvodnaObrazovka() {
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        Pocasie p = dbh.getPocasie(date, "" + prefsMesto);
        if (p == null) {
            TextView uvodDatum = (TextView) findViewById(R.id.uvod_datum);
            TextView uvodPredpoved = (TextView) findViewById(R.id.uvod_predpoved);
            TextView uvodStupen = (TextView) findViewById(R.id.uvod_stupen);
            ImageView uvodImage = (ImageView) findViewById(R.id.uvod_image);

            uvodDatum.setText("Pridajte počasie na dneska :)");
            uvodPredpoved.setText("");
            uvodStupen.setText("");
            uvodImage.setImageResource(0);
        } else {
            Mesto m = dbh.getMesto(p.getID_mesto());

            TextView uvodDatum = (TextView) findViewById(R.id.uvod_datum);
            TextView uvodPredpoved = (TextView) findViewById(R.id.uvod_predpoved);
            TextView uvodStupen = (TextView) findViewById(R.id.uvod_stupen);
            ImageView uvodImage = (ImageView) findViewById(R.id.uvod_image);

            uvodDatum.setText("Today, " + m.getNazov());
            uvodPredpoved.setText(p.getPredpoved());
            uvodStupen.setText(p.getStupen() + "°");

            myAdapter.predpoved(p.getPredpoved(), uvodImage);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String novyDatum = data.getStringExtra("novyDatum");
                    String novaPredpoved = data.getStringExtra("novaPredpoved");
                    String novyStupen = data.getStringExtra("novyStupen");
                    int idNoveMesto = data.getIntExtra("idNoveMesto", 1);

                    Pocasie newPocasie = new Pocasie(1, novyDatum, novaPredpoved, novyStupen, idNoveMesto);
                    dbh.addPocasie(newPocasie);
                    PripojAdapter();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    int noveIdMesto = data.getIntExtra("idMesto", 1);
                    saveData(noveIdMesto);
                    loadData();
                    PripojAdapter();
                } else {
                    PripojAdapter();
                }
                break;
            case 3:
                PripojAdapter();
                break;
        }
    }

    private void PripojAdapter() {
        loadData();

        Cursor myCursor = dbh.VratKurzor(prefsMesto);

        myAdapter = new CustomCursorAdapter(this, myCursor, 0);

        UvodnaObrazovka();

        ListView lvItems = (ListView) findViewById(R.id.listView);
        lvItems.setAdapter(myAdapter);

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(myAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) findViewById(R.id.listView);
                Cursor cursor = ((CustomCursorAdapter) lv.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                int ID = cursor.getInt(cursor.getColumnIndex(MyContrast.Pocasie.COLUMN_ID));

                // ukáže detaily počasia
                Intent newIntent = new Intent(MainActivity.this, PocasieDetails.class);
                newIntent.putExtra("idPocasie", ID);
                startActivityForResult(newIntent, 3);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());

                builder.setMessage("Vymazať naozaj?")
                        .setTitle("Mazanie ...")
                        .setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ListView lv = (ListView) findViewById(R.id.listView);
                                Cursor cursor = ((CustomCursorAdapter) lv.getAdapter()).getCursor();
                                cursor.moveToPosition(position);
                                int ID = cursor.getInt(cursor.getColumnIndex(MyContrast.Pocasie.COLUMN_ID));
                                dbh.deletePocasie(ID);
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "mažem", Toast.LENGTH_SHORT).show();
                                PripojAdapter();
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
                return false;
            }
        });
    }

    // uloži informaciu o meste, ktoré sa má zobrazovať
    public void saveData(int idMesto) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(TEXT, idMesto);
        editor.apply();
    }

    // načita informaciu o meste, ktoré sa má zobrazovať
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        prefsMesto = sharedPreferences.getInt(TEXT, 1);
    }

}
