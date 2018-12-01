# Project 4  - Reliable Data Transfer

## Instructions

Implement reliable data transfer using checksums, ACKs, sequence numbers and countdown timers

Starting point:

RDTClient.java in github

## Requirements
Implement the functions required for a rdt3.0 sender.


  `public static int compute_checksum(String message) {}` </br>
  `public static boolean isCorrupt(rdtPacket packet) {}` </br>
  `public static boolean isACK(rdtPacket packet) {}` </br>
  `public static boolean inOrder(rdtPacket packet, int seqno) {} // has_seq()` </br>

And add a countdown timer to the socket when waiting for ACKs.

Additionally, the packet format will be:

+ --------- + ---------- + ----------------- +
| message | checksum | sequence number |
+ --------- + ---------- + ----------------- +

Where the entire packet is a String and should be decoded to:

message is a String </br>
checksum is an int (and may be negative since java uses signed integers) </br>
sequence number is an int [0,1] </br>
The values are separated by pipes, e.g.</br>
String packet = "Hello, world|-1129|0"</br>
 
Finally, ACKs will be sent using the message "ACK"

Note that, in java, to split a string using pipes you'll need to escape it, e.g.   
`message.split("\\|")`
