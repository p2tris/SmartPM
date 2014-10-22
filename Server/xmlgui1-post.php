<?php
// xmlgui form # 1
// this page is expecting
// fname
// lname
// gender
// age


$filename = "datafile.txt";


$f = fopen($filename,"a");
fprintf($f,"Data received @ ".date(DATE_RFC822));
fprintf($f,"\n");
fprintf($f,'First Name:['.$_GET['fname'].']');
fprintf($f,"\n");
fprintf($f,'Last Name:['.$_GET['lname'].']');
fprintf($f,"\n");
fprintf($f,'Gender:['.$_GET['gender'].']');
fprintf($f,"\n");
fprintf($f,'Checked?:['.$_GET['checked'].']');
fprintf($f,"\n");
fprintf($f,'GPS:['.$_GET['gps'].']');
fprintf($f,"\n");
fprintf($f,'Age:['.$_GET['age'].']');
fclose($f);
print "SUCCESS";
?>

