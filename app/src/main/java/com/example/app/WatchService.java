package com.example.app;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app.api.CurrentApp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WatchService extends Service {
    private Handler handler = null;
    private Runnable runnable = null;
    public WatchService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String vid = intent.getStringExtra("Vid");
        final String host = intent.getStringExtra("Host");

        handler = new Handler();
        runnable = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                // execute tasks
                count++;
                Log.d("WatchService", "Count: " + count);

                CurrentApp usageApp = UsageStatCurrentReader.read(getApplicationContext(), vid);
                if(usageApp != null){
                    ApiRequest.postCurrentApps(usageApp, host);
                    Log.d("WatchService", "Time:" + usageApp.getTime() + " VId:" + usageApp.getVid() + " Apps:" + usageApp.getPackageName());
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("WatchService", "onDestroy");
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static class UsageStatCurrentReader {
        public static CurrentApp read(Context context, String vid){
            UsageStatsManager manager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);

            long time = System.currentTimeMillis();
            List<UsageStats> usageStats = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if(usageStats != null && usageStats.size() > 0) {
                SortedMap<Long, UsageStats> map = new TreeMap<Long, UsageStats>();
                for(UsageStats usageStat : usageStats) {
                    map.put(usageStat.getLastTimeUsed(), usageStat);
                }
                if(map != null && !map.isEmpty()) {
                    UsageStats usageStat = map.get(map.lastKey());
                    return new CurrentApp(getStringDate(time), vid, usageStat.getPackageName());
                }
            }
            return null;
        }

        private static String getStringDate(long milliseconds) {
            final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPANESE);
            final Date date = new Date(milliseconds);
            return df.format(date);
        }
    }

    public static class ApiRequest {
        public static void postCurrentApps(CurrentApp currentApps, String host) {
            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(currentApps);
                RequestBody body = RequestBody.create(JSON, json);
                Request req = new Request.Builder().url(host).post(body).build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                        Log.d("WatchService", response.body().string());
                        if(response != null){
                            response.close();
                        }
                    }
                });
            } catch (JsonProcessingException e ) {
                e.printStackTrace();
            }
        }
    }

    /*
    public static class UsageStatReader {
        public static List<UsageApps> read(Context context){
            UsageStatsManager manager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);

            List<UsageApps> ret = new ArrayList<UsageApps>();
            List<UsageStats> usageStats = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(), System.currentTimeMillis());
            for(UsageStats stat : usageStats) {
                if(stat.getTotalTimeInForeground() == 0) {
                    continue;
                }
                Log.d("WatchService", "[" + stat.getPackageName() + "][" + stat.getTotalTimeInForeground() + "]" + getStringDate(stat.getFirstTimeStamp()) + "---" + getStringDate(stat.getLastTimeStamp()));
                UsageApps usageApp = new UsageApps(stat.getPackageName(), String.valueOf(stat.getTotalTimeInForeground()), getStringDate(stat.getFirstTimeStamp()), getStringDate(stat.getLastTimeStamp()));
                ret.add(usageApp);
            }
            return ret;
        }

        private static String getStringDate(long milliseconds) {
            final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPANESE);
            final Date date = new Date(milliseconds);
            return df.format(date);
        }
    }
*/


}