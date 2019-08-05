package luculent.net.cleanfactory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import net.luculent.http.ApiProxy;
import net.luculent.http.subscriber.SimpleSubscriber;

import luculent.net.cleanfactory.bean.TodayVideoBean;
import luculent.net.cleanfactory.http.OpenApi;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView resultTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListener();
    }

    private void initListener() {
        resultTxt = (TextView) findViewById(R.id.request_result);
        findViewById(R.id.make_request).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.make_request:
                ApiProxy.newProxyInstance(OpenApi.class)
                        .todayVideo(null)
                        .clazzConverter(TodayVideoBean.class)
                        .subscribe(new SimpleSubscriber<TodayVideoBean>(){
                            @Override
                            public void onNext(TodayVideoBean todayVideoBean) {
                                resultTxt.setText(todayVideoBean.getMessage());
                            }
                        })
                ;
                break;
        }
    }
}
