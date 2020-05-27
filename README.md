# Ajax
Syed Rehman


How to run your software
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
to run on windown :

cd into 			cd cse312_hw5\src
then run the exact command 	javac p1\Server.java
then				java p1/Server   
//note that I used '/' intentionally instead of '\' sometimes so careful

to run of linux :

cd into 			cd cse312_hw5/src
then run the exact command 	javac p1/Server.java
then				java p1/Server   


the program should run on an while loop so do your testing while its running.


How to test your software
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To test the program open Chrome as I tested on Chrome. Search localhost:8080
My website should load. You can check control+shift+i  to check the responses and 
requests. Following are how you can check each objectives:

1. If you search localhost:8080 on your browser's search bar you will load the page
and right after you should see ajax request to load a simple text.

2. You should see a form, where you can type your name and a message and submit. It
will create a POST request and send that data to the server running. It will then be
stored in the folder src/website in the file called output.txt

3. Then the Server will read from the output.txt and respond with that text. This is
all done without any page refresh. You should see the text on the webpage. 

4. If you open up another tab, search localhost:8080 it should load the page with all
the messages. You then can submit more messages from either tabs and see all the 
messages on any of the tabs. 


The architecture of your software
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

I used java. I created a server socket and had it wait for connection in a while loop
and when a get request comes in, it checks to see the whether its a POST or GET 
request. If its a get request then the request is handled based on its accept tag.
Then if they are HTML or CSS then there is only one. If javascript or text then it
goes to path and based on that the request is processed.

On the POST side of things, the body is recieved as JSON. I manually parse it. Remove
any html tags and replace it with its string corraspondance. And write that to a txt
file called output.txt. The output is then read and returned if recieved a GET 
request.  

End of every loop the socket is closed and re-establied again and again in a while 
loop.

For further questions email syedrehm@buffalo.edu
