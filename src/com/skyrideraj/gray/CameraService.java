package com.skyrideraj.gray;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.VideoView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;


public class CameraService extends Activity implements SurfaceHolder.Callback
{
      //a variable to store a reference to the Image View at the main.xml file
      //private ImageView iv_image;
      //a variable to store a reference to the Surface View at the main.xml file
    private SurfaceView sv;
    private Bitmap mFaceBitmap;
	private int mFaceWidth = 200;
	private int mFaceHeight = 200;   
	private static final int MAX_FACES = 10;
	private static String TAG = "GrayRoutes";
	private YouTubePlayerView ytpv;
	private YouTubePlayer ytp;
   
    //a bitmap to display the captured image
      private Bitmap bmp;
      private TextView tv;
     
      //Camera variables
      //a surface holder
      private SurfaceHolder sHolder; 
      //a variable to control the camera
      private Camera mCamera;
      //the camera parameters
      private Parameters parameters;
      private String videoid;

    private Button b1,b2; 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VideoView videoView = (VideoView)findViewById(R.id.videoView1);

        MediaController mediaController = new MediaController(this);
         mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        Uri video = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.vid1);
        videoView.setVideoURI(video);
        videoView.start();
        videoid="video1";
        tv=(TextView) findViewById(R.id.textView1);
        tv.setText("Game of thrones trailer 1");
        b1=(Button) findViewById(R.id.button1);
        b2=(Button) findViewById(R.id.button2);
        //videoView.setVideoPath("/Gray/res/raw/vid.mp4");
        b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				videoid="video1";
				tv.setText("Game of thrones trailer 1");
				Uri videouri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.vid1);
				VideoView view2 = (VideoView)findViewById(R.id.videoView1);
				view2.setVideoURI(videouri);
		        view2.start();
				
			}
		});
        b2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				videoid="video2";
				tv.setText("Pink Floyd Another brick");
				Uri videouri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.vid2);
				VideoView view2 = (VideoView)findViewById(R.id.videoView1);
		        view2.setVideoURI(videouri);
				view2.start();
				
			}
		});
        
        

        //videoView.start();
     
        sv = (SurfaceView) findViewById(R.id.surfaceView1);
       
        //Get a surface
        sHolder = sv.getHolder();
       
        //add the callback interface methods defined below as the Surface   View callbacks
        sHolder.addCallback(this);
       
        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


      public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
      {
             //get camera parameters
             parameters = mCamera.getParameters();
             
             //set camera parameters
           mCamera.setParameters(parameters);
           mCamera.startPreview();
          
           //sets what code should be executed after the picture is taken
           
          
           	Timer myTimer = new Timer();
     	  	myTimer.scheduleAtFixedRate(new TimerTask(){
     		  @Override
     		  public void run(){
     			 Camera.PictureCallback mCall = new Camera.PictureCallback()
     	           {
     	           
     	             public void onPictureTaken(byte[] data, Camera camera)
     	             {
     	                   //decode the data obtained by the camera into a Bitmap
     	                   bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
     	                   //set the iv_image
     	                   //iv_image.setImageBitmap(bmp);
     	                  int width= bmp.getWidth();
     	                  int height=bmp.getHeight();
     	                  Matrix matrix=new Matrix();
     	                  matrix.postRotate(270);
     	                  Bitmap br = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
     	                  Bitmap bs = Bitmap.createScaledBitmap(br, 200, 200, false);
     	                  
     	                  mFaceBitmap = bs.copy(Bitmap.Config.RGB_565, true); 
     	                  bs.recycle();
     	                  br.recycle();
     	                  mFaceWidth = mFaceBitmap.getWidth();
     	                  mFaceHeight = mFaceBitmap.getHeight();  
     	                  // perform face detection and set the feature points
     	                  int no_faces = setFace();
     	                  System.out.println("No of faces : "+no_faces);
     	                
     	                  
     	                  
     	                   FileOutputStream outStream = null;
     	                  String filename = "/sdcard/Image"+System.currentTimeMillis()+".jpg";
     	                        try{
     	                        	
     	                            outStream = new FileOutputStream(filename);
     	                            outStream.write(data);
     	                            outStream.close();
     	                        } catch (FileNotFoundException e){
     	                            Log.d("CAMERA", e.getMessage());
     	                        } catch (IOException e){
     	                            Log.d("CAMERA", e.getMessage());
     	                        }
     	                        
     	                        
     	                       try
     	     	                {
     	     	                    File root = Environment.getExternalStorageDirectory();
     	     	                    File gpxfile = new File(root, "gray.csv");
     	     	                    FileWriter writer = new FileWriter(gpxfile,true);
     	     	                    SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
     	     	                    String format = s.format(new Date());
     	     	                    writer.append("Timestamp");
   	     	                    	writer.append(',');
     	     	                    writer.append("VideoID");
     	     	                    writer.append(',');
     	    	                    writer.append("No of Faces");
     	    	                    writer.append(',');
    	    	                    writer.append("Snap Path");
     	     	                    writer.append('\n');

     	     	                    writer.append(format);
     	     	                  	writer.append(',');
     	     	                    writer.append(videoid);
     	     	                    writer.append(',');
     	     	                    writer.append(""+no_faces);
     	     	                    writer.append(',');
     	     	                    writer.append(filename);
     	     	                    writer.append('\n');

     	     	                    //generate whatever data you want

     	     	                    writer.flush();
     	     	                    writer.close();
     	     	                }
     	     	                catch(IOException e)
     	     	                {
     	     	                    e.printStackTrace();
     	     	                } 
     	             }
     	           };
     	           try{
     	        	   mCamera.takePicture(null, null, mCall);
     	           }catch (NullPointerException e){
     	        	  Log.d("CameraService", "Task is being disposed!");
                   }
     		  }
     	  }, 0, 10000);
           //Set the time interval in milliseconds here for eg is 10 seconds
      }


      public int setFace() {
  		FaceDetector fd;
  		FaceDetector.Face [] faces = new FaceDetector.Face[MAX_FACES];
  		PointF midpoint = new PointF();
  		int [] fpx = null;
  		int [] fpy = null;
  		int count = 0;

  		try {
  			fd = new FaceDetector(mFaceWidth, mFaceHeight, MAX_FACES);        
  			count = fd.findFaces(mFaceBitmap, faces);
  			Log.d(TAG, "faces :" +count);
  			
  		} catch (Exception e) {
  			Log.e(TAG, "setFace(): " + e.toString());
  			return 0;
  		}
  		return count;
  		//return the no of faces detected
  		

  		
  	}
      
      public void surfaceCreated(SurfaceHolder holder)
      {
           
    	  Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
          Log.d("No of cameras",Camera.getNumberOfCameras()+"");
          for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
              CameraInfo camInfo = new CameraInfo();
              Camera.getCameraInfo(camNo, camInfo);
             
              if (camInfo.facing==(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                  mCamera = Camera.open(camNo);
              }
          }
          if (mCamera == null) {
             // no front-facing camera, use the first back-facing camera instead.
             // or display an error!
               mCamera = Camera.open();
          }
        
          
          
        try {
           mCamera.setPreviewDisplay(holder);
           
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
      }


      public void surfaceDestroyed(SurfaceHolder holder)
      {
            //stop the preview
            mCamera.stopPreview();
            //release the camera
        mCamera.release();
        //unbind the camera from this object
        mCamera = null;
      }





}
