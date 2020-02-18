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
});