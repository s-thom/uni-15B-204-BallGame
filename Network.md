Networking and Comms
=======

This outlines how commincation works throughout the app, with other players.


Concept
-----------
Simply put, when a new player starts playing, they broadcast and ask who is the server.   If no one replies within 1 second, they become the server.  They send out a 301 to let everyone know.

Other plays can join, and the server acknowledges this with a 101.

When a game is about to start, the server will annouce this, and then count the players down.

In game play, the clients constantly broadcast their location using 102.  


When a player gets to the finish spot, the server acknowledges this, and the game is over.


| Code                | Description                        |
|---------------------|------------------------------------|
| 300                 | Who is the server?                 |
| 301                 | I am the server                    |
| 100 <playername>    | Player ___ Joining Game            |
| 101 <ip>            | Player has been accepted into game |
| 200 <level>         | Level ___ is about to start        |
| 201 <time>          | Time till game starts, 0 means go! |
| 202 <ip,playername> | Game Over, announcing winner       |
| 102 <x,y>           | Update Location of Client.         |