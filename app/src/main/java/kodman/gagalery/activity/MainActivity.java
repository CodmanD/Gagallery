package kodman.gagalery.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.app.FragmentTransaction;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import kodman.gagalery.R;
import kodman.gagalery.adapter.GalleryAdapter;
import kodman.gagalery.app.AppController;
import kodman.gagalery.model.Image;


public class MainActivity extends AppCompatActivity {

    private static final String TAG=MainActivity.class.getSimpleName();
    private static final String apiKey="7181142-317827c02044bd32a17d15378";
    private static  String  tag="red";
    private static final String endPoint="https://api.androidhive.info/json/glide.json";
//    private static final String endPoint="https://pixabay.com/api/?key="+apiKey+"&q=yellow+flowers&image_type=photo";
   private static  String fromPixabay="https://pixabay.com/api/?key="+apiKey+"&q="+tag+"&image_type=photo";

   // private static final String endPoint="https://pixabay.com/api/?key=7181142-317827c02044bd32a17d15378&id=11574";
    private ArrayList<Image>images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;


    public static int status=0;
    private Menu menu;

   public final static int STATUS_GALLERY = 0;
    public final static int STATUS_CAMERA = 1;
    public final static int STATUS_PIXABAY = 2;
    FragmentManager  fragmentManager = this.getFragmentManager();

    private final static int MY_PERMISSIONS_CAMERA = 99;

    File directory;
    final int TYPE_PHOTO = 1;
    final int TYPE_VIDEO = 2;

    final int REQUEST_CODE_PHOTO = 1;
    final int REQUEST_CODE_VIDEO = 2;

Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.rv);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);





         recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();

                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
              //  Toast.makeText(getApplicationContext(),"Touch",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        showGallery();
      // fetchImages();
    }

    private void fetchImages() throws Exception {

        images.clear();
        Log.d(TAG,"---------0");
        pDialog.setMessage("Downloading json...");

        Log.d(TAG,"---------1");
        pDialog.show();

       final JSONObject object= new JSONObject();
       // object.getJSONArray()
        fromPixabay="https://pixabay.com/api/?key="+apiKey+"&q="+tag+"&image_type=photo";

        JsonObjectRequest req=new JsonObjectRequest (Request.Method.GET, fromPixabay, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse( JSONObject response) {
                Log.d(TAG,"Response: " + response.toString());


                try{
                    JSONArray objectArray= response.getJSONArray("hits");



                    for(int i=0;i<objectArray.length();i++)
                    {
                    JSONObject  objectImage = objectArray.getJSONObject(i);
                        Image image = new Image();
                        String name=objectImage.getString("previewURL");
                        String tag=objectImage.getString("webformatURL");
                        Log.d(TAG,"NAME : "+name);
                        Log.d(TAG,"TAG : "+tag);
                        image.setName(name);
                        image.setTag(tag);
                   // String strMedium=objectImage.getString("previewURL");
                   // String strLarge=objectImage.getString("webformatURL");
                    image.setSmall(objectImage.getString("previewURL"));
                    image.setMedium(objectImage.getString("previewURL"));
                    image.setLarge(objectImage.getString("webformatURL"));
                   // Log.d(TAG,"med= "+strMedium);
                    images.add(0,image);
                    }
                    mAdapter.notifyDataSetChanged();
                   // pDialog.hide();
                    pDialog.dismiss();

                }
                    catch(JSONException ex)
                {
                    Log.d(TAG,"JSONException oops..."+ex.getMessage());
                    pDialog.hide();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
               // pDialog.hide();
                pDialog.dismiss();
            }
        });


        // Adding request to request queue
        AppController.getmInstatnce().addToRequestQueue(req);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;

        //Change colour for selected icon
        if (Build.VERSION.SDK_INT >= 21) {
            for (int i = 0; i < menu.size(); i++) {
               MenuItem mItem = menu.getItem(i);
                Drawable icon = mItem.getIcon();
                if (status == i)
                    icon.setTint(getResources().getColor(R.color.colorActiveIcon));
                else
                    icon.setTint(getResources().getColor(R.color.colorNoActiveIcon));
            }
        }
        Log.d(TAG,"------------------------STATUS = "+status);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {




        int id = item.getItemId();
        switch (id) {
            case R.id.action_gallery:
                status=this.STATUS_GALLERY;
                showGallery();
break;
            case R.id.action_camera:
                status=this.STATUS_CAMERA;
/*
if(ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA )!= PackageManager.PERMISSION_GRANTED )
{

    // Should we show an explanation?
    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.CAMERA)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

    } else {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_CAMERA);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
    }
}

         */
                createDirectory();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               // intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                startActivityForResult(intent, REQUEST_CODE_PHOTO);


                Toast.makeText(this,"CAMERA",Toast.LENGTH_SHORT).show();
//                this.status = 0;
//                this.setContentView(R.layout.activity_main);
//                toolbar = this.findViewById(R.id.toolBar_MainActivity);
//
//                toolbar.setTitle(MainActivity.actualTime);
//                this.setSupportActionBar(toolbar);
//                this.addToGridViewButtonsActivity();
//                MainActivity.this.lvActivity.setAdapter(MainActivity.this.adapterListLogActivity);
//                this.createActivityLog();

                break;
            case R.id.action_pixabay:
                status=this.STATUS_PIXABAY;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter TAG for search images");
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.fragment_pixabay_tag, null);
                final EditText editText = view.findViewById(R.id.etTag);
                editText.setText(this.tag);
                builder.setView(view)
                        .setCancelable(true)
                        .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                EditText editText = view.findViewById(R.id.etTag);
                                MainActivity.tag = editText.getText().toString();

                                if(MainActivity.tag!="")
                                    try{
                                fetchImages();
                                }
                                catch (Exception ex)
                                {
                                    Toast.makeText(MainActivity.this,"Error Downloading",Toast.LENGTH_SHORT).show();
                                }
                                //checking the button for uniqueness

                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "create",
                                            Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                status=STATUS_GALLERY;
                                dialog.dismiss();
                            }
                        });
                builder.create().show();

                Toast.makeText(this,"Pixabay",Toast.LENGTH_SHORT).show();
                break;

        }

        if (Build.VERSION.SDK_INT >= 21) {
            for (int i = 0; i < this.menu.size(); i++) {
                MenuItem mItem = this.menu.getItem(i);
                Drawable icon = mItem.getIcon();
                if (status == i)
                    icon.setTint(getResources().getColor(R.color.colorActiveIcon));
                else
                    icon.setTint(getResources().getColor(R.color.colorNoActiveIcon));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Uri generateFileUri(int type) {
        File file = null;


                file = new File(directory.getPath() + "/" + "photo_"
                        + System.currentTimeMillis() + ".jpg");
        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyFolder");
        if (!directory.exists())
            directory.mkdirs();
    }

    private void showGallery()
    {

        images.clear();

          // String[] proj = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };

           // for(String s :proj)
             //   Log.d(TAG,s);

            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader loader = new CursorLoader(this, MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, proj, null, null, null);
            Cursor cursorThumnail = loader.loadInBackground();
             loader = new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
            Cursor cursorFullSize = loader.loadInBackground();


        //    Log.d(TAG,"Count="+cursor.getCount());
         while(cursorThumnail.moveToNext()&&cursorFullSize.moveToNext())
        {

            String name=cursorFullSize.getString(cursorFullSize.getColumnIndex("_data"));

            Image i=new Image();
            i.setName(name);
            i.setTag(name);
            i.setSmall(cursorThumnail.getString(cursorThumnail.getColumnIndex("_data")));
            i.setMedium(cursorFullSize.getString(cursorFullSize.getColumnIndex("_data")));
            i.setLarge(cursorFullSize.getString(cursorFullSize.getColumnIndex("_data")));
            images.add(0,i);
        }

        mAdapter.notifyDataSetChanged();

        }

}
