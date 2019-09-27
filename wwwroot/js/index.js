$(document).ready(function() {
  $('#loginbtn').click(function(e) {
    e.preventDefault();
    console.log("clicked login button");
    var username = $("#userfield")[0].value;
    var password = $("#passfield")[0].value;

    $.post('/senddata/loginget','username='+username+'&password='+password,function(data){

      console.log(data);
      if (data.accept == true) {
        console.log("successfully logged in!");
        document.cookie = "username="+username;
        window.location = 'settings.html';
      }
    },'json');
  });
});
