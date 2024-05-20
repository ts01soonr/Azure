if not exist soonr.jar play fget soonr.jar https://us.workplace.datto.com/filelink/6813-79cc5be9-b5c9d2ad2e-2
::if not exist TrackMan.apk play fget TrackMan.apk https://us.workplace.datto.com/filelink/6813-7a1f89c4-6b6001872b-2
if exist log\demo.txt move log\demo.log log\demo_%RANDOM%.txt
java -jar soonr.jar 2>&1 | mtee log\demo.txt