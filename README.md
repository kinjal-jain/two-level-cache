Two Level Cache
===========================
This is the codebase for the implementation of Two Level Cache. L1 Cache is implemented using Concurrent HashMap, where as L2 Cache is implemented using an in memory Database.
The application has a main method but doesnt allow realtime use of cache and all the functionality is tested using Unit testing.

##### Content :
1. Source Files
2. Test Files

##### Steps to build the project :

1. Clone the project from the link : [Two Level Cache](https://github.com/kinjal-jain/two-level-cache)
2. Navigate to the directory on local machine and run `mvn clean install` in terminal to build the project.
3. To run the tests, run the command `mvn test` in the terminal.

#### Assumptions:
1. L1 and L2 Cache both are refreshed when the application is started.
2. The Object Value to put in Cache is stringified and then put into the file system.

#### Design Assumptions:
- The application is written with keeping following pointers in mind :
    - L1 Cache is implemented using Concurrent HashMap, to make it synchronized and Thread Safe.
    - L2 Cache is implemented in memory Database.
    - Re-usability of Code using the same interface to implement L1 and L2 Cache.

#### Dependencies:
- Lombok : To generate the Getter and Setter methods
- H2 : For implementing in memory database to be used as L2 Cache
- GSON : For transforming the Object value in a String and vice versa.
- Testing :
    - JUnit
    - Mockito
    
#### Future Scope:
- Write the two level cache service as a Spring Boot micro service and expose the Rest Endpoints to use Two level cache in a concurrent environment.
- The service works as a Key Value Pair service, in a highly concurrent environment and is easily scalable.

##### Created By :
```
Kinjal Jain
Email: jain.kinjalkj@gmail.com
```