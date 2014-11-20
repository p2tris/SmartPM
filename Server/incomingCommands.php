<?php
require_once('loader.php');

$str = $_POST['string'];
$filename = "uploads/incomingCommands.txt";

// Write to file to log messages
$f = fopen($filename,"a");
fprintf($f,$_POST['string']);
fprintf($f,"\n");
fclose($f);

$str = "assign(act1,[workitem(go,id_1,[loc00,loc33],[loc33])])";
$arr = (preg_split('/\\) \\(|\\(|\\)/', $str, -1, PREG_SPLIT_NO_EMPTY));
$commandsArr = array();
/* parse input to multidimensional array like 
assign(act1,[workitem(go,id_1,[loc00,loc33],[loc33])])
TO
Array(
	[0] =>Array([0] => assign)
    [1] => Array([0] => act1)
    [2] => Array([0] => workitem)
    [3] => Array([0] => go [1] => id_1)
    [4] => Array([0] => loc00 [1] => loc33)
    [5] => Array([0] => loc33)
)
*/
foreach($arr as $elem){
	$ar1 = (preg_split('/\\] \\[|\\[|\\]/', $elem, 0, PREG_SPLIT_NO_EMPTY));
	foreach($ar1 as $elem2){
			$temparr = array_diff(explode(',' ,$elem2), array(""));
			if(empty($temparr) != 1){
				array_push($commandsArr, $temparr);
			}
	}
}


// Logic of different commands
$taskType = $commandsArr[0][0];
if($taskType == "assign"){
	
	$actName = $commandsArr[1][0];
	$taskName = $commandsArr[3][0];
	$id = $commandsArr[3][1];
	$expectedResult = $commandsArr[5][0];

	// create task XML
	$url = createTaskXML($id, $taskName, $expectedResult);
	
	// send to device following message:
	// taskName|Fill the Form;URL|http://halapuu.host56.com/pn/xmlgui1.xml
	sendToDevice($actName, $url, $taskName);
	
} elseif ($taskType == "start"){
	
}

function createTaskXML($id, $taskName, $expectedResult){
	
	// array of info from XSD
	$typeOptLibRule = parseTypeFromXSD();
	
	$filename = "tasks/task".time().".xml";
	$f = fopen($filename,"a");
	
	fprintf($f,'<?xml version="1.0" encoding="utf-8"?><xmlgui>');
	fprintf($f,'<form id="'.$id.'" name="SmartPM taskXML" submitTo="http://smartpm.cloudapp.net/replyToServer.php" >');
	// TODO: one field per task?
	// get info for xml elements
	fprintf($f,'<field name="'.$taskName.'" label="'.$taskName.'" type="text" required="Y" options="" autoLib="" rules=""/>');
	fprintf($f,'</form></xmlgui>');
	fclose($f);
	return "http://smartpm.cloudapp.net/".$filename;
}

function parseTypeFromXSD(){
	$result = array();
	
	// todo - get options for createTaskXML and to get type, also autolib and rules. Default all required
	// explore XSD logic with Andrea!
	
	return $result;
}

function sendToDevice($name, $url, $taskName){
	// taskName|Fill the Form;URL|http://halapuu.host56.com/pn/xmlgui1.xml
	$gcmRegID = "";
	$result = mysql_query("SELECT gcm_regid from gcm_users WHERE name = '$name'");
	if (!$result) {
		die('Invalid query: ' . mysql_error());
	}
	if ($row = mysql_fetch_assoc($result)) {
		$gcmRegID = $row["gcm_regid"];
	}

	$NumOfRows = mysql_num_rows($result);
	if ($NumOfRows > 0) {
		// user existed
		$pushMessage = 'taskName|'.$taskName.';URL|'.$url;
		
		if (isset($gcmRegID) && isset($pushMessage)) {
		
		
			$registration_ids = array($gcmRegID);
			$message = array("message" => $pushMessage);
			$result = send_push_notification($registration_ids, $message);
		
			echo $result;
		}
		return true;
	} else {
		// user not existed
		return false;
	}
}

?>