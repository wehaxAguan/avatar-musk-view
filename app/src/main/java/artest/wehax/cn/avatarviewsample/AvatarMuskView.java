package artest.wehax.cn.avatarviewsample;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mayuhan on 15/7/24.
 */
public class AvatarMuskView extends View {
    private static int DEFAULT_SIZE = 25;
    private static int DEFAULT_BORDER_WIDTH = 3;
    private static int DEFAULT_POINT_RADIUS = 4;
    private static int DEFAULT_DURATION = 1000;
    private static int DEFAULT_COLOR = Color.parseColor("#FFC545");
    private static float POINT_STATUS_1 = 0.3333f;
    private static float POINT_STATUS_2 = 0.6666f;
    private static float POINT_STATUS_3 = 1f;

    private int size;
    private Paint borderPaint;
    private Paint pointPaint;
    private int borderWidth;
    private int pointRadius;
    private int viewHeight;
    private int viewWidth;
    private RectF borderBoundRectF;
    private float animProgress;
    private float startRight;
    private float endRight;
    private float endLeft;
    private float startLeft;
    private float centerX;
    private float centerY;
    private float leftX;
    private float rightX;
    private float leftY;
    private float rightY;
    private float leftAlpha;
    private float centerAlpha;
    private float righAlpha;
    private int color;


    public AvatarMuskView(Context context) {
        super(context);
        init(null);
    }

    public AvatarMuskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AvatarMuskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray attributesArray = getContext().getTheme()
                .obtainStyledAttributes(attrs, R.styleable.AvatarMuskView, 0, 0);

        int size = attributesArray.getDimensionPixelSize(R.styleable.AvatarMuskView_size, DEFAULT_SIZE);
        setSize(size);
        int borderWidth = attributesArray.getDimensionPixelOffset(R.styleable.AvatarMuskView_borderWidth, DEFAULT_BORDER_WIDTH);
        setBorderWidth(borderWidth);
        int pointRadius = attributesArray.getDimensionPixelOffset(R.styleable.AvatarMuskView_pointRadius, DEFAULT_POINT_RADIUS);
        setPointRadius(pointRadius);

        int color = attributesArray.getColor(R.styleable.AvatarMuskView_color, DEFAULT_COLOR);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(DEFAULT_BORDER_WIDTH);
        borderPaint.setAntiAlias(true);

        pointPaint = new Paint();
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(DEFAULT_POINT_RADIUS);
        pointPaint.setAntiAlias(true);

        setPaintsColor(color);
    }

    protected void setSize(int size) {
        this.size = size;
        invalidate();
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        invalidate();
    }

    public void setPointRadius(int radius) {
        this.pointRadius = radius;
        invalidate();
    }

    public void setColor(String colorStr) {
        this.color = Color.parseColor(colorStr);
        setPaintsColor(this.color);
        invalidate();
    }

    private void setPaintsColor(int color) {
        this.pointPaint.setColor(color);
        this.borderPaint.setColor(color);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewHeight = getMeasuredHeight();
        this.viewWidth = getMeasuredWidth();

        if (viewHeight != viewWidth) {
            throw new RuntimeException("AvatarView need width and height must be the same");
        }

        borderBoundRectF = new RectF(0 + borderWidth, 0 + borderWidth, viewHeight - borderWidth, viewHeight - borderWidth);

        centerX = viewWidth / 2f;
        centerY = centerX;
        leftX = (viewWidth - borderWidth * 2) / 3f / 2f + borderWidth;
        leftY = centerY;

        rightX = (viewWidth - borderWidth * 2) / 3f * 2.5f + borderWidth;
        rightY = centerY;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
        drawPoints(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startBorderAnimation();
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawArc(borderBoundRectF, startRight, endRight - startRight, false, borderPaint);
        canvas.drawArc(borderBoundRectF, startLeft, endLeft - startLeft, false, borderPaint);
    }

    private void drawPoints(Canvas canvas) {
        pointPaint.setAlpha((int) (leftAlpha * 255));
        canvas.drawCircle(leftX, leftY, pointRadius, pointPaint);
        pointPaint.setAlpha((int) (centerAlpha * 255));
        canvas.drawCircle(centerX, centerY, pointRadius, pointPaint);
        pointPaint.setAlpha((int) (righAlpha * 255));
        canvas.drawCircle(rightX, rightY, pointRadius, pointPaint);
    }

    private void startBorderAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1)
                .setDuration(DEFAULT_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animProgress = (float) valueAnimator.getAnimatedValue();
                calculateSweepingAngle();
                calculatePointOpacity();
                invalidate();
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setTarget(this);
        animator.start();
    }

    private void calculateSweepingAngle() {
        float progress = Math.abs(animProgress - 0.5f) / 0.5f;
        this.startRight = 0 - progress * 90;
        this.endRight = 0 + progress * 90;
        this.startLeft = 180 - progress * 90;
        this.endLeft = 180 + progress * 90;
    }

    private void calculatePointOpacity() {

        if (animProgress < POINT_STATUS_1) {
            this.leftAlpha = animProgress % POINT_STATUS_1 / POINT_STATUS_1;
            this.centerAlpha = 0;
            this.righAlpha = 0;
        } else if (animProgress >= POINT_STATUS_1 && animProgress <= POINT_STATUS_2) {
            this.centerAlpha = (animProgress - POINT_STATUS_1) % POINT_STATUS_1 / POINT_STATUS_1;
            this.leftAlpha = 1f;
            this.righAlpha = 0;
        } else if (animProgress >= POINT_STATUS_2 && animProgress <= POINT_STATUS_3) {
            this.leftAlpha = 1f;
            this.centerAlpha = 1f;
            this.righAlpha = (animProgress - POINT_STATUS_2) % POINT_STATUS_1 / POINT_STATUS_1;
        } else {
            this.centerAlpha = 1f;
            this.leftAlpha = 1f;
            this.righAlpha = 1f;
        }
    }
}
