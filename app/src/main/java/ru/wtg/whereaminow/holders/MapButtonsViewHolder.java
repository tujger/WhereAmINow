package ru.wtg.whereaminow.holders;

import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.SupportMapFragment;

import ru.wtg.whereaminow.State;
import ru.wtg.whereaminow.abstracts.AbstractView;
import ru.wtg.whereaminow.abstracts.AbstractViewHolder;
import ru.wtg.whereaminow.helpers.MyUser;

import static ru.wtg.whereaminow.State.EVENTS.MAP_MY_LOCATION_BUTTON_CLICKED;

/**
 * Created 11/29/16.
 */
public class MapButtonsViewHolder extends AbstractViewHolder {

    public MapButtonsViewHolder(SupportMapFragment mapFragment) {
        try {
            View myLocationButton = mapFragment.getView().findViewWithTag("GoogleMapMyLocationButton");

            if(myLocationButton != null) {
                myLocationButton.setVisibility(View.VISIBLE);
                myLocationButton.setEnabled(true);
                myLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        State.getInstance().fire(MAP_MY_LOCATION_BUTTON_CLICKED);
                    }
                });

                View zoomButtons = (View) mapFragment.getView().findViewWithTag("GoogleMapZoomInButton").getParent();

                int positionWidth = zoomButtons.getLayoutParams().width;
                int positionHeight = zoomButtons.getLayoutParams().height;

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(positionWidth, positionHeight);
                int margin = positionWidth / 5;
                params.setMargins(margin, 0, 0, margin);
                params.addRule(RelativeLayout.BELOW, myLocationButton.getId());
                params.addRule(RelativeLayout.ALIGN_LEFT, myLocationButton.getId());
                zoomButtons.setLayoutParams(params);

                /*params = new RelativeLayout.LayoutParams(positionWidth, positionHeight);
                margin = positionWidth / 5;
                params.setMargins(margin, 0, 0, margin);

                params.addRule(RelativeLayout.ALIGN_TOP, mapFragment.getView().getId());
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                myLocationButton.setLayoutParams(params);*/

            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public AbstractView create(MyUser myUser) {
        return null;
    }

    @Override
    public boolean dependsOnUser() {
        return false;
    }

    @Override
    public boolean dependsOnEvent() {
        return false;
    }


 /*   @Override
    public ArrayList<IntroRule> getIntro() {

//        MarkerView markerView = (MarkerView) State.getInstance().getMe().getEntity(TYPE);
//        LatLng latLng = new LatLng(markerView.myUser.getLocation().getLatitude(), markerView.myUser.getLocation().getLongitude());

        ArrayList<IntroRule> rules = new ArrayList<>();
//        rules.put(new IntroRule().setEvent(ACTIVITY_RESUME).setId("map_button_my_location").setViewTag("GoogleMapMyLocationButton").setTitle("My location").setDescription("Click here to center active member."));
//        rules.put(new IntroRule().setEvent(PREPARE_FAB).setId("fab_intro_menu").setView(fab_buttons).setTitle("Click any item to perform some action"));
//        rules.put(new IntroRule().setId("menu_set_name").setLinkTo(IntroRule.LINK_TO_OPTIONS_MENU).setTitle("Click menu"));

        return rules;
    }
*/
}
