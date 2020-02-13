package com.github.leondevlifelog.gesturelockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * 手势解锁密码控件
 *
 * @author liang
 *         日期 2017-11-01 01:57:58
 */
@SuppressWarnings("unused")
public class GestureLockView extends View {
    private static final String TAG = "GestureLockView";
    private final static int DEFAULT_ROW = 3;

    private final static int DEFAULT_COL = 3;
    private static final int PORTRAIT = 1;
    private static final int LANDSCAPE = 0;
    private Context mContext;
    private Paint dotPaint;
    private int paddingLeft;
    private int contentWidth;
    private int contentHeight;
    private int row;
    private int col;
    private int dotColor;
    private int dotRadius;
    private int dotPressedColor;
    private int lineColor;
    private int lineWidth;
    private int widthHeightOffset;
    private Region[][] dotsRegion;
    private Region globalRegion;
    private int[][] dotsStatus;
    private Path tmpPath;
    private Point[][] dotsPos;
    private Point currentPoint;
    /**
     * 安全模式,不显示按下的点和手指移动路径
     */
    private boolean securityMode;
    private Paint linePaint;
    private Path linePath;

    private Point lastPoint;
    private StringBuilder password;
    private int dotPressedRadius;
    private Vibrator vibrator;
    private boolean isTouching = false;
    private int minLength;
    private OnCheckPasswordListener onCheckPasswordListener;
    private int ERROR = 1;
    private int NORMAL = 2;
    private int realLineColor;
    private int realDotPressedColor;
    private Runnable action;
    private int STATUS = NORMAL;

    public GestureLockView(Context context) {
        super(context);
        this.mContext = context;
        init(null, 0);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs, 0);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init(attrs, defStyle);
    }

    /**
     * 初始化一些属性
     *
     */
    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GestureLockView, defStyle, 0);
        col = a.getInt(R.styleable.GestureLockView_row, DEFAULT_ROW);
        row = a.getInt(R.styleable.GestureLockView_col, DEFAULT_COL);
        dotColor = a.getColor(R.styleable.GestureLockView_dot_color,
                ContextCompat.getColor(mContext, R.color.default_dot_color));
        dotRadius = a.getDimensionPixelSize(R.styleable.GestureLockView_dot_radius,
                getResources().getDimensionPixelSize(R.dimen.default_dot_radius));
        dotPressedColor = a.getColor(R.styleable.GestureLockView_dot_color_pressed,
                ContextCompat.getColor(mContext, R.color.default_dot_pressed_color));
        lineColor = a.getColor(R.styleable.GestureLockView_line_color,
                ContextCompat.getColor(mContext, R.color.default_line_color));
        lineWidth = a.getDimensionPixelSize(R.styleable.GestureLockView_line_width,
                getResources().getDimensionPixelSize(R.dimen.default_line_width));
        securityMode = a.getBoolean(R.styleable.GestureLockView_security_mode, false);
        dotPressedRadius = a.getDimensionPixelSize(R.styleable.GestureLockView_dot_pressed_radius,
                getResources().getDimensionPixelSize(R.dimen.default_dot_pressed_radius));
        boolean vibrateable = a.getBoolean(R.styleable.GestureLockView_vibrate, true);
        minLength = a.getInt(R.styleable.GestureLockView_min_length, 4);
        a.recycle();
        if (vibrateable) {
            vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
        }
        dotPaint = new Paint();
        dotPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setAntiAlias(true);
        dotPaint.setStyle(Paint.Style.FILL);
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        dotsRegion = new Region[row][col];
        dotsPos = new Point[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                dotsRegion[i][j] = new Region();
                dotsPos[i][j] = new Point(0, 0);
            }
        }
        dotsStatus = new int[row][col];
        tmpPath = new Path();

        currentPoint = new Point();
        linePath = new Path();
        lastPoint = new Point(0, 0);
        password = new StringBuilder();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;
        widthHeightOffset = Math.abs(contentHeight - contentWidth);

        globalRegion = new Region(0, 0, getWidth(), getBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 400;
        int desiredHeight = 400;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //<editor-fold desc="画最后一个点和手指触摸点之间的连线">
        if ((lastPoint.x != 0 || lastPoint.y != 0) //不是初始点
                && !securityMode//不是安全模式
                && password.length() > 0) {
            canvas.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y, linePaint);
        }
        //</editor-fold>
        //<editor-fold desc="画点与点之间的轨迹线">
        if (!securityMode || STATUS == ERROR) {
            canvas.drawPath(linePath, linePaint);
        }
        //</editor-fold>
        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= col; j++) {
                //<editor-fold desc="画点">
                float x = getMiniViewSize() / row * i - getMiniViewSize() / row / 2f + paddingLeft;
                float y = getMiniViewSize() / col * j - getMiniViewSize() / col / 2f + paddingLeft;
                //<editor-fold desc="判断横竖View,保证点的间距相等">
                if (getViewOrientation() == PORTRAIT) {
                    y += widthHeightOffset / 2f;
                } else {
                    x += widthHeightOffset / 2f;
                }
                //</editor-fold>
                int realDotRadius;
                if (dotsStatus[i - 1][j - 1] == 1) {
                    if (!securityMode || STATUS == ERROR) {
                        dotPaint.setColor(realDotPressedColor);
                    }
                    realDotRadius = dotPressedRadius > getTouchAreaMimiRadius() ? getTouchAreaMimiRadius() - 16 : dotPressedRadius;
                } else {
                    dotPaint.setColor(dotColor);
                    realDotRadius = dotRadius;
                }
                canvas.drawCircle(x, y, realDotRadius, dotPaint);
                //</editor-fold>
                //<editor-fold desc="存储每个点的坐标">
                dotsPos[i - 1][j - 1].set((int) x, (int) y);
                //</editor-fold>
                //<editor-fold desc="设置每个点的触摸区域">
                tmpPath.reset();
                tmpPath.addCircle(x, y, getTouchAreaMimiRadius(), Path.Direction.CW);
                dotsRegion[i - 1][j - 1].setPath(tmpPath, globalRegion);
                //</editor-fold>
            }
        }
    }

    /**
     * 获得最小触摸范围的半径
     * <br>为了防止触摸范围重叠
     *
     * @return 最小接触范围半径
     */
    private int getTouchAreaMimiRadius() {
        int max = Math.max(col, row);
        return getMiniViewSize() / max / 2;
    }

    /**
     * 获取长宽的最小值
     *
     * @return 获取最小值
     */
    private int getMiniViewSize() {
        return Math.min(contentHeight, contentWidth);
    }

    /**
     * 获得view形状,是竖屏形状还是横屏形状
     *
     * @return 形状
     */
    private int getViewOrientation() {
        if (contentWidth > contentHeight) {
            return LANDSCAPE;
        } else {
            return PORTRAIT;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dealDown(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                dealMove(event);
                return true;
            case MotionEvent.ACTION_UP:
                resetStatus();
                return true;
            default:
                return true;
        }
    }

    /**
     * 处理Action Down事件
     *
     * @param event 事件
     */
    private void dealDown(MotionEvent event) {
        if (action != null) {
            removeCallbacks(action);
        }
        reset();
        setStatus(NORMAL);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (dotsRegion[i][j].contains((int) event.getX(), (int) (event.getY()))) {
                    if (dotsStatus[i][j] == 0) {
                        linePath.moveTo(dotsPos[i][j].x, dotsPos[i][j].y);
                        lastPoint.set(dotsPos[i][j].x, dotsPos[i][j].y);
                        vibrator();
                        String posString = String.valueOf((char) (j * row + i + 1 + 96));
                        Log.d(TAG, "dealDown: posString" + posString);
                        if (password.indexOf(posString) == -1) {
                            password.append(posString);
                        }
                    }
                    dotsStatus[i][j] = 1;
                }
            }
        }
    }

    /**
     * 处理Action Move事件
     *
     * @param event 事件
     */
    private void dealMove(MotionEvent event) {
        currentPoint.set(((int) event.getX()), ((int) event.getY()));
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (dotsRegion[i][j].contains((int) event.getX(), (int) (event.getY()))) {
                    if (dotsStatus[i][j] == 0) {
                        //只有这个点没被选中过,才能链接到这个点
                        linePath.lineTo(dotsPos[i][j].x, dotsPos[i][j].y);
                        lastPoint.set(dotsPos[i][j].x, dotsPos[i][j].y);
                        vibrator();
                        isTouching = true;
                        String posString = String.valueOf((char) (j * row + i + 1 + 96));
                        Log.d(TAG, "dealDown: posString" + posString);
                        if (password.indexOf(posString) == -1) {
                            password.append(posString);
                        }
                    }
                    dotsStatus[i][j] = 1;
                } else {
                    isTouching = false;
                }
            }
        }
        invalidate();
    }

    /**
     * 恢复状态
     */
    private void resetStatus() {
        currentPoint.set(lastPoint.x, lastPoint.y);
        if (password.toString().length() < minLength) {
            //密码长度小于最小长度
            setStatus(ERROR);
            if (onCheckPasswordListener != null) {
                onCheckPasswordListener.onError();
            }
        } else {
            //密码长度符合要求
            if (onCheckPasswordListener != null && onCheckPasswordListener.onCheckPassword(password.toString())) {
                if (onCheckPasswordListener != null) {
                    onCheckPasswordListener.onSuccess();
                }
            } else {
                setStatus(ERROR);
                if (onCheckPasswordListener != null) {
                    onCheckPasswordListener.onError();
                }
            }
        }
        password.delete(0, password.length());
        invalidate();
        //抬起手指后过一秒钟再清除输入轨迹
        action = new Runnable() {
            //抬起手指后过一秒钟再清除输入轨迹
            @Override
            public void run() {
                reset();
                invalidate();
            }
        };
        postDelayed(action, 1000);
    }

    /**
     * 重置一些状态
     */
    private void reset() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                dotsStatus[i][j] = 0;
            }
        }
        linePath.rewind();
        linePath.moveTo(currentPoint.x, currentPoint.y);
        password.delete(0, password.length());
    }

    /**
     * 设置当前状态<br>
     * <code>NORMAL</code> 正常输入状态<br>
     * <code>SUCCESS</code> 密码正确状态<br>
     * <code>ERROR</code> 密码输入错误状态
     *
     * @param i 状态
     */
    private void setStatus(int i) {
        if (ERROR == i) {
            realLineColor = Color.parseColor("#66FF0000");
            realDotPressedColor = Color.parseColor("#FF0000");
        } else if (NORMAL == i) {
            realLineColor = lineColor;
            realDotPressedColor = dotPressedColor;
        }
        STATUS = i;
        linePaint.setColor(realLineColor);
    }

    /**
     * 震动,只有第一次进入点的触摸范围时才震动
     */
    private void vibrator() {
        if (!isTouching) {
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(30, 10));
                } else {
                    vibrator.vibrate(30);
                }
            }
        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
        invalidate();
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
        invalidate();
    }

    public int getDotColor() {
        return dotColor;
    }

    public void setDotColor(int dotColor) {
        this.dotColor = dotColor;
        invalidate();
    }

    public int getDotRadius() {
        return dotRadius;
    }

    public void setDotRadius(int dotRadius) {
        this.dotRadius = dotRadius;
        invalidate();
    }

    public int getDotPressedColor() {
        return dotPressedColor;
    }

    public void setDotPressedColor(int dotPressedColor) {
        this.dotPressedColor = dotPressedColor;
        invalidate();
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }

    public boolean isSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(boolean securityMode) {
        this.securityMode = securityMode;
        invalidate();
    }

    public int getDotPressedRadius() {
        return dotPressedRadius;
    }

    public void setDotPressedRadius(int dotPressedRadius) {
        this.dotPressedRadius = dotPressedRadius;
        invalidate();
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
        invalidate();
    }

    public OnCheckPasswordListener getOnCheckPasswordListener() {
        return onCheckPasswordListener;
    }

    public void setOnCheckPasswordListener(OnCheckPasswordListener onCheckPasswordListener) {
        this.onCheckPasswordListener = onCheckPasswordListener;
    }

    public interface OnCheckPasswordListener {
        /**
         * 手势密码输入完成时回调,验证密码
         * <br>这里只做密码校验
         *
         * @param passwd 输入完成的手势密码
         * @return <code>true</code>:输入的手势密码和存储在本地的密码一致
         * <br>反之<code>false</code>
         */
        boolean onCheckPassword(String passwd);

        /**
         * 当密码校验成功时的回调
         */
        void onSuccess();

        /**
         * 当密码校验失败时的回调
         */
        void onError();
    }

}
