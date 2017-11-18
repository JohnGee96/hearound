# Hearound
### A geolocation-based message board system

## Branch Naming Convention:

    master - production level code 
      |- dev - collection of tested features
      |- feature/<feature-name> - singular feature that usually takes a week to implement

## File Tree Structure:

    SourceFolder
      |- app - contains the Android App source code
          |- res
          |- ...
      |- backend - contains the configuration for the Flask API Server

# Project Hearound API Documentations

### GET /api/posts
Retrieves all posts

Example request: 

    curl -X GET http://<Server_IP>/api/posts

Example Response: 

    {
        "num_results": 5, 
        "objects": [
            {
                "body": "This is the geolocation of Boston", 
                "created_at": "2017-10-30T23:51:21", 
                "id": 1, 
                "lat": 42.3639457, 
                "lng": -71.0569078, 
                "title": "Boston", 
                "updated_at": "2017-10-30T23:54:12"
            }, 
            {
                "body": "This is the geolocation of New York", 
                "created_at": "2017-10-31T00:40:23", 
                "id": 2, 
                "lat": 40.7137746, 
                "lng": -74.0060504, 
                "title": "New York", 
                "updated_at": "2017-10-31T00:49:39"
            }, 
            {
                "body": "This is the geolocation of Los Angeles", 
                "created_at": "2017-10-31T00:42:50", 
                "id": 3, 
                "lat": 34.055588, 
                "lng": -118.2283376, 
                "title": "Los Angeles", 
                "updated_at": "2017-10-31T00:42:50"
            }, 
            {
                "body": "This is the geolocation of London", 
                "created_at": "2017-10-31T00:45:01", 
                "id": 4, 
                "lat": 51.5076913, 
                "lng": -0.1288709, 
                "title": "London", 
                "updated_at": "2017-10-31T00:45:01"
            }, 
            {
                "body": "This is the geolocation of Hillside", 
                "created_at": "2017-10-31T00:47:00", 
                "id": 5, 
                "lat": 42.4088871, 
                "lng": -71.1196583, 
                "title": "Hillside Apartments", 
                "updated_at": "2017-10-31T00:47:00"
            }
        ], 
        "page": 1, 
        "total_pages": 1
    }

### GET /api/posts/#
Retrieves a post with id #.

Example request: 

    curl -X GET http://<Server_IP>/api/posts/1

Example Response:

    {
        "body": "This is the geolocation of Boston", 
        "created_at": "2017-10-30T23:51:21", 
        "id": 1, 
        "lat": 42.3639457, 
        "lng": -71.0569078, 
        "title": "Boston", 
        "updated_at": "2017-10-30T23:54:12"
    }

### POST /api/nearby_posts
Retrieve posts within a fixed radius around a geolocation. Must provide a POST request with the following
JSON format:

    {
        "lat": 42.408226
        "lng": -71.115944
        "radius": 5  #in kilometer
    }

Example request:

    curl -H "Content-Type: application/json" -X POST -d '{ "lat": 42.408226, "lng": -71.115944, "radius": 5}' http://<Server_IP>/api/nearby_posts

Example Response:

    [
        {
            "body": "This is the geolocation of Hillside", 
            "id": 5, 
            "lat": 42.4088871, 
            "lng": -71.1196583, 
            "modified_at": [
            "2017-10-31", 
            "00:47:00"
            ], 
            "title": "Hillside Apartments", 
            "updated_at": [
            "2017-10-31", 
            "00:47:00"
            ]
        }, 
        {
            "body": "This is the geolocation of Halligan", 
            "id": 6, 
            "lat": 42.408226, 
            "lng": -71.115944, 
            "modified_at": [
            "2017-10-31", 
            "01:08:39"
            ], 
            "title": "Halligan Hall", 
            "updated_at": [
            "2017-10-31", 
            "01:08:39"
            ]
        }
    ]

### POST /api/nearby_post_locations
Retrieve the coordinates of nearby posts in a json format Must provide a POST request with the following
JSON format:

    {
        "lat": 42.408226
        "lng": -71.115944
        "radius": 5  #in kilometer
    }

Example request:

    curl -H "Content-Type: application/json" -X POST -d '{ "lat": 42.408226, "lng": -71.115944, "radius": 5}' http://<Server_IP>/api/nearby_post_locations

Example Response:

    {
        "features": [
            {
            "geometry": {
                "coordinates": [
                -71.1196583, 
                42.4088871
                ], 
                "type": "Point"
            }, 
            "properties": {
                "author": "John Smith"
            }, 
            "type": "Feature"
            }, 
            {
            "geometry": {
                "coordinates": [
                -71.115944, 
                42.408226
                ], 
                "type": "Point"
            }, 
            "properties": {
                "author": "John Smith"
            }, 
            "type": "Feature"
            }
        ], 
        "type": "FeatureCollection"
    }


### POST /api/posts
Create a new post by providing a POST request with following JSON format:

    {
        "title": "Sample Post",
        "body": "This the body of a sample post", 
        "lat": 42.408226
        "lng": -71.115944
    }

Example request:

    curl -H "Content-Type: application/json" -X POST -d '{"title":"Sample Post", "body": "This the body of a sample post", "lat": 42.408226, "lng": -71.115944}' http://<Server_IP>/api/posts

### PUT /api/posts/#
Update the Post with id #. Provide the PUT request with the following JSON:

    {
        "title": "Sample Post",
        "body": "This will replace the body of the original Sample Post", 
        "lat": 42.408226
        "lng": -71.115944
    }


Example request:

    curl -H "Content-Type: application/json" -X PUT -d '{"title":"Sample Post", "body": "This will replace the body of the original Sample Post", "lat": 42.408226, "lng": -71.115944}' http://<Server_IP>/api/posts/1

