<?php

$timestamp = time();
$filename = 'GPSrules'.$timestamp.'.xml';


$f = fopen($filename,"a");
fprintf($f, '<?xml version="1.0" encoding="UTF-8"?><location><lib name="GPS lib" url="http://halapuu.host56.com/pn/SmartPM_libGPS.apk" />');
fprintf($f, stripslashes($_POST['rules'] ));
fprintf($f, '</location>');
fclose($f);

echo 'http://halapuu.host56.com/pn/'.$filename;
?>
