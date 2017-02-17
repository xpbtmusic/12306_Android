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

import com.akari.tickets.R;
import com.akari.tickets.adapter.StationAdapter;
import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;

import java.util.HashMap;

public class ChooseStationActivity extends AppCompatActivity implements OnQuickSideBarTouchListener {

    private ListView listView;
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

        listView = (ListView) findViewById(R.id.list_view);
        quickSideBarTipsView = (QuickSideBarTipsView) findViewById(R.id.quickSideBarTipsView);
        quickSideBarView = (QuickSideBarView) findViewById(R.id.quickSideBarView);
        divider = (TextView) findViewById(R.id.divider);

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
        else if (firstVisibleItem < 72) {
            divider.setText("A");
        }
        else if (firstVisibleItem < 168) {
            divider.setText("B");
        }
        else if (firstVisibleItem < 274) {
            divider.setText("C");
        }
        else if (firstVisibleItem < 418) {
            divider.setText("D");
        }
        else if (firstVisibleItem < 431) {
            divider.setText("E");
        }
        else if (firstVisibleItem < 488) {
            divider.setText("F");
        }
        else if (firstVisibleItem < 584) {
            divider.setText("G");
        }
        else if (firstVisibleItem < 769) {
            divider.setText("H");
        }
        else if (firstVisibleItem < 893) {
            divider.setText("J");
        }
        else if (firstVisibleItem < 925) {
            divider.setText("K");
        }
        else if (firstVisibleItem < 1106) {
            divider.setText("L");
        }
        else if (firstVisibleItem < 1174) {
            divider.setText("M");
        }
        else if (firstVisibleItem < 1244) {
            divider.setText("N");
        }
        else if (firstVisibleItem < 1310) {
            divider.setText("P");
        }
        else if (firstVisibleItem < 1388) {
            divider.setText("Q");
        }
        else if (firstVisibleItem < 1408) {
            divider.setText("R");
        }
        else if (firstVisibleItem < 1616) {
            divider.setText("S");
        }
        else if (firstVisibleItem < 1723) {
            divider.setText("T");
        }
        else if (firstVisibleItem < 1832) {
            divider.setText("W");
        }
        else if (firstVisibleItem < 2002) {
            divider.setText("X");
        }
        else if (firstVisibleItem < 2186) {
            divider.setText("Y");
        }
        else {
            divider.setText("Z");
        }
    }

    private void putValue() {
        map.put("A", 33);
        map.put("B", 72);
        map.put("C", 168);
        map.put("D", 274);
        map.put("E", 418);
        map.put("F", 431);
        map.put("G", 488);
        map.put("H", 584);
        map.put("I", 769);
        map.put("J", 769);
        map.put("K", 893);
        map.put("L", 925);
        map.put("M", 1106);
        map.put("N", 1174);
        map.put("O", 1244);
        map.put("P", 1244);
        map.put("Q", 1310);
        map.put("R", 1388);
        map.put("S", 1408);
        map.put("T", 1616);
        map.put("U", 1723);
        map.put("V", 1723);
        map.put("W", 1723);
        map.put("X", 1832);
        map.put("Y", 2002);
        map.put("Z", 2186);
    }
}
