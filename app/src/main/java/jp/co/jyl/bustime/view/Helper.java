package jp.co.jyl.bustime.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jiang on 2015/05/01.
 */
public class Helper {

    private static final int TEXT_PADDING = 2;
    public static final int TEXT_ALIGN_LEFT = 1;
    public static final int TEXT_ALIGN_CENTER = 2;
    public static final int TEXT_ALIGN_RIGHT = 3;

    private Helper(){}

    public static void saveStringToPreference(Context context,String key,String value){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static String getStringFromPreference(Context context,String key){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key,null);
    }


    public static String genBusRouteGoing(String routeName,String going,String goingSuffix){
        StringBuilder sb = new StringBuilder();
        sb.append(routeName);
        sb.append(' ');
        sb.append(going);
        sb.append(goingSuffix);
        return sb.toString();
    }

    public static void setTextView(Activity activity,int id,String text){
        TextView tv = (TextView)activity.findViewById(id);
        tv.setText(text);
    }

    /**
     * 短いトーストを表示する
     * @param context　コンテキスト
     * @param msgId R.stringで定義されたメッセージID
     */
    public static void showShortToast(Context context,int msgId){
        Toast toast = Toast.makeText(context,context.getString(msgId),
                Toast.LENGTH_SHORT);
        toast.show();
    }

    public static float getTextSizeForWidth(Paint paint, float desiredWidth,
                                      String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        return desiredTextSize;
    }

    public static void drawIcon(Canvas canvas, Bitmap bmp,Rect rect,int iconSize,Paint paint){
        int x1 = rect.left;
        int y1 = rect.top;
        int x2 = rect.right;
        int y2 = rect.bottom;
        // 中心座標
        float centerX = x1 + (x2 - x1)  / 2;
        float centerY = y1 + (y2 - y1) / 2;

        Rect rectSrc = new Rect(0,0,bmp.getWidth(),bmp.getHeight());
        Rect rectDes = new Rect((int)(centerX - iconSize / 2),(int)(centerY - iconSize / 2),
                (int)(centerX + iconSize/2),(int)(centerY + iconSize/2));
        canvas.drawBitmap(bmp,rectSrc,rectDes,paint);
    }

    public static float getTextHeight(String text,Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float h = fontMetrics.bottom - fontMetrics.top + TEXT_PADDING;
        return h;
    }

    public static void drawText(Canvas canvas,String text,Rect rect,
                          int color,Paint paint,int textAlign){
        int x1 = rect.left;
        int y1 = rect.top;
        int x2 = rect.right;
        int y2 = rect.bottom;
        // 中心座標
        float centerX = x1 + (x2 - x1)  / 2;
        float centerY = y1 + (y2 - y1) / 2;

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        // 文字列の幅を取得
        float textWidth = paint.measureText( text);

        // 中心にしたいX座標から文字列の幅の半分を引く
        float baseX = centerX - textWidth / 2;
        if(textAlign == TEXT_ALIGN_LEFT){
            baseX =  x1 + TEXT_PADDING;
        }else if(textAlign == TEXT_ALIGN_RIGHT){
            baseX =  x2 - TEXT_PADDING - textWidth;
        }
        // 中心にしたいY座標からAscentとDescentの半分を引く
        float a1 = fontMetrics.ascent;
        float d1 = fontMetrics.descent;
        float baseY = centerY -  (fontMetrics.ascent + fontMetrics.descent) / 2;

        // テキストの描画
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawText( text, baseX, baseY, paint);
    }

}
