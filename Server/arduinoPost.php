<?php

$timestamp = time();
$filename = 'arduinorules/humidrules'.$timestamp.'.xml';


$f = fopen($filename,"a");
fprintf($f, '<?xml version="1.0" encoding="UTF-8"?><humid_type><lib name="humid lib" url="http://smartpm.cloudapp.net/SmartPM_libHumid.apk" />');
fprintf($f, stripslashes($_POST['rules'] ));
fprintf($f, '</humid_type>');
fclose($f);

echo 'http://smartpm.cloudapp.net/'.$filename;
?>
