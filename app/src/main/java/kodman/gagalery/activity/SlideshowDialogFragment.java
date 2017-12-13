package kodman.gagalery.activity;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import kodman.gagalery.R;
import kodman.gagalery.model.Image;


@TargetApi(16)
public class SlideshowDialogFragment extends DialogFragment  {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private Button btn;
    private MyViewPagerAdapter myViewPagerAdapter;
    final String DIR_SD = "MyPixabay";
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    private ImageView ivPreview;
    private android.view.ScaleGestureDetector scaleGestureDetector;
    //private ArrayList<android.view.ScaleGestureDetector>detectors;
    private ScaleGestureDetector[]detectors;
    //private static Context mContext;
    String fileName;

    View view;

    private static int  countView=0;


    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
 return f;
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView Fragment");

        countView=0;

       View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        //btn=v.findViewById(R.id.btnSave);




        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);

        images = (ArrayList<Image>) getArguments().getSerializable("images");


        Log.d(TAG,"CreateView ="+images.size());
        //detectors=new ArrayList<>(images.size());
        detectors=new ScaleGestureDetector[images.size()];
        Log.d(TAG,"CreateView ="+images.size()+"detectors = "+detectors.length);

        selectedPosition = getArguments().getInt("position");

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);





            btn= new Button(getContext());
        LinearLayout.LayoutParams lButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
            if(MainActivity.status==MainActivity.STATUS_PIXABAY)
            {
                btn.setText("SAVE");






            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveToFile();
                     //   Toast.makeText(getContext(),"Saved+"+ivPreview.getDrawable().toString(),Toast.LENGTH_SHORT).show();

                    //    Toast.makeText(getContext(),"No Saved +"+ivPreview.getDrawable().toString(),Toast.LENGTH_SHORT).show();
                }
            });
            }
            else
                {
                    btn.setText("DELETE");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //saveToFile();

                            String path=lblTitle.getTag().toString();
                            File fileDel=new File(path);
                            Log.d(TAG,"DALETE "+path);
                            if(fileDel.delete())
                                Toast.makeText(getContext(),"DELETE :",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(),"Can`t DELETE :",Toast.LENGTH_SHORT).show();
                            //   Toast.makeText(getContext(),"Saved+"+ivPreview.getDrawable().toString(),Toast.LENGTH_SHORT).show();

                            //    Toast.makeText(getContext(),"No Saved +"+ivPreview.getDrawable().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }

         RelativeLayout RLF=v.findViewById(R.id.RLF);
          RLF.addView(btn,lButtonParams);


        ObjectAnimator objectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(getContext(),R.animator.fragment_animation);
        objectAnimator.setTarget(v.findViewById(R.id.RLF));
        objectAnimator.start();
        view=v;
        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }




    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {

        Image image = images.get(position);
        lblTitle.setText(image.getName());
        lblTitle.setTag(image.getTag());
        lblDate.setText(image.getTimestamp());
        //Log.d(TAG,"---------------------------displayMetaInfo");
        lblCount.setText((position + 1) + " of " + images.size());
        //selectedPosition++;



    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }



    private boolean saveToFile()
    {

        Log.d(TAG,"--- For SAVE ------------------selectedPos"+myViewPagerAdapter.getCount());
      //Target<GlideDrawable> t

//
//



        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void ... params)
            {
                Looper.prepare();

                //TextView tv=view.findViewById(R.id.title);
                //  = tv.getText().toString();

                String uri=lblTitle.getTag().toString();
                 Log.d(TAG,"ASYNc ---------------   "+uri);
                Bitmap bitmap=null;
                try {

                     bitmap = Glide.with(getContext())
                    .load(uri)
                    .asBitmap()
                    .into(-1, -1)
                    .get();

        }
        catch(Exception ex)

                {
                    Log.d(TAG, "--- For SAVE EXCEPTION ------------------selectedPos" + ex.getMessage());
                }

                fileName=lblTitle.getText().toString();

                fileName=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());

              //  Log.d(TAG,"=========="+fileName);



                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
                    return null;
                }
                // получаем путь к SD
                File sdPath = Environment.getExternalStorageDirectory();
                // добавляем свой каталог к пути
                sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
                sdPath.mkdir();
                // формируем объект File, который содержит путь к файлу
                File sdFile = new File(sdPath, fileName);
                FileOutputStream fos=null;

                try {
                    fos = new FileOutputStream(sdFile);
                    Bitmap.CompressFormat format= Bitmap.CompressFormat.JPEG;
                    bitmap.compress(format,100,fos);
                    fos.close();
                    Toast.makeText(getContext(),"Saved :"+uri,Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"----------------------------------------------SAVED");
                    return null;
                }
                catch (IOException e)
                {
                    Toast.makeText(getContext(),"Save Error ",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"---------------------------------------------- ERROR SAVED");

                    if (fos != null)
                    {
                        try
                        {
                            fos.close();
                        }
                        catch (IOException e1)
                        {
                            e1.printStackTrace(); }
                    }
                }

                return null;
          }

          @Override
            protected void onPostExecute(Void dummy)

          {}
        }.execute();



        //myViewPagerAdapter.curIV.setDrawingCacheEnabled(true);
        //Bitmap bitmap = myViewPagerAdapter.curIV.getDrawingCache();;
      //  Log.d(TAG,"---SAVED ------------------ivPreview="+ivPreview+" Bitmap"+bitmap);
/*
        if (drawable instanceof BitmapDrawable )
        {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null)
            {
                bitmap=   bitmapDrawable.getBitmap();
            }
        }
*/


      //  if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
       //     bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
       // } else {
       //     bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
       // }


       // Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        //BitmapDrawable bitmapDrawable = (BitmapDrawable)ivPreview.getBackground();// drawable;
        //Bitmap bmp= bitmapDrawable.getBitmap();



        return false;
        //Toast.makeText(getContext()," Save filename = "+fileName,Toast.LENGTH_SHORT).show();
    }



        private ImageView ivSave;

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {


            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            final Image image = images.get(position);

          //  final TextView tv = view.findViewById(R.id);
           //final ImageView ivPreview = view.findViewById(R.id.image_preview);
            ivPreview = view.findViewById(R.id.image_preview);


            Log.d(TAG,"---Adapter SaY------------------ivPreview="+ivPreview);

            detectors[position]=activeScaleGestureDetector(ivPreview);


            Glide.with(getActivity())
                    .load(image.getLarge())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPreview);




            ivPreview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    if(   detectors[position].onTouchEvent(event))
                 {

                     Log.d(TAG,"Touch action = "+event.getAction()); ;
                 }
                    //scaleGestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            container.addView(view);

            return view;
        }



        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }





    public ScaleGestureDetector activeScaleGestureDetector(ImageView iv)
    {
        //  this.scaleGestureDetector= new ScaleGestureDetector(this.getContext(), new MySimpleOnScaleGestureListener(iv) );
        return new ScaleGestureDetector(this.getContext(), new MySimpleOnScaleGestureListener(iv) );
    }


    private class MySimpleOnScaleGestureListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        // TextView viewMessage;
        ImageView viewMyImage;

        float factor=1.0f;
        long lastTouch;

        public MySimpleOnScaleGestureListener( ) {
            super();
            //   viewMessage = v;
       //     viewMyImage = iv;
        }

        public void setImage(ImageView iv)
        {
            this.viewMyImage=iv;
        }


        public MySimpleOnScaleGestureListener( ImageView iv) {
            super();
            //   viewMessage = v;
            viewMyImage = iv;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //Toast.makeText(getContext(),"ScaleBegin",Toast.LENGTH_SHORT).show();
            //Log.d(TAG,"-------scaleGesture Begin");

           //factor  = 1.0f;
            Log.d(TAG,"-------scaleGesture scale  factor="+factor);
           // return true;
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {


           //if( detector.getCurrentSpan()<1)
               Log.d(TAG,"curSpan = "+detector.getCurrentSpan()+" time = "+detector.getTimeDelta());
             //  Toast.makeText(getContext(),"Scale Only Tuch",Toast.LENGTH_SHORT).show();
            float scaleFactor = detector.getScaleFactor() - 1;
            factor += scaleFactor;
            //      viewMessage.setText(String.valueOf(scaleFactor)
            //             + "\n" + String.valueOf(factor));
            viewMyImage.setScaleX(factor);
            viewMyImage.setScaleY(factor);

            Log.d(TAG,"-------scaleGesture scale ="+factor);
            //viewMyImage.invalidate();

//            if(detector.getEventTime()-lastTouch>1000)
//            {
//                Toast.makeText(getContext(),"Save",Toast.LENGTH_SHORT).show();
//
//            }
            lastTouch=detector.getEventTime();
            return true;
            //return super.onScale(detector);
        }
    }



}
