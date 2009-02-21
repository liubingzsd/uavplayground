"%PROGRAMFILES%\FlightGear\bin\Win32\fgfs" ^
 "--fg-root=%PROGRAMFILES%\FlightGear\data" ^
 "--nmea=socket,out,0.5,127.0.0.1,5557,tcp" ^
 "--generic=socket,in,10,127.0.0.1,5556,tcp,UAVsim-Protocol" ^
 "--generic=socket,out,3,127.0.0.1,5555,tcp,UAVsim-Protocol" ^
 "--timeofday=noon"
 