package io.virtualapp.utils;

import androidx.appcompat.app.AlertDialog;

/**
 * @author weishu
 * @date 2018/7/5.
 */
public class DialogUtil {
    public static void showDialog(AlertDialog dialog) {
        if (dialog == null) {
            return;
        }
        try {
            dialog.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
