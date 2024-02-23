# Java TCP Chat Room

# Table of Contents
1. [Description](#Description)
2. [Getting Started](#Getting-Started)
3. [Help](#Help)
4. [Authors](#Authors)  


## Description

This project is a java TCP based group chat/messaging service. Once successfully running, it is a fully functioning chat messaging CLI based application. All the commands and how-to-run will be provided below.

## Getting Started

### Installing

* Download any IDE of your choice that can run / compile the required java files.
* The source code can be downloaded to any location as long as you are able to CD into it via command prompt.

### Executing program

* Step-By-Step guide on how to run the CLI application.
* Run Host.java, in this case I done this through Visual Studio Code however most IDE's that support java should work.
* Once you have ran Host.java. Using command prompt, CD into the directory of the source code and execute the following:
```
javac Client.java
```
* Once you have compiled Client.java file you can now execute it using:
```
java Client
```
* Once this is done it will prompt the user to enter their ID. You have now connected a user to the chat room!
* To further connect more users to the chat room, you can CD into the directory using another command prompt window and execute Client again.
```
java Client
```

## Help
Current Actiive Commands:  
The prefix used for commands is "/"
If [] are present, remove them and insert required text.
  
* This command changes the users ID.
```
/ChangeID [ID]
```
* This command quits the user from the chat room.
```
/quit
```
* This command shows the connected users ID, IP and Port.
```
/info
```
* This command allows the user to private message another user.
```
/pm [ID] [MESSAGE]
```
* This command allows the admin to kick another member.
```
/kick [ID]
```

## Authors

