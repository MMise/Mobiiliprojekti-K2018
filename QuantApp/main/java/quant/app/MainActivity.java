package quant.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity{

    private Typeface type;
    private TextView txt;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        txt = findViewById(R.id.textView2);
        type = Typeface.createFromAsset(getAssets(), "fonts/HansKendrick-Regular.otf");
        txt.setTypeface(type);

        startCountdown();
    }

    public void startCountdown(){
        handler = new Handler();
        runnable = new Runnable(){

            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                SimpleDateFormat dFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                try {
                    Date launchDate = dFormat.parse("26-4-2018 15:00:00");
                    Date currentDate = new Date();
                    String parsedString;
                    if (!currentDate.after(launchDate)) {
                        long diff = launchDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        parsedString = String.format("%02d", days);
                        parsedString += "d ";
                        parsedString += String.format("%02d", hours);
                        parsedString += "h ";
                        parsedString += String.format("%02d", minutes);
                        parsedString += "m ";
                        parsedString += String.format("%02d", seconds);
                        parsedString += "s ";
                        txt.setText(parsedString);
                    }else{
                        txt.setText("UPDATE PENDING");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        };
        handler.postDelayed(runnable, 1000);
    }
}
