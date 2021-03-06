package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportAdapter extends BaseAdapter {
    private static final String AppName = "Dengue";
    private LayoutInflater reportListInflater;

    private CharSequence[] img;
    private CharSequence[] type;
    private CharSequence[] address;
    private CharSequence[] description;
    private CharSequence[] date;
    private CharSequence[] status;
    private CharSequence[] lat;
    private CharSequence[] lon;
    private Activity Main;

    public ReportAdapter(Context context, CharSequence[] img,
                         CharSequence[] type, CharSequence[] address,
                         CharSequence[] description, CharSequence[] date, CharSequence[] status,
                         CharSequence[] lat, CharSequence[] lon,
                         Activity mMain) {
        reportListInflater = LayoutInflater.from(context);
        this.img = img;
        this.type = type;
        this.address = address;
        this.description = description;
        this.date = date;
        this.status = status;
        this.lat = lat;
        this.lon = lon;
        this.Main = mMain;
    }

    @Override
    public int getCount() {
        return img.length;
    }

    @Override
    public Object getItem(int position) {
        return img[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Item item;
        if(convertView == null) {
            convertView = reportListInflater.inflate(R.layout.report_item, null);
            item = new Item(
                    (TextView) convertView.findViewById(R.id.reportList_check_type),
                    (TextView) convertView.findViewById(R.id.reportList_check_location),
                    (TextView) convertView.findViewById(R.id.reportList_check_description),
                    (TextView) convertView.findViewById(R.id.reportList_check_date),
                    (ImageView) convertView.findViewById(R.id.reportList_check_img)
                    );

            convertView.setTag(item);
        }
        else {
            item = (Item) convertView.getTag();
        }

        setIcon(position, item.type);
        setImg(position, item.img);
        setDate(position, item.date);
        setAddress(position, item.address);
        item.description.setText(description[position]);

        return convertView;
    }

    class Item{
        TextView type;
        TextView address;
        TextView description;
        TextView date;
        ImageView img;
        Button button_yes;
        Button button_wait;
        Button button_no;

        public Item(TextView type, TextView address, TextView description,
                    TextView date, ImageView img){
            this.type = type;
            this.address = address;
            this.description = description;
            this.date = date;
            this.img = img;

        }
    }

    private void setAddress(int position, TextView textAddress) {
        gps Gps = new gps(Main);
        String output = address[position].toString();
        if(output.equals("")) {
            output = Gps.get(Double.valueOf(lat[position].toString()), Double.valueOf(lon[position].toString()));
            if(output == null) {
                output = "";
            }
        }
        textAddress.setText(output);
    }

    private void setDate(int position, TextView textDate) {
        SimpleDateFormat date_parser = new SimpleDateFormat("yyyy-mm-dd", Locale.TAIWAN);
        SimpleDateFormat data_format = new SimpleDateFormat("yyyy.mm.dd", Locale.TAIWAN);
        Date output_date = null;
        try {
            output_date = date_parser.parse(date[position].toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textDate.setText("通報時間： " + data_format.format(output_date));
    }

    /*private void setButtons(final int position, Button button_yes, Button button_wait, Button button_no) {
        switch(status[position].toString()) {
            case "待審核":
                button_yes.setTextColor(Main.getResources().getColor(R.color.report_button_yes));
                break;
            case "已通過":
                button_wait.setTextColor(Main.getResources().getColor(R.color.report_button_wait));
                break;
            case "未通過":
                button_no.setTextColor(Main.getResources().getColor(R.color.report_button_no));
                break;
        }
    }*/

    private void setIcon(int position, TextView icon) {
        switch(type[position].toString()) {
            case "室內":
                icon.setText("室內");
                break;
            case "戶外":
                icon.setText("戶外");
                break;
            default:
                icon.setText("室內");
                break;
        }
    }

    private void setImg(int position, ImageView Img) {
        byte[] decodedString = Base64.decode(img[position].toString(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Img.setImageBitmap(decodedByte);
    }
}

