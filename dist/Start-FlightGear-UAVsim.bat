"%PROGRAMFILES%\FlightGear\bin\Win32\fgfs" ^
 "--fg-root=%PROGRAMFILES%\FlightGear\data" ^
 "--nmea=socket,out,5,127.0.0.1,5557,tcp" ^
 "--generic=socket,in,12,127.0.0.1,5556,tcp,UAVsim-Protocol" ^
 "--generic=socket,out,10,127.0.0.1,5555,tcp,UAVsim-Protocol" ^
 "--airport=ksfo" ^
 "--in-air" ^
 "--altitude=1500" ^
 "--vc=90" ^
 "--heading=300" ^
 "--timeofday=noon" ^
 "--prop:/instrumentation/attitude-indicator/config/tumble-flag=0"
