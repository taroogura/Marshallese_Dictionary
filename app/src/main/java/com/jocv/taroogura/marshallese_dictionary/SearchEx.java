package com.jocv.taroogura.marshallese_dictionary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by taro on 2016/07/26.
 */
public class SearchEx extends SetActionBar {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_examples);
        initActionBar();

        //get intent from SetActionBar.sendIntent2ex
        final Intent intent_from = getIntent();

        int table_id = intent_from.getIntExtra("table_id", 0);
        int word_id = intent_from.getIntExtra("word_id", 0);
        String word_word = intent_from.getStringExtra("word_word");
        String word_phon = intent_from.getStringExtra("word_phon");
        String word_def = intent_from.getStringExtra("word_def");

        //add "_id" of word in Dictionary DB to "word_id" in Hitory DB
        addHistory(table_id, word_id);

        showSearchExTitle(table_id, word_word);
        showWordData(word_word, word_phon, word_def);

        Cursor cursor = findExample(table_id, word_word);
        startManagingCursor(cursor);
        cursor.moveToFirst();
        ListAdapter adapterEx = new SimpleCursorAdapter(this, R.layout.list_item_ex, cursor,
                new String[]{"_id", "word", "ex", "exTrans"},
                new int[]{R.id.ex_id, R.id.ex_word, R.id.ex, R.id.exTrans});
        ListView lvEx = (ListView) findViewById(R.id.listView_examples);
        lvEx.setAdapter(adapterEx);
    }
    private void showSearchExTitle(int table_id, String word_word)
    {
        TextView tv_searchResTitle = (TextView) findViewById(R.id.textView_def_exTitle);
        if(table_id == StaticInfo.M2E_TABLE_ID) {
            tv_searchResTitle.setText("Definition and Examples of Marshallese word :"+ word_word);
        }else if (table_id == StaticInfo.E2M_TABLE_ID) {
            tv_searchResTitle.setText("Definition and Examples of English word :"+ word_word);
        }
    }
    private Cursor findExample(int table_id, String word) {
        Cursor cursor = null;
        if(table_id == StaticInfo.M2E_TABLE_ID) {
            String sortOrder = "word ASC";
            String[] queryWord = {word};
            cursor = DatabaseHandler.dict_db.query(StaticInfo.M2E_CON_TABLE_NAME, StaticInfo.M2E_CON_COLUMNS, "word = ?", queryWord, null, null, null);
        }else if(table_id == StaticInfo.E2M_TABLE_ID)
        {
            String sortOrder = "word ASC";
            String[] queryWord = {word};
            cursor = DatabaseHandler.dict_db.query(StaticInfo.E2M_CON_TABLE_NAME, StaticInfo.E2M_CON_COLUMNS, "word = ?", queryWord, null, null, null);
        }
        return cursor;
    }
    private void showWordData(String word_word, String word_phon, String word_def)
    {
        TextView textView_word = (TextView)findViewById(R.id.textView_word);
        TextView textView_phon = (TextView)findViewById(R.id.textView_phon);
        TextView textView_def = (TextView)findViewById(R.id.textView_def);
        textView_word.setText(word_word);
        textView_phon.setText(word_phon);
        textView_def.setText(word_def);
    }
    private void addHistory(int table_id, int word_id){
        String table_name = null;
        if(table_id == StaticInfo.M2E_TABLE_ID) {table_name = StaticInfo.M2E_HIST_TBL_NAME;}
        if(table_id == StaticInfo.E2M_TABLE_ID) {table_name = StaticInfo.E2M_HIST_TBL_NAME;}
        //When the word_id is already in History DB, delete it
        DatabaseHandler.hist_db.delete( table_name, "word_id = ?", new String[]{ "" + word_id } );

        ContentValues val = new ContentValues();
        val.put( "word_id", word_id);
        //Insert the _id of the word searched inn History DB
        DatabaseHandler.hist_db.insert( table_name, null, val );
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
