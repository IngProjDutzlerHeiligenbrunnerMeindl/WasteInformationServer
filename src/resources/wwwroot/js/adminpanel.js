$(document).ready(function () {
    console.log("page loaded");
    $.post('/senddata/checkloginstate', 'action=getloginstate', function (data) {
        console.log(data);
        if (data.loggedin == true) {
            $("#userlabel").html(" " + data.username);
            if (data.permission > 0) {
                $("#adminpanel").show();
            }
        } else {
            $("#userlabel").html(" not logged in!!");
        }
    }, 'json');


    $("#btn-shutdown").click(function (event) {
        console.log("shutting down server");

        $.post('/senddata/admindata', 'action=shutdownserver', function (data) {
            console.log(data);

        }, 'json');

    });

    $("#btn-restart").click(function (event) {
        console.log("restarting server");

        $.post('/senddata/admindata', 'action=restartserver', function (data) {
            console.log(data);

        }, 'json');
    });
});