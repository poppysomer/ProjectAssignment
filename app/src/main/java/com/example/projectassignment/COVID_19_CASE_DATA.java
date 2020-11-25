package com.example.projectassignment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class COVID_19_CASE_DATA extends AppCompatActivity {
    TextView caseTitle;
    EditText caseSearch;
    Button searchButton;
    ProgressBar caseBar;
    TextView caseResult;
    ListView covidListView;
    public int covidCase;
    public Bundle dataToPassFred;
    public ArrayList<Covid> covidArrayList = new ArrayList<>();
    public CovidListAdapter covidListAdapter = new CovidListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_case_data);

        covidListView = findViewById(R.id.caseListView);
        covidListView.setAdapter(covidListAdapter);

    }

private class CovidListAdapter extends BaseAdapter{
    @Override // number of items in the list
    public int getCount() {
        return  covidArrayList.size();
    }

    @Override // what string goes at row i
    public Object getItem(int i) {
        return covidArrayList.get(i);
    }

    @Override //database id of item at row i
    public long getItemId(int i) {
        return covidArrayList.get(i).getId();
    }


    @Override //controls which widgets are on the row
    public View getView(int i, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.activity_covid, parent, false);
            TextView textview = newView.findViewById(R.id.covidTitle);
            Covid thisCovid = (Covid) getItem(i);
            textview.setText(thisCovid.getCountry());
            return newView;

        }
    }
}
