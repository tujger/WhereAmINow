/**
 * Created 1/19/17.
 */
function Home() {

    var title = "Home";

    var u = new Utils();

    var start = function() {
        div = document.getElementsByClassName("content")[0];

        u.create("p", "To be implemented soon...", div);

    }

    return {
        start: start,
        page: "home",
        icon: "home",
        title: title,
        menu: title,
    }
}
document.addEventListener("DOMContentLoaded", (new Home()).start);