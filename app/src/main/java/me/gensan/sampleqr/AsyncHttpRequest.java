package me.gensan.sampleqr;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsyncHttpRequest extends AsyncTask<String, Void, String> {

    private Activity mainActivity;

    public AsyncHttpRequest(Activity activity) {

        // 呼び出し元のアクティビティ
        this.mainActivity = activity;
    }

    // このメソッドは必ずオーバーライドする必要があるよ
    // ここが非同期で処理される部分みたいたぶん。
    @Override
    protected String doInBackground(String... params) {
        // httpリクエスト投げる処理を書く。
        // ちなみに私はHttpClientを使って書きましたー
        String url = params[0];
        return _loadXml(url);
    }

    // 以下、プライベート ----------------------------------------
    private String _loadXml(String url){
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        String resultString = ""; // 通信結果
        try{
            HttpResponse response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();
            // 失敗してたら例外
            if (status != HttpStatus.SC_OK) throw new Exception("Error!");

            InputStream ins = response.getEntity().getContent();
            resultString = inputStreamToString(ins);
        }catch(Exception e){
            resultString = "読み込みに失敗しました。:" + e.toString();
        }

        // 確認
        return resultString;
    }

    // InputStream から 文字列への変換
    private static String inputStreamToString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuffer buf = new StringBuffer();
        String str;
        while ((str = reader.readLine()) != null) {
            buf.append(str);
            buf.append("\n");
        }
        return buf.toString();
    }

    // このメソッドは非同期処理の終わった後に呼び出されます
    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject rootObject = new JSONObject(result);
            String res = rootObject.getString("res");

            Toast.makeText(mainActivity.getApplicationContext(), res, Toast.LENGTH_LONG)
                    .show();
        }catch(JSONException e)
        {
        }

        // 取得した結果をテキストビューに入れちゃったり
//        TextView tv = (TextView) mainActivity.findViewById(R.id.name);
//        tv.setText(result)
    }
}
