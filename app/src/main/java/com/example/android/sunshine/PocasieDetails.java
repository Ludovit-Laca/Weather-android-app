package com.example.android.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PocasieDetails extends AppCompatActivity {

    private TextView detail_datum;
    private TextView detail_datum2;
    private ImageView detail_obrazok;
    private TextView detail_predpoved;
    private TextView detail_stupen;
    private TextView detail_mesto;

    private DBHelper dbh;
    private int id_pocasie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocasie_details);

        setTitle("Detaily");
        dbh = new DBHelper(this);

        detail_datum = (TextView) findViewById(R.id.detail_datum);
        detail_obrazok = (ImageView) findViewById(R.id.detail_obrazok);
        detail_predpoved = (TextView) findViewById(R.id.detail_predpoved);

        detail_datum2 = (TextView) findViewById(R.id.detail_datum2);
        detail_stupen = (TextView) findViewById(R.id.detail_stupen);
        detail_mesto = (TextView) findViewById(R.id.detail_mesto);

        Intent intent = getIntent();
        id_pocasie = intent.getIntExtra("idPocasie", 0);

        napln(id_pocasie);
    }

    // naplni udaje
    private void napln(int id_pocasie) {
        Pocasie p = dbh.getPocasie(id_pocasie);
        Mesto m = dbh.getMesto(p.getID_mesto());

        detail_datum2.setText(p.getDatum());
        CustomCursorAdapter.predpoved(p.getPredpoved(), detail_obrazok);
        detail_predpoved.setText(p.getPredpoved());

        try {
            SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = inFormat.parse(p.getDatum());
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            detail_datum.setText(outFormat.format(date));
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        detail_stupen.setText(p.getStupen() + "°");
        detail_mesto.setText(m.getNazov());
    }

    // pridelenie menu pre tuto aktivitu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pocasie_uprav, menu);
        return true;
    }

    // čo sa ma spraviť po kliknuti na polozku v menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pocasie_uprav:
                Intent intent = new Intent(PocasieDetails.this, UpravPocasie.class);
                intent.putExtra("idPocasie", id_pocasie);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    napln(id_pocasie);
                }
        }
    }
}
