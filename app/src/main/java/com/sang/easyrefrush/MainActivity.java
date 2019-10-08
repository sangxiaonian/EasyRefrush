package com.sang.easyrefrush;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sang.refrush.adapter.XAdapter;
import com.sang.refrush.holder.BaseHolder;
import com.sang.refrush.utils.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        recyclerView=findViewById(R.id.recycler_view);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        List<String> date=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            date.add("我是数据"+i);
        }
        recyclerView.setAdapter(new XAdapter<String>(this,date) {
            @Override
            public BaseHolder<String> initHolder(ViewGroup parent, int viewType) {
                return new BaseHolder<String>(context,parent,R.layout.item_single_text){
                    @Override
                    public void initView(View itemView, int position, String data) {
                        super.initView(itemView, position, data);
                        TextView tv = itemView.findViewById(R.id.tv_text);
                        tv.setText(data);

                    }
                };
            }
        });
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
    }
}
