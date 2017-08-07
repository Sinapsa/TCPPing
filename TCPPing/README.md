# TCPPing
Java program that messures RTT between Pitcher and Catcher using sockets.

-p Pitcher workmode <br />
-c Catcher workmode <br />
-port <port> [Pitcher] TCP socket port used for connect, [Catcher] TCP socket port used for listen <br />
-bind <ip_address> [Catcher] TCP socket bind address where listen will be started <br />
-mps <rate> [Pitcher] sending speed expressed in "messages per second"  Default: 1 <br />
-size <size> [Pitcher] message lenght  Minimum: 50, Maximum: 3000,  Default: 300 <br />
hostname [Pitcher] computer name where Catcher is started <br />

Run example: <br />
 „compB“: java TCPPing –c –bind 192.168.0.1 –port 9900 <br />
 „compA“: java TCPPing –p –port 9900 –mps 30 –size 1000 compB <br />
