package kodman.gagalery.app;

import android.app.Application;
import android.app.DownloadManager;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by User on 11/25/2017.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController mInstatnce;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstatnce=this;
    }


    public static synchronized AppController getmInstatnce(){return mInstatnce;}

    public RequestQueue getRequestQueue()
    {
        if(mRequestQueue==null)
        {
            mRequestQueue= Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue( Request<T> req,String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
    public <T> void addToRequestQueue( Request<T> req) {
        req.setTag(TAG );
        getRequestQueue().add(req);
    }

    public void cancelPendingRequest(Object tag)
    {
        if(mRequestQueue!=null)
        {
            mRequestQueue.cancelAll(tag);
        }
    }
}

