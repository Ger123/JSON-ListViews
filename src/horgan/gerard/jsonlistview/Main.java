package horgan.gerard.jsonlistview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Main extends Activity {
	
	ListView videoList;
	ArrayList<String> videoArrayList = new ArrayList<String>();
	ArrayAdapter<String> videoAdapter;
	Context context;
	String feedUrl ="https://gdata.youtube.com/feeds/api/users/TwistedEquations/uploads?v=2&alt=jsonc&start-index=1&max-results=10";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		context = this;
		
		videoList=(ListView) findViewById(R.id.videoList);
		videoAdapter = new ArrayAdapter<String>(this, R.layout.video_list_item,videoArrayList);
		videoList.setAdapter(videoAdapter);
		
		VideoListTask loaderTask= new VideoListTask();
		loaderTask.execute();
		
		

		
	}
	
	public class VideoListTask extends AsyncTask<Void, Void, Void>{
		
		ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setTitle("Video Loading");
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			HttpClient client= new DefaultHttpClient();
			HttpGet getRequest= new HttpGet(feedUrl);
			try {
				
				HttpResponse response= client.execute(getRequest);
				StatusLine statusLine= response.getStatusLine();
				int statusCode= statusLine.getStatusCode();
				
				if(statusCode !=200){
					return null;
					}
				
				InputStream jsonStream= response.getEntity().getContent();
				BufferedReader reader=new BufferedReader(new InputStreamReader(jsonStream));
				StringBuilder builder= new StringBuilder();
				String line;
				while((line =reader.readLine())!=null){
					builder.append(line);
					
				}
				String jsonData =builder.toString();
				Log.i("JsonData",jsonData);
				
				
				JSONObject json= new JSONObject(jsonData);
				JSONObject data= json.getJSONObject("data");
				JSONArray items= data.getJSONArray("items");
				
				for(int i=0; i<items.length(); i++){
					
					
					JSONObject videos= items.getJSONObject(i);
					videoArrayList.add(videos.getString("title"));
					
				}
				
				
				
				
				
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
			
			}
		
		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
			super.onPostExecute(result);
		}
		
	}
		
}
