<?php

$timestamp = time();
$filename = 'arduinorules/'.$_POST['sensor_type'].'rules'.$timestamp.'.xml';


$f = fopen($filename,"a");
fprintf($f, '<?xml version="1.0" encoding="UTF-8"?><'.$_POST['sensor_type'].'_type><lib name="arduino lib" keyword="'.$_POST['sensor_type'].'" url="http://www.dis.uniroma1.it/~smartpm/webtool/SmartPM_libHumid.apk" />');
fprintf($f, stripslashes($_POST['rules'] ));
fprintf($f, '</'.$_POST['sensor_type'].'_type>');
fclose($f);

echo 'http://www.dis.uniroma1.it/~smartpm/webtool/'.$filename;
?>
