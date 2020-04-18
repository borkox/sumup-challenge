# SumUp code challenge variant 1

### build

>mvn clean install

### start the application

> java -jar target/task1a-0.0.1-SNAPSHOT.jar

### test the app

From the main folder of the application, execute this in a shell:
 >curl -v -d @src/test/resources/example.json http://localhost:8080/api/job/tasksAsBash|bash
 
 ## Rest services
 
 
 ### ordered tasks of bash
 > PUT
 > /api/job/tasksInOrder

payload:

 ```json
 {
   "tasks":[
     {
       "name":"task-1",
       "command":"touch /tmp/file1"
     },
     {
       "name":"task-2",
       "command":"cat /tmp/file1",
       "requires":[
         "task-3"
       ]
     },
     {
       "name":"task-3",
       "command":"echo 'Hello World!' > /tmp/file1",
       "requires":[
         "task-1"
       ]
     },
     {
       "name":"task-4",
       "command":"rm /tmp/file1",
       "requires":[
         "task-2",
         "task-3"
       ]
     }
   ]
 
 }
 ```
 Response example:
 ```json
[
  {
    "name": "task-1",
    "command": "touch /tmp/file1"
  },
  {
    "name": "task-3",
    "command": "echo 'Hello World!' > /tmp/file1"
  },
  {
    "name": "task-2",
    "command": "cat /tmp/file1"
  },
  {
    "name": "task-4",
    "command": "rm /tmp/file1"
  }
]
```
 
  ### returns the bash script
  > PUT
  > /api/job/tasksAsBash
  
