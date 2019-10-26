package jp.co.jyl.bustime.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import jp.co.jyl.bustime.R;
import jp.co.jyl.bustime.bean.BusComingInfo;
import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.bean.BusPlaceInfo;
import jp.co.jyl.bustime.bean.BusStopInfo;

/**
 * Created by jiang on 2015/04/25.
 */
public class AccessDrawView extends View {
    private static final int DRAW_ROUTE_CNT = 3;
    private static final String BUS_ROUTE_NAME_SAMPLE="あいうえおか";
    private static final String BUS_STOP_NAME_SAMPLE="あいうえおかきくけこ";
    private static final int BUS_ROUTE_PADDING = 5;
    private static final int BUS_ICON_SIZE = 64;
    private static final int BUS_STOP_INTERVAL = 150;
    //1行の文字数
    private static final int ONE_LINE_CHAR_NUM =9;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int maxHeight = 0;
    private int maxWidth = 0;
    private float textSizeForRoute = 30;
    private float textSizeForStop = 20;

    List<BusComingInfo> busComingInfoList = null;
    List<List<Rect>> busStopRectList = new ArrayList<>();

    public AccessDrawView(Context context) {
        super(context);
    }

    public AccessDrawView(Context context,AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    /**
     * onMeasureがないと、このViewのonDraw()がよばれません！！
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(maxWidth,maxHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(busComingInfoList == null || busComingInfoList.size() == 0){
            return;
        }

        int count = busComingInfoList.size();
        int subWidth = genSubWidthForOneRoute();
        int x = getXBegPos(count,subWidth);
        textSizeForRoute = Helper.getTextSizeForWidth(paint, subWidth, BUS_ROUTE_NAME_SAMPLE);
        textSizeForStop = Helper.getTextSizeForWidth(paint, subWidth, BUS_STOP_NAME_SAMPLE);
        int colorOdd = Color.rgb(176, 224, 230);
        int colorEvent = Color.rgb(176, 196, 222);
        for(int i = 0; i < count;i++){
            drawBackground(canvas,x,subWidth,i % 2==1?colorOdd:colorEvent);
            drawBusComingInfo(canvas,busComingInfoList.get(i),x,subWidth);
            x += subWidth + BUS_ROUTE_PADDING * 2;
        }
    }
    private int getXBegPos(int count,int subWidth){
        int x = BUS_ROUTE_PADDING;
        if(count == 1){
            x = this.maxWidth / 2  - subWidth / 2;
        }else if(count == 2){
            x = this.maxWidth / 2  - subWidth;
        }
        return x;
    }

    private int genSubWidthForOneRoute(){
        return this.maxWidth / DRAW_ROUTE_CNT - BUS_ROUTE_PADDING * 2;
    }


    private void drawBackground(Canvas canvas,int leftX,int width,int color){
        // fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRect(new Rect(leftX,0,leftX + width,this.maxHeight ), paint);
    }

    private void drawBusComingInfo(Canvas canvas,BusComingInfo busComingInfo,
        int leftX,int width) {

        int x1,y1,x2,y2;

        int beginPosY = 0;
        //バスルート名
        paint.setTextSize(textSizeForRoute);
        int fontHeight = (int) Helper.getTextHeight(BUS_ROUTE_NAME_SAMPLE, paint);
        x1 = leftX;
        y1 = beginPosY;
        x2 = leftX + width;
        y2 = fontHeight;
        Helper.drawText(canvas,busComingInfo.getBusRouteName(),new Rect(x1,y1,x2,y2),
                Color.BLACK,paint,Helper.TEXT_ALIGN_CENTER);

        //行き
        paint.setTextSize(textSizeForStop);
        y1 = y2;
        fontHeight = (int) Helper.getTextHeight(busComingInfo.getGoing(), paint);
        y2 = y1 +  fontHeight * 2;
        Resources res = getResources();
        String going = busComingInfo.getGoing() +  res.getString(R.string.going_text);
        if(going.length() > ONE_LINE_CHAR_NUM){
            String going1 = going.substring(0,ONE_LINE_CHAR_NUM);
            Helper.drawText(canvas,going1,new Rect(x1,y1,x2,y2  - fontHeight),
                    Color.BLACK,paint,Helper.TEXT_ALIGN_CENTER);
            String going2 = going.substring(ONE_LINE_CHAR_NUM);
            Helper.drawText(canvas,going2,new Rect(x1,y1 + fontHeight,x2,y2),
                    Color.BLACK,paint,Helper.TEXT_ALIGN_CENTER);
        }else{
            Helper.drawText(canvas,going,new Rect(x1,y1,x2,y2 - fontHeight),
                    Color.BLACK,paint,Helper.TEXT_ALIGN_CENTER);
        }

       if(busComingInfo.hasBusComing()){
            Rect rectRoute = new Rect(x1,beginPosY,x2,y2);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rectRoute,paint);
        }

        y2 += BUS_ROUTE_PADDING;
        //bus stop
        drawBusStops(canvas,busComingInfo,x1,x2,y2,fontHeight);

        //coming bus
        String waitTime = drawComingBus(canvas,busComingInfo,x1,x2,y2,fontHeight);
        if(waitTime != null){
            Helper.drawText(canvas,waitTime,new Rect(x1 + (x2 -x1) /2  ,
                            y2,x2,y2 + BUS_ICON_SIZE),
                    Color.RED,paint,Helper.TEXT_ALIGN_RIGHT);
        }
    }

    private void drawBusStops(Canvas canvas,BusComingInfo busComingInfo,
                              int x1,int x2,int yPos,int fontHeight){
        Resources r = getResources();
        int y1,y2;

        y2 = yPos;
        //bus stop
        Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.bus_stop);
        List<BusStopInfo> busStopList = busComingInfo.getBusStopInfoList();
        for(int i = 0; i < busStopList.size();i++){
            BusStopInfo busStop = busStopList.get(i);
            y1 = y2;
            y2 = y1 + BUS_ICON_SIZE;
            Helper.drawIcon(canvas,bmp,new Rect(x1,y1,x2,y2),BUS_ICON_SIZE,paint);

            y1 = y2;
            fontHeight = (int) Helper.getTextHeight(busStop.getBusStopName(), paint);
            y2 = y1 +  fontHeight;
            int bottom = y2;
            if(busStop.getBusStopName().length() > ONE_LINE_CHAR_NUM){
                String sub1 = busStop.getBusStopName().substring(0,ONE_LINE_CHAR_NUM );
                String sub2 = busStop.getBusStopName().substring(ONE_LINE_CHAR_NUM);
                Helper.drawText(canvas,sub1,new Rect(x1,y1,x2,y2),
                        Color.BLACK,paint,Helper.TEXT_ALIGN_CENTER);
                y1 = y2;
                y2 = y1 +  fontHeight;
                Helper.drawText(canvas,sub2,new Rect(x1,y1,x2,y2),
                        Color.BLACK,paint,Helper.TEXT_ALIGN_CENTER);
                bottom = y2;
                y2 += BUS_STOP_INTERVAL - fontHeight;
            }else{
                Helper.drawText(canvas,busStop.getBusStopName(),new Rect(x1,y1,x2,y2),
                        Color.BLACK,paint,Helper.TEXT_ALIGN_CENTER);
                y2 += BUS_STOP_INTERVAL;
            }
            //draw line between stops
            if(i < busStopList.size() - 1){
                int centerX = x1 + (x2 -x1) /2;
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.rgb(135, 206, 235));
                canvas.drawRect(new Rect(centerX - 5,bottom,centerX + 5,y2), paint);
            }
        }
    }

    private String drawComingBus(Canvas canvas,BusComingInfo busComingInfo,
                               int x1,int x2,int yPos,int fontHeight){

        List<BusPlaceInfo> busPlaceInfoList = busComingInfo.getBusPlaceInfoList();
        if(busPlaceInfoList.size() == 0){
            return null;
        }
        String waitTime = null;
        Resources r = getResources();
        String going = busComingInfo.getGoing() +  r.getString(R.string.going_text);
        Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.bus_coming);
        for(int i= 0; i<  busPlaceInfoList.size();i++){
            BusPlaceInfo busPlaceInfo = busPlaceInfoList.get(i);
            int placePos = busPlaceInfo.getCurrentPos();
            int y1 = yPos + (BUS_ICON_SIZE + fontHeight + BUS_STOP_INTERVAL) * placePos
                    + BUS_ICON_SIZE + fontHeight + BUS_STOP_INTERVAL /2 - BUS_ICON_SIZE / 2
                    - fontHeight;
            int y2 = y1 + BUS_ICON_SIZE;
            Helper.drawIcon(canvas,bmp,new Rect(x1,y1,x2,y2),BUS_ICON_SIZE,paint);
            y1 = y2;
            fontHeight = (int) Helper.getTextHeight(busPlaceInfo.getDescription(), paint);
            y2 = y1 +  fontHeight;
            String description = busPlaceInfo.getDescription();
            if(description.length() > ONE_LINE_CHAR_NUM){
                String description1 = description.substring(0,ONE_LINE_CHAR_NUM);
                String description2 = description.substring(ONE_LINE_CHAR_NUM);
                Helper.drawText(canvas,description1,new Rect(x1,y1,x2,y2),
                        Color.RED,paint,Helper.TEXT_ALIGN_CENTER);
                Helper.drawText(canvas,description2,new Rect(x1,y1 + fontHeight,x2,y2 + fontHeight),
                        Color.RED,paint,Helper.TEXT_ALIGN_CENTER);

            }else{
                Helper.drawText(canvas,description,new Rect(x1,y1,x2,y2),
                        Color.RED,paint,Helper.TEXT_ALIGN_CENTER);
            }
            if(i==0 && placePos > 0){
                int pos = description.indexOf(going);
                if(pos >= 0){
                    waitTime = description.substring(pos + going.length());
                }
            }

        }
        return waitTime;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setBusComingInfoList(List<BusComingInfo> busComingInfoList) {
        this.busComingInfoList = busComingInfoList;
    }

    public int initAndGetDrawMaxHeight() {
        int maxYPosition = 0;
        int count = busComingInfoList.size();
        int subWidth = genSubWidthForOneRoute();
        textSizeForStop = Helper.getTextSizeForWidth(paint, subWidth, BUS_STOP_NAME_SAMPLE);
        int x1 = getXBegPos(count,subWidth);
        //int x1 = BUS_ROUTE_PADDING;
        busStopRectList.clear();
        for(int i = 0; i < count;i++){
            BusComingInfo busComingInfo = busComingInfoList.get(i);
            int x2 = x1 + subWidth;
            int y1,y2;

            //バスルート名
            int fontHeight = (int) Helper.getTextHeight(BUS_ROUTE_NAME_SAMPLE, paint);
            y1 = 0;
            y2 = fontHeight;

            //行き
            paint.setTextSize(textSizeForStop);
            y1 = y2;
            fontHeight = (int) Helper.getTextHeight(busComingInfo.getGoing(), paint);
            y2 = y1 +  fontHeight * 2;

            y2 += BUS_ROUTE_PADDING;
            //bus stop
            List<BusStopInfo> busStopList = busComingInfo.getBusStopInfoList();
            List<Rect> busRouteRectList = new ArrayList<>();
            for(BusStopInfo busStop :busStopList){
                y1 = y2;
                int begY = y1;
                y2 = y1 + BUS_ICON_SIZE;

                y1 = y2;
                fontHeight = (int) Helper.getTextHeight(busStop.getBusStopName(), paint);
                y2 = y1 +  fontHeight;
                y2 += BUS_STOP_INTERVAL;
                Rect rect = new Rect(x1 + (x2 -x1 - BUS_ICON_SIZE) /2,begY,
                        x2 - (x2 - x1 - BUS_ICON_SIZE) /2,y2 - BUS_STOP_INTERVAL);
                busRouteRectList.add(rect);
            }

            if(maxYPosition < y2){
                maxYPosition = y2;
            }
            this.busStopRectList.add(busRouteRectList);
            x1 = x2 + BUS_ROUTE_PADDING * 2;
        }

        return maxYPosition;
    }

    public BusDirectionStopInfo busStopAroundPoint(int tx,int ty){
        for(int i = 0; i < busStopRectList.size();i++){
            List<Rect> busRouteRectList = busStopRectList.get(i);
            BusComingInfo busComingInfo = busComingInfoList.get(i);
            for(int j= 0; j < busRouteRectList.size();j++){
                Rect rect = busRouteRectList.get(j);
                if(rect.contains(tx,ty)){
                    BusDirectionStopInfo busRouteStopInfo =  new BusDirectionStopInfo();
                    BusStopInfo busStopInfo = busComingInfo.getBusStopInfoList().get(j);
                    int companyCD = busComingInfo.getCompanyCD();
                    int busRouteCD = busComingInfo.getBusRouteCD();
                    String busRouteName = busComingInfo.getBusRouteName();
                    String going = busComingInfo.getGoing();
                    int goingCD = busComingInfo.getGoingCD();
                    int busStopCD = busStopInfo.getBusStopCD();
                    busRouteStopInfo.setCompanyCD(companyCD);
                    busRouteStopInfo.setBusRouteCD(busRouteCD);
                    busRouteStopInfo.setBusStopCD(busStopCD);
                    busRouteStopInfo.setDirection(busComingInfo.getBusDirection());
                    busRouteStopInfo.setBusRouteName(busRouteName);
                    busRouteStopInfo.setGoing(going);
                    busRouteStopInfo.setGoingCD(goingCD);
                    busRouteStopInfo.setBusStopName(busStopInfo.getBusStopName());
                    busRouteStopInfo.setpToStart(busStopInfo.getpToStart());
                    busRouteStopInfo.setpToEnd(busStopInfo.getpToEnd());
                    return busRouteStopInfo;
                }
            }
        }
        return null;
    }

}
