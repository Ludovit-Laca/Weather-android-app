package com.example.android.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class UpravMesto extends AppCompatActivity {

    private DBHelper dbh;
    private EditText editextMesto;
    private Intent intent;
    private int idMesto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uprav_mesto);

        setTitle("Uprav mesto");
        getSupportActionBar().setElevation(0);
        dbh = new DBHelper(this);
        editextMesto = (EditText) findViewById(R.id.uprav_nazov);
        intent = getIntent();
        idMesto = intent.getIntExtra("idMesto", 1);

        nacitajUdaje();
    }

    // prida mesto do editextu
    public void nacitajUdaje() {
        Mesto m = dbh.getMesto(idMesto);
        editextMesto.setText(m.getNazov());
    }

    // upravi mesto a ukonči aktivitu
    public void upravUdaje(View view) {
        String noveMesto = editextMesto.getText().toString().trim();
        Mesto m = new Mesto(idMesto, noveMesto);
        dbh.updateMesto(m);

        Intent intentNavrat = new Intent();
        setResult(RESULT_OK, intentNavrat);
        finish();
    }

    // vymaže mesto aj jeho počasie a ukonči aktivitu
    public void vymazUdaje(View view) {
        dbh.deleteMesto(idMesto);
        dbh.deletePocasiePomocouMesta(idMesto);

        Intent intentNavrat = new Intent();
        setResult(RESULT_OK, intentNavrat);
        finish();
    }
}
