package com.tory.nestedceiling.app.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tory.nestedceiling.app.R;import com.tory.nestedceiling.app.utils.SystemProperties;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by tao.xu2 on 2017/5/22.
 */

public class SystemBarUtils {

    public static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    public static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    public static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    public static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
    public static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";

    public static String sNavBarOverride;
    public static Boolean sHasNavbar;
    public static int sDarkModeFlag;
    public static Boolean sSupportNavbarDarkModeReflection;

    private static Boolean sMiuiDarkModSupport;
    private static Boolean sFlymeDarkModeSupport;

    static{
        String mv = SystemProperties.get("ro.miui.ui.version.name","");
        sMiuiDarkModSupport = "V6".equals(mv) || "V7".equals(mv) || "V8".equals(mv);
                //|| "V9".equals(mv);

        sNavBarOverride = SystemProperties.get("qemu.hw.mainkeys","");
    }

    /**
     * 透明状态栏
     * @param activity
     */
    public static void translucentStatusBar(@NonNull Activity activity){
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility() |
                   View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 透明导航栏
     * @param window
     */
    public static void translucentNavBar(@NonNull Window window){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility() |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    public static void translucentNavBar(@NonNull Activity activity){
        translucentNavBar(activity.getWindow());
    }

    public static boolean isSupportStatusBarDarkMode() {
        return sMiuiDarkModSupport || supportFlymeStatusBarDrakMode()
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static void setStatusBarColor(@NonNull Activity activity, @ColorInt int color) {
        setStatusBarColor(activity.getWindow(), color);
    }


    public static void setStatusBarColor(@NonNull Window window, @ColorInt int color) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);
    }

    public static boolean setStatusBarDarkMode(@NonNull Activity activity, boolean darkMode){
        return setStatusBarDarkMode(activity.getWindow(), darkMode);
    }

    public static boolean setStatusBarDarkMode(@NonNull Window window, boolean darkMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (sMiuiDarkModSupport) {
                return setMiuiStatusBarLightMode(window, darkMode);
            } else if (supportFlymeStatusBarDrakMode()) {
                return setFlymeStatusBarLightMode(window, darkMode);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                View decorView = window.getDecorView();
                int uiVisibility = decorView.getSystemUiVisibility();
                if(darkMode){
                    uiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }else{
                    uiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(uiVisibility);
                return true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        return false;
    }

    private static Boolean supportFlymeStatusBarDrakMode(){
        //flyme 6.0以上也是通过系统api切换statusbar的颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return false;
        }
        if(sFlymeDarkModeSupport == null){
            try {
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                darkFlag.setAccessible(true);
                int bit = darkFlag.getInt(null);
                sFlymeDarkModeSupport = bit != 0;
            } catch (Exception e) {
                sFlymeDarkModeSupport = false;
            }
        }
        return sFlymeDarkModeSupport;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean setMiuiStatusBarLightMode(Window window, boolean dark) {
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                return true;
            } catch (Exception e) {

            }
        }
        return false;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean setFlymeStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static void setNavigationBarDarkMode(@NonNull Activity activity, boolean darkMode){
        setNavigationBarDarkMode(activity.getWindow(), darkMode);
    }

    /**
     * 设置导致栏反色，支持
     * @param window
     * @param darkMode
     */
    public static void setNavigationBarDarkMode(@Nullable Window window, boolean darkMode){
        if(window == null || !hasNavBar(window.getContext())){
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setNavigationBarDarkMode(window, darkMode, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }else if(supportNavbarDarkModeReflection()){
            setNavigationBarDarkMode(window, darkMode, sDarkModeFlag);
        }
    }

    /**
     * 是否支持白色导航栏
     * @return
     */
    public static boolean supportNavbarDarkMode(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || supportNavbarDarkModeReflection();
    }

    private static Boolean supportNavbarDarkModeReflection(){
        if(sSupportNavbarDarkModeReflection == null){
            try {
                Field filed = View.class.getField("SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR");
                sDarkModeFlag = filed.getInt(View.class);
            } catch (Exception e) {
                e.printStackTrace();
                sDarkModeFlag = 0;
            }
            sSupportNavbarDarkModeReflection = sDarkModeFlag != 0;
        }
        return sSupportNavbarDarkModeReflection;
    }

    private static void setNavigationBarDarkMode(@NonNull Window window, boolean darkMode, int darkModeFlag){
        View decorView = window.getDecorView();
        int uiVisibility = decorView.getSystemUiVisibility();
        if(darkMode){
            uiVisibility |= darkModeFlag;
        }else{
            uiVisibility &= ~darkModeFlag;
        }
        decorView.setSystemUiVisibility(uiVisibility);
    }

    public static Boolean hasNavBar(@NonNull Context context) {
        if (sHasNavbar != null) {
            return sHasNavbar;
        }
        Resources res = context.getResources();
        int resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag (see static block)
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            sHasNavbar = hasNav;
            return sHasNavbar;
        } else { // fallback
            sHasNavbar = !ViewConfiguration.get(context).hasPermanentMenuKey();
            return sHasNavbar;
        }
    }


    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context){
        return getInternalDimensionSize(context.getResources(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    /**
     * 获取虚拟导航栏的高度
     * @param context
     * @return
     */
    @TargetApi(14)
    public static  int getNavigationBarHeight(Context context) {
        Resources res = context.getResources();
        int result = 0;
        if (hasNavBar(context)) {
            String key;
            boolean mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
            if (mInPortrait) {
                key = NAV_BAR_HEIGHT_RES_NAME;
            } else {
                key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
            }
            return getInternalDimensionSize(res, key);
        }
        return result;
    }

    /**
     * 获取虚拟导航栏的宽度
     * @param context
     * @return
     */
    @TargetApi(14)
    public static  int getNavigationBarWidth(Context context) {
        Resources res = context.getResources();
        int result = 0;
        if (hasNavBar(context)) {
            return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
        }
        return result;
    }

    /**
     * 获取一些系统隐藏的试题值
     * @param res
     * @param key
     * @return
     */
    public static  int getInternalDimensionSize(Resources res, String key) {
        int resId = res.getIdentifier(key, "dimen", "android");
        int result = resId > 0 ? res.getDimensionPixelSize(resId) : 0;
        return result;
    }

    /**
     * actionbar的高度
     * @param context
     * @return
     */
    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
        return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
    }

    public static void setRealFullUi(@NonNull Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
        // Set the content to appear under the system bars so that the
        // content doesn't resize when the system bars hide and show.
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        // Hide the nav bar and status bar
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
