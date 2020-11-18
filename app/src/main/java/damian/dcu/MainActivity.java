package damian.dcu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    String nums = "";
    TextView textView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] titles,links,dates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.number);

        final Bundle bundle = new Bundle();

        new Thread() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect("https://www.cu.ac.kr/plaza/notice/notice").get();
                    Elements contents;
                    contents = doc.select("div.board_list > table > tbody > tr");// > td > a");
                    nums += "https://www.cu.ac.kr/" + contents.select("td > a").attr("href") + "\n" + contents.select("td > a").first().ownText() + "  []  " + contents.next().select("td > a").first().ownText();
                    for(int i = 0;i<10;i++){
                        titles[i] = contents.select("td > a").first().ownText();
                        links[i] = contents.select("td > a").attr("href");
                        dates[i] = "날짜" + i;
                        contents.next();
                    }
                    bundle.putString("numbers", nums);
                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //리사이클러 뷰
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(titles, links, dates);
        recyclerView.setAdapter(mAdapter);
        //리사이클러 뷰
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            textView.setText(bundle.getString("numbers"));                      //이런식으로 View를 메인 쓰레드에서 뿌려줘야한다.
        }
    };
}