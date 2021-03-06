package com.github.badoualy.ui.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.badoualy.ui.listener.ActionBarHandler;
import com.github.badoualy.ui.listener.FragmentTransactionHandler;
import com.github.badoualy.ui.listener.NavigationDrawerHandler;
import com.github.badoualy.ui.listener.SnackHandler;


/**
 A BasicFragment that should be extended by almost all fragments except fragments displayed in ViewPager
 */
public abstract class BaseFragment extends DelegateFragment {

    /**
     EventBus default priority, lower than BaseFragment
     */
    protected static final int DEFAULT_PRIORITY = 300;

    /**
     Display back-arrow instead of NavigationDrawer toggle
     */
    public static final int DISPLAY_HOME_AS_UP = 1;
    /**
     Lock NavigationDrawer closed
     */
    public static final int DISPLAY_DRAWER_LOCKED = 1 << 1;
    /**
     Has menu to inflate in ActionBar
     */
    public static final int DISPLAY_HAS_MENU = 1 << 2;
    /**
     Hide the title in ActionBar
     */
    public static final int DISPLAY_NO_TITLE = 1 << 3;
    /**
     Hide the navigation icon in ActionBar
     */
    public static final int DISPLAY_NO_NAVIGATION_ICON = 1 << 4;

    private FragmentTransactionHandler transactionHandler;
    private ActionBarHandler actionBarHandler;
    private NavigationDrawerHandler navDrawerHandler;
    private SnackHandler snackHandler;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof FragmentTransactionHandler) {
            transactionHandler = (FragmentTransactionHandler) activity;
        } else {
            Log.d(TAG, "Activity is not a " + FragmentTransactionHandler.class.getSimpleName());
        }

        if (activity instanceof ActionBarHandler) {
            actionBarHandler = (ActionBarHandler) activity;
        } else {
            Log.d(TAG, "Activity is not a " + ActionBarHandler.class.getSimpleName());
        }

        if (activity instanceof NavigationDrawerHandler) {
            navDrawerHandler = (NavigationDrawerHandler) activity;
        } else {
            Log.d(TAG, "Activity is not a " + NavigationDrawerHandler.class.getSimpleName());
        }

        if (activity instanceof SnackHandler) {
            snackHandler = (SnackHandler) activity;
        } else {
            Log.d(TAG, "Activity is not a " + SnackHandler.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        transactionHandler = null;
        actionBarHandler = null;
        navDrawerHandler = null;
        snackHandler = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        register();
    }

    @Override
    public void onStop() {
        unregister();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(containsFlags(DISPLAY_HAS_MENU));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(!containsFlags(DISPLAY_NO_TITLE) && getTitle() != null);
            actionBar.setTitle(getTitle());
            actionBar.setSubtitle(getSubtitle());
        } else {
            Log.d(TAG, "SupportActionBar is null");
        }

        if (getToolbar() != null) {
            Drawable navigationIcon = getToolbar().getNavigationIcon();
            if (navigationIcon != null)
                navigationIcon.setAlpha(containsFlags(DISPLAY_NO_NAVIGATION_ICON) ? 0 : 255);
        }

        if (actionBarHandler != null) {
            actionBarHandler.setHomeAsUpEnabled(containsFlags(DISPLAY_HOME_AS_UP));
        } else {
            Log.d(TAG, "ActionBarHandler is null");
        }

        if (navDrawerHandler != null) {
            navDrawerHandler
                    .setNavigationDrawerLocked(containsFlags(DISPLAY_DRAWER_LOCKED | DISPLAY_NO_NAVIGATION_ICON));
        } else {
            Log.d(TAG, "navDrawerHandler is null");
        }
    }

    @Override
    public void onPause() {
        closeKeyboard();
        super.onPause();
    }

    /**
     Called when back button is pressed

     @return true if the event was consumed
     */
    public boolean onBackPressed() {
        return false;
    }

    /**
     @return the title to display in the ActionBar
     */
    protected String getTitle() {
        return null;
    }

    /**
     @return the subtitle to display in the ActionBar
     */
    protected String getSubtitle() {
        return null;
    }

    /**
     @return a combination of DISPLAY_* flags
     */
    protected int getDisplayFlags() {
        return 0;
    }

    /**
     @return true if {@link com.github.badoualy.ui.fragment.BaseFragment#getDisplayFlags()} contains the given flags
     */
    private boolean containsFlags(int flags) {
        return (getDisplayFlags() & flags) != 0;
    }

    public Toolbar getToolbar() {
        return actionBarHandler != null ? actionBarHandler.getToolbar() : null;
    }

    /**
     Overwrite this to register to components (EventBus, ...). This method is called from onStart()
     */
    public void register() {

    }

    /** Overwrite this to unregister to components (EventBus, ...). This method is called from onStop() */
    public void unregister() {

    }

    /** Callback method when the Navigation Drawer is opened */
    public void onDrawerOpened() {

    }

    /** Callback method when the Navigation Drawer is closed */
    public void onDrawerClosed() {

    }

    public ActionBarHandler getActionBarHandler() {
        return actionBarHandler;
    }

    public FragmentTransactionHandler getTransactionHandler() {
        return transactionHandler;
    }

    public NavigationDrawerHandler getNavDrawerHandler() {
        return navDrawerHandler;
    }

    public SnackHandler getSnackHandler() {
        return snackHandler;
    }
}