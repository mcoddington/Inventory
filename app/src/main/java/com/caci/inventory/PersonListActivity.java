package com.caci.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.caci.inventory.data.PeopleDbHelper;


public class PersonListActivity extends ActionBarActivity {

    private PeopleDbHelper peopleDbHelper;
    private SimpleCursorAdapter dataAdapter;
    private static final String[] columns = new String[] {
            PeopleDbHelper.COLUMN_NAME,
            PeopleDbHelper.COLUMN_LOCATION,
            PeopleDbHelper.COLUMN_ROOM
    };
    private static final int[] to = new int[] {
            R.id.name,
            R.id.location,
            R.id.room
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        peopleDbHelper = new PeopleDbHelper(this);

        displayListView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor cursor = peopleDbHelper.getAllPeopleCursor();
        dataAdapter.swapCursor(cursor);
        dataAdapter.notifyDataSetChanged();
    }

    private void displayListView() {
        Cursor cursor = peopleDbHelper.getAllPeopleCursor();

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.person_info,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                Intent intent = new Intent(PersonListActivity.this, PersonActivity.class);
                intent.putExtra("id", cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
                startActivity(intent);

            }
        });

//        EditText myFilter = (EditText) findViewById(R.id.myFilter);
//        myFilter.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                dataAdapter.getFilter().filter(s.toString());
//            }
//        });

//        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
//            public Cursor runQuery(CharSequence constraint) {
//                return dbHelper.fetchCountriesByName(constraint.toString());
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
