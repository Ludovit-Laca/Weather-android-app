package com.example.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomCursorAdapter extends CursorAdapter {

    private SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");

    public CustomCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from
                (context).inflate(R.layout.pocase_layout, // ako naplnat
                parent, // čo naplnať
                false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        DBHelper dbh = new DBHelper(context);

        ImageView tvimage = (ImageView) view.findViewById(R.id.image1);
        TextView tvid = (TextView) view.findViewById(R.id.id1);
        TextView tvdatum = (TextView) view.findViewById(R.id.datum1);
        TextView tvpredpoved = (TextView) view.findViewById(R.id.predpoved1);
        TextView tvstupen = (TextView) view.findViewById(R.id.stupen1);

        String id = cursor.getString(cursor.getColumnIndex(MyContrast.Pocasie.COLUMN_ID));
        String datum = cursor.getString(cursor.getColumnIndex(MyContrast.Pocasie.COLUMN_DATUM));
        String predpoved = cursor.getString(cursor.getColumnIndex(MyContrast.Pocasie.COLUMN_PREDPOVED));
        String stupen = cursor.getString(cursor.getColumnIndex(MyContrast.Pocasie.COLUMN_STUPEN));

        String goal = datum;

        // premení dátum na deň v týždni

        try {
            Calendar c = Calendar.getInstance();
            String eee = inFormat.format(c.getTime());

            Date current_date = inFormat.parse(eee);
            Date date = inFormat.parse(datum);

            if (date.after(current_date) || date.equals(current_date)) {
                goal = outFormat.format(date);
                predpoved(predpoved, tvimage);

                tvid.setText(id);
                tvdatum.setText(goal);
                tvpredpoved.setText(predpoved);
                tvstupen.setText(stupen + "°");
            } else {
                dbh.deletePocasie(Integer.parseInt(id));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // statická metóda na pridelovanie obrázka pomocou predpovede

    public static ImageView predpoved(String predpoved, ImageView image) {
        switch (predpoved) {
            case "Clear":
                image.setImageResource(R.drawable.art_clear);
                return image;
            case "Clouds":
                image.setImageResource(R.drawable.art_clouds);
                return image;
            case "Fog":
                image.setImageResource(R.drawable.art_fog);
                return image;
            case "Light clouds":
                image.setImageResource(R.drawable.art_light_clouds);
                return image;
            case "Light rain":
                image.setImageResource(R.drawable.art_light_rain);
                return image;
            case "Rain":
                image.setImageResource(R.drawable.art_rain);
                return image;
            case "Snow":
                image.setImageResource(R.drawable.art_snow);
                return image;
            case "Storm":
                image.setImageResource(R.drawable.art_storm);
                return image;
            default:
                image.setImageResource(R.drawable.art_clear);
                return image;
        }
    }
}
