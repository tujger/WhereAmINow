/**
 * Part of Waytous <http://waytous.net>
 * Copyright (C) Edeqa LLC <http://www.edeqa.com>
 *
 * Version 1.${SERVER_BUILD}
 * Created 1/19/17.
 */
function Groups() {

    var title = "Groups";

    var alertArea;
    var trhead;
    var table;
    var user;
    var firebaseToken;
    var div;
    var groupNodes = {};
    var ref;

    var renderInterface = function() {

        div = document.getElementsByClassName("layout")[0];
        u.clear(div);
//        u.create("div", {className:"summary"}, div);
//        u.create("h2", "Groups", div);
        ref = database.ref();

        table = u.table({
            id: "groups",
            caption: {
                items: [
                    { label: "ID" },
                    { label: "Requires password", className: "media-hidden" },
                    { label: "Persistent", className: "media-hidden" },
                    { label: "Time to live, min", className: "media-hidden" },
                    { label: "Dismiss inactive, sec", className: "media-hidden" },
                    { label: "Users" },
                    { label: "Created", className: "media-hidden" },
                    { label: "Updated" }
                ]
            },
            placeholder: "No data, try to refresh page."
        }, div);

        u.create("br", null, div);
        buttons = u.create("div", {className:"buttons"}, div);
        renderButtons(buttons);
    }


    function updateData(){

        var initial = true;
        setTimeout(function(){initial = false;}, 3000);
        var resign = true;

        table.placeholder.show();
        u.clear(table.body);

        ref.child(DATABASE.SECTION_GROUPS).off();
        ref.child(DATABASE.SECTION_GROUPS).on("child_added", function(data) {
            resign = false;
            ref.child(data.key).child(DATABASE.SECTION_OPTIONS).once("value").then(function(snapshot) {
                if(!snapshot || !snapshot.val()) return;

                var row = table.add({
                    id: data.key,
                    className: "highlight",
                    onclick: function(){
                        WTU.switchTo("/admin/group/"+data.key);
                        return false;
                    },
                    cells: [
                        { innerHTML: data.key },
                        { className: "media-hidden", innerHTML:snapshot.val()[DATABASE.OPTION_REQUIRES_PASSWORD] ? "Yes" : "No" },
                        { className: "media-hidden", innerHTML:snapshot.val()[DATABASE.OPTION_PERSISTENT] ? "Yes" : "No" },
                        { className: "media-hidden", innerHTML:snapshot.val()[DATABASE.OPTION_PERSISTENT] ? "&#150;" : snapshot.val()[DATABASE.OPTION_TIME_TO_LIVE_IF_EMPTY] },
                        { className: "media-hidden", innerHTML:snapshot.val()[DATABASE.OPTION_DISMISS_INACTIVE] ? snapshot.val()[DATABASE.OPTION_DELAY_TO_DISMISS] : "&#150;" },
                        { innerHTML: "..." },
                        { className: "media-hidden", sort: snapshot.val()[DATABASE.OPTION_DATE_CREATED], innerHTML:snapshot.val()[DATABASE.OPTION_DATE_CREATED] ? new Date(snapshot.val()[DATABASE.OPTION_DATE_CREATED]).toLocaleString() : "&#150;" },
                        { sort: 0, innerHTML:"..." }
                    ]
                });
                var usersNode = row.cells[5]
                var changedNode = row.cells[7]

                ref.child(data.key).child(DATABASE.SECTION_USERS_DATA).on("value", function(snapshot){
                    if(!snapshot.val()) return;

                    var changed = 0, active = 0, total = 0;
                    for(var i in snapshot.val()) {
                        total++;
                        var c = parseInt(snapshot.val()[i][DATABASE.USER_CREATED]);
                        if(c > changed) changed = c;
                        if(snapshot.val()[i][DATABASE.USER_ACTIVE]) active ++;
                    }
                    usersNode.innerHTML = active + " / " + total;

                    var changed = 0;
                    for(var i in snapshot.val()) {
                        var c = parseInt(snapshot.val()[i][DATABASE.USER_CHANGED]);
                        if(c > changed) changed = c;
                    }
                    changedNode.sort = changed;
                    changedNode.innerHTML = new Date(changed).toLocaleString();
                    if(!initial) row.classList.add("changed");
                    setTimeout(function(){row.classList.remove("changed")}, 5000);
                    table.update();
                });
            }).catch(function(error){
                console.error(error);
                table.placeholder.show();
            });
        }, function(e) {
            console.warn("Resign because of",e.message);
            resign = true;
            WTU.resign(updateData);
        });
        ref.child(DATABASE.SECTION_GROUPS).on("child_removed", function(data) {
            for(var i in table.rows) {
                if(table.rows[i].id == data.key) {
                    table.body.removeChild(table.rows[i]);
                    table.rows.splice(i,1);
                }
            }
            u.toast.show("Group "+data.getKey()+" was removed.");
        }, function(error){
            console.error("REMOVED",error);

        })
    }

    function renderButtons(div) {
        u.clear(div);
        u.create(HTML.BUTTON, { innerHTML:"Clean groups", onclick: cleanGroupsQuestion}, div);
    }

    function cleanGroupsQuestion(e){
        u.clear(buttons);
        u.create({className:"question", innerHTML: "This will check expired users and groups immediately using default options. Continue?"}, buttons);
        u.create(HTML.BUTTON,{ className:"question", innerHTML:"Yes", onclick: function() {
           renderButtons(buttons);
           u.toast.show("Clean groups is performing.");
           u.put("/admin/rest/v1/groups/clean")
            .then(function(xhr){
//               WTU.switchTo("/admin/groups");
            }).catch(function(code,xhr){
//               console.warn("Resign because of",code,xhr);
//               WTU.resign(updateData);
               var res = JSON.parse(xhr.responseText) || {};
               u.toast.show(res.message || xhr.statusText);
             });
        }}, buttons);
        u.create(HTML.BUTTON,{ innerHTML:"No", onclick: function(){
            renderButtons(buttons);
        }}, buttons);
    }

    return {
        start: function() {
            renderInterface();
            updateData();
        },
        page: "groups",
        icon: "group",
        title: title,
        menu: title,
        move:true,
    }
}


