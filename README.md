# SuperDuo : An android project for Udacity students!


###Welcome to the SuperDuo! project
This repository actually contains 2 projects; the _**alexandria**_ app that allows for scanning and maintaining a list of books, and the _**Football**_ app where we can follow soccer matches and their results. 
This Readme covers the release notes of both apps, beginning with the alexandria.


##Alexandria 1.0
_**The alexandria Android app allows for scanning and maintaining a list of books**_

This application retrieves book information from the [Google Books API](https://developers.google.com/books/). All titles, cover images, and author information come from there. The original version of this app was built by Sascha Jaschke, and a modified version is given to students in the Udacity Android Nanodegree program.


###Release notes - v1.0 - 20151205

##### System Requirements
Android 4.1 Jelly Bean or later (API level 16)

#####Fixes
* M01 - Updated build and compile version to latest SDK
* M02 - Fixed issue of app crashing when book does not have authors
* M03 - Fixed issue of app rotation for list of books and details book
* M04 - Modified broadcast sender and reciever with intent filter
* M05 - Added functionality of book scan with bar code
* M06 - Added error code handling for no internet, bad response, IO error ..etc
* M07 - Optimized UI based on material design guidelines
* M08 - updated libraries for butter knife, piccaso and stetho 


##Football 1.0
_**With the Football app we can follow soccer matches and know their results**_


The Football app uses the Football-data.org Api to retrieve the fixtures- and teams data. To be able to use it you need to request an Api Key at: [http://api.football-data.org/register](http://api.football-data.org/register). After you've received the key you need to enter it in the app Settings.


###Release notes - v1.0 - 20151101


#####Requirements
Android 3.0 Honeycomb or later (API level 11)


#####Fixes
* J01 - Updated build and compile version to latest SDK
* J02 - Utilized latest availabe libraries
* J03 - Optimized UI for layout re-use
* J04 - added wigets for today and latest scores.
* J05 - added functionality to get team names and logo dynamically from server based on league's









