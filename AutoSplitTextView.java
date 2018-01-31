import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * TextView按行拆分文本
 * Created by dell
 */

public class AutoSplitTextView extends android.support.v7.widget.AppCompatTextView implements ViewTreeObserver.OnGlobalLayoutListener {
    private boolean mEnabled = true;

    public AutoSplitTextView(Context context) {
        super(context);
    }

    public AutoSplitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoSplitTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置是否支持换行
     * @param enabled
     */
    public void setAutoSplitEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public void setAutoSplitText(String text){
        this.setText(text);
        if(mEnabled){
            this.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    private void autoSplitText(final TextView textView) {
        final String rawText = textView.getText().toString(); //原始文本
        final Paint tvPaint = textView.getPaint(); //paint，包含字体等信息
        final float tvWidth = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight(); //控件可用宽度
        //只有获取到View的宽度的时候，才进行换行判断，否则会造成死循环
        if(tvWidth>0) {
            //将原始文本按行拆分
            String[] rawTextLines = rawText.replaceAll("\r", "").split("\n");
            StringBuilder sbNewText = new StringBuilder();
            for (String rawTextLine : rawTextLines) {
                if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                    //如果整行宽度在控件可用宽度之内，就不处理了
                    sbNewText.append(rawTextLine);
                } else {
                    //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                    float lineWidth = 0;
                    for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                        char ch = rawTextLine.charAt(cnt);
                        lineWidth += tvPaint.measureText(String.valueOf(ch));
                        if (lineWidth < tvWidth) {
                            sbNewText.append(ch);
                        } else {
                            sbNewText.append("\n");
                            lineWidth = 0;
                            --cnt;
                        }
                    }
                }
                sbNewText.append("\n");
            }

            //把结尾多余的\n去掉
            if (!rawText.endsWith("\n")) {
                sbNewText.deleteCharAt(sbNewText.length() - 1);
            }
            if (!TextUtils.isEmpty(sbNewText.toString())) {
                textView.setText(sbNewText.toString());
            }
        }
    }

    @Override
    public void onGlobalLayout() {
        autoSplitText(this);
        AutoSplitTextView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }
}
