/**
 * Created 1/20/17.
 */

function Main() {
    var holders = {};
    var users;
    var me;
    var layout;
    var right;
    var loading;
    var origin;
    var main = w = this;
    var progress;

    function start() {
//        firebase.auth().signInWithCustomToken(sign.token)
//        .then(function(e){
//            window.location.href = "/tracking/home";
//            return;

//        origin = window.location.href;
//        if (window.redirect && window.redirect.url) {
//            window.location.href = redirect.url;
//
//            setTimeout(loadBase,500);
//
//            // loadScripts.call(this);
//        } else {

//        if ('serviceWorker' in navigator) {
//            navigator.serviceWorker.register('/js/tracking/ServiceWorker.js').then(function(reg) {
//            // registered
//if(reg.installing) {
//      console.log('Service worker installing');
//    } else if(reg.waiting) {
//      console.log('Service worker installed');
//    } else if(reg.active) {
//      console.log('Service worker active');
//    }
//
//            }).catch(function(error) {
//            // not registered
//                console.log("ERROR SERVICE WORKER< REGULAR LOADING",error);
//                loadBase.call(main);
//            });
//        } else {
//            loadBase.call(main);
//        }
//        }
//        if(window.location.href == origin){
            var a = document.createElement("script");
            a.setAttribute("src","/js/helpers/Utils.js");
            a.setAttribute("onload","preloaded()");
            document.head.appendChild(a);
//        }
    }

    preloaded = function(){
        window.u = new Utils();

        main.layout = u.create("div", {className:"layout"}, document.body);

        loading = u.create("div", {style:{
            position: "fixed", top: 0, bottom: 0, left: 0, right: 0,
            zIndex: 10000, backgroundColor: "white", display: "flex", flexDirection: "column",
            justifyContent: "center", alignItems: "center", fontFamily: "sans-serif"
        }}, main.layout);
        u.create("div", {className:"progress-circle"}, loading);
        u.create("div", {className:"progress-title", innerHTML:"Service loading, please wait... "}, loading);
        progress = u.create("div", {className:"progress-title", innerHTML:"0%"}, loading);
//        window.onload = function() {
//            window.scrollTo(0, 1);
//            document.addEventListener("touchmove", function(e) { e.preventDefault() });
//        };

        //addConsoleLayer(main.right);

        u.require("/js/helpers/Constants", function(e){
            initializeHeader.call(main);
        });
    };

    function addConsoleLayer(parent) {
        var consoleLayer = u.create("div", {className: "console hidden", innerHTML:"Console:\n",
        onclick:function(){
            this.classList.add("hidden");
        }}, parent);
//        a.setAttribute("onclick","console.log(this);");
        var systemConsole = window.console.log;
        var errorConsole = window.console.error;
        window.console.log = function() {
            systemConsole(arguments);
            for(var i in arguments) {
                consoleLayer.innerHTML += arguments[i] + " ";
            }
            consoleLayer.innerHTML += "\n";
            consoleLayer.scrollTop = consoleLayer.scrollHeight;
        };
        window.console.error = function() {
            consoleLayer.classList.remove("hidden");
            errorConsole(arguments);
            for(var i in args) {
                consoleLayer.innerHTML += arguments[i] + " ";
            }
            consoleLayer.innerHTML += "\n";
            consoleLayer.scrollTop = consoleLayer.scrollHeight;
        };
        window.onerror = function(errorMsg, url, lineNumber){
            window.console.error(url+": "+lineNumber+", "+errorMsg);
        };
        return consoleLayer;
    }

    function initializeHeader() {

        u.create(HTML.META, {name:"viewport", content:"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0"}, document.head);
        u.create(HTML.LINK, {rel:HTML.STYLESHEET, href:"/css/tracking.css"}, document.head);
        u.create(HTML.LINK, {rel:HTML.STYLESHEET, href:"https://fonts.googleapis.com/icon?family=Material+Icons"},document.head);

        // u.create(HTML.LINK, {rel:"apple-touch-icon", href:"/images/apple-touch-icon.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-icon", href:"/images/apple-touch-icon.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-icon", sizes:"57x57", href:"/images/apple-touch-icon-57x57.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-icon", sizes:"60x60", href:"/images/apple-touch-icon-60x60.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-icon", sizes:"72x72", href:"/images/apple-touch-icon-72x72.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-icon", sizes:"76x76", href:"/images/apple-touch-icon-76x76.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-icon", sizes:"114x114", href:"/images/apple-touch-icon-114x114.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-icon", sizes:"120x120", href:"/images/apple-touch-icon-120x120.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-icon", sizes:"152x152", href:"/images/apple-touch-icon-152x152.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-icon", sizes:"180x180", href:"/images/apple-touch-icon.png"},document.head);
        // u.create(HTML.LINK, {rel:"apple-touch-startup-image", href: "/images/apple-touch-icon.png"},document.head);
        // u.create(HTML.LINK, {rel:"icon", type:"image/png", sizes:"32x32", href:"/images/favicon-32x32.png"},document.head);
        // u.create(HTML.LINK, {rel:"icon", type:"image/png", sizes:"16x16", href:"/images/favicon-16x16.png"},document.head);
        // u.create(HTML.LINK, {rel:"manifest", href:"/images/manifest.json"},document.head);
        // u.create(HTML.LINK, {rel:"mask-icon", href:"/images/safari-pinned-tab.svg", color:"#007574"},document.head);
        // u.create(HTML.LINK, {rel:"shortcut icon", href:"/images/favicon.ico"},document.head);
        // u.create(HTML.META, {name:"apple-mobile-web-app-capable", content:"yes"},document.head);
        // u.create(HTML.META, {name:"mobile-web-app-capable", content:"yes"},document.head);
        // u.create(HTML.META, {name:"application-name", content:"Waytogo"},document.head);
        // u.create(HTML.META, {name:"msapplication-config", content:"/images/browserconfig.xml"},document.head);
        // u.create(HTML.META, {name:"theme-color", content:"#ffffff"},document.head);

        loadScripts.call(main);
    }

    function loadScripts(){

        u.create("div", {className:"alpha", innerHTML:"α"}, main.layout);

        main.right = u.create("div", {className:"right changeable"}, main.layout);
        main.fire = fire;
        main.holders = holders;
        main.me = me;
        main.initialize = initialize;

        var files = [
            "https://www.gstatic.com/firebasejs/3.6.8/firebase-app.js",
            "https://www.gstatic.com/firebasejs/3.6.8/firebase-auth.js",
            "https://www.gstatic.com/firebasejs/3.6.8/firebase-database.js",
            // "https://code.jquery.com/jquery-3.1.1.min.js",
            "https://cdnjs.cloudflare.com/ajax/libs/fingerprintjs2/1.5.0/fingerprint2.min.js",
            "/js/helpers/MyUser",
            "/js/helpers/MyUsers",
            "/js/helpers/NoSleep.min.js",
            "PropertiesHolder",
            "AddressHolder",
            "ButtonHolder",
            "CameraHolder",
            "DistanceHolder",
            "DrawerHolder",
            // "FabHolder",
            "GpsHolder",
            "MapHolder",
            "MarkerHolder",
            "MessagesHolder",
            "NavigationHolder",
            "PlaceHolder",
            "SocialHolder",
            "StreetViewHolder",
            "TrackingHolder",
            "TrackHolder",
            "SampleHolder",
        ];
        // u.require("https://code.jquery.com/jquery-3.1.1.min.js");

        var loaded = 0;
        for(var i in files) {
            var file = files[i];
            if(!file.match(/^(https?:)|\//i)) file = "/js/holders/"+file;
            u.require(file, function(e) {
                loaded++;
                if(e && e.type) {
                    holders[e.type] = e;
                }
                progress.innerHTML = Math.ceil(loaded / files.length * 100) + "%";
                if(loaded == u.keys(files).length) {
                    console.log("Preload finished: "+loaded+" files done.");
                    loading.classList.add("hidden");

                    u.getUuid(initialize.bind(main));
                }
            }, main);
        }

        // main.right.webkitRequestFullScreen();


        /*window.addEventListener("load",function() { setTimeout(function(){ // This hides the address bar:
            window.scrollTo(0, 1); }, 0);
        });*/

    }

    function initialize() {

        var config = {
            apiKey: "AIzaSyCRH9g5rmQdvShE4mI2czumO17u_hwUF8Q",
            authDomain: "where-am-i-now-1373.firebaseapp.com",
            databaseURL: "https://where-am-i-now-1373.firebaseio.com",
            storageBucket: "where-am-i-now-1373.appspot.com",
            messagingSenderId: "365115596478"
        };
        firebase.initializeApp(config);
        if(!firebase.database || !firebase.auth) {
            console.error("Failed initializing, trying again...");
            initialize.bind(main);
            return;
        }
        database = firebase.database();

        if(me == null){
            main.me = me = new MyUser(main);
            me.user = true;
            me.color = "#0000FF";

            if(u.load("name")){
                me.name = u.load("name");
            }
            me.createViews();

            me.fire(EVENTS.SELECT_USER, 0);
            me.fire(EVENTS.CHANGE_COLOR, "#0000FF");
        }
        me.fire(EVENTS.MAKE_ACTIVE);

        main.users = users = new MyUsers(main);
        users.setMe();

        for(var x in holders){
            if(holders[x] && holders[x].start) holders[x].start();
        }

//        window.addEventListener("load", hideAddressBar );
//        window.addEventListener("orientationchange", hideAddressBar );
    }

    function hideAddressBar() {
        if(!window.location.hash) {
            if(document.height < window.outerHeight) {
                document.body.style.height = (window.outerHeight + 50) + 'px';
            }
            setTimeout( function(){ window.scrollTo(0, 1); }, 50 );
        }
    }

    function fire(EVENT,object) {
        for(var i in holders) {
            if(holders[i] && holders[i].dependsOnEvent && holders[i].onEvent) {
                try {
                    if (!holders[i].onEvent(EVENT, object)) break;
                } catch(e) {
                    console.error(i +": "+ e.message,e);
                }
            }
        }
    }

    return {
        start: start,
        main:main,
        fire:fire,
        initialize:initialize,
    }
}
//document.addEventListener("DOMContentLoaded", (window.WAIN = new Main()).start);
/*document.addEventListener("readystatechange", function(){
    if(document.readyState == "complete"){(
        window.WAIN = new Main()
    ).start()}
});*/
