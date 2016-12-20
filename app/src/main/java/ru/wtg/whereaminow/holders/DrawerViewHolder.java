package ru.wtg.whereaminow.holders;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import ru.wtg.whereaminow.MainActivity;
import ru.wtg.whereaminow.R;
import ru.wtg.whereaminow.State;
import ru.wtg.whereaminow.helpers.MyUser;
import ru.wtg.whereaminow.interfaces.SimpleCallback;

import static ru.wtg.whereaminow.State.ACTIVITY_RESUME;
import static ru.wtg.whereaminow.State.CREATE_DRAWER;
import static ru.wtg.whereaminow.State.PREPARE_DRAWER;

/**
 * Created 11/27/16.
 */
public class DrawerViewHolder extends AbstractViewHolder {

    public static final String TYPE = "drawer";

    private final MainActivity context;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    public DrawerViewHolder(MainActivity context){
        this.context = context;
    }

    public DrawerViewHolder setViewAndToolbar(View view, final Toolbar toolbar) {
        drawer = (DrawerLayout) view;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                context, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (!isDrawerOpen()) {
                        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_custom);
                        menuItem.setVisible(false);
                        State.getInstance().fire(PREPARE_DRAWER, menuItem);

                    } else {
                        System.out.println("CLOSING");
                    }
                    drawer.invalidate();
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) drawer.findViewById(R.id.nav_view);
        return this;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean dependsOnUser() {
        return false;
    }

    @Override
    public boolean dependsOnEvent() {
        return true;
    }

    @Override
    public AbstractView create(MyUser myUser) {
        return null;
    }

    @Override
    public boolean onEvent(String event, Object object) {
        switch(event){
            case ACTIVITY_RESUME:
                MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_custom);
                menuItem.setVisible(false);
                State.getInstance().fire(CREATE_DRAWER, menuItem);

                break;
        }
        return true;
    }

    public DrawerViewHolder setCallback(final SimpleCallback callback) {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                callback.call(item.getItemId());
                drawer.closeDrawer(GravityCompat.START);

                return false;
            }
        });
        return this;
    }

    public boolean isDrawerOpen() {
        if(drawer == null) return false;
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

}
