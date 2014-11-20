<?php
if(!($sock = socket_create(AF_INET, SOCK_STREAM, 0)))
{
	$errorcode = socket_last_error();
	$errormsg = socket_strerror($errorcode);
	 
	die("Couldn't create socket: [$errorcode] $errormsg \n");
}

//Connect socket to server
if(!socket_connect($sock , 'localhost' , 5555))
{
    $errorcode = socket_last_error();
    $errormsg = socket_strerror($errorcode);
     
    die("Could not connect: [$errorcode] $errormsg \n");
}

echo "Connection established \n";
 
$message = "Here is your message from replyToServer.php  \r\n";
 
//Send the message to the server
if( ! socket_send ( $sock , $message , strlen($message) , 0))
{
    $errorcode = socket_last_error();
    $errormsg = socket_strerror($errorcode);
     
    die("Could not send data: [$errorcode] $errormsg \n");
}
 
echo "Message send successfully \n";
// print SUCCESS necessary for app to know the message is sent!
print "SUCCESS";

?>