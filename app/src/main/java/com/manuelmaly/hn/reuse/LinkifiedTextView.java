package com.manuelmaly.hn.reuse;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class LinkifiedTextView extends TextView {

    public LinkifiedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Inspired by http://stackoverflow.com/a/7327332/458603 - without this
     * custom code, clicks on links also trigger comment folding/unfolding.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TextView widget = (TextView) this;
        Object text = widget.getText();
        boolean linkHandled = false;
        if (text instanceof Spanned) {
            Spannable buffer = (Spannable) text;

            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                    }
                    linkHandled = true;
                    return true;
                }
            }
        }
        if (!linkHandled) {
            return super.onTouchEvent(event);
        } else
            return false;
    }

}
