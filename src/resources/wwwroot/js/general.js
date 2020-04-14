$(document).ready(function () {
    $('.sandwich').click(function () {
        const bdy = $('body');
        if (bdy.hasClass("sidebar-collapse")) {
            bdy.removeClass("sidebar-collapse");
        } else {
            bdy.addClass("sidebar-collapse");
        }

        if(bdy.hasClass("sidebar-open")){
            bdy.removeClass("sidebar-open");
        } else {
            bdy.addClass("sidebar-open");
        }
    });

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