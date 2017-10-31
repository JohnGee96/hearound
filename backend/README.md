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
        "lat": 0.000000
        "lng": 0.000000
        "radius": 5  #in kilometer
    }

Example request:

    curl -H "Content-Type: application/json" -X POST -d '{ "lat": 0.000000, "lng": 0.000000, "radius": 5}' http://<Server_IP>/api/nearby_posts


### POST /api/posts
Create a new post by providing a POST request with following JSON format:

    {
        "title": "Sample Post",
        "body": "This the body of a sample post", 
        "lat": 0.000000
        "lng": 0.000000
    }

Example request:

    curl -H "Content-Type: application/json" -X POST -d '{"title":"Sample Post", "body": "This the body of a sample post", "lat":0.000000, "lng": 0.000000}' http://<Server_IP>/api/posts

### PUT /api/posts/#
Update the Post with id #. Provide the PUT request with the following JSON:

    {
        "title": "Sample Post",
        "body": "This will replace the body of the original Sample Post", 
        "lat": 0.000000
        "lng": 0.000000
    }
