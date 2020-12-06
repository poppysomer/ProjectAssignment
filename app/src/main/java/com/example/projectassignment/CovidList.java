package com.example.projectassignment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.String.valueOf;

public class CovidList extends AppCompatActivity {
    ProgressBar progressBar;
    ListView covidList;

    private CovidListAdapter listAdapter  = new CovidListAdapter();
    private ArrayList<Covid> covidArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covidlist);

        covidList = findViewById(R.id.covidListView);
        covidList.setAdapter(listAdapter);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String countryNameList = intent.getStringExtra("country");
        String startDateList = intent.getStringExtra("dates");

        CovidQuery query = new CovidQuery();
        try {
            query.execute("https://api.covid19api.com/country/" + countryNameList + "/status/confirmed/live?from=" + startDateList + "T00:00:00Z&to=2020-12-05T00:00:00Z");
        }catch(Exception e){
            e.printStackTrace();
        }
        boolean isTablet = findViewById(R.id.frameLayout) != null;

        covidList.setOnItemClickListener((list,view,position,id) -> {
            Bundle dataToPassFred = new Bundle();
            dataToPassFred.putString("country",covidArrayList.get(position).getCountry());
            dataToPassFred.putString("province",covidArrayList.get(position).getProvince());
            dataToPassFred.putString("countryCode",covidArrayList.get(position).getCountryCode());
            dataToPassFred.putString("startDate",covidArrayList.get(position).getStartDate());
            dataToPassFred.putString("cases",covidArrayList.get(position).getCaseNumber());
            dataToPassFred.putString("button", "SAVE");

            if(isTablet){
                CovidFragment cFragment = new CovidFragment();
                cFragment.setArguments(dataToPassFred);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,cFragment).commit();
            }else{
                Intent nextActivity = new Intent(CovidList.this, CovidDetail.class);
                nextActivity.putExtras(dataToPassFred);
                startActivity(nextActivity);
            }
        });
    }

    private class CovidListAdapter extends BaseAdapter {
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
            return i;
        }

        @Override //controls which widgets are on the row
        public View getView(int i, View old, ViewGroup parent){
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.activity_covid, parent, false);
            TextView textview = newView.findViewById(R.id.covidTitle);
            Covid thisCovid = (Covid) getItem(i);
            textview.setText(thisCovid.getProvince());
            return newView;
        }
    }

    private class CovidQuery extends AsyncTask<String, Integer, String> {
        public String countryName;
        public String provinceName;
        public int idList;
        public String countryCode;
        public String startDate;
        public String numberOfCase;
        boolean result;
        Context context = getApplicationContext();
        CharSequence text = "";
        int duration = Toast.LENGTH_LONG;

        @Override
        protected String doInBackground(String... args) {
            try {

                    //Initialize Connection
                    URL url = new URL(args[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream response = urlConnection.getInputStream();

                    //Make buffered reader to grab the information
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    //Store the results into a string variable
                    String results = sb.toString();

                JSONArray covidCases = new JSONArray(results);

                if(covidCases.length()!=0){
                    //Results
                    for(int i =0; i<covidCases.length(); i++){
                        JSONObject covidCase = covidCases.getJSONObject(i);
                        countryName = covidCase.getString("Country");
                        provinceName = covidCase.getString("Province");
                        countryCode = covidCase.getString("CountryCode");
                        startDate = covidCase.getString("Date");
                        numberOfCase = String.valueOf(covidCase.getInt("Cases"));
                        covidArrayList.add(new Covid(countryCode, countryName, provinceName, numberOfCase, startDate));
                    }

                }else{
                    //No results
                }
            }catch (Exception e){
            }
            return "Done";
        }

        public void onProgressUpdate(Integer... value) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        public void onPostExecute(String fromDoInBackground) {
            listAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}