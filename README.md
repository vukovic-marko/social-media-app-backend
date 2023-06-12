# Social Media App Backend

This is the backend component of the Social Media App, a full-stack web application for creating a simple social media network. The backend is developed using Play Scala and MySQL.

For the frontend component of the application, please visit the [Social Media App Frontend repository](https://github.com/vukovic-marko/social-media-app-frontend).

## Key Features

- User registration and authentication
- Posting textual content
- Searching for other users
- Sending and accepting friend requests
- Liking posts
- Viewing friends' profiles

## Required Software

Make sure you have the following software installed:

- Java Development Kit (JDK) 8
- sbt
- MySQL Community Server

## Installation

1. Clone this repository:

   ```bash
   git clone https://github.com/vukovic-marko/social-media-app-backend.git
   ```
2. Navigate to the project directory:

   ```bash
   cd social-media-app-backend
   ```
3. Set up the database configuration:
   - Create a MySQL database for the application.
   - Update the `conf/application.conf` file with your database credentials:
    
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
4. Run the application:
5. 
   ```bash
   sbt run
   ```
   The application will start on `http://localhost:9000`.
5. Visit `http://localhost:9000` in your web browser and apply SQL evolutions.

## Database Configuration
To configure the database for the Social Media App backend, follow these steps:

1. Create a MySQL database for the application.
2. Update the conf/application.conf file with your database credentials as shown in the Installation section.

## Example data
App provides example data with several users (`user1` - `user5` each with password `pass`) and several posts and friendships for testing purposes.

## Frontend Application
To access the frontend component of the Social Media App, please visit the Social Media App Frontend repository.

Feel free to explore, contribute, and provide feedback.
