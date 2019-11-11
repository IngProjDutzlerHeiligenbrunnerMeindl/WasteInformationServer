$(document).ready(function () {
    // TODO: check login state
    console.log("page loaded");
    $.post('/senddata/checkloginstate', 'action=getloginstate', function (data) {
        console.log(data);
        if (data.loggedin == true) {
            $("#userlabel").html(" " + data.username);
        } else {
            $("#userlabel").html(" not logged in!!");
        }
    }, 'json');


    $('#logoutbtn').click(function () {
        $.post('/senddata/checkloginstate', 'action=logout', function (data) {
            console.log(data);
        }, 'json');
    });

    $('#addnewtable-btn').click(function () {
        console.log("clicked");
        $('.create-table').show();
    });

    $('.exit-icon').click(function () {
        $('.create-table').hide();
    });

    $('.dropdown-item').click(function () {
        $('#dropdown-wastetype').html($(this).html());
    });

    $('.btn-addtolist').click(function () {
        console.log("added new row to table");
        $('#addtable-body').append("<tr>" +
            "<td class='td-dropdown-wastetype'>" + $('#dropdown-wastetype').html() + "</td>" +
            "<td class='td-input-wastetime'>" + $('#input-wastetime').val() + "</td>" +
            "<td class='td-input-wasteregion'>" + $('#input-wasteregion').val() + "</td>" +
            "<td class='td-input-wastezone'>" + $('#input-wastezone').val() + "</td>" +
            "</tr>");
    });

    $('#btn-savelist').click(function () {
        console.log("saving list");
        var wastetypearr = $('.td-dropdown-wastetype');
        var wastetime = $('.td-input-wastetime');
        var wasteregionarr = $('.td-input-wasteregion');
        var wastezonearr = $('.td-input-wastezone');

        for (var i = 0; i < wastetypearr.length; i++) {
            console.log(wastetypearr[i].innerHTML);
            $.post('/senddata/wastedata', 'action=senddata&wastetype=' + wastetypearr[i].innerHTML +"&wastetime="+wastetime[i].innerHTML+"&wasteregion="+wasteregionarr[i].innerHTML+"&wastezone="+wastezonearr[i].innerHTML, function (data) {
                console.log(data);
            }, 'text');
        }

    });


    var date_input = $('input[name="date"]'); //our date input has the name "date"
    var container = $('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
    var options = {
        format: 'mm/dd/yyyy',
        container: container,
        todayHighlight: true,
        autoclose: true,
    };
    date_input.datepicker(options);
});
