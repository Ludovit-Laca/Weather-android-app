package com.example.android.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class UpravPocasie extends AppCompatActivity {

    private EditText upravEditextDatum;
    private EditText upravEditextStupen;
    private Spinner upravSpinnerPredpoved;
    private Spinner upravSpinnerMesto;

    private ArrayAdapter<CharSequence> adapter_predpoved;
    private ArrayAdapter<String> adapter_mesto;

    private HashMap<String, String> plants_map = new HashMap<String, String>();
    private SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
    private DBHelper dbh = new DBHelper(this);

    private Intent intent;
    private int idPocasie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uprav_pocasie);
        getSupportActionBar().setElevation(0);
        setTitle("Uprav počasie");

        nacitajUdaje();
        pripojAdapterPredpoved();
        pripojAdapterMesto();
    }

    // načita udaje do editextov
    private void nacitajUdaje() {
        intent = getIntent();
        idPocasie = intent.getIntExtra("idPocasie", 1);
        Pocasie p = dbh.getPocasie(idPocasie);

        upravEditextDatum = (EditText) findViewById(R.id.uprav_datum);
        upravEditextStupen = (EditText) findViewById(R.id.uprav_stupen);
        upravEditextDatum.setText(p.getDatum());
        upravEditextStupen.setText(p.getStupen());
    }

    // prepoji spinner predpoved a naplni
    private void pripojAdapterPredpoved() {
        upravSpinnerPredpoved = (Spinner) findViewById(R.id.spinner_predpoved_uprav);
        adapter_predpoved = ArrayAdapter.createFromResource(this, R.array.predpoved_array, android.R.layout.simple_spinner_item);
        adapter_predpoved.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upravSpinnerPredpoved.setAdapter(adapter_predpoved);
    }

    // prepoji spinner mesto a naplni
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
        upravSpinnerMesto = (Spinner) findViewById(R.id.spinner_mesto_uprav);
        adapter_mesto = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, plants);
        adapter_mesto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upravSpinnerMesto.setAdapter(adapter_mesto);
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

    // upravi počasie a ukonči aktivitu
    public void upravUdajePocasie(View view) {
        String novaPredpoved = upravSpinnerPredpoved.getSelectedItem().toString();

        if (upravSpinnerMesto.getSelectedItem() == null) ;
        else {
            String noveMesto = upravSpinnerMesto.getSelectedItem().toString();
            int idNoveMesto = Integer.parseInt(plants_map.get(noveMesto));

            if (overDatum(upravEditextDatum.getText().toString())) {
                Pocasie p = new Pocasie(
                        idPocasie,
                        upravEditextDatum.getText().toString().trim(),
                        novaPredpoved,
                        upravEditextStupen.getText().toString().trim(),
                        idNoveMesto
                );

                dbh.updatePocasie(p);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Zadali ste zly/starý dátum!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
