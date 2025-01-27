package com.termux.api;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.termux.api.util.ResultReturner;

import java.io.PrintWriter;

public class NetworkAPI {

    static void onReceive(TermuxApiReceiver apiReceiver, final Context context, Intent intent) {
        final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clipData = clipboard.getPrimaryClip();

        boolean version2 = "2".equals(intent.getStringExtra("api_version"));
        if (version2) {
            boolean set = intent.getBooleanExtra("set", false);
            if (set) {
                ResultReturner.returnData(apiReceiver, intent, new ResultReturner.WithStringInput() {
                    @Override
                    protected boolean trimInput() {
                        return false;
                    }

                    @Override
                    public void writeResult(PrintWriter out) {
                        clipboard.setPrimaryClip(ClipData.newPlainText("", inputString));
                    }
                });
            } else {
                ResultReturner.returnData(apiReceiver, intent, out -> {
                    if (clipData == null) {
                        out.print("");
                    } else {
                        int itemCount = clipData.getItemCount();
                        for (int i = 0; i < itemCount; i++) {
                            Item item = clipData.getItemAt(i);
                            CharSequence text = item.coerceToText(context);
                            if (!TextUtils.isEmpty(text)) {
                                out.print(text);
                            }
                        }
                    }
                });
            }
        } else {
            final String newClipText = intent.getStringExtra("text");
            if (newClipText != null) {
                // Set clip.
                clipboard.setPrimaryClip(ClipData.newPlainText("", newClipText));
            }

            ResultReturner.returnData(apiReceiver, intent, out -> {
                if (newClipText == null) {
                    // Get clip.
                    if (clipData == null) {
                        out.print("");
                    } else {
                        int itemCount = clipData.getItemCount();
                        for (int i = 0; i < itemCount; i++) {
                            Item item = clipData.getItemAt(i);
                            CharSequence text = item.coerceToText(context);
                            if (!TextUtils.isEmpty(text)) {
                                out.print(text);
                            }
                        }
                    }
                }
            });
        }
    }

}
