package com.akari.tickets.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        listView = (ListView) findViewById(R.id.list_view);
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
                Toast.makeText(ChooseStationActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });

        map = new HashMap<>();
        putValue();

        quickSideBarView.setOnQuickSideBarTouchListener(this);
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
        if (firstVisibleItem < 39) {
            divider.setText("A");
        }
        else if (firstVisibleItem < 135) {
            divider.setText("B");
        }
        else if (firstVisibleItem < 241) {
            divider.setText("C");
        }
        else if (firstVisibleItem < 385) {
            divider.setText("D");
        }
        else if (firstVisibleItem < 398) {
            divider.setText("E");
        }
        else if (firstVisibleItem < 455) {
            divider.setText("F");
        }
        else if (firstVisibleItem < 551) {
            divider.setText("G");
        }
        else if (firstVisibleItem < 736) {
            divider.setText("H");
        }
        else if (firstVisibleItem < 860) {
            divider.setText("J");
        }
        else if (firstVisibleItem < 892) {
            divider.setText("K");
        }
        else if (firstVisibleItem < 1073) {
            divider.setText("L");
        }
        else if (firstVisibleItem < 1141) {
            divider.setText("M");
        }
        else if (firstVisibleItem < 1211) {
            divider.setText("N");
        }
        else if (firstVisibleItem < 1277) {
            divider.setText("P");
        }
        else if (firstVisibleItem < 1355) {
            divider.setText("Q");
        }
        else if (firstVisibleItem < 1375) {
            divider.setText("R");
        }
        else if (firstVisibleItem < 1583) {
            divider.setText("S");
        }
        else if (firstVisibleItem < 1690) {
            divider.setText("T");
        }
        else if (firstVisibleItem < 1799) {
            divider.setText("W");
        }
        else if (firstVisibleItem < 1969) {
            divider.setText("X");
        }
        else if (firstVisibleItem < 2153) {
            divider.setText("Y");
        }
        else {
            divider.setText("Z");
        }
    }

    private void putValue() {
        map.put("A", 0);
        map.put("B", 39);
        map.put("C", 135);
        map.put("D", 241);
        map.put("E", 385);
        map.put("F", 398);
        map.put("G", 455);
        map.put("H", 551);
        map.put("I", 736);
        map.put("J", 736);
        map.put("K", 860);
        map.put("L", 892);
        map.put("M", 1073);
        map.put("N", 1141);
        map.put("O", 1211);
        map.put("P", 1211);
        map.put("Q", 1277);
        map.put("R", 1355);
        map.put("S", 1375);
        map.put("T", 1583);
        map.put("U", 1690);
        map.put("V", 1690);
        map.put("W", 1690);
        map.put("X", 1799);
        map.put("Y", 1969);
        map.put("Z", 2153);
    }
}
