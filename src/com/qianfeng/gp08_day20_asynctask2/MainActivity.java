package com.qianfeng.gp08_day20_asynctask2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

/**
 * 
 * 获取网络上的图片
 *
 */
public class MainActivity extends Activity {
	public static final String  path = "http://news.fdc.com.cn/newsimageupload/307117/23.jpg";
    private ImageView imageView;
    private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		imageView = (ImageView) findViewById(R.id.imageView);
		
		dialog = new ProgressDialog(this);
		dialog.setTitle("下载");
		dialog.setMessage("正在努力加载......");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}
	public void downLoad(View v)
	{
		new DownImageAsyncTask().execute(path);
	}
	
	//实现图片下载的异步任务
	//第二个泛型是显示进度的值的类型
	class DownImageAsyncTask extends AsyncTask<String,Integer,byte[]>
	{
		/**
		 * 该方法由主线程执行的, 在doInBackground 方法之前执行
		 */
		@Override
		protected void onPreExecute() {
			dialog.show();//显示下载对话框
		}

		@Override
		protected byte[] doInBackground(String... params) {
			InputStream inputStream = null;
			ByteArrayOutputStream bos = null;
			if(params[0]!=null){
				HttpGet get = new HttpGet(params[0]);
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = null;
				long current_len =0;//记录当前的下载量
				try {
					response = client.execute(get);
					if(response.getStatusLine().getStatusCode()==200)
					{
						inputStream = response.getEntity().getContent();
						//获取下载的文件的总大小
						long total_size = response.getEntity().getContentLength();
						bos = new ByteArrayOutputStream();
						byte[] arr = new byte[1024];
						int len = 0;
						while((len = inputStream.read(arr))!=-1)
						{
							bos.write(arr,0,len);
							current_len+=len;
							int progress = (int)((current_len/(float)total_size)*100);
							//发布进度,执行了该方法就回去执行回调方法  onProgressUpdate()
							publishProgress(progress);
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					if(inputStream!=null)
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return bos.toByteArray();
		}
		
		@Override
		protected void onPostExecute(byte[] result) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
			imageView.setImageBitmap(bitmap);
			//下载完成时，关闭对话框
			dialog.dismiss();
		}
		
		//当调用了publishProgress就会回调该方法
		//该方法由主线程执行
		//values接收的是publishProgress(int)方法发布的进度值 
		@Override
		protected void onProgressUpdate(Integer... values) {
			dialog.setProgress(values[0]);
		}
		
	}

	

}




