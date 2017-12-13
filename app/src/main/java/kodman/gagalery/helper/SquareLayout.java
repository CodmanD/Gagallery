package kodman.gagalery.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.jar.Attributes;

/**
 * Created by User on 11/25/2017.
 */

public class SquareLayout extends RelativeLayout implements View.OnLongClickListener
{
    public SquareLayout(Context context)
    {
        super(context);
        this.setOnLongClickListener(this);
    }

    public SquareLayout(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        this.setOnLongClickListener(this);
    }

    public SquareLayout(Context context, AttributeSet attrs,int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
        this.setOnLongClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareLayout(Context context, AttributeSet attrs,int defStyleAttr,int defStyleRes)
    {
        super(context, attrs,defStyleAttr,defStyleRes);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec,widthMeasureSpec);
    }


    @Override
    public boolean onLongClick(View v) {

        return false;
    }
}
