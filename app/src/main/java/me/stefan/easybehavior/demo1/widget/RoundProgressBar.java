package me.stefan.easybehavior.demo1.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import me.stefan.easybehavior.R;


/**
 * 带进度的进度条，线程安全的View，可直接在线程中更新进度
 *
 * @author stefan 修正改进
 */
public class RoundProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否循环
     **/
    public boolean isSpinning = false;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_round_color, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_round_progressColor, Color.GREEN);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_round_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_round_textsize, 15);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_round_width, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_round_max, 360);

        mTypedArray.recycle();//必须Recycle
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        int centre = getWidth()/2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth/2); //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿


        if(isSpinning){
            /**
             * 画圆弧 ，画圆环的进度
             */
            //设置进度是实心还是空心
            paint.setStrokeWidth(roundWidth); //设置圆环的宽度
            paint.setColor(roundProgressColor);  //设置进度的颜色
            RectF oval = new RectF(centre - radius, centre - radius, centre
                    + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(oval, progress-90, 320 , false, paint);  //根据进度画圆弧
        }else{
            /**
             * 画圆弧 ，画圆环的进度
             */
            //设置进度是实心还是空心
            paint.setStrokeWidth(roundWidth); //设置圆环的宽度
            paint.setColor(roundProgressColor);  //设置进度的颜色
            RectF oval = new RectF(centre - radius, centre - radius, centre
                    + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(oval, -90, progress , false, paint);  //根据进度画圆弧
        }



    }


    /**
     * Reset the count (in increment mode)
     */
    public void resetCount() {
        progress = 0;
        invalidate();
    }

    /**
     * Turn off spin mode
     */
    public void stopSpinning() {
        isSpinning = false;
        progress = 0;
        spinHandler.removeMessages(0);
    }

    /**
     * Puts the view on spin mode
     */
    public void spin() {
        isSpinning = true;
        spinHandler.sendEmptyMessage(0);
    }

    /**
     * Increment the progress by 1 (of 360)
     */
    public void incrementProgress() {
        isSpinning = false;
        if (progress > 360)
            progress = 0;
        spinHandler.sendEmptyMessage(0);
    }

    private Handler spinHandler = new Handler() {
        /**
         * This is the code that will increment the progress variable
         * and so spin the wheel
         */
        @Override
        public void handleMessage(Message msg) {
            invalidate();
            if (isSpinning) {
                progress += 10;
                if (progress > 360) {
                    progress = 0;
                }
                spinHandler.sendEmptyMessageDelayed(0, 0);
            }
            //super.handleMessage(msg);
        }
    };

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }


}