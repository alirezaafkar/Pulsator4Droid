package pl.bclogic.pulsator4droid.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by booncol on 04.07.2016.
 */
public class PulsatorLayout extends RelativeLayout {

    public static final int INFINITE = 0;

    public static final int INTERP_LINEAR = 0;
    public static final int INTERP_ACCELERATE = 1;
    public static final int INTERP_DECELERATE = 2;
    public static final int INTERP_ACCELERATE_DECELERATE = 3;

    public static final int STYLE_FILL = 0;
    public static final int STYLE_STROKE = 1;

    public static final int REPEAT_RESTART = 1;
    public static final int REPEAT_REVERSE = 2;

    private static final int DEFAULT_COUNT = 4;
    private static final int DEFAULT_COLOR = Color.rgb(0, 116, 193);
    private static final int DEFAULT_DURATION = 7000;
    private static final int DEFAULT_REPEAT = INFINITE;
    private static final boolean DEFAULT_START_FROM_SCRATCH = true;
    private static final int DEFAULT_INTERPOLATOR = INTERP_LINEAR;
    private static final int DEFAULT_STYLE = STYLE_FILL;
    private static final float DEFAULT_STROKE_WIDTH = 0.7f;
    private static final float DEFAULT_MIN_SCALE = 0f;
    private static final float DEFAULT_MIN_ALPHA = 0f;
    private static final float DEFAULT_MAX_SCALE = 1f;
    private static final float DEFAULT_MAX_ALPHA = 1f;
    private static final float DEFAULT_START_ANGLE = 0f;
    private static final float DEFAULT_SWEEP_ANGLE = 360f;

    private int mCount;
    private int mDuration;
    private int mRepeat;
    private boolean mStartFromScratch;
    private int mColor;
    private int mInterpolator;

    private final List<View> mViews = new ArrayList<>();
    private AnimatorSet mAnimatorSet;
    private Paint mPaint;
    private float mRadius;
    private float mCenterX;
    private float mCenterY;
    private boolean mIsStarted;
    private int mMargin;
    private int mRepeatMode;
    private int mPaintStyle;
    private float mMinScale;
    private float mMaxScale;
    private float mMinAlpha;
    private float mMaxAlpha;
    private float mStartAngle;
    private float mSweepAngle;
    private float mStrokeWidth;
    private String[] mColors;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *                theme, resources, etc.
     */
    public PulsatorLayout(Context context) {
        this(context, null, 0);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in, through which it can access the current
     *                theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public PulsatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute.
     *
     * @param context      The Context the view is running in, through which it can access the current
     *                     theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     *                     resource that supplies default values for the view. Can be 0 to not look
     *                     for defaults.
     */
    public PulsatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // get attributes
        TypedArray attr = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Pulsator4Droid, 0, 0);

        mCount = DEFAULT_COUNT;
        mDuration = DEFAULT_DURATION;
        mRepeat = DEFAULT_REPEAT;
        mStartFromScratch = DEFAULT_START_FROM_SCRATCH;
        mColor = DEFAULT_COLOR;
        mInterpolator = DEFAULT_INTERPOLATOR;

        try {
            mCount = attr.getInteger(R.styleable.Pulsator4Droid_pulse_count, DEFAULT_COUNT);
            mDuration = attr.getInteger(R.styleable.Pulsator4Droid_pulse_duration,
                    DEFAULT_DURATION);
            mRepeat = attr.getInteger(R.styleable.Pulsator4Droid_pulse_repeat, DEFAULT_REPEAT);
            mStartFromScratch = attr.getBoolean(R.styleable.Pulsator4Droid_pulse_startFromScratch,
                    DEFAULT_START_FROM_SCRATCH);
            mColor = attr.getColor(R.styleable.Pulsator4Droid_pulse_color, DEFAULT_COLOR);
            mInterpolator = attr.getInteger(R.styleable.Pulsator4Droid_pulse_interpolator,
                    DEFAULT_INTERPOLATOR);

            mRepeatMode = attr.getInteger(R.styleable.Pulsator4Droid_pulse_repeat_mode, REPEAT_RESTART);
            mPaintStyle = attr.getInteger(R.styleable.Pulsator4Droid_pulse_style, DEFAULT_STYLE);
            mStrokeWidth = attr.getFloat(R.styleable.Pulsator4Droid_pulse_strokeWidth, DEFAULT_STROKE_WIDTH);
            mMinScale = attr.getFloat(R.styleable.Pulsator4Droid_pulse_min_scale, DEFAULT_MIN_SCALE);
            mMaxScale = attr.getFloat(R.styleable.Pulsator4Droid_pulse_max_scale, DEFAULT_MAX_SCALE);
            mMinAlpha = attr.getFloat(R.styleable.Pulsator4Droid_pulse_min_alpha, DEFAULT_MIN_ALPHA);
            mMaxAlpha = attr.getFloat(R.styleable.Pulsator4Droid_pulse_max_alpha, DEFAULT_MAX_ALPHA);
            mStartAngle = attr.getFloat(R.styleable.Pulsator4Droid_pulse_start_angle, DEFAULT_START_ANGLE);
            mSweepAngle = attr.getFloat(R.styleable.Pulsator4Droid_pulse_sweep_angle, DEFAULT_SWEEP_ANGLE);
            mMargin = attr.getDimensionPixelSize(R.styleable.Pulsator4Droid_pulse_margin, 0);

            int colors = attr.getResourceId(R.styleable.Pulsator4Droid_pulse_colors, 0);
            if (colors != 0) {
                mColors = getResources().getStringArray(colors);
            }
        } finally {
            attr.recycle();
        }

        // create paint
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(createPaintStyle(mPaintStyle));
        mPaint.setStrokeWidth(mStrokeWidth);

        // create views
        build();
    }

    /**
     * Start pulse animation.
     */
    public synchronized void start() {
        if (mAnimatorSet == null || mIsStarted) {
            return;
        }

        mAnimatorSet.start();

        if (!mStartFromScratch) {
            ArrayList<Animator> animators = mAnimatorSet.getChildAnimations();
            for (Animator animator : animators) {
                ObjectAnimator objectAnimator = (ObjectAnimator) animator;

                long delay = objectAnimator.getStartDelay();
                objectAnimator.setStartDelay(0);
                objectAnimator.setCurrentPlayTime(mDuration - delay);
            }
        }
    }

    /**
     * Stop pulse animation.
     */
    public synchronized void stop() {
        if (mAnimatorSet == null || !mIsStarted) {
            return;
        }

        mAnimatorSet.end();
    }

    public synchronized boolean isStarted() {
        return (mAnimatorSet != null && mIsStarted);
    }

    /**
     * Get number of pulses.
     *
     * @return Number of pulses
     */
    public int getCount() {
        return mCount;
    }

    /**
     * Get pulse duration.
     *
     * @return Duration of single pulse in milliseconds
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * Set number of pulses.
     *
     * @param count Number of pulses
     */
    public void setCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }

        if (count != mCount) {
            mCount = count;
            reset();
            invalidate();
        }
    }

    /**
     * Set single pulse duration.
     *
     * @param millis Pulse duration in milliseconds
     */
    public void setDuration(int millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        if (millis != mDuration) {
            mDuration = millis;
            reset();
            invalidate();
        }
    }

    /**
     * Gets the current color of the pulse effect in integer
     * Defaults to Color.rgb(0, 116, 193);
     *
     * @return an integer representation of color
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Sets the current color of the pulse effect in integer
     * Takes effect immediately
     * Usage: Color.parseColor("<hex-value>") or getResources().getColor(R.color.colorAccent)
     *
     * @param color : an integer representation of color
     */
    public void setColor(int color) {
        if (color != mColor) {
            this.mColor = color;

            if (mPaint != null) {
                mPaint.setColor(color);
            }
        }
    }

    /**
     * Get current interpolator type used for animating.
     *
     * @return Interpolator type as int
     */
    public int getInterpolator() {
        return mInterpolator;
    }

    /**
     * Set current interpolator used for animating.
     *
     * @param type Interpolator type as int
     */
    public void setInterpolator(int type) {
        if (type != mInterpolator) {
            mInterpolator = type;
            reset();
            invalidate();
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        mCenterX = width * 0.5f;
        mCenterY = height * 0.5f;
        mRadius = Math.min(width, height) * 0.5f;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Remove all views and animators.
     */
    private void clear() {
        // remove animators
        stop();

        // remove old views
        for (View view : mViews) {
            removeView(view);
        }
        mViews.clear();
    }

    /**
     * Build pulse views and animators.
     */
    private void build() {
        // create views and animators
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        int repeatCount = (mRepeat == INFINITE) ? ObjectAnimator.INFINITE : mRepeat;

        List<Animator> animators = new ArrayList<>();
        for (int index = 0; index < mCount; index++) {
            // setup view
            PulseView pulseView = new PulseView(getContext(), getColor(index));
            pulseView.setScaleX(mMinScale);
            pulseView.setScaleY(mMinScale);
            pulseView.setAlpha(mMinAlpha);

            addView(pulseView, index, layoutParams);
            mViews.add(pulseView);
        }

        if (mPaintStyle == STYLE_STROKE) {
            Collections.reverse(mViews);
        }

        for (int index = 0; index < mViews.size(); index++) {
            View pulseView = mViews.get(index);
            int duration = mDuration;
            long delay = index * mDuration / mCount;

            if (mPaintStyle == STYLE_STROKE) {
                duration = mDuration / mCount;
                delay = index * mDuration;
            }

            // setup animators
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(pulseView, "ScaleX", mMinScale, mMaxScale);
            scaleXAnimator.setRepeatCount(repeatCount);
            scaleXAnimator.setRepeatMode(mRepeatMode);
            scaleXAnimator.setStartDelay(delay);
            scaleXAnimator.setDuration(duration);
            animators.add(scaleXAnimator);

            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(pulseView, "ScaleY", mMinScale, mMaxScale);
            scaleYAnimator.setRepeatCount(repeatCount);
            scaleYAnimator.setRepeatMode(mRepeatMode);
            scaleYAnimator.setStartDelay(delay);
            scaleXAnimator.setDuration(duration);
            animators.add(scaleYAnimator);

            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(pulseView, "Alpha", mMaxAlpha, mMinAlpha);
            alphaAnimator.setRepeatCount(repeatCount);
            alphaAnimator.setRepeatMode(mRepeatMode);
            alphaAnimator.setStartDelay(delay);
            scaleXAnimator.setDuration(duration);
            animators.add(alphaAnimator);
        }

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
        mAnimatorSet.setInterpolator(createInterpolator(mInterpolator));
        mAnimatorSet.setDuration(mDuration);
        mAnimatorSet.addListener(mAnimatorListener);
    }

    /**
     * Get color from string array color
     */
    private int getColor(int index) {
        if (mColors == null) {
            return mColor;
        }
        if (index >= mColors.length) {
            index -= mColors.length;
        }
        return Color.parseColor(mColors[index]);
    }

    /**
     * Reset views and animations.
     */
    private void reset() {
        boolean isStarted = isStarted();

        clear();
        build();

        if (isStarted) {
            start();
        }
    }

    /**
     * Create interpolator from type.
     *
     * @param type Interpolator type as int
     * @return Interpolator object of type
     */
    private static Interpolator createInterpolator(int type) {
        switch (type) {
            case INTERP_ACCELERATE:
                return new AccelerateInterpolator();
            case INTERP_DECELERATE:
                return new DecelerateInterpolator();
            case INTERP_ACCELERATE_DECELERATE:
                return new AccelerateDecelerateInterpolator();
            default:
                return new LinearInterpolator();
        }
    }

    /**
     * Create interpolator from type.
     *
     * @param style Paint.Style type as int
     * @return Paint.Style object of type
     */
    private static Paint.Style createPaintStyle(int style) {
        switch (style) {
            case STYLE_STROKE:
                return Paint.Style.STROKE;
            default:
                return Paint.Style.FILL;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    private class PulseView extends View {
        private final int color;
        private final RectF rect;

        public PulseView(Context context, int color) {
            super(context);
            rect = new RectF();
            this.color = color;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            mPaint.setColor(color);
            canvas.drawArc(rect, mStartAngle, mSweepAngle, false, mPaint);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mRadius -= mMargin;
            rect.set(mCenterX - mRadius, mCenterY - mRadius,
                    mCenterX + mRadius, mCenterY + mRadius);
        }
    }

    private final Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animator) {
            mIsStarted = true;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mIsStarted = false;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            mIsStarted = false;
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }

    };

}
