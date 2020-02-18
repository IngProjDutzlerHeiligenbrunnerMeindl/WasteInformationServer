$(document).ready(function() {
    $("#firstname").value("hhh");

    $.post('/senddata/checkloginstate', 'action=getfirstname', function (data) {
        console.log(data);
    }, 'json');
});
