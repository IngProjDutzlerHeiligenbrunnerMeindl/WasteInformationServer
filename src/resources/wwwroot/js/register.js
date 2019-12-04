$(document).ready(function() {
  $('#loginbtn').click(function(e) {
    e.preventDefault();
    console.log("clicked login button");
    var username = $("#usernamefield")[0].value;
    var firstname = $("#firstnamefield")[0].value;
    var lastname = $("#lastnamefield")[0].value;
    var email = $("#emailfield")[0].value;
    var password = $("#passfield")[0].value;
    var replypassword = $("#replpassfield")[0].value;

    if (password != replypassword) {
      console.log("passwords doesnt match");
    }else {
      $.post('/senddata/registerpost','username='+username+
                                    '&firstname='+firstname+
                                    '&lastname='+lastname+
                                    '&email='+email+
                                    '&password='+password,function(data){

        console.log(data);

        if (data.accept == true) {
          console.log("successfully registered!");
          $("#successbar").show();

          setTimeout(function() {
            window.location = 'index.html';
          },3000);
        }else {
          console.log("error!");
          $("#errorbar").show();
        }
      },'json');
    }
  });
});
