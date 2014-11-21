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
 
$message="replyToServer.php file \r\n";
if(isset($_GET['taskId']) && !empty($_GET['taskId'])){
	$taskId = $_GET['taskId'];
	$taskName = $_GET['taskName'];
	$actName = $_GET['actName'];
	$output = $_GET[$taskName.'_field'];
	$message = "finishedTask(".$actName.",".$taskId.",".$taskName.",[".$output."])\r\n";
} elseif (isset($_POST['taskId']) && !empty($_POST['taskId'])) {
	$taskId = $_POST['taskId'];
	$taskName = $_POST['taskName'];
	$actName = $_POST['actName'];
	$message = "readyToStart(".$actName.",".$taskId.",".$taskName.")\r\n";
} else {
	$message = "Todo other messages \r\n";
}




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