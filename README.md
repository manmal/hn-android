HN (Android)
============

This is the official repo for **HN**, an unofficial Hacker News client for Android, built for reliability and usability.  
[Download the app](https://play.google.com/store/apps/details?id=com.manuelmaly.hn) and read the [introductory blog post](http://manuelmaly.com/blog/HN-Hacker-News-Reader/).

If you find any issues, please post them into the [Issues section](https://github.com/manmal/hn-android/issues), send a
[Pull request](https://github.com/manmal/hn-android/pulls), or tweet me at [@manuelmaly](https://twitter.com/manuelmaly/).

Note about Pull Requests: If you commit your additions into feature branches (http://nvie.com/posts/a-successful-git-branching-model/) instead of
the master branch, you will have no problems later on when you pull in updates from my repo. If you do that, you always have a clean
master branch at all times (meaning it does not contain anything other than my repo's master branch commits).

Setup
-----

1. Clone to a folder on your harddisk (```git clone git@github.com:manmal/hn-android.git```) - if you will use Eclipse, you can instead use EGit and skip step 3
2. Make sure you have an SDK version of >= Android 2.2 (API Level 8) installed
3. Import the project to Eclipse, and select an available Android SDK target if it complains that "Android 2.2" is not available on your system
4. Import the appcompat project into Eclipse.

If you use Eclipse, please setup the provided eclipse formatter and cleanup profile in order to maintain the code style.
Here are a few instructions:
1. Select the project in the package explorer
2. Right Click on the project
3. Select Properties
4. Select Java Code Style > Cleanup
5. Check the 'Enable specific project settings' checkbox
6. Import the provided cleanup profile from hn-android/code_style/eclipse/hn_eclipse_cleanup_profile.xml
7. Select Java Code Style > Formatter
8. Check the 'Enable specific project settings' checkbox
9. Import the provided cleanup profile from hn-android/code_style/eclipse/hn_eclipse_formatter.xml

Now you can automatically format and cleanup your code by selecting Source > Clean Up or Source > Format from the Menu Bar. 

Third party software
--------------------

* [Android Support Library v4](http://developer.android.com/tools/extras/support-library.html) - Apache License 2.0
* [Android Annotations](https://github.com/excilys/androidannotations) - Apache License 2.0
* [Google Analytics](http://www.google.com/analytics/) - [custom license](http://www.google.com/intl/en/analytics/tos_content.html)
* [Jsoup](http://jsoup.org) - MIT License

* [Comfortaa Font](http://www.google.com/webfonts/specimen/Comfortaa) - SIL Open Font License

License
-------

[MIT License] (http://opensource.org/licenses/mit-license.php):

Copyright (c) 2012 Manuel Maly

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
