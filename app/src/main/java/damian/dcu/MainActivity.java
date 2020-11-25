package damian.dcu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String nums = "";
    TextView textView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] titles = new String[10],links = new String[10],dates = new String[10];

    ArrayList<DataModelMainActivity> data;
    GridLayoutManager gridLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //activity_main.xml 파일 불러오기 (레이아웃)
        textView = (TextView) findViewById(R.id.number);

        final Bundle bundle = new Bundle();
        new Thread() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect("https://www.cu.ac.kr/plaza/notice/notice").get(); // 학교홈페이지 연결
                    Elements contents;
                    contents = doc.select("div.board_list > table > tbody > tr");//공지사항 리스트          // > td > a");
                    /*
                    contents.select("td > a") -> n번째 리스트 에서 제목을 가리킴
                    contents.next() -> 다음번째로 이동



                    */
                    //nums += "https://www.cu.ac.kr/" + contents.select("td > a").attr("href") + "\n" + contents.select("td > a").first().ownText() + "  []  " + contents.next().select("td > a").first().ownText();
                    for(int i = 0;i<10;i++){ //공지사항 갯수만큼 반복
                        dates[i] = contents.select("td").next().next().next().next().first().ownText();//날짜
                        titles[i] = contents.select("td > a").first().ownText();//제목
                        links[i] = contents.select("td > a").attr("href");//링크

                        contents = contents.next();//다음번째로 이동
                    }
                    bundle.putStringArray("titles",titles);
                    bundle.putStringArray("links",links);
                    bundle.putStringArray("dates",dates);

                    bundle.putString("numbers", nums); //쓸모없는 코드들 (테스트 할때 쓰던거)

                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);







                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            //textView.setText(bundle.getString("numbers")); //테스트 할때 쓰는거
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
            recyclerView.setLayoutManager(gridLayoutManager);
            String titles2[]=bundle.getStringArray("titles");
            String links2[]=bundle.getStringArray("links");
            String dates2[]=bundle.getStringArray("dates");
            data = new ArrayList<DataModelMainActivity>();
            for (int i = 0; i < titles.length; i++) {
                data.add(new DataModelMainActivity(R.mipmap.ic_launcher, titles2[i], links2[i], dates2[i]));
            }
            mAdapter = new Adapter(data, MainActivity.this);
            recyclerView.setAdapter(mAdapter);
        }
    };
}