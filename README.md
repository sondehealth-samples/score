# Setting up the Sonde Samples

Clone the repo using: `https://github.com/sondehealth-samples/score.git`

**The repo contains below directories:**
1. Server - This contains a java app server that can be run locally
2. Client - This has Web, iOS and Android client examples on how to connect and get started on the server running locally


# Setting up the Server:
3. Open cmd or linux terminal
4. Execute cd server/java/ - This directory contains the app server
5. vim src/main/java/com/sondehealth/apiserver/model/Constants.java
6. Replace the `'<clientId>'` and `'<clientSecret>'` by the clientId and clientSecret that you must have received from Sonde.
7. Now, execute  the command `mvn clean install`. This will build the app server
8. Next, execute  `java -jar target/server-0.0.1-SNAPSHOT.jar`

This will start the AppServer on your local machine on the port **8080**. This completes the server setup.

# Setting up clients:

 **1. iOS**
	How to run iOS Sample:
	**Prerequisite:**
	To run the iOS sample, you need a mac OSx 10.14 machine and x code 10.2 or above to open the project.

**Steps:**
1. Goto the /score/⁨client⁩/⁨ios/⁨sonde_ios_sample⁩ directory
 2. Open the sonde_ios_sample.xcodeproj file and it will open in xcode.
 3. Change the sonde's platform URL in APIHandler file.
 4. Setup sample server on your localhost if setup in some public IP then need to change the path of localhost with your server path..
 5. Click on the Run button in xcode.
 6. After few seconds app will install on iPhone simulator.
 
**How to use the sample:**
1. After running the application, click on the "Start Activity" button.
 2. A new screen will open to record the audio. Click on "Start Recording" Button.
 3. Say "Pa-Ta-Ka" for 6-10 seconds as voice sample.
 4. Click on "Stop Recording". APIs calls are initiaed on click of "Stop Recording".
 5. After few seconds, Your measures score is popped up in app.

**2. Android**

**Overview**

This app demostrates

1. User signup at sample server
2. Fetching of list of assigned measures to users
3. Recording of wav file
4. Uploading wav to S3 bucket and gettting the score for wav file
	
**Configuration**

- Open the Configuration.java file and replace the BASE_URL with your provided by Sonde
- For ACCESS_TOKEN_URL and REGISTER_USER url you can replace the localhost with your machine's ip address (If you are not able to access with localhost)
	
**Run**

1. Clone or download the source code
2. Import the android sample (score/client/android) in android studio
3. Run the project

**How to use the sample app**

1. After running the application, click on the "Start Activity" button.
2. A new screen will open to record the audio. Click on "Start Recording" Button.
3. Say "Pa-Ta-Ka" for 6 seconds as voice sample.
4. Click on "Stop Recording", APIs calls are initiaed on click of "Stop Recording".
5. After few seconds, Your score for measure is shown in dialog.


**3. Web**

 - pre-requisite:
  
	1. Backend server should be run on http://localhost:8080.
	2. Internet should work properly.

 - Steps to run:

	1. Go to score/client/web directory in [Master] branch of score repository.
	2. Open index.html in chrome.
	3. You will get log in right side which include user id, file location, and score.
	4. In left side, you need to choose measures.
	5. Allow permission to record audio in browser.
	6. Once recording started, Say Banana or Pa-Ta-Ka repeatedly.
	7. After recording, score will calculate and displayed in log.
	8. Click on check again for score analysis for same user id otherwise reload the page from browser for  new user.


 [Web]: <https://github.com/sondehealth-samples/score/tree/master/client/web>
 [Master]: <https://github.com/sondehealth-samples/score>