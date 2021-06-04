var page = 1;

function search(type) {

    var confirmation;

    var results = document.getElementById('results');
    results.innerHTML = '';

    page = 1;
    var input1 = document.getElementById('filter-string');
    var text1 = input1.value;

    var text2 = "";

    if (type == "post") {
        var input2 = document.getElementById('filter-long');
        text2 = input2.value;
        if (text2 == "") {
            text2 = "0"
        }
    }

    if(text1 != "" || text2 != "") {

        var client = new XMLHttpRequest();
        client.responseType = "json";

        var url = "/api/" + type + "s/filtered?title=" + text1;
        if (type == "post") {
            url += "&minLikes=" + text2;
        }
        url += "&page=" + page;

        client.open("GET", url);

        client.addEventListener("load", function () {
            if (client.status == 404) {
                confirmation = confirm("There are no results with the introduced filter, would you like to return to the "+type+"s main site?");
                if (confirmation){
                    window.location.href = "/" + type + "s";
                }

            } else {
                if (type == "post") {
                    showResultsPost(this.response);
                } else {
                    showResultsUser(this.response);
                }
                document.getElementById('load-more-button').hidden = false;
            }
        });

        client.send();



    } else {
        confirmation = confirm("No filter has been introduced, would you like to return to the "+type+"s main site?");
        if (confirmation){
            window.location.href = "/" + type + "s";
        }
    }
}

function showResultsPost(response) {

    var results = document.getElementById('results');
    var li;
    var aux;
    for (item of response.values()) {

        li = document.createElement('li')
        var user = item.userCreator;

        aux = document.createElement('img')
        aux.className = "comment-user-avatar"
        if (user == null){
            aux.src="/assets/ico/deletedUser.jpg"
        } else {
            aux.src="/api/user/"+user.username+"/image"
        }
        li.appendChild(aux);

        aux = document.createTextNode("Posted by ")
        li.appendChild(aux);

        if(user == null){
            aux = document.createTextNode("[deleted]")
        } else {
            aux = document.createElement('a');
            var user_textNode = document.createTextNode(user.username);
            aux.href = "/user/" + user.username.toString();
            aux.appendChild(user_textNode);
        }
        li.appendChild(aux);

        var date = item.date;
        date = date.split('T');
        fecha = date[0].replaceAll('-', '/');
        hora = date[1].split('.')[0].split(':');
        var date_textNode = document.createTextNode(" on " + fecha + " " + hora[0] + ":" + hora[1]);
        li.appendChild(date_textNode)
        results.appendChild(li);

        var titulo = item.title;
        var titulo_textNode = document.createTextNode(titulo);
        var h3 = document.createElement('h3');
        aux = document.createElement('strong');
        aux.appendChild(titulo_textNode);
        h3.appendChild(aux);
        li = document.createElement('li');
        li.appendChild(h3);
        results.appendChild(li);

        aux = document.createElement("a");
        aux.href="/post/"+item.id;
        var image = document.createElement('img');
        image.src = "/api/post/" + item.id + "/image";
        image.className = "index-images"
        aux.appendChild(image);
        li.appendChild(aux);

        results.appendChild(li);

        aux = document.createElement('br');
        results.appendChild(aux);
        aux = document.createElement('br');
        results.appendChild(aux);
        aux = document.createElement('br');
        results.appendChild(aux);


    }

}

function showResultsUser(response) {

    var results = document.getElementById('results');
    var li;
    for (item of response.values()) {

        var user = item.username;
        user_textNode = document.createTextNode(user);
        var h3 = document.createElement('h3');
        h3.appendChild(user_textNode);
        var a = document.createElement('a');
        a.href = "/user/" + user.toString();
        a.appendChild(h3);
        li = document.createElement('li');
        li.appendChild(a);
        results.appendChild(li);


        var userImage = document.createElement('img');
        userImage.src = "/api/user/" + user + "/image";
        userImage.className = "user-list-image"
        li.appendChild(userImage);

        results.appendChild(li);

    }
}

function loadMore(type) {
    page++;
    var input1 = document.getElementById('filter-string');
    var text1 = input1.value;

    if (type == "post") {
        var input2 = document.getElementById('filter-long');
        var text2 = input2.value;
        if (text2 == "") {
            text2 = "0"
        }
    }
    var client = new XMLHttpRequest();
    client.responseType = "json";

    var url = "/api/" + type + "s/filtered?title=" + text1;
    if (type == "post") {
        url += "&minLikes=" + text2;
    }
    url += "&page=" + page;

    client.open("GET", url);

    client.addEventListener("load", function () {
        if (client.status == 204) {
            var button = document.getElementById('load-more-button');
            button.hidden = true;
        } else {
            if (type == "post") {
                showResultsPost(this.response);
            } else {
                showResultsUser(this.response);
            }
        }
    });

    client.send();
}

