package com.jocv.taroogura.marshallese_dictionary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends SetActionBar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DatabaseHandler.dict_db ==null || DatabaseHandler.hist_db==null)
            //initialize Dictionary DB and History DB
            DatabaseHandler.setDatabase(this);
        setContentView(R.layout.activity_main);
        initActionBar();
        showHistory();
    }
    private void showHistory(){
        //show the history of Marshallese to English search
        Cursor m2eHistListCursor = getHistDict(StaticInfo.M2E_TABLE_ID);
        if(m2eHistListCursor.getCount()>0){
            String[] m2eHistStrArray = histcursor2str(m2eHistListCursor);            //strHistArray{[0]:word_id list, [1]:query order statement}
            Cursor m2eHistWordCursor = findHistWordinDict(StaticInfo.M2E_TABLE_ID, m2eHistStrArray);
            showHistoryList(StaticInfo.M2E_TABLE_ID, m2eHistWordCursor);
        }
        //show the history of English to Marshallese search
        Cursor e2mHistListCursor = getHistDict(StaticInfo.E2M_TABLE_ID);
        if(e2mHistListCursor.getCount()>0){
            String[] e2mHistStrArray  = histcursor2str(e2mHistListCursor);              //strHistArray{[0]:word_id list, [1]:query order statement}
            Cursor e2mHistWordCursor = findHistWordinDict(StaticInfo.E2M_TABLE_ID, e2mHistStrArray);
            showHistoryList(StaticInfo.E2M_TABLE_ID, e2mHistWordCursor);
        }
    }
    private Cursor getHistDict(final int table_id)
    {
        String table_name = null;
        if(table_id == StaticInfo.M2E_TABLE_ID) {table_name = StaticInfo.M2E_HIST_TBL_NAME;}
        if(table_id == StaticInfo.E2M_TABLE_ID) {table_name = StaticInfo.E2M_HIST_TBL_NAME;}
        String[] queryColumns = {"_id", "word_id"};
        String sortOrder = "_id DESC";
        String limitStatements = "" + StaticInfo.HISTORY_SHOW_LIMIT;            //limit the number of history records shown on the list
        Cursor cursor = DatabaseHandler.hist_db.query(table_name, queryColumns, null, null, null, null, sortOrder, limitStatements);
        return cursor;
    }
    private String[] histcursor2str(Cursor cursor){
        String result = "";                                   //word_id(_id in dictionary DB) list for select sql to dictionary DB
        String histOrderStatements = "case _id ";           //order statements for select sql to dictionary DB
        int word_id = cursor.getColumnIndex( "word_id" );
        int id;             //word_id(_id in dictionary DB) of history DB
        int i = 0;          //order of the word on the list

        cursor.moveToFirst();
        id  = cursor.getInt( word_id );
        result += id;
        i += 1;
        histOrderStatements += " when " + id + " then " + i;
        while( cursor.moveToNext()){
            id  = cursor.getInt( word_id );
            result += ", " + id;
            i += 1;
            histOrderStatements += " when " + id + " then " + i;
        }
        histOrderStatements += " end";

        return  new String[]{result, histOrderStatements};
    }
    private Cursor findHistWordinDict(final int table_id, String[] histStrArray)
    {
        String table_name = null;
        String [] columns = null;
        if(table_id == StaticInfo.M2E_TABLE_ID) {
            table_name = StaticInfo.M2E_TABLE_NAME;
            columns = StaticInfo.M2E_COLUMNS;
        }
        if(table_id == StaticInfo.E2M_TABLE_ID) {
            table_name = StaticInfo.E2M_TABLE_NAME;
            columns = StaticInfo.E2M_COLUMNS;
        }
        String queryWhere = " _id in (" + histStrArray[0] + ")";
        Cursor cursor = DatabaseHandler.dict_db.query(table_name, columns, queryWhere, null, null, null, histStrArray[1]);

        return  cursor;
    }
    private void showHistoryList(final int table_id,  Cursor cursor)
    {
        cursor.moveToFirst();
        ListAdapter adapter = null;
        ListView lv = null;
        if(table_id == StaticInfo.M2E_TABLE_ID) {
            adapter = new SimpleCursorAdapter(this, R.layout.list_item_m2e, cursor,
                    new String[]{"_id", "word", "phon", "def"},
                    new int[]{R.id.res_id, R.id.res_word, R.id.res_phon, R.id.res_def});
            lv = (ListView) findViewById(R.id.listView_historyM2E);
        }else if(table_id == StaticInfo.E2M_TABLE_ID) {
            adapter = new SimpleCursorAdapter(this, R.layout.list_item_e2m, cursor,
                    new String[]{"_id", "word", "def"},
                    new int[]{R.id.res_id, R.id.res_word, R.id.res_def});
            lv = (ListView) findViewById(R.id.listView_historyE2M);
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
    }
    @Override
    protected void onDestroy() {
//        DatabaseHandler.dict_db.close();  ->to prevent IOexception error when you push back button at home activity
//        DatabaseHandler.hist_db.close();  ->to prevent IOexception error when you push back button at home activity
        super.onDestroy();
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
