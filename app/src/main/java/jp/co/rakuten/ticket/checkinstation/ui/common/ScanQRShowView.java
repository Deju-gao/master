package jp.co.rakuten.ticket.checkinstation.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.ViewfinderView;
import java.util.List;
import jp.co.rakuten.ticket.checkinstation.R;

public class ScanQRShowView extends ViewfinderView {
    protected final int qrCodeBorderColor;

    public ScanQRShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.custom_finder);
        this.qrCodeBorderColor = attributes.getColor(R.styleable.custom_finder_qrCode_borderColor, getResources().getColor(R.color.colorAccent));
        attributes.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }

        final Rect frame = framingRect;
        final Rect previewFrame = previewFramingRect;
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
        drawQrCodeBorders(frame, canvas);
        if (resultBitmap != null) {
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            paint.setColor(laserColor);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            final int middle = frame.height() / 2 + frame.top;
            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
            final float scaleX = frame.width() / (float) previewFrame.width();
            final float scaleY = frame.height() / (float) previewFrame.height();
            final int frameLeft = frame.left;
            final int frameTop = frame.top;
            if (!lastPossibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (final ResultPoint point : lastPossibleResultPoints) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            radius, paint
                    );
                }
                lastPossibleResultPoints.clear();
            }
            if (!possibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (final ResultPoint point : possibleResultPoints) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            POINT_SIZE, paint
                    );
                }
                final List<ResultPoint> temp = possibleResultPoints;
                possibleResultPoints = lastPossibleResultPoints;
                lastPossibleResultPoints = temp;
                possibleResultPoints.clear();
            }
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    public void drawQrCodeBorders(Rect frame, Canvas canvas) {
        paint.setColor(qrCodeBorderColor);
        paint.setStrokeWidth(5);

        final int halfQuarterWidth = frame.width() / 4;
        final int halfQuarterHeight = frame.height() / 4;

        //Top Bars
        canvas.drawLine(frame.left, frame.top, (frame.left + halfQuarterWidth), frame.top, paint);
        canvas.drawLine(frame.right, frame.top, (frame.right - halfQuarterWidth), frame.top, paint);

        //Bottom bars
        canvas.drawLine(frame.left, frame.bottom, (frame.left + halfQuarterWidth), frame.bottom, paint);
        canvas.drawLine(frame.right, frame.bottom, (frame.right - halfQuarterWidth), frame.bottom, paint);

        //Left bars
        canvas.drawLine(frame.left, frame.top, frame.left, (frame.top + halfQuarterHeight), paint);
        canvas.drawLine(frame.left, frame.bottom, frame.left, (frame.bottom - halfQuarterHeight), paint);

        //Right bars
        canvas.drawLine(frame.right, frame.top, frame.right, (frame.top + halfQuarterHeight), paint);
        canvas.drawLine(frame.right, frame.bottom, frame.right, (frame.bottom - halfQuarterHeight), paint);
    }
}
