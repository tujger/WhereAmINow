/**
 * Created 2/9/17.
 */
EVENTS.NEW_MESSAGE = "new_message";
EVENTS.SEND_MESSAGE = "send_message";
EVENTS.PRIVATE_MESSAGE = "private";
EVENTS.USER_MESSAGE = "user_message";
EVENTS.WELCOME_MESSAGE = "welcome_message";

function MessagesHolder(main) {

    var type = "message";
    var chat;
    var messages;
    var reply;
    var replyTo;
    var replyInput;
    var replyButton;
    var lastReadTimestamp;

    function start() {
        // console.log("MESSAGESHOLDER",main);

        chat = u.dialog({
            title: "Chat",
            className: "chat",
            tabindex: 3,
            items: [
                { type: HTML.DIV, className: "chat-messages" },
                { type: HTML.DIV, className: "chat-reply" }
            ],
            negative: {
                onclick: function(){
                    u.save("message:chat");
                }
            },
            onopen: function() {
                lastReadTimestamp = new Date().getTime();
                u.save("message:lastread", lastReadTimestamp);
            }
        });

        messages = chat.items[0];
        reply = chat.items[1];
        replyTo = u.create(HTML.INPUT, {type:HTML.HIDDEN, value:""}, reply);
        replyInput = u.create(HTML.INPUT, {className: "chat-reply-input", tabindex:5, onkeyup:function(e){
            if(e.keyCode == 13) {
                replyButton.click();
            }
        }, onclick: function(){
            this.focus();
        }}, reply);
        replyButton = u.create(HTML.BUTTON, {className: "chat-reply-button", innerHTML:"send", onclick:sendUserMessage}, reply);

        if(u.load("message:chat")) chat.classList.remove("hidden");
        lastReadTimestamp = u.load("message:lastread");

    }

    function onEvent(EVENT,object){
        switch (EVENT){
            case EVENTS.CREATE_DRAWER:
                var item = object.add(DRAWER.SECTION_COMMUNICATION,type+"_1","Chat","chat",function(){
                    if(chat.classList.contains("hidden")) {
                        u.save("message:chat", true);
                        chat.onopen();
                        chat.focus();
                        replyInput.focus();
                        main.users.forAllUsers(function(number,user){
                            user.fire(EVENTS.HIDE_BADGE);
                        });

                    } else {
                        u.save("message:chat");
                        chat.onclose();
                    }
                });
                break;
            case EVENTS.CREATE_CONTEXT_MENU:
                var user = this;
                if(user != main.me) {
                    object.add(MENU.SECTION_COMMUNICATION, type + "_1", "Private message", "chat", function () {
                        chat.classList.remove("hidden");
                        replyTo.value = user.properties.number;
                        replyInput.focus();
                    });
                }
                break;
            case EVENTS.USER_MESSAGE:
                var div = u.create(HTML.DIV, {
                    className:"chat-message" + (object.private ? " chat-message-private" : ""),
                    dataTimestamp: object.timestamp,
                });
                u.create(HTML.DIV, {className:"chat-message-timestamp", innerHTML: new Date(object.timestamp).toLocaleString()}, div);

                var toUser = null;
                if(object.private) {
                    toUser = main.users.users[object.to] || main.me;
                }

                u.create(HTML.DIV, {
                    className:"chat-message-name",
                    style: {color: this.properties.color},
                    innerHTML:this.properties.getDisplayName() + (object.private ? " → " + toUser.properties.getDisplayName() : "") + ":"}, div);
                u.create(HTML.DIV, {className:"chat-message-body", innerHTML: object.body}, div);

                var inserted = false;
                for(var i = messages.children.length - 1; i >= 0; i--) {
                    var current = messages.children[i];
                    if(parseInt(current.dataset.timestamp) > object.timestamp) {
                        messages.insertBefore(div, current);
                        inserted = true;
                    }
                }
                if(!inserted) messages.appendChild(div);

                div.scrollIntoView();

                if(object.timestamp > lastReadTimestamp && chat.classList.contains("hidden")) {
                    this.fire(EVENTS.SHOW_BADGE, EVENTS.INCREASE_BADGE);
                }
                break;
            default:
                break;
        }
        return true;
    }

    function createView(user){
        return {
            user:user,
            messages:[],
        }
    }

    function sendUserMessage(){
        try {
            var text = replyInput.value;
            if(!text) return;
            replyInput.value = "";

            main.tracking.put(USER.MESSAGE, text);
            if(replyTo.value) {
                main.tracking.put(RESPONSE.PRIVATE, parseInt(replyTo.value));
                main.me.fire(EVENTS.USER_MESSAGE, {body: text, timestamp: new Date().getTime(), private: true, to: parseInt(replyTo.value)});
                replyTo.value = "";
            }
            main.tracking.put(REQUEST.DELIVERY_CONFIRMATION, true);
            main.tracking.put(REQUEST.MESSAGE, text);

            main.tracking.send(REQUEST.MESSAGE);

        } catch(e) {
            console.error(e);
        }
    }

    function perform(json) {
        var number = json[USER.NUMBER];
        var text = json[USER.MESSAGE];
        var time = json[REQUEST.TIMESTAMP];
        var key = json["key"];
        var private = json[EVENTS.PRIVATE_MESSAGE] || false;

        main.users.forUser(number, function(number,user){
                user.fire(EVENTS.USER_MESSAGE, {body: text, timestamp: time, key: key, private: private});
        });
    }

    return {
        type:type,
        start:start,
        dependsOnEvent:true,
        onEvent:onEvent,
        dependsOnUser:true,
        createView:createView,
        perform:perform,
        saveable:true,
        loadsaved:-1,
    }
}