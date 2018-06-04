// import express module
var express = require("express");

// create an express object
var bodyParser = require("body-parser");
var app = express();
var fs = require("fs");


app. use(bodyParser());

app. get('/news' , function(request, response){
	let html = "", rHtml = "";
	let upperHtml = fs.readFileSync('news.html');
	

	fs.readFile('news.json', 'utf8',function (err,data) {
		let dictionary;
		if(data.trim()==''){
			dictionary = {};
		} else{
			dictionary = JSON.parse(data.trim());
		}

		for(let title in dictionary){
			html = '';
			html += "<h1>" + title + "</h1></a>";
			html += "<div class='body'>";
			for(let para of dictionary[title].split("\n")){
				html += "<p>" + para + "</p>";
			}
			html += "</div>";
			rHtml += "<article style='padding:5px'>" + html + "</article>";
		}
		
		rHtml = upperHtml + rHtml + "</section></section></body></html>";
	
		response.send(rHtml);
	});
});

app. get('/news/add' , function(request, response){
	fs.readFile('newsAdd.html', 'utf8',function (err,data) {
		response.send(data);
	});
});

function checkExist(request, dictionary){
	if(request.body.title && request.body.title.trim()!=""){
		let title = request.body.title.trim();
		if(dictionary.hasOwnProperty(title)){
			return true;
		}
	}
	return false;
}

app. post('/news' , function(request, response){
	if(request.body.title && request.body.title.trim()!="" && 
	   request.body.content && request.body.content.trim()!=""){
		fs.readFile('news.json', 'utf8',function (err,data) {
			let dictionary;
			if(data.trim()==''){
				dictionary = {};
			} else{
				dictionary = JSON.parse(data.trim());
			}
			

			if(checkExist(request, dictionary)){
				//response.json({message:"News already exists"});
				console.log("News exist");	
				response.redirect('/news/add');
				return;
			}
			
			dictionary[request.body.title.trim()] = request.body.content.trim();

			fs.writeFile('news.json',JSON.stringify(dictionary));
			console.log("Writing to file");
			//response.json({message:"Successfully added news"});
	
			response.redirect('/news/add');
		})
		
	} else{
		console.log("Empty field");
		response.redirect('/news/add');

		//response.json({message:"All fields must be filled!"});
	}
});

// tell express to listen on the specified port
app. listen(8000);