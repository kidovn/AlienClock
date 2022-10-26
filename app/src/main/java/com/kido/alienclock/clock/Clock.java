package com.kido.alienclock.clock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.kido.alienclock.R;
import com.kido.alienclock.clock.enumaration.NumericFormat;
import com.kido.alienclock.clock.listener.ClockListener;
import com.kido.alienclock.clock.runnable.ClockRunnable;
import com.kido.alienclock.clock.theme.NumericTheme;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public abstract class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    // Constantns

    private static final int DEFAULT_PRIMARY_COLOR = Color.BLACK;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;
    private static final boolean DEFAULT_STATE = false;
    private static final float DEFAULT_BORDER_THICKNESS = 0.015f;
    private static final int DEFAULT_BORDER_RADIUS = 20;
    private static final int DEFAULT_MIN_RADIUS_ = 10;
    private static final int DEFAULT_MAX_RADIUS = 90;
    private static final float DEFAULT_MINUTES_BORDER_FACTOR = 0.4f;
    private static final float DEFAULT_SECONDS_BORDER_FACTOR = 0.9f;

    // Typed Array
//    private ClockType clockType;

    private Drawable clockBackground;

    private boolean showBorder;
    private int borderColor;
//    private BorderStyle borderStyle;
    private int borderRadiusRx = DEFAULT_BORDER_RADIUS;
    private int borderRadiusRy = DEFAULT_BORDER_RADIUS;

    private Typeface valuesFont;
    private int valuesColor;
    private int valuesColorDate;

    private NumericFormat numericFormat;

    private boolean numericShowSeconds;

    // Attributes
    private int size, centerX, centerY, radius;
    private float defaultThickness;
    private RectF defaultRectF;
    private ClockRunnable mClockRunnable;
    private ClockListener mClockListener;
    protected Calendar mCalendar;
    private Handler mHandler;

    public Clock(Context context) {
        super(context);
        setSaveEnabled(true);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();
        size = Math.min(widthWithoutPadding, heightWithoutPadding);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size/2 + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {
        mClockRunnable = new ClockRunnable(this);
        mHandler = new Handler();

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Clock, 0, 0);

        try {

            this.clockBackground = typedArray.getDrawable(R.styleable.Clock_clock_background);

            this.showBorder = typedArray.getBoolean(R.styleable.Clock_show_border, DEFAULT_STATE);
            this.borderColor = typedArray.getColor(R.styleable.Clock_border_color, DEFAULT_PRIMARY_COLOR);
            int typedBorderRadiusX = typedArray.getInt(R.styleable.Clock_border_radius_rx, DEFAULT_BORDER_RADIUS);

            if (typedBorderRadiusX > DEFAULT_MIN_RADIUS_ && typedBorderRadiusX < DEFAULT_MAX_RADIUS) {
                this.borderRadiusRx = typedBorderRadiusX;
            } else {
                throw new IllegalArgumentException("border_radius_rx should be in ]" + DEFAULT_MIN_RADIUS_ + "," + DEFAULT_MAX_RADIUS + "[");
            }

            int typedBorderRadiusY = typedArray.getInt(R.styleable.Clock_border_radius_ry, DEFAULT_BORDER_RADIUS);
            if (typedBorderRadiusY > DEFAULT_MIN_RADIUS_ && typedBorderRadiusY < DEFAULT_MAX_RADIUS) {
                this.borderRadiusRy = typedBorderRadiusY;
            } else {
                throw new IllegalArgumentException("border_radius_ry should be in ]" + DEFAULT_MIN_RADIUS_ + "," + DEFAULT_MAX_RADIUS + "[");
            }

            this.valuesFont = ResourcesCompat.getFont(getContext(), typedArray.getResourceId(R.styleable.Clock_values_font, R.font.proxima_nova_thin));
            this.valuesColor = typedArray.getColor(R.styleable.Clock_values_color, DEFAULT_PRIMARY_COLOR);
            this.valuesColorDate = typedArray.getColor(R.styleable.Clock_values_color_date, DEFAULT_PRIMARY_COLOR);

            this.numericFormat = NumericFormat.fromId(typedArray.getInt(R.styleable.Clock_numeric_format, NumericFormat.hour_12.getId()));

            this.numericShowSeconds = typedArray.getBoolean(R.styleable.Clock_numeric_show_seconds, DEFAULT_STATE);

            typedArray.recycle();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        onPreDraw(canvas);

        mCalendar = getCalendar();

        if (mClockListener != null) {
            mClockListener.getCalendar(mCalendar);
        }

        drawNumericClock(canvas);
    }

    protected Calendar getCalendar() {
        return Calendar.getInstance();
    }


    private void onPreDraw(Canvas canvas) {

        this.size = getWidth();
        this.centerX = size / 2;
        this.centerY = getHeight() / 2;
        this.radius = (int) ((this.size * (1 - DEFAULT_BORDER_THICKNESS)) / 2);

        this.defaultThickness = this.size * DEFAULT_BORDER_THICKNESS;
        this.defaultRectF = new RectF(
                defaultThickness, defaultThickness,
                this.size - defaultThickness, getHeight() - defaultThickness);

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(defaultThickness);

        //canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);
        //canvas.drawCircle(centerX, centerY, radius, paint);
    }

    abstract int getCurrentMinute();
    abstract int getCurrentSecond();
    abstract int getCurrentHour();
    abstract int getCurrentHourOfDay();
    abstract int getCurrentDayOfMonth();
    abstract int getCurrentMonth();
    abstract int getCurrentYear();
    abstract int getCurrentAM_PM();

    private void drawNumericClock(Canvas canvas) {

        if (showBorder) {
            drawCustomBorder(canvas);
        }

        if (clockBackground != null) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            Bitmap bitmap = ((BitmapDrawable) clockBackground).getBitmap();
            RectF rectF = new RectF(centerX - radius, centerY - radius/2, centerX + radius, centerY + radius/2);

            Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas tCanvas = new Canvas(output);

            float rx = radius - (radius * (100 - borderRadiusRx)) / 100;
            float ry = radius - (radius * (100 - borderRadiusRy)) / 100;
            tCanvas.drawRoundRect(defaultRectF, rx, ry, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            tCanvas.drawBitmap(bitmap, null, rectF, paint);
            canvas.drawBitmap(output, null, rectF, new Paint());
        }

        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(valuesFont);

        textPaint.setTextSize(size * 0.22f);
        textPaint.setColor(this.valuesColor);

        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        SpannableStringBuilder spannableStringDate = new SpannableStringBuilder();


        int amPm = getCurrentAM_PM();
        String minute = String.format(Locale.getDefault(), "%02d",getCurrentMinute());
        String second = String.format(Locale.getDefault(), "%02d", getCurrentSecond());

        if (this.numericShowSeconds) {
            if (this.numericFormat == NumericFormat.hour_12) {
                spannableString.append(String.format(Locale.getDefault(), "%02d",getCurrentHour()));
                spannableString.append(":");
                spannableString.append(minute);
                spannableString.append(".");
                spannableString.append(second);
                spannableString.append(amPm == Calendar.AM ? "AM" : "PM");
                spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent
            } else {
                spannableString.append(String.format(Locale.getDefault(), "%02d", getCurrentHourOfDay()));
                spannableString.append(":");
                spannableString.append(minute);
                spannableString.append(".");
                spannableString.append(second);
            }
        } else {
            if (this.numericFormat == NumericFormat.hour_12) {
                spannableString.append(String.format(Locale.getDefault(), "%02d", getCurrentHour()));
                spannableString.append(":");
                spannableString.append(minute);
                spannableString.append(amPm == Calendar.AM ? "AM" : "PM");
                spannableString.setSpan(new RelativeSizeSpan(0.4f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent
            } else {
                spannableString.append(String.format(Locale.getDefault(), "%02d", getCurrentHourOfDay()));
                spannableString.append(":");
                spannableString.append(minute);
            }
        }

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(centerX - layout.getWidth() / 2, centerY - layout.getHeight() );
        layout.draw(canvas);

        //draw date
        TextPaint textPaintDate = new TextPaint();
        textPaintDate.setAntiAlias(true);
        textPaintDate.setTypeface(valuesFont);

        textPaintDate.setTextSize(size * 0.1f);
        textPaintDate.setColor(this.valuesColor);

        spannableStringDate.append(String.format(Locale.getDefault(), "%02d",getCurrentMonth()));
        spannableStringDate.append("-");
        spannableStringDate.append(String.format(Locale.getDefault(), "%02d",getCurrentDayOfMonth()));
        spannableStringDate.append("-");
        spannableStringDate.append(String.format(Locale.getDefault(), "%02d",getCurrentYear()));

        StaticLayout layoutDate = new StaticLayout(spannableStringDate, textPaintDate, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(centerX - layoutDate.getWidth() / 2, centerY +layoutDate.getHeight()/2);
        layoutDate.draw(canvas);
    }

    private void drawCustomBorder(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(defaultThickness);

        float rx = radius - (radius * (100 - borderRadiusRx)) / 100;
        float ry = radius - (radius * (100 - borderRadiusRy)) / 100;
        canvas.drawRoundRect(defaultRectF, rx, ry, paint);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mClockRunnable != null)
            mClockRunnable.run();
    }

    public void setClockListener(ClockListener clockListener) {
        mClockListener = clockListener;
    }

    /**
     * Ref : https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/
     *
     * @param timezoneCountry
     */
    public void setTimezone(String timezoneCountry) {
        mCalendar = Calendar.getInstance(TimeZone.getTimeZone(timezoneCountry));
    }


    // Getters

//    public ClockType getClockType() {
//        return clockType;
//    }
//
//
//    // Setters
//
//    public void setClockType(ClockType clockType) {
//        this.clockType = clockType;
//        invalidate();
//    }

    public void setClockBackground(int clockBackground) {
        this.clockBackground = ResourcesCompat.getDrawable(getContext().getResources(), clockBackground, null);
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = ResourcesCompat.getColor(getContext().getResources(), borderColor, null);
    }

    public void setBorderRadius(int borderRadiusRx, int borderRadiusRy) {
        this.borderRadiusRx = borderRadiusRx;
        this.borderRadiusRy = borderRadiusRy;
    }

    public void setValuesFont(int valuesFont) {
        this.valuesFont = ResourcesCompat.getFont(getContext(), valuesFont);
    }

    public void setValuesColor(int valuesColor) {
        this.valuesColor = ResourcesCompat.getColor(getContext().getResources(), valuesColor, null);
    }

    public void setValuesDateColor(int valuesColor) {
        this.valuesColorDate = ResourcesCompat.getColor(getContext().getResources(), valuesColor, null);
    }

    public void setNumericFormat(NumericFormat numericFormat) {
        this.numericFormat = numericFormat;
    }

    public void setNumericShowSeconds(boolean numericShowSeconds) {
        this.numericShowSeconds = numericShowSeconds;
    }


    public void setNumericTheme(NumericTheme numericTheme) {


        this.clockBackground = ResourcesCompat.getDrawable(getContext().getResources(), numericTheme.getClockBackground(), null);

        this.valuesFont = ResourcesCompat.getFont(getContext(), numericTheme.getValuesFont());
        this.valuesColor = ResourcesCompat.getColor(getContext().getResources(), numericTheme.getValuesColor(), null);
        this.valuesColorDate = ResourcesCompat.getColor(getContext().getResources(), numericTheme.getValuesColor(), null);

        this.showBorder = numericTheme.isShowBorder();
        this.borderColor = numericTheme.getBorderColor();
        this.borderRadiusRx = numericTheme.getBorderRadiusRx();
        this.borderRadiusRy = numericTheme.getBorderRadiusRy();

        this.numericFormat = numericTheme.getNumericFormat();
    }

}
