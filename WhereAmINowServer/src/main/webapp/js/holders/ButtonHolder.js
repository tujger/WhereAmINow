/**
 * Created 2/11/17.
 */
function ButtonHolder(main) {

    var type = "button";
    var buttons;
    var contextMenu;
    var sections;
    var contextMenuLayout;
    var delayDismiss;

    function start() {
        // console.log("BUTTONHOLDER",this);
        buttons = u.create("div", {className:"user-buttons shadow hidden"}, main.right);
        contextMenuLayout = u.create("div", {className:"user-context-menu shadow hidden", onblur: function(){
            contextMenuLayout.classList.add("hidden");
        }, onmouseleave: function(){
            contextMenuLayout.classList.add("hidden");
        }, onmouseenter: function(){
            clearTimeout(delayDismiss);
        }
        }, main.right);
        contextMenu = new ContextMenu();
    }

    function onEvent(EVENT,object){
        // console.log(EVENT)
        switch (EVENT){
            case EVENTS.TRACKING_ACTIVE:
                buttons.classList.remove("hidden");
                break;
            case EVENTS.TRACKING_DISABLED:
                buttons.classList.add("hidden");
                break;
            case EVENTS.SELECT_USER:
                this.views.button.classList.add("selected");
                break;
            case EVENTS.UNSELECT_USER:
                this.views.button.classList.remove("selected");
                break;
            case EVENTS.CHANGE_NAME:
                var name;
                if(object){
                    name = object;
                } else {
                    if(this.number == main.me.number) {
                        name = "Me";
                    } else {
                        name = "Friend "+this.number;
                    }
                }
                this.views.button.children[1].innerHTML = name;
                break;
            case EVENTS.MAKE_ACTIVE:
                if(this.views && this.views.button && this.views.button.classList) this.views.button.classList.remove("hidden");
                break;
            case EVENTS.MAKE_INACTIVE:
                if(this.views && this.views.button && this.views.button.classList) this.views.button.classList.add("hidden");
                break;
            default:
                break;
        }
        return true;
    }

    var clicked = false;
    function createView(user){
        if(!user || !user.properties) return;
        var color = user.properties.color || "#0000FF";
        color = color.replace("#","").split("");
        var r = parseInt(color[0]+color[1],16);
        var g = parseInt(color[2]+color[3],16);
        var b = parseInt(color[4]+color[5],16);
        color = "rgba("+r+", "+g+", "+b+", 0.4)";

        var b = u.create("div", {className:"user-button" +(user.properties.active ? "" : " hidden"), style:{backgroundColor:color}, onclick: function(){
            if(clicked) {
                user.fire(EVENTS.CAMERA_ZOOM);
                clicked = false;
            } else {
                user.fire(EVENTS.SELECT_SINGLE_USER);
                clicked = true;
                setTimeout(function(){
                    clicked = false;
                }, 500);
                openContextMenu(user);
            }
            // console.log(user);
        }}, buttons);
        u.create("i", {className:"material-icons", innerHTML:"person"}, b);
        u.create("div", {className:"user-button-title",innerHTML:user.properties.getDisplayName()}, b);
        return b;
    }

    function openContextMenu(user) {
        // console.log(user);
        u.clear(contextMenuLayout);
        sections = [];
        for(var i = 0; i < 10; i ++) {
            sections[i] = u.create("div", {className:"user-context-menu-section hidden"}, contextMenuLayout);
        }
        user.fire(EVENTS.CREATE_CONTEXT_MENU, contextMenu);
        var size = user.views.button.getBoundingClientRect();

        contextMenuLayout.style.right = Math.floor(document.body.offsetWidth - size.left + 10) + "px";
        contextMenuLayout.style.top = Math.floor(size.top) + "px";
        contextMenuLayout.classList.remove("hidden");
        clearTimeout(delayDismiss);
        delayDismiss = setTimeout(function(){
            contextMenuLayout.classList.add("hidden");
        },2000);
    }

    function ContextMenu() {

        function add(section,id,name,icon,callback) {
            var th = u.create("div", {className:"user-context-menu-item"}, sections[section]);
            u.create("i", { className:"material-icons md-14", innerHTML: icon }, th);
            u.create("div", { className:"user-context-menu-item-title", onclick: function() {
                setTimeout(function(){
                    contextMenuLayout.blur();
                    callback();
                }, 300);
            }, innerHTML: name}, th);
            sections[section].classList.remove("hidden");
        }
        function getContextMenu(){
            console.log("GETCONTEXTMENU:",items);
        }

        return {
            add:add,
            getContextMenu:getContextMenu,
        }
    }

    return {
        type:type,
        start:start,
        dependsOnEvent:true,
        dependsOnUser:true,
        onEvent:onEvent,
        createView:createView,
    }
}