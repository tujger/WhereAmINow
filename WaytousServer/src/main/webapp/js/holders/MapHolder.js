/**
 * Created 2/9/17.
 */
EVENTS.REQUEST_MODE_DAY = "request_mode_day";
EVENTS.REQUEST_MODE_NIGHT = "request_mode_night";
EVENTS.REQUEST_MODE_NORMAL = "request_mode_normal";
EVENTS.REQUEST_MODE_SATELLITE = "request_mode_satellite";
EVENTS.REQUEST_MODE_TERRAIN = "request_mode_terrain";
EVENTS.REQUEST_MODE_TRAFFIC = "request_mode_traffic";
EVENTS.REQUEST_MODE_TRANSIT = "request_mode_transit";
EVENTS.REQUEST_MODE_BIKE = "request_mode_bike";

function MapHolder(main) {

    var map;
    var trafficLayer;
    var transitLayer;
    var bikeLayer;
    var buttonRecenter;

    u.create("div", {id: "map"}, main.right);

    function start() {
        u.create(HTML.SCRIPT, {
            src: "https://maps.googleapis.com/maps/api/js?key=AIzaSyCRH9g5rmQdvShE4mI2czumO17u_hwUF8Q&callback=initMap&libraries=geometry,places",
            async: "",
            defer: ""
        }, document.head);
        buttonRecenter = u.create(HTML.BUTTON, {
            className: "map-recenter hidden",
            innerHTML: u.lang.re_center,
            onclick: function() {
                main.fire(EVENTS.CAMERA_UPDATE);
                google.maps.event.trigger(map, "resize");
                main.users.forAllUsers(function(number,user){
                    if(user.views.properties.selected) user.fire(EVENTS.SELECT_USER);
                });
                this.classList.add("hidden");
                return false;
            }
        }, main.right);
    }

    window.initMap = function() {
        // Create a map object and specify the DOM element for display.
        var center = u.load("map:coords") || {};
        center.lat = center.lat || 38.93421936035156;
        center.lng = center.lng || -77.35877990722656;
        center.zoom = center.zoom || 15;
        map = new google.maps.Map(document.getElementById("map"), {
            center: {lat: center.lat, lng: center.lng},
            scrollwheel: true,
            panControl: false,
            zoom: center.zoom,
            zoomControl: true,
            zoomControlOptions: {
                position: google.maps.ControlPosition.TOP_RIGHT
            },
            mapTypeControl: false,
            scaleControl: true,
            streetViewControl: false,
            fullscreenControl: false,
            /*streetViewControlOptions: {
                position: google.maps.ControlPosition.RIGHT_TOP
            },*/
            overviewMapControl: true,
            rotateControl: true,
        });
        main.map = map;
        utils.label.prototype = new google.maps.OverlayView;
        main.fire(EVENTS.MAP_READY);
        main.map.addListener("zoom_changed", function() {
            main.fire(EVENTS.CAMERA_ZOOM, main.map.getZoom());
        });
        main.map.addListener("dragstart", function() {
            buttonRecenter.classList.remove("hidden");
        });
    }

    function onEvent(EVENT,object){
        var center = main.map && main.map.getCenter();
        if(center) {
            u.save("map:coords", {
                lat:center.lat(),
                lng:center.lng(),
                zoom:main.map.getZoom()
            });
        }
        switch (EVENT){
            case EVENTS.CREATE_DRAWER:
                object.add(DRAWER.SECTION_MAP, EVENTS.REQUEST_MODE_TRAFFIC, u.lang.traffic, "traffic", function(){
                    main.fire(EVENTS.REQUEST_MODE_TRAFFIC);
                });
                object.add(DRAWER.SECTION_MAP, EVENTS.REQUEST_MODE_TRANSIT, u.lang.transit, "directions_transit", function(){
                    main.fire(EVENTS.REQUEST_MODE_TRANSIT);
                });
                object.add(DRAWER.SECTION_MAP, EVENTS.REQUEST_MODE_BIKE, u.lang.bicycle, "directions_bike", function(){
                    main.fire(EVENTS.REQUEST_MODE_BIKE);
                });
                object.add(DRAWER.SECTION_MAP, EVENTS.REQUEST_MODE_SATELLITE, u.lang.satellite, "satellite", function(){
                    main.fire(EVENTS.REQUEST_MODE_SATELLITE);
                });
                object.add(DRAWER.SECTION_MAP, EVENTS.REQUEST_MODE_TERRAIN, u.lang.terrain, "terrain", function(){
                    main.fire(EVENTS.REQUEST_MODE_TERRAIN);
                });
                break;
            case EVENTS.REQUEST_MODE_TRAFFIC:
                if(trafficLayer){
                    trafficLayer.setMap(null);
                    trafficLayer = null;
                } else {
                    if(transitLayer) {
                        transitLayer.setMap(null);
                        transitLayer = null;
                    }
                    if(bikeLayer){
                        bikeLayer.setMap(null);
                        bikeLayer = null;
                    }
                    trafficLayer = new google.maps.TrafficLayer();
                    trafficLayer.setMap(map);
                }
                break;
            case EVENTS.REQUEST_MODE_TRANSIT:
                if(transitLayer){
                    transitLayer.setMap(null);
                    transitLayer = null;
                } else {
                    if(trafficLayer) {
                        trafficLayer.setMap(null);
                        trafficLayer = null;
                    }
                    if(bikeLayer){
                        bikeLayer.setMap(null);
                        bikeLayer = null;
                    }

                    transitLayer = new google.maps.TransitLayer();
                    transitLayer.setMap(map);
                }
                break;
            case EVENTS.REQUEST_MODE_BIKE:
                if(bikeLayer){
                    bikeLayer.setMap(null);
                    bikeLayer = null;
                } else {
                    if(transitLayer) {
                        transitLayer.setMap(null);
                        transitLayer = null;
                    }
                    if(trafficLayer){
                        trafficLayer.setMap(null);
                        trafficLayer = null;
                    }
                    bikeLayer = new google.maps.BicyclingLayer();
                    bikeLayer.setMap(map);
                }
                break;
            case EVENTS.REQUEST_MODE_NORMAL:
                if(map){
                    map.setMapTypeId(google.maps.MapTypeId.ROADMAP);
                }
                break;
            case EVENTS.REQUEST_MODE_SATELLITE:
                if(map && map.getMapTypeId() != google.maps.MapTypeId.SATELLITE){
                    map.setMapTypeId(google.maps.MapTypeId.SATELLITE);
                } else {
                    main.fire(EVENTS.REQUEST_MODE_NORMAL);
                }
                break;
            case EVENTS.REQUEST_MODE_TERRAIN:
                if(map && map.getMapTypeId() != google.maps.MapTypeId.TERRAIN){
                    map.setMapTypeId(google.maps.MapTypeId.TERRAIN);
                } else {
                    main.fire(EVENTS.REQUEST_MODE_NORMAL);
                }
                break;
            case EVENTS.SELECT_USER:
                buttonRecenter && buttonRecenter.classList.add("hidden");
                break;
            default:
                break;
        }
        return true;
    }

    function createView() {
        return {};
    }

    return {
        type:"map",
        start:start,
        onEvent:onEvent,
        createView:createView,
        map:map,
    }
}