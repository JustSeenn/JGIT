## SERVER-SIDE

### Push

In order to implement the push feature on our server we will be using the same classes that we’ve created in the client. To do so the best way is to create a JAR of the client that will be imported into our server.

We will be using Spring, we will define two main routes. the Push route ('push') behavior will be defined in a controller file. We will create a custom PushRequest class that depends on the information that we’ll be sending to our server, in this class we’ll have a commit instance and a list of objects, the objects could be either files or folders exactly like our client, our files and folders are stored in the .jgit/objects in text files without extension. Since the server and the client will have the same basic behavior there is no need to define methods that are capable of constructing the instances of each class from the stored objects. We can copy the files stored in our objects folder as is. Also, for now, we can just send the HEAD file also as is.

Therefore the form of our request object should be similar to:
```
{
  "commitLog": [
    "commitLogLine1",
    "commitLogLine2"
  ],
  "commitHash": "commitHash1",
  "head": [
    "headLine1",
    "headLine2"
  ],
  "objects": [
      {"objectName": [
        "objectLine1",
        "objectLine2",
        "objectLine3"
      ] }
  ]
}
```

### Notes
* There should be a class to represent each type of file, on the client, which will be imported from the package to our server. We could use these classes on our request and for storing.

* We are supposed to keep track of newly created objects and only send them in our push request object but for now, we will be sending all the objects from our client .jgit/object directory.

* We could send the content of files (commitLog, Head, or the objects) as a single String but for better readability, we’ve chosen to use a list of lines.

* We could serialize and serialize the objects and import the client jar to do so, but we can also just send the created files as is.

* The behavior of our push route method is defined as recreating the new jgit files from the received request and deleting older ones.

* Ther are many conditions to look for before applying a push:

    * Compare the date of the commit of the client with the last commit on our server.

    * conflicts.

    * …



### Pull

pulling from our server is recovering last modifications on our jgit repository and applying them to the local client repository. On the server pulling is an inverse push. There are many conditions to look for while pulling.