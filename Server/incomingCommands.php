<?php

$filename = "uploads/incomingCommands.txt";


$f = fopen($filename,"a");
fprintf($f,$_POST['string']);
fprintf($f,"\n");
fclose($f);
?>