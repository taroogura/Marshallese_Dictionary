package com.jocv.taroogura.marshallese_dictionary;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by taro on 2016/07/22.
 */
public class SearchWord extends SetActionBar {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        initActionBar();

        //get intent from SetActionBar.onNewIntent
        final Intent intent_from = getIntent();
        String searchWord = intent_from.getStringExtra("searchWord");

        showSearchResTitle(searchWord);
        showSearchRes(searchWord);
    }

    private void showSearchResTitle(String searchWord) {
        TextView tv_searchResTitle = (TextView) findViewById(R.id.textView_searchResTitle);
        tv_searchResTitle.setText("Search Results :"+searchWord);
    }

    private void showSearchRes(String searchWord){

        int cursorM2Ecount = searchDictionary(StaticInfo.M2E_TABLE_ID, searchWord);
        int cursorE2Mcount = searchDictionary(StaticInfo.E2M_TABLE_ID, searchWord);

        if(cursorM2Ecount==0 && cursorE2Mcount==0) {
            String noSearchResultsErrMsg = "No search results for " + searchWord;
            Toast.makeText(SearchWord.this, noSearchResultsErrMsg, Toast.LENGTH_LONG).show();
        }
    }

    private int searchDictionary(final int table_id, String searchWord)
    {
        Cursor cursor = findWord(table_id, searchWord);
        startManagingCursor(cursor);
        cursor.moveToFirst();

        ListAdapter adapter = null;
        ListView lv = null;
        if(table_id == StaticInfo.M2E_TABLE_ID) {
            adapter = new SimpleCursorAdapter(this, R.layout.list_item_m2e, cursor,
                    new String[]{"_id", "word", "phon", "def"},
                    new int[]{R.id.res_id, R.id.res_word, R.id.res_phon, R.id.res_def});
            lv = (ListView) findViewById(R.id.listView_searchResM2E);
        }else if(table_id == StaticInfo.E2M_TABLE_ID) {
            adapter = new SimpleCursorAdapter(this, R.layout.list_item_e2m, cursor,
                    new String[]{"_id", "word", "def"},
                    new int[]{R.id.res_id, R.id.res_word, R.id.res_def});
            lv = (ListView) findViewById(R.id.listView_searchResE2M);
        }
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                Cursor item = (Cursor) listView.getItemAtPosition(position);
                int word_id = item.getInt(item.getColumnIndex("_id"));
                String word_word = item.getString(item.getColumnIndex("word"));
                String word_def = item.getString(item.getColumnIndex("def"));

                String word_phon = null;
                if(table_id == StaticInfo.M2E_TABLE_ID) {
                    word_phon = item.getString(item.getColumnIndex("phon"));
                }else if(table_id == StaticInfo.E2M_TABLE_ID) {
                    word_phon = "";
                }
                sendIntent2ex(table_id, word_id, word_word, word_phon, word_def);
            }
        });

        return cursor.getCount();
    }

    private Cursor findWord(int table_id, String word) {
        Cursor cursor = null;
        if(table_id == StaticInfo.M2E_TABLE_ID) {
            String sortOrder = "wordE ASC";
            String[] queryWord = {word + "%"};
            cursor = DatabaseHandler.dict_db.query(StaticInfo.M2E_TABLE_NAME, StaticInfo.M2E_COLUMNS, "wordE like ?", queryWord, null, null, sortOrder);
        }else if(table_id == StaticInfo.E2M_TABLE_ID)
        {
            String sortOrder = "word ASC";
            String[] queryWord = {word + "%"};
            cursor = DatabaseHandler.dict_db.query(StaticInfo.E2M_TABLE_NAME, StaticInfo.E2M_COLUMNS, "word like ?", queryWord, null, null, sortOrder);
        }
        return cursor;
    }
    public void sendIntent2ex(int table_id, int word_id, String word_word, String word_phon, String word_def){
        super.sendIntent2ex(table_id, word_id, word_word, word_phon,  word_def);
    }
    @Override
    public void initActionBar(){
        super.initActionBar();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {return super.onOptionsItemSelected(item);}
    @Override
    //original onNewIntent method for searchRes
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if(Intent.ACTION_SEARCH.equals(intent.getAction())==true){
            String searchWord = intent.getStringExtra(SearchManager.QUERY);
            showSearchResTitle(searchWord);
            showSearchRes(searchWord);
        }
    }
}
