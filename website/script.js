

function changeParagraph(text1){
    console.log(text1);
    var arr = text1.split("\n");
    var line = "";
    for(var i = 0; i< arr.length; i++){
        line = line + arr[i] + "<br>";
    }
   
    var lb = document.createElement("br");
    var par = document.getElementById("content_post");
    par.innerHTML = line;
}
var request = new XMLHttpRequest();

request.onreadystatechange = function(){	
    if	(this.readyState === 4 && this.status === 200){	
        // request.open('GET', 'after.txt', true);
        console.log(this.responseText);	
        //Do something with the response
        changeParagraph(this.responseText);	
    }	
};	
request.open("GET", "/after.txt", true);	
request.send();

function getData(){
    request = new XMLHttpRequest();
    request.onreadystatechange = function(){	
        if (this.readyState === 4 && this.status === 200){	
            console.log(this.responseText);	
            //	Do something with the response
            changeParagraph(this.responseText);	
        }
    };
    request.open("GET",	"/output.txt", true);	
    request.send();	
}


function sendData(){
	request = new XMLHttpRequest();
	request.onreadystatechange = function(){	
	    if	(this.readyState	===	4	&&	this.status	===	200){	
	        console.log(this.response);	
	        //	Do	something	with	the	response	
	    }	
	};	
    request.open("POST", "/post_data", true);	
    const formname = document.getElementById("form-name").value;
    const formcomment = document.getElementById("form-comment").value;
	let	data = JSON.stringify({'username': formname, 'message': formcomment});	
	request.send(data);
	
    var ti = setTimeout(console.log("post was sent now getting data"), 500);
    clearTimeout(ti);
    getData();
}



