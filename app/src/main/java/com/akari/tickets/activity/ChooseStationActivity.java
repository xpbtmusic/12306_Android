package com.akari.tickets.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akari.tickets.R;
import com.akari.tickets.adapter.StationAdapter;
import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;

import java.util.HashMap;

public class ChooseStationActivity extends AppCompatActivity implements OnQuickSideBarTouchListener {

    private ListView listView;
//    private RecyclerView recyclerView;
    private QuickSideBarTipsView quickSideBarTipsView;
    private QuickSideBarView quickSideBarView;
    private HashMap<String, Integer> map;
    private TextView divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_station);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        listView = (ListView) findViewById(R.id.list_view);
        quickSideBarTipsView = (QuickSideBarTipsView) findViewById(R.id.quickSideBarTipsView);
        quickSideBarView = (QuickSideBarView) findViewById(R.id.quickSideBarView);
        divider = (TextView) findViewById(R.id.divider);

//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new StationAdapter1());
        listView.setAdapter(new StationAdapter(this));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                showHeader(firstVisibleItem);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView clickTextView = (TextView) view.findViewById(R.id.text_view);
                Intent intent = new Intent();
                intent.putExtra("station", clickTextView.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        map = new HashMap<>();
        putValue();
        quickSideBarView.setOnQuickSideBarTouchListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        if(map.containsKey(letter)) {
//            recyclerView.scrollToPosition(map.get(letter));
            listView.setSelection(map.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        quickSideBarTipsView.setVisibility(touching? View.VISIBLE:View.INVISIBLE);
    }

    private void showHeader(int firstVisibleItem) {
        if (firstVisibleItem < 33) {
            divider.setText("热门车站");
        }
        else if (firstVisibleItem < 39 + 33) {
            divider.setText("A");
        }
        else if (firstVisibleItem < 135 + 33) {
            divider.setText("B");
        }
        else if (firstVisibleItem < 241 + 33) {
            divider.setText("C");
        }
        else if (firstVisibleItem < 385 + 33) {
            divider.setText("D");
        }
        else if (firstVisibleItem < 398 + 33) {
            divider.setText("E");
        }
        else if (firstVisibleItem < 455 + 33) {
            divider.setText("F");
        }
        else if (firstVisibleItem < 551 + 33) {
            divider.setText("G");
        }
        else if (firstVisibleItem < 736 + 33) {
            divider.setText("H");
        }
        else if (firstVisibleItem < 860 + 33) {
            divider.setText("J");
        }
        else if (firstVisibleItem < 892 + 33) {
            divider.setText("K");
        }
        else if (firstVisibleItem < 1073 + 33) {
            divider.setText("L");
        }
        else if (firstVisibleItem < 1141 + 33) {
            divider.setText("M");
        }
        else if (firstVisibleItem < 1211 + 33) {
            divider.setText("N");
        }
        else if (firstVisibleItem < 1277 + 33) {
            divider.setText("P");
        }
        else if (firstVisibleItem < 1355 + 33) {
            divider.setText("Q");
        }
        else if (firstVisibleItem < 1375 + 33) {
            divider.setText("R");
        }
        else if (firstVisibleItem < 1583 + 33) {
            divider.setText("S");
        }
        else if (firstVisibleItem < 1690 + 33) {
            divider.setText("T");
        }
        else if (firstVisibleItem < 1799 + 33) {
            divider.setText("W");
        }
        else if (firstVisibleItem < 1969 + 33) {
            divider.setText("X");
        }
        else if (firstVisibleItem < 2153 + 33) {
            divider.setText("Y");
        }
        else {
            divider.setText("Z");
        }
    }

    private void putValue() {
        map.put("A", 33);
        map.put("B", 39 + 33);
        map.put("C", 135 + 33);
        map.put("D", 241 + 33);
        map.put("E", 385 + 33);
        map.put("F", 398 + 33);
        map.put("G", 455 + 33);
        map.put("H", 551 + 33);
        map.put("I", 736 + 33);
        map.put("J", 736 + 33);
        map.put("K", 860 + 33);
        map.put("L", 892 + 33);
        map.put("M", 1073 + 33);
        map.put("N", 1141 + 33);
        map.put("O", 1211 + 33);
        map.put("P", 1211 + 33);
        map.put("Q", 1277 + 33);
        map.put("R", 1355 + 33);
        map.put("S", 1375 + 33);
        map.put("T", 1583 + 33);
        map.put("U", 1690 + 33);
        map.put("V", 1690 + 33);
        map.put("W", 1690 + 33);
        map.put("X", 1799 + 33);
        map.put("Y", 1969 + 33);
        map.put("Z", 2153 + 33);
    }
}
