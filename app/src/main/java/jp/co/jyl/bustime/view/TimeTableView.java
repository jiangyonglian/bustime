package jp.co.jyl.bustime.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.co.jyl.bustime.R;
import jp.co.jyl.bustime.bean.BusTimeInfo;
import jp.co.jyl.bustime.bean.DayType;

/**
 * Created by jiang on 2015/05/02.
 */
public class TimeTableView extends View {

    private static final int BUS_TIMETABLE_PADDING = 5;
    private static final int HOUR_COLUMN_WIDTH = 60;

    //1行に描画する分の数
    private int timeCountOneLine = 5;
    //font size
    private float textSizeForTime = 20;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int maxWidth = 0;
    private int maxHeight = 0;
    private int fontHeight = 0;
    private List<BusTimeInfo> timeInfoList = null;
    private List<DrawingInfo> drawingInfoList = null;

    public TimeTableView(Context context) {
        super(context);
    }

    public TimeTableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * onMeasureがないと、このViewのonDraw()がよばれません！！
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(maxWidth,maxHeight);
        //以下の行があると、何故か描画されない
        //super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    static class DrawingInfo implements  Comparable<DrawingInfo>{
        private int hour;
        private List<Integer> workDayList = new ArrayList<>();
        private List<Integer> saturDayList = new ArrayList<>();
        private List<Integer> holiDayList = new ArrayList<>();

        public int compareTo(DrawingInfo o){
            return hour - o.hour;
        }

        public int getMaxCount(){
            int max = 0;
            if(max < workDayList.size()){
                max = workDayList.size();
            }
            if(max < saturDayList.size()){
                max = saturDayList.size();
            }
            if(max < holiDayList.size()){
                max = holiDayList.size();
            }
            return max;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int subWidth = (this.maxWidth - BUS_TIMETABLE_PADDING * 2 - HOUR_COLUMN_WIDTH) / 3;
        int x1,y1,x2,y2;
        x1 = BUS_TIMETABLE_PADDING;
        x2 = this.maxWidth - BUS_TIMETABLE_PADDING;
        y1 = BUS_TIMETABLE_PADDING;

        textSizeForTime = Helper.getTextSizeForWidth(paint, subWidth, "01 02 03 04 05 06 ");
        paint.setTextSize(textSizeForTime);
        fontHeight = (int) Helper.getTextHeight("あいう",paint);
         //draw title line
        y2 = y1 + fontHeight;
        drawTitleLine(canvas,x1,y1,x2,y2,subWidth);

        if(timeInfoList == null || timeInfoList.size() == 0){
            return;
        }

        y1 = y2;
        drawTimeTable(canvas,x1,y1,x2,subWidth,drawingInfoList);
    }

    private void drawTimeTable(Canvas canvas,int x1,int y1,int x2,int subWidth,
                               List<DrawingInfo> drawingInfoList ){

        int x = x1;
        int y2 = 0;
        for(DrawingInfo drawInfo:drawingInfoList){
            int maxCount = drawInfo.getMaxCount();
            int lines = maxCount / timeCountOneLine;
            if(maxCount % timeCountOneLine > 0){
                lines++;
            }
            int lineHeight = fontHeight * lines;
            y2 = y1 + lineHeight;

            //draw hour
            int subXRight = x + HOUR_COLUMN_WIDTH;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(176, 196, 222));
            canvas.drawRect(new Rect(x,y1,subXRight,y2),paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            Rect rect = new Rect(x,y1,subXRight,y2);
            canvas.drawRect(rect,paint);
            Helper.drawText(canvas,String.valueOf(drawInfo.hour),
                    rect,Color.BLACK,paint,
                    Helper.TEXT_ALIGN_CENTER);
            //平日
            x = subXRight;
            subXRight = x + subWidth;
            canvas.drawRect(new Rect(x,y1,subXRight,y2),paint);
            drawOneTimeTable(canvas,x,y1,subXRight,y2,Color.BLACK,drawInfo.workDayList);

            //土曜
            x = subXRight;
            subXRight = x + subWidth;
            canvas.drawRect(new Rect(x,y1,subXRight,y2),paint);
            drawOneTimeTable(canvas,x,y1,subXRight,y2,Color.BLACK,drawInfo.saturDayList);

            //休日
            x = subXRight;
            subXRight = x + subWidth;
            canvas.drawRect(new Rect(x,y1,subXRight,y2),paint);
            drawOneTimeTable(canvas,x,y1,subXRight,y2,Color.BLACK,drawInfo.holiDayList);

            y1 = y2;
            y2 = y1 + lineHeight;
            x = x1;
        }
     }

    private void drawOneTimeTable(Canvas canvas,int x1,int y1,int x2,int y2,
           int color,List<Integer> minutesList){
        if(minutesList.size() == 0){
            return;
        }
        int y = y1 + fontHeight;
        int indexFrom = 0;
        int indexTo = indexFrom + timeCountOneLine;
        while(indexFrom < minutesList.size()){
            String timeData = joinTimesData(minutesList,indexFrom,indexTo);
            Helper.drawText(canvas,timeData,new Rect(x1,y1,x2,y),color,paint,
                    Helper.TEXT_ALIGN_LEFT);
            indexFrom = indexTo;
            indexTo = indexFrom + timeCountOneLine;
            y1 = y;
            y = y1 + fontHeight;
        }
    }

    private String joinTimesData(List<Integer> minutesList,int from,int to){
        StringBuilder sb = new StringBuilder();
        for(int i= from; i < to && i < minutesList.size();i++ ){
            int one = minutesList.get(i);
            if(one < 10){
                sb.append('0');
            }
            sb.append(String.valueOf(one));
            sb.append(' ');
        }
        return sb.toString();
    }

    private void drawTitleLine(Canvas canvas,int x1,int y1,int x2,int y2,int subWidth){

        Resources res = getResources();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(176, 224, 230));
        canvas.drawRect(new Rect(x1,y1,x2,y2),paint);

        int subXRight = x1 + HOUR_COLUMN_WIDTH;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        //時
        Rect rect = new Rect(x1,y1,subXRight,y2);
        canvas.drawRect(rect,paint);
        Helper.drawText(canvas,res.getString(R.string.column_title_hour),
                rect,Color.BLACK,paint,
                Helper.TEXT_ALIGN_CENTER);

        //平日
        x1 = subXRight;
        subXRight = x1 + subWidth;
        rect = new Rect(x1,y1,subXRight,y2);
        canvas.drawRect(rect,paint);
        Helper.drawText(canvas,res.getString(R.string.column_title_workday),
                rect,Color.BLACK,paint,
                Helper.TEXT_ALIGN_CENTER);

        //土曜
        x1 = subXRight;
        subXRight = x1 + subWidth;
        rect = new Rect(x1,y1,subXRight,y2);
        canvas.drawRect(new Rect(x1,y1,subXRight,y2),paint);
        Helper.drawText(canvas,res.getString(R.string.column_title_saturday),
                rect,Color.BLACK,paint,
                Helper.TEXT_ALIGN_CENTER);

        //休日
        x1 = subXRight;
        subXRight = x1 + subWidth;
        rect = new Rect(x1,y1,subXRight,y2);
        canvas.drawRect(new Rect(x1,y1,subXRight,y2),paint);
        Helper.drawText(canvas,res.getString(R.string.column_title_holiday),
                rect,Color.BLACK,paint,
                Helper.TEXT_ALIGN_CENTER);
    }

    private List<DrawingInfo> genDrawingInfoList(){
        List<DrawingInfo> drawingInfoList = new LinkedList<>();
        Map<Integer,DrawingInfo > drawingInfoMap = new HashMap<>();
        for(BusTimeInfo busTimeInfo:timeInfoList){
            int hour = busTimeInfo.getHour();
            DrawingInfo drawingInfo = drawingInfoMap.get(hour);
            if(drawingInfo == null){
                drawingInfo = new DrawingInfo();
                drawingInfoList.add(drawingInfo);
                drawingInfoMap.put(hour,drawingInfo);
                drawingInfo.hour = hour;
            }
            DayType dayType = busTimeInfo.getDayType();
            switch(dayType){
                case WORK_DAY:
                    drawingInfo.workDayList.add(busTimeInfo.getMinute());
                    break;
                case SATURDAY:
                    drawingInfo.saturDayList.add(busTimeInfo.getMinute());
                    break;
                case HOLIDAY:
                    drawingInfo.holiDayList.add(busTimeInfo.getMinute());
                    break;
                default:
                    break;
            }
        }
        Collections.sort(drawingInfoList);
        return drawingInfoList;
    }

    public int setTimeInfoList(List<BusTimeInfo> timeInfoList) {
        this.timeInfoList = timeInfoList;
        if(timeInfoList != null){
            drawingInfoList = genDrawingInfoList();
            int newMaxHeight = reCalMaxHeight();
            if(newMaxHeight > maxHeight){
                maxHeight = newMaxHeight;
            }
        }
        return maxHeight;
    }

    private int reCalMaxHeight(){
        int y1 = BUS_TIMETABLE_PADDING;
        //title
        y1 += fontHeight;
        if(drawingInfoList == null){
            return y1;
        }
        for(DrawingInfo drawInfo:drawingInfoList) {
            int maxCount = drawInfo.getMaxCount();
            int lines = maxCount / timeCountOneLine;
            if (maxCount % timeCountOneLine > 0) {
                lines++;
            }
            int lineHeight = fontHeight * lines;
            y1 = y1 + lineHeight;
        }
        return y1;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(int param) {
         this.maxHeight = param;
    }

    public int getMaxHeight(){
        return this.maxHeight;
    }


}
