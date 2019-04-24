package com.example.android.sunshine;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PridajPocasie extends AppCompatActivity {

    private HashMap<String, String> plants_map;

    private Spinner spinner_predpoved;
    private Spinner spinner_mesto;

    private ArrayAdapter<CharSequence> adapter_predpoved;
    private ArrayAdapter<String> adapter_mesto;

    private DBHelper dbh;

    private String m_Text = "";
    private SimpleDateFormat inFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pridaj_pocasie);
        getSupportActionBar().setElevation(0);
        setTitle("Pridať počasie");

        plants_map = new HashMap<String, String>();
        inFormat = new SimpleDateFormat("dd-MM-yyyy");
        dbh = new DBHelper(this);

        pripojAdapterPredpoved();
        pripojAdapterMesto();
    }

    // prepoji spinner predpoved
    private void pripojAdapterPredpoved() {
        spinner_predpoved = (Spinner) findViewById(R.id.spinner_predpoved);
        adapter_predpoved = ArrayAdapter.createFromResource(this, R.array.predpoved_array, android.R.layout.simple_spinner_item);
        adapter_predpoved.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_predpoved.setAdapter(adapter_predpoved);
    }

    // prepoji spinner mesto
    protected void pripojAdapterMesto() {
        boolean b = true;
        ArrayList plants = new ArrayList();
        List<HashMap<String, String>> zoznam = dbh.getMesto();

        for (HashMap<String, String> entry : zoznam) {
            String id = "", mesto = "";
            for (String key : entry.keySet()) {
                if (b) {
                    String value = entry.get(key);
                    plants.add(value);
                    mesto = value;
                    b = false;
                } else {
                    String value = entry.get(key);
                    id = value;
                    b = true;
                }
            }
            plants_map.put(mesto, id);
        }
        spinner_mesto = (Spinner) findViewById(R.id.spinner_mesto);
        adapter_mesto = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, plants);
        adapter_mesto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_mesto.setAdapter(adapter_mesto);
    }

    // odosle nove počasie
    public void OdosliUdaje(View view) {
        EditText novyDatum = (EditText) findViewById(R.id.pridaj_datum);
        EditText novyStupen = (EditText) findViewById(R.id.pridaj_stupen);
        String novaPredpoved = spinner_predpoved.getSelectedItem().toString();


        if (spinner_mesto.getSelectedItem() == null) ;
        else {
            String noveMesto = spinner_mesto.getSelectedItem().toString();
            int idNoveMesto = Integer.parseInt(plants_map.get(noveMesto));

            if (overDatum(novyDatum.getText().toString())) {
                Intent intent = new Intent();
                intent.putExtra("novyDatum", novyDatum.getText().toString().trim());
                intent.putExtra("novyStupen", novyStupen.getText().toString().trim());
                intent.putExtra("novaPredpoved", novaPredpoved);
                intent.putExtra("idNoveMesto", idNoveMesto);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Zadali ste zly/starý dátum!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // overi datum
    private boolean overDatum(String newDatum) {
        try {
            Calendar c = Calendar.getInstance();
            String eee = inFormat.format(c.getTime());
            Date current_date = inFormat.parse(eee);

            Date date = inFormat.parse(newDatum);

            if (date.before(current_date)) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }

    // vytvori alertDialog pre zadanie noveho mesta
    public void pridajNoveMesto(View view) {
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
                        pripojAdapterMesto();
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
    }

}

