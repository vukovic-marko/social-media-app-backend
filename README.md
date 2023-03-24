# Social Network App - Backend (Play Scala)
See [this repo](https://github.com/vukovic-marko/social-network-frontend-react) for the fronted app.

Backend application for a social network web application written in Scala using Play Framework and MySQL database. 

## Instructions

 1. Setup MySQL db connection parameters in `conf/application.config`
 2. Run `sbt run`
 
 
## Example data
App provides example data with several users (`user1` - `user5` each with password `pass`) and several posts and friendships for testing purposes.

## Configuring database connection parameters
Database parameters can be configured using `conf/application.config` configuration file.
Predefined values are: 
```
slick.dbs.default {
  db {
  ...
    url="jdbc:mysql://localhost:3306/test"
    user=user1
    password=pass
  }
}
```
