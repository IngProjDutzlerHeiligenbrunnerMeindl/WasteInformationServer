$(document).ready(function () {

    $.post('/senddata/Devicedata', 'action=getdevices', function (data) {

        console.log(data);
    }, 'json');
});

