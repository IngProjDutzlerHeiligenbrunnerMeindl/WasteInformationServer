$(document).ready(function () {


    $.post('/senddata/Devicedata', 'action=getdevices', function (data) {

        console.log(data);
        for (var i = 0; i < data.data.length; i++) {
            var id = data.data[i].deviceid;
            var cityid = data.data[i].cityid;

            if (cityid == -1) {
                $("#devices-tablebody").append("<tr><td>" + id + "</td><td>new Device</td><td><button deviceid=\"" + id + "\"type=\"button\" class=\"btn btn-primary configuredevicebutton\">Configure</button></td><td></td><td></td></tr>");
            } else {
                var devicename = data.data[i].devicename;
                var devicelocation = data.data[i].devicelocation;

                $("#devices-tablebody").append("<tr><td>" + id + "</td><td>" + devicename + "</td><td>" + devicelocation + "</td><td>" + cityid + "</td><td>DEL</td></tr>");
            }
            console.log();
            //devices-tablebody

        }
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
                                                            console.log('Popup closed. ')

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
        var test = $('#table-devices').DataTable();
    }, 'json');


});

