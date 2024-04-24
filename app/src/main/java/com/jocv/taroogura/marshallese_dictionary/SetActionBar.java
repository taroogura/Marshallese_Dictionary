package com.jocv.taroogura.marshallese_dictionary;

import android.app.ActionBar;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by taro on 2016/07/30.
 */
public abstract class SetActionBar extends AppCompatActivity {
    public void initActionBar(){
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //Not showing the title on ActionBar
        actionBar.setDisplayShowTitleEnabled(false);
        //Add go home button on ActionBar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        //Show icon on ActionBar
        actionBar.setIcon(R.mipmap.ic_launcher);
    }

    //Add searchview on ActionBar
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView;
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = getComponentName();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);

        MenuItem menuItem = menu.findItem(R.id.action_Search);
        searchView = (SearchView)menuItem.getActionView();
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconifiedByDefault(false);

        //to enable home button and searchview on ActionBar
        //http://stackoverflow.com/questions/26376429/detecting-a-click-on-action-bar-back-button-onoptionsitemselected-not-calling
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_Search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {return true;}
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Move to MainActivity when the home button pressed
            case android.R.id.home:
                Intent intent_to = new Intent();
                final String packageName = getPackageName();
                intent_to.setClassName(packageName, packageName + ".MainActivity");
                startActivity(intent_to);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //send intent to SearchEx Activity with  word data
    public void sendIntent2ex(int table_id, int word_id, String word_word, String word_phon, String word_def){
        final String packageName = getPackageName();
        Intent intent_to = new Intent();
        intent_to.setClassName(packageName, packageName + ".SearchEx");
        intent_to.putExtra("table_id", table_id);
        intent_to.putExtra("word_id", word_id);
        intent_to.putExtra("word_word", word_word);
        intent_to.putExtra("word_phon", word_phon);
        intent_to.putExtra("word_def", word_def);
        startActivity(intent_to);
    }

    //get intent from searchview on ActionBar and send intent to SearchWord Activity with entered word
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(Intent.ACTION_SEARCH.equals(intent.getAction())==true){
            String searchWord = intent.getStringExtra(SearchManager.QUERY);
            Intent intent_to = new Intent();

            final String packageName = getPackageName();
            intent_to.setClassName(packageName, packageName + ".SearchWord");

            if(searchWord.length() > 0){
                intent_to.putExtra("searchWord", searchWord);
                startActivity(intent_to);
            }else {
                //not necessary(searchview won't send intent when nothing is entered)
                String noInputErrMsg = "Please enter Marshallese or English word";
                Toast.makeText(getApplicationContext(), noInputErrMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
}
