package com.example.projectassignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecipeListActivity extends AppCompatActivity
{
    ProgressBar progressBar;

    private MyListAdapter listAdapter;
    private ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        ListView listView = findViewById(R.id.recipeList);
        listView.setAdapter(listAdapter = new MyListAdapter());

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String recipeStr = intent.getStringExtra("recipe");
        String ingredientsStr = intent.getStringExtra("ingredients");

        RecipeQuery query = new RecipeQuery();
        query.execute("http://www.recipepuppy.com/api/?i="+ ingredientsStr +"&q="+ recipeStr +"&p=3&format=xml");

        boolean isTablet = findViewById(R.id.frameLayout) != null;

        Button helpBtn = findViewById(R.id.help);
        helpBtn.setOnClickListener(v->
        {
            AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Instructions:")
                    .setMessage( "Click on a recipe in the list to view the details."
                    + " You can save a recipe by pressing 'favourite' and you can " +
                            "go to the recipe's website by clicking on the URL.")
                    .setNeutralButton("Okay",(click,arg)->
                    {
                        //Do nothing
                    })
                    .setView(getLayoutInflater().inflate(R.layout.recipe_row_alert_layout,null))
                    .create().show();
        });

        listView.setOnItemClickListener((list,view,position,id)->
        {
            Bundle dataToPass = new Bundle();
            dataToPass.putString("recipe",recipes.get(position).getTitle());
            dataToPass.putString("website",recipes.get(position).getWebsite());
            dataToPass.putString("ingredients",recipes.get(position).getIngredients());

            if(isTablet)
            {


                RecipeDetailsFragment detailsFragment = new RecipeDetailsFragment();
                detailsFragment.setArguments(dataToPass);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout,detailsFragment)
                        .commit();

            }
            else
            {
                Intent nextActivity = new Intent(RecipeListActivity.this,RecipeDetailsActivity.class);
                nextActivity.putExtras(dataToPass);
                startActivity(nextActivity);
            }
        });

    }


    public class MyListAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return recipes.size();
        }

        @Override
        public Object getItem(int position)
        {
            return recipes.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            //TODO: set this to database id after
            /*return ((MessageText)getItem(position)).getId()*/ ;
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();

            View newView = inflater.inflate(R.layout.row_recipe_layout,parent,false);

            TextView titleTextView = newView.findViewById(R.id.titleText);
            titleTextView.setText( ((Recipe)getItem(position)).getTitle()) ;

            return newView;
        }
    }



    //It wants recipe, then ingredients, then format
    class RecipeQuery extends AsyncTask<String, Integer,String>
    {
        String title = "";
        String website = "";
        String ingredients = "";
        private ArrayList<Recipe> tempRecipes = new ArrayList<>();
        @Override
        protected String doInBackground(String... args)
        {
            try
            {
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT)
                {


                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equals("title"))
                        {
                            title = xpp.nextText();
                            publishProgress(25);
                        }
                        else if (xpp.getName().equals("href"))
                        {
                            website = xpp.nextText();
                            publishProgress(50);
                        }
                        else if (xpp.getName().equals("ingredients"))
                        {
                            ingredients = xpp.nextText();
                            publishProgress(75);
                        }

                        if (!title.equals("") && !website.equals("") && !ingredients.equals("")) //if we have a full recipe object
                        {
                            recipes.add(new Recipe(title,website,ingredients));
                            title = "";
                            website = "";
                            ingredients = "";

                        }

                    }
                    eventType = xpp.next();

                }


            }
            catch (Exception e)
            {

            }

            //recipes.add(new Recipe("Recipe Title","Recipes.com","Onions, Food"));


            return "Done";
        }
        @Override
        protected void onProgressUpdate(Integer ... values)
        {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        public void onPostExecute(String fromDoInBackground)
        {
            //recipes = tempRecipes;
            listAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

}