# pentaho-userAgent-plugin

# Info
Pentaho Data Integration plugin for processing user-agent strings.
Using for example the user agent string (Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36), the step can extract the device type, the Operating system , the Browser name, the browser version and so on.

<img src='https://s31.postimg.org/o4qf8eoav/pdi_Step.png' border='0' alt='postimage' width='450px'/>
<img src='https://s31.postimg.org/ncrfk61bb/window.png' border='0' alt='postimage' width='450px'/>
<img src='https://s31.postimg.org/b7zb0cuwn/preview.png' border='0' alt='postimage' width='450px'/>

#Build the project
Clone the project and execute the command mvn package

#Deploy in the Pentaho data Integration
To deploy the OSGI bundle in the Pentaho data integration, copy the jar file located in the target folder to your data-integration/system/karaf/deploy


# Acknowledgments #
Thanks Harald Walker that made freely available the user-agent-utils, on his [blog](http://www.bitwalker.eu/blog), and in the [GIT](https://github.com/HaraldWalker/user-agent-utils) the code that we are used to parse the user agent strings. It was held all the original class packages.
