package com.example.rionay.antitigerpoaching;

/**
 * Created by Da Huo on 2017/4/22.
 * 791094
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageControl extends ImageView {
    public ImageControl(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ImageControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ImageControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    // ImageView img;
    Matrix imgMatrix = null;

    static final int DOUBLE_CLICK_TIME_INTERVAL = 300; // time space between 2 clicks
    static final int DOUBLE_TOUCH_POINT_DISTANCE = 10;
    static final int NONE = 0;
    static final int DRAG = 1; // Dragging operation
    static final int ZOOM = 2;
    private int ImgMode = NONE; // current state

    float bigScale = 3f;
    Boolean isBig = false;
    long lastClickMoment = 0;
    float startDistance;
    float endDistance;

    float topHeight;
    Bitmap primaryBitmap = null;

    float contentWidth; // width of content of screen
    float contentHeight; // height of content of screen

    float priImgW; // width of primary image
    float priImgH; // height of primary image

    float ScreenScale; // scale suitable for the screen
    Boolean AllMoveX = true; // whether allow to move on direction x
    Boolean AllMoveY = true; // whether allow to move on direction y
    float startCoordinateX;   //start coordinator of X
    float startCoordinateY;
    float endCoordinateX;
    float endCoordinateY;
    float subX;
    float subY;
    float limitMovX1;
    float limitMovX2;
    float limitMovY1;
    float limitMovY2;
    ICustomMethod mCustomMethod = null;

    public void imageInitial(Bitmap bitmap, int contentW, int contentH, int topHeight, ICustomMethod iCustomMethod) {
        this.primaryBitmap = bitmap;
        this.contentWidth = contentW;
        this.contentHeight = contentH;
        this.topHeight = topHeight;
        mCustomMethod = iCustomMethod;
        priImgW = primaryBitmap.getWidth();
        priImgH = primaryBitmap.getHeight();
        float ImgScaleX = (float) contentW / priImgW;
        float ImgScaleY = (float) contentH / priImgH;
        ScreenScale = ImgScaleX < ImgScaleY ? ImgScaleX : ImgScaleY;
        if (ScreenScale < 1 && 1 / ScreenScale < bigScale) {
            bigScale = (float) (1 / ScreenScale + 0.5);
        }

        imgMatrix = new Matrix();
        subX = (contentW - priImgW * ScreenScale) / 2;
        subY = (contentH - priImgH * ScreenScale) / 2;
        this.setImageBitmap(primaryBitmap);
        this.setScaleType(ScaleType.MATRIX);
        imgMatrix.postScale(ScreenScale, ScreenScale);
        imgMatrix.postTranslate(subX, subY);
        this.setImageMatrix(imgMatrix);
    }                //after implementing the imageInit method, imgMatric has been a instantiation
    // so that the change size method will not handle a none point exception caused by the ingMatrics






    //the operation of clicking down
    public void mouseClickDown(MotionEvent event) {             //OK
        ImgMode = NONE;
        startCoordinateX = event.getRawX();
        startCoordinateY = event.getRawY();
//        int time = (int) event.getEventTime();

        if (event.getPointerCount() == 1) {
            // if the time between 2 clicks smaller than DOUBLE_CLICK_TIME_SPACE, this could be treated as a double click event
            if (event.getEventTime() - lastClickMoment < DOUBLE_CLICK_TIME_INTERVAL) {
                changeImgSize(startCoordinateX, startCoordinateY);        //Invoke the changeSize method to change the size of the image
            } else if (isBig) {
                ImgMode = DRAG;
            }
        }

        lastClickMoment = event.getEventTime();      //set the lastClickTime to the Time of the Event so that the lastClickTime could be renewed to the latest time
    }

    public void mousePointDown(MotionEvent event) {
        startDistance = getMovingDistance(event);
        if (startDistance > DOUBLE_TOUCH_POINT_DISTANCE) {
            ImgMode = ZOOM;
        } else {
            ImgMode = NONE;
        }
    }




    //the operation of mouse movement
    public void mouseMovement(MotionEvent event) {
        if ((ImgMode == DRAG) && (AllMoveX || AllMoveY)) {
            float[] XYMovement = getTranslateXY(imgMatrix);
            float MovX = 0;
            float MovY = 0;

            if (AllMoveY) {
                endCoordinateY = event.getRawY();
                MovY = endCoordinateY - startCoordinateY;
                if ((XYMovement[1] + MovY) <= limitMovY1) {
                    MovY = limitMovY1 - XYMovement[1];
                }
                if ((XYMovement[1] + MovY) >= limitMovY2) {
                    MovY = limitMovY2 - XYMovement[1];
                }
            }

            if (AllMoveX) {
                endCoordinateX = event.getRawX();
                MovX = endCoordinateX - startCoordinateX;
                if ((XYMovement[0] + MovX) <= limitMovX1) {
                    MovX = limitMovX1 - XYMovement[0];
                }
                if ((XYMovement[0] + MovX) >= limitMovX2) {
                    MovX = limitMovX2 - XYMovement[0];
                }
            }

            imgMatrix.postTranslate(MovX, MovY);
            startCoordinateX = endCoordinateX;
            startCoordinateY = endCoordinateY;
            this.setImageMatrix(imgMatrix);
        } else if (ImgMode == ZOOM && event.getPointerCount() > 1) {          //multiple click on the screen
            endDistance = getMovingDistance(event);
            float distance = endDistance - startDistance;
            if (Math.abs(endDistance - startDistance) > DOUBLE_TOUCH_POINT_DISTANCE) {
                if (isBig) {
                    if (distance < 0) {
                        changeImgSize(0, 0);
                        ImgMode = NONE;
                    }                                                      //make no size changes
                } else if (distance > 0) {
                    float Movingx = event.getX(0) / 2 + event.getX(1) / 2;
                    float Movingy = event.getY(0) / 2 + event.getY(1) / 2;
                    changeImgSize(Movingx, Movingy);
                    ImgMode = NONE;
                }                                                         //magnify the picture
            }
        }
    }

//operation of mouse up
    public void mouseClickUp() {

        ImgMode = NONE;

    }



    //magnify the pictures
    private void changeImgSize(float x, float y) {
        if (isBig) {
            // if isBig is true, then recover to normal size
            imgMatrix.reset();
            imgMatrix.postScale(ScreenScale, ScreenScale);
            imgMatrix.postTranslate(subX, subY);
            isBig = false;  //set the state to normal size
        } else {
            imgMatrix.postScale(bigScale, bigScale); // magnify the image on both x and y coordinate
            float MovX = -((bigScale - 1) * x);
            float MovY = -((bigScale - 1) * (y - topHeight)); // (bigScale-1)(y-statusBarHeight-subY)+2*subY;
            float cutImageWidth = priImgW * ScreenScale * bigScale; //current width
            float cutImageHeight = priImgH * ScreenScale * bigScale;

            if (cutImageWidth > contentWidth) {
                limitMovX1 = -(cutImageWidth - contentWidth);
                limitMovX2 = 0;
                AllMoveX = true;
                float cutSubX = bigScale * subX;
                if (-MovX < cutSubX) {
                    MovX = -cutSubX;
                }
                if (cutSubX + MovX < limitMovX1) {
                    MovX = -(cutImageWidth + cutSubX - contentWidth);
                }
            } else {
                AllMoveX = false;
            }

            if (cutImageHeight > contentHeight) {
                limitMovY1 = -(cutImageHeight - contentHeight); // limitation of movement
                limitMovY2 = 0;
                AllMoveY = true; // moving on y coordinate is approved
                float cutSubY = bigScale * subY;
                if (-MovY < cutSubY) {
                    MovY = -cutSubY;
                }
                if (cutSubY + MovY < limitMovY1) {
                    MovY = -(cutImageHeight + cutSubY - contentHeight);
                }
            } else {
                AllMoveY = false;
            }

            imgMatrix.postTranslate(MovX, MovY);
            isBig = true;
        }

        this.setImageMatrix(imgMatrix);                  //set the latest image into the matrix
        if (mCustomMethod != null) {
            mCustomMethod.customMethod(isBig);
        }
    }

    private float[] getTranslateXY(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        float[] floats = new float[2];
        floats[0] = values[Matrix.MTRANS_X];
        floats[1] = values[Matrix.MTRANS_Y];
        return floats;
    }

// get the distance between 2 points
    private float getMovingDistance(MotionEvent event) {
        float Movingx = event.getX(0) - event.getX(1);
        float Movingy = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(Movingx * Movingx + Movingy * Movingy);
    }

    public interface ICustomMethod {
        public void customMethod(Boolean currentStatus);
    }
}