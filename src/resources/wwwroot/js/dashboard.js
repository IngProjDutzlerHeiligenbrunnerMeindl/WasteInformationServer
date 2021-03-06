$(document).ready(function () {
    new Dashboard();
});

class Dashboard {
    constructor() {
        //load header data
        this.loadHeaderData();

        //load footer
        this.loadVersionFooter();

        //reload city table
        this.reloadtable();

        //reload Date table
        this.reloadDateTable();

        //init time picker
        this.initDatePicker();

        //add click listeners to all buttons
        this.addClickListeners();

        // allow moveable tiles
        $('.connectedSortable').sortable({
            placeholder         : 'sort-highlight',
            connectWith         : '.connectedSortable',
            handle              : '.card-header, .nav-tabs',
            forcePlaceholderSize: true,
            zIndex              : 999999
        })
        $('.connectedSortable .card-header, .connectedSortable .nav-tabs-custom').css('cursor', 'move')
    }

    /* Constants */
    citytable;
    datetable;

    /* Btn Action Listeners */

    addClickListeners() {
        const _this = this;
        //btn listeners
        $('#logoutbtn').click(function () {
            $.post('/senddata/checkloginstate', 'action=logout', function (data) {
                console.log(data);
            }, 'json');
        });

        $('.wastetype-citynew-item').click(function (event) {
            event.preventDefault();
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
                        icon: "success",
                        title: 'Successfully created city!',
                        html: 'This alert closes automatically.',
                        timer: 1000,
                    }).then((result) => {
                        console.log('Popup closed. ')

                    });
                    _this.reloadtable();
                } else if (data.status == "exists") {
                    Swal.fire({
                        icon: "warning",
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
        });


        /* new Date create:  */
        $("#dropdown-city").click(function (event) {
            event.preventDefault();
            var dropdata = $("#dropdown-city-data");
            dropdata.html("");
            console.log("loading city names")

            $.post('/senddata/newdate', 'action=getCitynames', function (data) {
                console.log(data);
                if (data.query == "ok") {
                    var prev = "";
                    for (var i = 0; i < data.data.length; i++) {
                        var name = data.data[i].cityname;
                        dropdata.append("<a class=\"dropdown-data-cityname dropdown-item\" href=\"#\">" + name + "</a>");
                    }

                    $(".dropdown-data-cityname").off();
                    $(".dropdown-data-cityname").click(function (evnt) {
                        evnt.preventDefault();
                        console.log($(this).html());
                        $("#dropdown-city").html($(this).html());
                    });
                }
            }, "json");
        });

        $("#dropdown-zone").click(function (event) {
            event.preventDefault();
            var dropdata = $("#dropdown-zone-data");
            dropdata.html("");

            $.post('/senddata/newdate', 'action=getzones&cityname=' + $("#dropdown-city").html(), function (data) {
                console.log(data);
                if (data.query == "ok") {
                    var prev = "";
                    for (var i = 0; i < data.data.length; i++) {
                        var zone = data.data[i].zone;
                        dropdata.append("<a class=\"dropdown-data-zonename dropdown-item\" href=\"#\">" + zone + "</a>");
                    }

                    $(".dropdown-data-zonename").off();
                    $(".dropdown-data-zonename").click(function (evnt) {
                        evnt.preventDefault();
                        console.log($(this).html());
                        $("#dropdown-zone").html($(this).html());
                    });
                }
            });
        });

        $("#dropdown-type-data").click(function (event) {
            event.preventDefault();

            var dropdata = $("#dropdown-type-drops");
            dropdata.html("");
            console.log("clickeeeed");

            $.post('/senddata/newdate', 'action=gettypes&cityname=' + $("#dropdown-city").html() + '&zonename=' + $("#dropdown-zone").html(), function (data) {
                console.log(data);
                if (data.query == "ok") {
                    for (var i = 0; i < data.data.length; i++) {
                        var type = data.data[i].wastetype;
                        dropdata.append("<a class=\"dropdown-data-typename dropdown-item\" href=\"#\">" + type + "</a>");
                    }

                    $(".dropdown-data-typename").off();
                    $(".dropdown-data-typename").click(function (evnt) {
                        evnt.preventDefault();
                        $("#dropdown-type-data").html($(this).html());
                    });
                }
            });


        });


        $('.btn-savelist').click(function () {
            console.log("saving date");

            var cityname = $("#dropdown-city");
            var zone = $("#dropdown-zone");
            var wastetype = $("#dropdown-type-data");
            var date = $("#input-wastetime");

            $.post('/senddata/newdate', 'action=newdate&cityname=' + cityname.html() + "&zone=" + zone.html() + "&wastetype=" + wastetype.html() + "&date=" + date.val(), function (data) {
                if (data.status == "success") {
                    Swal.fire({
                        icon: "success",
                        title: 'Successfully created Date!',
                        html: 'This alert closes automatically.',
                        timer: 1000,
                    }).then((result) => {
                        console.log('Popup closed. ')

                    });

                    cityname.html("Select City");
                    zone.html("Select Zone");
                    wastetype.html("Select waste type");
                    date.val("");
                    _this.reloadDateTable();
                } else if (data.status == "citydoesntexist") {
                    Swal.fire({
                        icon: "warning",
                        title: 'city name doesnt exist',
                        html: 'Close popup.',
                    }).then((result) => {
                        console.log('Popup closed. ')

                    });
                }

                console.log(data)
            }, "json");
        });
    }


    /* Functions */
    initDatePicker() {
        //Date picker pop up actions...
        var date_input = $('input[name="date"]'); //our date input has the name "date"
        var container = $('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
        var options = {
            format: 'yyyy-mm-dd',
            container: container,
            todayHighlight: true,
            autoclose: true,
        };
        date_input.datepicker(options);
    }

    loadHeaderData() {
        $.post('/senddata/wastedata', 'action=getStartHeaderData', function (data) {
            console.log(data);
            $("#total-connection-labels").html(data.collectionnumber);

            $("#planed-collection-label").html(data.futurecollections);

            $("#finished-collection-label").html(data.finshedcollections);

            $("#total-city-number-label").html(data.citynumber);
        }, 'json');
    }

    loadVersionFooter() {
        $.post('/senddata/wastedata', 'action=getversionandbuildtime', function (data) {
            $("#version-footer-label").html("<b>Version</b> " + data.version + " <b>Build</b> " + data.buildtime);
        }, 'json');
    }

    reloadtable() {
        const _this = this;
        $.post('/senddata/wastedata', 'action=getAllCities', function (data) {
            if (_this.citytable != null) {
                _this.citytable.destroy(); //delete table if already created
            }

            console.log(data);
            if (data.query == "ok") {
                var tabledata = $('#location-table-data');


                tabledata.html("");
                //todo in variable
                $(".delbtn").off();

                for (var i = 0; i < data.data.length; i++) {
                    tabledata.append("<tr>" +
                        "<td>" + data.data[i].cityname + "</td>" +
                        "<td>" + data.data[i].zone + "</td>" +
                        "<td>" + data.data[i].wastetype + "</td>" +
                        "<td>" + "<button dataid='" + data.data[i].id + "' type='button' class='delbtn btn btn-danger'>X</button>" + "</td>" +
                        "</tr>");
                }

                $(".delbtn").click(function (event) {
                    const id = event.target.getAttribute("dataid");
                    console.log("clicked btn data " + id);
                    $.post('/senddata/wastedata', 'action=deletecity&id=' + id, function (data) {
                        console.log(data);
                        if (data.status === "success") {
                            Swal.fire({
                                icon: "success",
                                title: 'Successfully deleted city!',
                                html: 'This alert closes automatically.',
                                timer: 1000,
                            }).then((result) => {
                                console.log('Popup closed. ')

                            });
                            _this.reloadtable();
                        } else if (data.status === "dependenciesnotdeleted") {
                            Swal.fire({
                                icon: "warning",
                                title: 'This city is a dependency of a date',
                                html: 'Please delete all dependencies first!',
                            }).then((result) => {
                                console.log('Popup closed. ')

                            });
                            //todo set yes no button here
                        }

                    }, "json");
                });

                _this.citytable = $("#example2").DataTable();
            } else if (data.query == "nodbconn") {
                Swal.fire({
                    icon: "error",
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

    reloadDateTable() {
        const _this = this;
        $.post('/senddata/wastedata', 'action=getAllDates', function (data) {
            if (_this.datetable != null) {
                _this.datetable.destroy(); //delete table if already created
            }
            console.log(data);

            if (data.query == "ok") {
                $('#picupdates-tablebody').html("");
                $(".delbtndate").off();

                for (var i = 0; i < data.data.length; i++) {
                    $('#picupdates-tablebody').append("<tr>" +
                        "<td>" + data.data[i].cityname + "</td>" +
                        "<td>" + data.data[i].zone + "</td>" +
                        "<td>" + data.data[i].wastetype + "</td>" +
                        "<td>" + data.data[i].date + "</td>" +
                        "<td>" + "<button dataid='" + data.data[i].id + "' type='button' class='delbtndate btn btn-danger'>X</button>" + "</td>" +
                        "</tr>");
                }

                $(".delbtndate").click(function (event) {
                    var id = event.target.getAttribute("dataid");
                    console.log("clicked btn data " + id);
                    $.post('/senddata/wastedata', 'action=deletedate&id=' + id, function (data) {
                        console.log(data);
                        if (data.status == "success") {
                            Swal.fire({
                                icon: "success",
                                title: 'Successfully deleted city!',
                                html: 'This alert closes automatically.',
                                timer: 1000,
                            }).then((result) => {
                                console.log('Popup closed. ')

                            });
                            _this.reloadDateTable();
                        } else if (data.status == "dependenciesnotdeleted") {
                            Swal.fire({
                                icon: "warning",
                                title: 'This city is a dependency of a date',
                                html: 'Do you want do delete it anyway with all dependencies?',
                            }).then((result) => {
                                console.log('Popup closed. ')

                            });
                            //todo set yes no button here
                        }

                    }, "json");
                });
            }
            _this.datetable = $("#table-pickupdates").DataTable({
                "order": [[3, "asc"]]
            });
        }, "json");
    }
}


