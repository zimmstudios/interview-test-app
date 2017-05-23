##Prerequisites
* Git and an account on Github
* Java 1.8
* Maven (3.3+ recommended)

##Instructions
Fork this git project into your own Github account. 

For this project, you will be calling CHPL's RESTful API. You will be using our Staging system and you will need to register to use our API. Complete the registration here and make sure to hang on to your API Key: https://chpl.ahrqstg.org/#/resources/chpl_api .

Put your API Key into the file src/test/resources/environment.properties. There is already a placeholder for it:

```
apiKey=yourKeyGoesHere
```

In the com.ainq.chpl package, you will find several unit tests written in Java. These tests are written against CHPL's RESTful API and you should expect them all to pass. API documentation is available on the page where you signed up for the API Key. Please write 3-5 additional unit tests which pass in a tool of your choice. You may test the same API methods as the samples or use the API documentation to find additional methods to test.

