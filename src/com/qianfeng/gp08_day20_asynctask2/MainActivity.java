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
 * ��ȡ�����ϵ�ͼƬ
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
		dialog.setTitle("����");
		dialog.setMessage("����Ŭ������......");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}
	public void downLoad(View v)
	{
		new DownImageAsyncTask().execute(path);
	}
	
	//ʵ��ͼƬ���ص��첽����
	//�ڶ�����������ʾ���ȵ�ֵ������
	class DownImageAsyncTask extends AsyncTask<String,Integer,byte[]>
	{
		/**
		 * �÷��������߳�ִ�е�, ��doInBackground ����֮ǰִ��
		 */
		@Override
		protected void onPreExecute() {
			dialog.show();//��ʾ���ضԻ���
		}

		@Override
		protected byte[] doInBackground(String... params) {
			InputStream inputStream = null;
			ByteArrayOutputStream bos = null;
			if(params[0]!=null){
				HttpGet get = new HttpGet(params[0]);
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = null;
				long current_len =0;//��¼��ǰ��������
				try {
					response = client.execute(get);
					if(response.getStatusLine().getStatusCode()==200)
					{
						inputStream = response.getEntity().getContent();
						//��ȡ���ص��ļ����ܴ�С
						long total_size = response.getEntity().getContentLength();
						bos = new ByteArrayOutputStream();
						byte[] arr = new byte[1024];
						int len = 0;
						while((len = inputStream.read(arr))!=-1)
						{
							bos.write(arr,0,len);
							current_len+=len;
							int progress = (int)((current_len/(float)total_size)*100);
							//��������,ִ���˸÷����ͻ�ȥִ�лص�����  onProgressUpdate()
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
			//�������ʱ���رնԻ���
			dialog.dismiss();
		}
		
		//��������publishProgress�ͻ�ص��÷���
		//�÷��������߳�ִ��
		//values���յ���publishProgress(int)���������Ľ���ֵ 
		@Override
		protected void onProgressUpdate(Integer... values) {
			dialog.setProgress(values[0]);
		}
		
	}

	

}




