var check = function () {
    if (document.getElementById('password').value ==
        document.getElementById('confirm_password').value) {
        document.getElementById('message').style.color = 'green';
        document.getElementById('message').innerHTML = 'Passwords matching';
        document.getElementById('register_button').disabled = false;
    } else {
        document.getElementById('message').style.color = 'red';
        document.getElementById('message').innerHTML = 'Passwords not matching';
        document.getElementById('register_button').disabled = true;
    }
}

function showPassword() {
    var x = document.getElementById("password");
    var y = document.getElementById("confirm_password");
    if (x.type === "password" && x.type === "password") {
        x.type = "text";
        y.type = "text";
    } else {
        x.type = "password";
        y.type = "password";
    }
}

var elem;
var like;
var checked = true;

function imageDetection() {
    elem = document.getElementById('imgClickAndChange');
    like = elem.src.includes('/assets/ico/liked.svg');
}

function changeImage() {
    if (checked) {
        imageDetection();
        checked = false;
    }
    like = !(like);
    if (like) {
        elem.src = '/assets/ico/liked.svg';
    } else {
        elem.src = '/assets/ico/like.svg';
    }
}


function createLike(id) {
    imageDetection();
    var client = new XMLHttpRequest();
    client.responseType = "json";
    client.addEventListener("load", function () {
        changeImage();
    });

    if (like) {
        client.open("DELETE", "/api/post/" + id + "/like");
    } else {
        client.open("POST", "/api/post/" + id + "/like");
    }
    client.setRequestHeader("Content-type", "application/json");
    client.send();


}


function createFollow(usernameFollowed) {
    var client = new XMLHttpRequest();
    client.responseType = "json";
    client.addEventListener("load", function () {
        changeFollow();
    });
    if (checkFollow()) {
        client.open("DELETE", "/api/user/" + usernameFollowed + "/follow");
    } else {
        client.open("POST", "/api/user/" + usernameFollowed + "/follow");
    }

    client.setRequestHeader("Content-type", "application/json");
    client.send();
}

function checkFollow() {
    var exists = document.getElementById("exists")
     return exists.value.localeCompare("exists")==0
}

function changeFollow(){
    if (checkFollow()){
        document.getElementById("exists").value = "notExists"
    } else {
        document.getElementById("exists").value = "exists"
    }
}