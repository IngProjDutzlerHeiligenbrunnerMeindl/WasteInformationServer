$(document).ready(function () {

    var devicetable = null;
    reloadDevices();

    function reloadDevices() {
        $.post('/senddata/Devicedata', 'action=getdevices', function (data) {
            if (devicetable != null) {
                devicetable.destroy();
            }
            console.log(data);

            $('#devices-tablebody').html("");
            $(".delbtn").off();

            for (var i = 0; i < data.data.length; i++) {
                var id = data.data[i].deviceid;
                var cityid = data.data[i].cityid;


                if (cityid == -1) {
                    $("#devices-tablebody").append("<tr><td>" + id + "</td><td>new Device</td><td><button deviceid=\"" + id + "\"type=\"button\" class=\"btn btn-primary configuredevicebutton\">Configure</button></td><td></td><td><button dataid='" + id + "' type='button' class='delbtn btn btn-danger'>X</button></td></tr>");
                } else {
                    var devicename = data.data[i].devicename;
                    var devicelocation = data.data[i].devicelocation;

                    var row = "<tr><td>" + id + "</td><td>" + devicename + "</td><td>" + devicelocation + "</td><td>";

                    for (var n = 0; n < data.data[i].devices.length; n++) {
                        var cityname = data.data[i].devices[n].cityname;
                        var cityzone = data.data[i].devices[n].zone;
                        var wastetype = data.data[i].devices[n].wastetype;
                        row += cityname + "/" + wastetype + "/" + cityzone + " </br>";
                    }

                    row += "</td><td><button dataid='" + id + "' type='button' class='delbtn btn btn-danger'>X</button><button dataid='" + id + "' type='button' class='addbtn btn btn-success'>ADD</button></td></tr>";

                    $("#devices-tablebody").append(row);

                }
            }

            addDeleteButton();
            addAddButton();
            addConfigDialog();
            devicetable = $('#table-devices').DataTable();
        }, 'json');
    }

    function addAddButton() {
        $('.addbtn').click(function (event) {
            var id = event.target.getAttribute("dataid");
            var cityname;
            var zone;
            var wastetype;
            var devicename;
            var devicelocation;

            $.post('/senddata/Devicedata', 'action=getCitynames', function (data) {
                Swal.mixin({
                    input: 'text',
                    confirmButtonText: 'Next &rarr;',
                    showCancelButton: true,
                    progressSteps: ['1']
                }).queue([{
                    title: 'City',
                    text: 'Select your City',
                    input: 'select',
                    inputOptions: data
                }
                ]).then((result) => {
                    if (result.value) {
                        console.log(result.value);
                        const answers = JSON.stringify(result.value);
                        cityname = result.value[0];

                        console.log("cityname=" + cityname);
                        $.post('/senddata/Devicedata', 'action=getzones&cityname=' + cityname, function (data) {
                            Swal.mixin({
                                input: 'text',
                                confirmButtonText: 'Next &rarr;',
                                showCancelButton: true,
                                progressSteps: ['1']
                            }).queue([
                                {
                                    title: 'City',
                                    text: 'Select your City',
                                    input: 'select',
                                    inputOptions: data
                                }
                            ]).then((result) => {
                                if (result.value) {
                                    console.log(result.value);
                                    zone = result.value[0];
                                    $.post('/senddata/Devicedata', 'action=gettypes&cityname=' + cityname + '&zonename=' + zone, function (data) {
                                        Swal.mixin({
                                            input: 'text',
                                            confirmButtonText: 'Next &rarr;',
                                            showCancelButton: true,
                                            progressSteps: ['1']
                                        }).queue([
                                            {
                                                title: 'City',
                                                text: 'Select your City',
                                                input: 'select',
                                                inputOptions: data
                                            }
                                        ]).then((result) => {
                                            if (result.value) {
                                                console.log(result.value);
                                                wastetype = result.value[0];


                                                //todo add to db
                                                $.post('/senddata/Devicedata', 'action=addtodb&deviceid=' + id + '&cityname=' + cityname + '&zonename=' + zone + '&wastetype=' + wastetype, function (data) {
                                                    if (data.success) {
                                                        Swal.fire({
                                                            type: "success",
                                                            title: 'Successfully configured!',
                                                            html: 'This alert closes added.',
                                                            timer: 1000,
                                                        }).then((result) => {
                                                            console.log('Popup closed. ');
                                                            reloadDevices();
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    });
                                }
                            });
                        });
                    }
                });
            });
        });
    }

    function addDeleteButton() {
        $(".delbtn").click(function (event) {
            var id = event.target.getAttribute("dataid");
            console.log("clicked btn data " + id);
            $.post('/senddata/Devicedata', 'action=deleteDevice&id=' + id, function (data) {
                console.log(data);
                if (data.status == "success") {
                    Swal.fire({
                        type: "success",
                        title: 'Successfully deleted city!',
                        html: 'This alert closes automatically.',
                        timer: 1000,
                    }).then((result) => {
                        console.log('Popup closed. ')

                    });
                    reloadDevices();
                } else if (data.status == "dependenciesnotdeleted") {
                    Swal.fire({
                        type: "warning",
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

    function addConfigDialog() {
        $(".configuredevicebutton").click(function (event) {
            var id = event.target.getAttribute("deviceid");
            var cityname;
            var zone;
            var wastetype;
            var devicename;
            var devicelocation;

            $.post('/senddata/Devicedata', 'action=getCitynames', function (data) {
                Swal.mixin({
                    input: 'text',
                    confirmButtonText: 'Next &rarr;',
                    showCancelButton: true,
                    progressSteps: ['1', '2', '3']
                }).queue([
                    {
                        title: 'Name of device',
                        text: 'Please define a device name'
                    }, {
                        title: 'Location of device',
                        text: 'Please define a device location'
                    }, {
                        title: 'City',
                        text: 'Select your City',
                        input: 'select',
                        inputOptions: data
                    }
                ]).then((result) => {
                    if (result.value) {
                        console.log(result.value);
                        const answers = JSON.stringify(result.value);
                        cityname = result.value[2];
                        devicename = result.value[0];
                        devicelocation = result.value[1];

                        console.log("cityname=" + cityname);
                        $.post('/senddata/Devicedata', 'action=getzones&cityname=' + cityname, function (data) {
                            Swal.mixin({
                                input: 'text',
                                confirmButtonText: 'Next &rarr;',
                                showCancelButton: true,
                                progressSteps: ['1']
                            }).queue([
                                {
                                    title: 'City',
                                    text: 'Select your City',
                                    input: 'select',
                                    inputOptions: data
                                }
                            ]).then((result) => {
                                if (result.value) {
                                    console.log(result.value);
                                    zone = result.value[0];
                                    $.post('/senddata/Devicedata', 'action=gettypes&cityname=' + cityname + '&zonename=' + zone, function (data) {
                                        Swal.mixin({
                                            input: 'text',
                                            confirmButtonText: 'Next &rarr;',
                                            showCancelButton: true,
                                            progressSteps: ['1']
                                        }).queue([
                                            {
                                                title: 'City',
                                                text: 'Select your City',
                                                input: 'select',
                                                inputOptions: data
                                            }
                                        ]).then((result) => {
                                            if (result.value) {
                                                console.log(result.value);
                                                wastetype = result.value[0];

                                                $.post('/senddata/Devicedata', 'action=savetodb&deviceid=' + id + '&cityname=' + cityname + '&zonename=' + zone + '&wastetype=' + wastetype + '&devicename=' + devicename + '&devicelocation=' + devicelocation, function (data) {
                                                    if (data.success) {
                                                        Swal.fire({
                                                            type: "success",
                                                            title: 'Successfully configured!',
                                                            html: 'This alert closes automatically.',
                                                            timer: 1000,
                                                        }).then((result) => {
                                                            console.log('Popup closed. ');
                                                            reloadDevices();
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    });
                                }
                            });
                        });
                    }
                });
            });


            console.log("click..." + id);
        });
    }

});

