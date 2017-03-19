
A proxy server and client which is to help you cross the great firewall wall ! suppurt socks4,socks5,ssl.

## Help:
```
-h --help Show this help and exit.

java -jar FILE.jar [OPTION] ...

options:
 localHost localPort leftMethod password rightMethod remoteHost remotePort

   localHost   : localHost to listen
   localPort   : localPort to listen
   leftMethod  : socks4 socks5 ssl non
   password    : password
   rightMethod : like leftMethod
   remoteHost  : remoteHost to connect
   remotePort  : remotePort to connect


examples:
 java -jar FILE.jar 3009                      --- a ssl server at 0.0.0.0:3009
 java -jar FILE.jar 192.168.1.101 3009        --- a ssl server at 192.168.1.101:3009
 java -jar FILE.jar 1080 192.168.56.101 3009  --- a socks5 client listen 127.0.0.1:1080 and connect to 192.168.56.101:3009 with ssl
 java -jar FILE.jar 127.0.0.1  1080 socks5   12345  ssl   192.168.1.101   3009  client
                                              --- a socks5 client listen 127.0.0.1:1080 and connect to 192.168.1.101:3009 with ssl
```         
## License:
```
Copyright 2016-2017 dogHere

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```