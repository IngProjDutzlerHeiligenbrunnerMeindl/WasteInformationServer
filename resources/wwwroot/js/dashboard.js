$(document).ready(function () {
    console.log("page loaded");
    $.post('/senddata/checkloginstate', 'action=getloginstate', function (data) {
        console.log(data);
        if (data.loggedin == true) {
            $("#userlabel").html(" " + data.username);
        } else {
            $("#userlabel").html(" not logged in!!");
        }
    }, 'json');

    var table;

    function reloadtable() {
        $.post('/senddata/wastedata', 'action=getAllCities', function (data) {
            console.log(data);
            if (data.query == "ok") {
                $('#location-table-data').html("");
                for (var i = 0; i < data.data.length; i++) {
                    $('#location-table-data').append("<tr>" +
                        "<td>" + data.data[i].cityname + "</td>" +
                        "<td>" + data.data[i].zone + "</td>" +
                        "<td>" + data.data[i].wastetype + "</td>" +
                        "<td>" + "<button dataid='" + data.data[i].id + "' type='button' class='delbtn btn btn-danger'>X</button>" + "</td>" +
                        "</tr>");
                    $(".delbtn").click(function (event) {
                        var id = event.target.getAttribute("dataid");
                        console.log("clicked btn data " + id);
                        $.post('/senddata/wastedata', 'action=deletecity&id=' + id, function (data) {
                            console.log(data);
                            reloadtable();
                        });
                    });
                }
                //todo entweda 1 od 2
                // $("#example2").reload();
                table = $("#example2").DataTable();

                // $('#example1').DataTable({
                //     "paging": true,
                //     "lengthChange": false,
                //     "searching": false,
                //     "ordering": true,
                //     "info": true,
                //     "autoWidth": false,
                // });
            } else if (data.query == "nodbconn") {
                Swal.fire({
                    type: "error",
                    title: 'No connection to Database',
                    html: 'Setup DB here --> <a href="index.html">click<a/>.',
                }).then((result) => {
                    console.log('Popup closed. ')

                });
            } else {
                console.log("Error: " + data.query);
            }


        }, 'json');
    }

    reloadtable();


    //btn listeners
    $('#logoutbtn').click(function () {
        $.post('/senddata/checkloginstate', 'action=logout', function (data) {
            console.log(data);
        }, 'json');
    });

    $('.dropdown-item').click(function () {
        $('#dropdown-wastetype').html($(this).html());
    });

    $('#btn-savecity').click(function () {
        var cityname = $("#new_city_cityname").val();
        var zonename = $("#new_city_zonename").val();
        var wastetype = $("#dropdown-wastetype").html();
        console.log("storing: " + cityname + "--" + wastetype + "in db");

        $.post('/senddata/wastedata', 'action=newCity&wastetype=' + wastetype + "&cityname=" + cityname + "&wastezone=" + zonename, function (data) {
            console.log(data);
            if (data.status == "inserted") {
                Swal.fire({
                    type: "success",
                    title: 'Successfully created city!',
                    html: 'This alert closes automatically.',
                    timer: 1000,
                }).then((result) => {
                    console.log('Popup closed. ')

                });
                table.destroy();
                reloadtable();
            } else if (data.status == "exists") {
                Swal.fire({
                    type: "warning",
                    title: 'Name already exists in db',
                    html: 'Close popup.',
                }).then((result) => {
                    console.log('Popup closed. ')

                });
            }


        }, 'json');

        //clear form data
        $("#new_city_cityname").val("");
        $("#new_city_zonename").val("");
        $("#dropdown-wastetype").html("select waste type");


        //todo reload table.

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
            $.post('/senddata/wastedata', 'action=senddata&wastetype=' + wastetypearr[i].innerHTML + "&wastetime=" + wastetime[i].innerHTML + "&wasteregion=" + wasteregionarr[i].innerHTML + "&wastezone=" + wastezonearr[i].innerHTML, function (data) {
                console.log(data);
            }, 'text');
        }

    });


    //Date picker pop up actions...
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
